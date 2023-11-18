import pt.isec.pd.trabalhoPratico.dataAccess.DbManage;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.recordDados.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class ProgServidor {
    public static final int MAX_SIZE = 4000;
    private final String ipMuticastString = "224.0.1.0";
    private InetAddress grupoMulticast;
    private DatagramSocket socketMulticast;
    private final int portoClientes;
    private ServerSocket socketServidor;
    List<Socket> clients = new ArrayList<>();
    private Boolean pararServidor = false;

    public ProgServidor(int portoClientes) {
        this.portoClientes = portoClientes;
    }

    //////////////////////////////////// SERVIÇO /////////////////////////////
    public void servico() {
        new Thread(new ThreadLeLinhaComandos()).start();
        try
        {
            socketServidor = new ServerSocket(portoClientes);
            try {
                socketMulticast = new DatagramSocket();
                this.grupoMulticast = InetAddress.getByName(ipMuticastString);
            } catch (SocketException | UnknownHostException e) {
                throw new RuntimeException("Nao foi possivel criar o socket para multicast, erro [" + e + "]");
            }

            while(!pararServidor){
                Socket cli = socketServidor.accept();// aceita clientes
                cli.setSoTimeout(10000);
                clients.add(cli);// adiciona cliente conectado a lista de clientes
                new Thread(new ThreadCliente(cli)).start();
            }
        } catch (IOException e) {
            System.out.println("<SERVIDOR> já não se aceitam mais clientes.");
        }
    }

    ////////////////////////////// ATUALIZAÇÃO ASSÍNCRONA /////////////////////////////
    public synchronized void envioDeAvisoDeAtualizacao(String msgAtaulizacao) {
        byte[] msg = msgAtaulizacao.getBytes();
        DatagramPacket atualizacaoPacket = new DatagramPacket(msg, msg.length, grupoMulticast, portoClientes);
        try {
            socketMulticast.send(atualizacaoPacket);
            System.out.println("Aviso de atualizacao enviado aos clientes...");
        } catch (IOException e) {
            throw new RuntimeException("Nao foi possivel informar da atualizacao ao clientes...");
        }
    }

    ////////////////////////////////// THREAD LINHA DE COMANDOS /////////////////////////////
    class ThreadLeLinhaComandos extends Thread {
        @Override
        public void run(){
            String inserido;
            do{
                Scanner linhaComandos = new Scanner(System.in);
                System.out.println("<SERVIDOR> Escreva \"sair\" para terminar o servidor");
                inserido = linhaComandos.nextLine();
                //if(inserido.equals("atua"))
                  //  envioDeAvisoDeAtualizacao("atualizacao");
            }while (!inserido.equals("sair"));
            envioDeAvisoDeAtualizacao("fimServidor");
            try {
                socketServidor.close();
                socketMulticast.close();
                clients.clear();
            } catch (IOException e) {
                throw new RuntimeException("erro a fechar sockets");
            }
            pararServidor = true;
        }
    }
    ////////////////////////////////// THREAD CLIENTE /////////////////////////////
    class ThreadCliente extends Thread {
        boolean flagStop, isadmin, logado;
        Socket client;
        String email;
        List <Evento> eventosPresencasUser = new ArrayList<>();
        List <Evento> eventosPresencasAdmin = new ArrayList<>();

        public ThreadCliente(Socket cli) {
            client = cli;
            flagStop = false;
            isadmin = false;
        }

        @Override
        public void run() {
            try(ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client.getInputStream())
            ) {
                while(!pararServidor) {
                    Geral msgRecebida = (Geral) in.readObject();

                    //Pedidos de Registo e Login
                    if(msgRecebida.getTipo() == Message_types.LOGIN){// para descobrir qual a classe estava a pensar em algo para o processamento depois dos dados
                        Msg_Login aux = (Msg_Login) msgRecebida;
                        email = aux.getEmail();
                        System.out.println(email);

                        Boolean [] resposta = DbManage.autentica_user(email, aux.getPassword());

                        if(!resposta[0])
                            out.writeObject(new Geral(Message_types.INVALIDO));
                        else {
                            logado = true;
                            if(resposta[1]) {
                                isadmin = true;
                                out.writeObject(new Msg_String(ipMuticastString, Message_types.ADMINISTRADOR));
                            } else
                                out.writeObject(new Msg_String(ipMuticastString, Message_types.UTILIZADOR));
                        }
                    }
                    else if (msgRecebida.getTipo() == Message_types.REGISTO) {// Aqui neste caso faltam fazer mais coisas como guardar na base de dados
                        logado = true;

                        Mgs_RegistarEditar_Conta aux=(Mgs_RegistarEditar_Conta)msgRecebida;
                        email = aux.getEmail();
                        Utilizador user = new Utilizador(aux.getNome(),aux.getEmail(),aux.getNum_estudante());

                        DbManage.RegistoNovoUser(user,aux.getPassword());
                        out.writeObject(new Msg_String(ipMuticastString, Message_types.UTILIZADOR));

                    }else{
                        out.writeObject(new Geral(Message_types.FAZER_LOGIN));
                    }

                    //Pedidos para clientes to tipo UTILIZADOR
                    if(!isadmin) {
                        while (!flagStop && !pararServidor) {
                            Geral message = (Geral) in.readObject();
                            switch (message.getTipo()) {
                                case EDITAR_REGISTO ->{
                                    Mgs_RegistarEditar_Conta aux = (Mgs_RegistarEditar_Conta)message;
                                    Utilizador user = new Utilizador(aux.getNome(), aux.getEmail(), aux.getNum_estudante());

                                    if(DbManage.edita_registo(user,aux.getPassword())){
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                        envioDeAvisoDeAtualizacao("atualizacao");
                                    }else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case SUBMICAO_COD -> {
                                    //vai à BD verificar -> têm de fazer, por enquanto está inválido
                                    out.writeObject(new Geral(Message_types.INVALIDO));
                                /*se for valido fazer
                                    envioDeAvisoDeAtualizacao("atualizacao");
                                 */
                                }
                                case CONSULTA_PRES_UTILIZADOR -> {
                                    Msg_ConsultaComFiltros aux = (Msg_ConsultaComFiltros) message;

                                    //Depois temos que mandar aí a classe com os critérios/filtros e substituir esses parametros extras

                                    List<Evento> eventosAssistidos = DbManage.ConsultaPresencas_user(email, aux);
                                    if(eventosAssistidos.isEmpty())
                                        out.writeObject(new Geral(Message_types.ERRO));
                                    else {
                                        eventosAssistidos.clear();
                                        eventosPresencasUser.addAll(eventosAssistidos); //vou utilizar o eventos presenças user para fazer o ficheiro csv do utilizador
                                        Evento[] res = eventosAssistidos.toArray(new Evento[0]);
                                        out.writeObject(new Msg_ListaEventos(Message_types.VALIDO, res));
                                    }
                                }
                                case CSV_UTILIZADOR -> {
                                    //NÃO DEVIAM ESTAR A FAZER ISTO, VÃO BUSCAR OS DADOS E CRIAR O CSV DESTE LADO
                                    File file = new File("minhasPresencas.csv");
                                    if(!file.exists()) {
                                        if(!file.createNewFile())
                                            out.writeObject(new Geral(Message_types.ERRO));
                                    }

                                    DbManage.PresencasCSV(eventosPresencasUser,file);
                                    sendfile(out,file);
                                }
                                case LOGOUT -> flagStop = true;
                                default -> out.writeObject(new Geral(Message_types.INVALIDO));
                            }
                        }
                    } else {
                        //Pedidos para clientes to tipo UTILIZADOR
                        while (!flagStop && !pararServidor) {
                            Geral message = (Geral) in.readObject();
                            switch (message.getTipo()) {
                                case CRIA_EVENTO -> {
                                    Msg_Cria_Evento evento = (Msg_Cria_Evento) message;
                                    if(DbManage.Cria_evento(evento)) {//.getNome(), evento.getLocal(), evento.getData(), evento.getHoreInicio(), evento.getHoraFim())){
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                        envioDeAvisoDeAtualizacao("atualizacao");
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case EDIT_EVENTO -> {
                                    Msg_Edita_Evento evento = (Msg_Edita_Evento) message;
                                    if(DbManage.Edita_evento(evento)) {
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                        envioDeAvisoDeAtualizacao("atualizacao");
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case ELIMINAR_EVENTO -> {
                                    Msg_String aux = (Msg_String)message;
                                    if(DbManage.Elimina_evento(aux.getConteudo())) {
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                        envioDeAvisoDeAtualizacao("atualizacao");
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case CONSULTA_EVENTOS -> {
                                    Msg_ConsultaComFiltros aux = (Msg_ConsultaComFiltros) message;

                                    List <Evento> eventosConsultados = DbManage.Consulta_eventos(aux);
                                    if(eventosConsultados.isEmpty())
                                        out.writeObject(new Geral(Message_types.ERRO));
                                    else {
                                        eventosConsultados.clear();
                                        eventosPresencasUser.addAll(eventosConsultados); //vou utilizar o eventos presenças user para fazer o ficheiro csv do utilizador
                                        Evento[] res = eventosConsultados.toArray(new Evento[0]);
                                        out.writeObject(new Msg_ListaEventos(Message_types.VALIDO, res));
                                    }
                                }
                                case GERAR_COD ->{
                                    Msg_String_Int aux = (Msg_String_Int)message;
                                    int  code = DbManage.GeraCodigoRegisto(aux.getConteudo(), aux.getNumero());
                                    if(code != 0)
                                        out.writeObject(new Msg_String(Integer.toString(code), Message_types.VALIDO));
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case INSERE_PRES ->  {
                                    Msg_EliminaInsere_Presencas aux = (Msg_EliminaInsere_Presencas)message;
                                    if(DbManage.InserePresencas(aux.getNome_evento(),aux.getLista())) {
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                        envioDeAvisoDeAtualizacao("atualizacao");
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case ELIMINA_PRES -> {
                                    Msg_EliminaInsere_Presencas aux = (Msg_EliminaInsere_Presencas)message;
                                    if(DbManage.EliminaPresencas(aux.getNome_evento(),aux.getLista())) {
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                        envioDeAvisoDeAtualizacao("atualizacao");
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                // ver esta (a baixo):
                                case CONSULTA_PRES_UTILIZADOR -> {
                                    //tem de ser este:
                                    Msg_String aux = (Msg_String) message;
                                    //NAO ESTA BEM!!! NAO SAO NECESSARIOS FILTROS
                                /*
                                Msg_ConsultaComFiltros aux = (Msg_ConsultaComFiltros)message;
                                List<Evento> eventosAssistidos = DbManage.ConsultaPresencas_user(email, aux);
                                Evento[] res = new Evento[eventosAssistidos.size()];
                                for (int i = 0; i <eventosAssistidos.size() ; i++) {
                                    res[i]= eventosAssistidos.get(i);
                                }
                                //Mandarmos isso pela classe que recebe a lista de eventos
                                out.writeObject(new Msg_ListaEventos(Message_types.VALIDO, res));*/
                                    out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case CONSULTA_PRES_EVENT -> {
                                    Msg_String aux = (Msg_String)message;
                                    List<Utilizador> cenas = DbManage.Presencas_evento(aux.getConteudo());
                                    if(cenas != null) {
                                        Utilizador[] res = new Utilizador[cenas.size()];
                                        for (int i = 0; i <cenas.size() ; i++) {
                                            res[i]= cenas.get(i);
                                        }
                                        out.writeObject(new Msg_ListaRegistos(Message_types.VALIDO, res));
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case CSV_PRESENCAS_UTI_NUM_EVENTO -> {
                                    //NÃO DEVIAM ESTAR A FAZER ISTO, VÃO BUSCAR OS DADOS E CRIAR O CSV DESTE LADO
                                    File file = new File("Presencas.csv");// talvez o nome do ficheiro seja outro ,idk
                                    if(!file.exists()) {
                                        if(!file.createNewFile())
                                            out.writeObject(new Geral(Message_types.ERRO));
                                    }
                                    DbManage.PresencasCSV(eventosPresencasUser, file);// not sure se e esta a funcao
                                    // Aqui colocar a funcao que vamos chamar na db
                                    sendfile(out,file);
                                }
                                case CSV_PRESENCAS_DO_EVENTO -> {
                                    //NÃO DEVIAM ESTAR A FAZER ISTO, VÃO BUSCAR OS DADOS E CRIAR O CSV DESTE LADO
                                    File file=new File("minhasPresencas.csv");// talvez o nome do ficheiro seja outro ,idk
                                    if(!file.exists()) {
                                        if(!file.createNewFile())
                                            out.writeObject(new Geral(Message_types.ERRO));
                                    }

                                    //  DbManage.PresencasCSV(eventosPresencasUser,file);
                                    // Aqui colocar a funcao que vamos chamar na db
                                    sendfile(out,file);
                                    //PresencasCSV(eventosPresencasAdmin,"presencasUtilizadores.csv");
                                    //out.writeObject(new Geral(Message_types.VALIDO));
                                    // Na toerio
                                } //Serão necessários 2 ficheiros csv
                                case LOGOUT -> flagStop = true;
                                default -> out.writeObject(new Geral(Message_types.INVALIDO));
                            }
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
            nbytes = filereader.read(fileChunk);
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
