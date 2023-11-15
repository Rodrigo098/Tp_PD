import pt.isec.pd.trabalhoPratico.dataAccess.DbManage;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.recordDados.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ProgServidor {
    public static final int MAX_SIZE = 4000;
    List<Socket> clients = new ArrayList<>();
    public void servico() {

            try(ServerSocket socket = new ServerSocket(6001)) {
                while (true){
                    Socket cli = socket.accept();// aceita clientes
                    cli.setSoTimeout(10000);
                    clients.add(cli);// adiciona cliente conectado a lista de clientes
                    new Thread(new ThreadCliente(cli)).start();

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                clients.clear();
            }
    }

    class ThreadCliente extends Thread {
        Socket client;
        boolean flagStop,isadmin;// flag para terminar a thread
        String email;

        List <Evento> eventosPresencasUser = new ArrayList<>();
        List <Evento> eventosPresencasAdmin = new ArrayList<>();


        public ThreadCliente(Socket cli) {
            client = cli;
            flagStop = false;
            isadmin=false;
        }

        @Override
        public void run() {
            try(ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client.getInputStream())
            ) {
                //Quando conecta a primeira vez vai guardar o email

                Geral o = (Geral) in.readObject();

                //Comandos de Registo e Login
                if(o.getTipo() == Message_types.LOGIN){// para descobrir qual a classe estava a pensar em algo para o processamento depois dos dados
                    Msg_Login aux = (Msg_Login) o;
                    String password=aux.getPassword();
                    email=aux.getEmail();
                    System.out.println(email);
                    Boolean []resposta=DbManage.autentica_user(email,password);
                    if(!resposta[0])
                        out.writeObject(new Geral(Message_types.INVALIDO));
                    else {
                        if(resposta[1]){
                            isadmin=true;
                            out.writeObject(new Geral(Message_types.ADMINISTRADOR));
                        }else out.writeObject(new Geral(Message_types.UTILIZADOR));
                    }
                    // aqui verifico se é o admin e ponho o boolean a true ou false
                }
                else if (o.getTipo() == Message_types.REGISTO) {// Aqui neste caso faltam fazer mais coisas como guardar na base de dados

                    Mgs_RegistarEditar_Conta aux=(Mgs_RegistarEditar_Conta)o;
                    email= aux.getEmail();
                    Utilizador user=new Utilizador(aux.getNome(),aux.getEmail(),aux.getNum_estudante());
                    DbManage.Registonovouser(user,aux.getPassword());
                    // A implementar
                }else{
                    out.writeObject("Comando Invalido para estabelecer conexão");// apenas como exemplo
                }

                //Comandos para os utilizadores normais
                if(!isadmin) {
                    while (!flagStop) {
                        Geral message = (Geral) in.readObject();
                        switch (message.getTipo()) {
                            case EDITAR_REGISTO ->{
                                Mgs_RegistarEditar_Conta aux=(Mgs_RegistarEditar_Conta)message;
                                Utilizador user=new Utilizador(aux.getNome(), aux.getEmail(), aux.getNum_estudante());
                                if(DbManage.edita_registo(user,aux.getPassword())){
                                    out.writeObject(new Geral(Message_types.VALIDO));
                                }else
                                    out.writeObject(new Geral(Message_types.ERRO));

                            }
                            case REGISTO -> {
                                Mgs_RegistarEditar_Conta aux = (Mgs_RegistarEditar_Conta) message;
                                Utilizador user = new Utilizador(aux.getNome(), aux.getEmail(), aux.getNum_estudante());

                                if(DbManage.Registonovouser(user, aux.getPassword())){
                                    out.writeObject(new Geral(Message_types.VALIDO));
                                }else
                                    out.writeObject(new Geral(Message_types.ERRO));

                            }
                            case SUBMICAO_COD -> out.writeObject("Insere dados na database");

                            case CONSULTA_PRES_UTILIZADOR -> {
                                Msg_ListaEventos aux = (Msg_ListaEventos)message;

                                //Depois temos que mandar aí a classe com os critérios/filtros e substituir esses parametros extras

                               // List<Evento> eventosAssistidos = DbManage.ConsultaPresencas_user(email, new Msg_ConsultaComFiltros());
                                //eventosPresencasUser.addAll(eventosAssistidos); //vou utilizar o eventos presenças user para fazer o ficheiro csv do utilizador

                                //Mandarmos isso pela classe que recebe a lista de eventos
                                //out.writeObject(new ConsultaEventos_EliminaPresencas_InserePresencas(aux.getNome(),Message_types.VALIDO,eventosAssistidos));
                            }

                            case CSV_UTILIZADOR -> {
                                File file=new File("minhasPresencas.csv");
                                if(!file.exists()) {
                                    if(!file.createNewFile())
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }

                                DbManage.PresencasCSV(eventosPresencasUser,file);
                                sendfile(out,file);

                                //out.writeObject(new Geral(Message_types.VALIDO));
                            }

                            case LOGOUT -> {
                                out.writeObject(new Geral(Message_types.VALIDO));
                                flagStop = true; // termina a thread
                            }

                            default -> out.writeObject("Operacao invalida");
                        }
                    }
                }else {
                    //Comandos para o admin
                    while (!flagStop) {
                        Geral message = (Geral) in.readObject();
                        switch (message.getTipo()) {
                            case CRIA_EVENTO -> {
                                //Falta a validacao do admin
                                Msg_Cria_Evento evento = (Msg_Cria_Evento) message;
                                if(DbManage.Cria_evento(evento)){//.getNome(), evento.getLocal(), evento.getData(), evento.getHoreInicio(), evento.getHoraFim())){
                                    out.writeObject(new Geral(Message_types.VALIDO));
                                }
                                else{
                                    out.writeObject(new Geral(Message_types.ERRO));
                                }

                            }
                            case EDIT_EVENTO -> {
                                //Estou a reutilizar a classe Cria_evento mas...
                                //Vamos ter um problema aqui por causa do nome do evento. Se ele for alterável é necessário saber o nome atual do evento e o novo nome portanto precisa-se de mais um campo
                                //Cria-se outra classe?

                                Msg_Edita_Evento evento = (Msg_Edita_Evento) message;

                                if(DbManage.Edita_evento(evento)){
                                    out.writeObject(new Geral(Message_types.VALIDO));
                                    //ATT: ESTOU UM POUCO CONFUSA COM ESSAS CLASSES DE COMUNICAÇÃO E AS DE DADOS.
                                    //Aqui posso apenas dizer que a alteração foi valida ou mando mais alguma coisa?
                                }
                                else{
                                    out.writeObject(new Geral(Message_types.ERRO));
                                }
                            }
                            case ELIMINAR_EVENTO -> {
                                Msg_String aux = (Msg_String)message;
                                if(DbManage.Elimina_evento(aux.getConteudo())){
                                    out.writeObject(new Geral(Message_types.VALIDO));
                                }
                                else{
                                    out.writeObject(new Geral(Message_types.ERRO));
                                }
                            }
                            case CONSULTA_EVENTOS -> {
                                //Vou usar o cria_evento só pq sim, mas cada campo preenchido vai funcionar como um filtro, logo a classe a utilizar pode vir vazia
                                Msg_ConsultaComFiltros aux = (Msg_ConsultaComFiltros)message;
                                List <Evento> eventosConsultados = DbManage.Consulta_eventos(aux);

                                //Falta mandar isso em condições como resposta para o cliente, pq aqui não faz sentido o nome do evento
                                //Talvez seja necessário outra classe... Idk. Vou deixar assim por enquanto
                                Evento [] resp=eventosConsultados.toArray(new Evento[0]);
                               // Nao tenho 100% certeza se é este o tipo de de dados a enviar
                                out.writeObject(new Msg_ListaEventos(Message_types.VALIDO,resp));
                            }
                            case GERAR_COD ->{
                                //Vai ser necessário outra classe também, acho eu. Vou fazer assim agr só para testar
                                Msg_String aux = (Msg_String)message;
                                int validade = 30; //validade em minutos
                                int  code = DbManage.GeraCodigoRegisto(aux.getConteudo() ,validade);

                                out.writeObject(new Msg_String(Integer.toString(code), Message_types.VALIDO));
                            }
                            case INSERE_PRES ->  {
                                Msg_EliminaInsere_Presencas aux = (Msg_EliminaInsere_Presencas)message;
                                if(DbManage.InserePresencas(aux.getNome_evento(),aux.getLista())){
                                    out.writeObject(new Geral(Message_types.VALIDO));
                                }
                                else{
                                    out.writeObject(new Geral(Message_types.ERRO));
                                }
                            }
                            case ELIMINA_PRES -> {
                                Msg_EliminaInsere_Presencas aux = (Msg_EliminaInsere_Presencas)message;
                                if(DbManage.EliminaPresencas(aux.getNome_evento(),aux.getLista())){
                                    out.writeObject(new Geral(Message_types.VALIDO));
                                }
                                else{
                                    out.writeObject(new Geral(Message_types.ERRO));
                                }
                            }
                            case CONSULTA_PRES_EVENT -> {
                                Msg_String aux = (Msg_String)message;
                                List<Utilizador> cenas = DbManage.Presencas_evento(aux.getConteudo());
                                if(cenas!=null){
                                    Utilizador[] res = new Utilizador[cenas.size()];
                                for (int i = 0; i <cenas.size() ; i++) {
                                    res[i]= cenas.get(i);
                                }
                                out.writeObject(new Msg_ListaRegistos(Message_types.VALIDO, res));}
                            }
                            case CONSULTA_PRES_UTILIZADOR -> {
                                Msg_ConsultaComFiltros aux = (Msg_ConsultaComFiltros)message;
                                //Os outros argumentos são os filtros que podem ser utilizados
                                List<Evento> eventosAssistidos = DbManage.ConsultaPresencas_user(email, aux);
                                Evento[] res = new Evento[eventosAssistidos.size()];
                                for (int i = 0; i <eventosAssistidos.size() ; i++) {
                                    res[i]= eventosAssistidos.get(i);
                                }
                                //Mandarmos isso pela classe que recebe a lista de eventos
                                out.writeObject(new Msg_ListaEventos(Message_types.VALIDO, res));
                            }
                            case CSV_PRESENCAS_UTI_NUM_EVENTO -> {

                            }
                            case CSV_PRESENCAS_DO_EVENTO -> {
                                //PresencasCSV(eventosPresencasAdmin,"presencasUtilizadores.csv");
                                //out.writeObject(new Geral(Message_types.VALIDO));
                            } //Serão necessários 2 ficheiros csv

                            case LOGOUT ->{
                                out.writeObject(new Geral(Message_types.VALIDO));
                                flagStop = true; // termina a thread
                            }
                            default -> out.writeObject("Operacao invalida");
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException ignored) {
            } finally {
                try {
                    client.close();
                } catch (IOException ignored) {
                }
                clients.remove(client);
                System.out.println("Cliente: "+ email +"terminado");
            }
        }
    }

    private void sendfile(OutputStream out,File file){
        byte []fileChunk = new byte[MAX_SIZE];
        try (FileInputStream filereader=new FileInputStream(file)){
        int nbytes;
        do{
            nbytes=filereader.read(fileChunk);
            if(nbytes!=-1){
                out.write(fileChunk,0,nbytes);
                out.flush();
            }
        }while (nbytes>0);
            System.out.println("File sended");

        } catch (IOException e) {
            System.out.println(""+e.getCause());
        }

    }

}
