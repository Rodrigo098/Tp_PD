import pt.isec.pd.trabalhoPratico.dataAccess.DbManage;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.classesDados.Utilizador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ThreadCliente implements Runnable {
    Socket Client;
    boolean flagStop,isadmin;// flag para terminar a thread
    String email;


    public ThreadCliente(Socket client) {
        Client = client;
        flagStop=false;
    }



    @Override
    public void run() {
        try(ObjectOutputStream out=new ObjectOutputStream(Client.getOutputStream());
            ObjectInputStream in=new ObjectInputStream(Client.getInputStream())
        ) {
            //Quando conecta a primeira vez vai guardar o email



            Geral o=(Geral) in.readObject();
            if(o.getTipo() == Message_types.LOGIN){// para descobrir qual a classe estava a pensar em algo para o processamento depois dos dados
                Login aux = (Login) o;
                String password=aux.getPassword();
                email=aux.getEmail();
                System.out.println(email);
                if(DbManage.autentica_user(email,password))
                    out.writeObject(new Geral(Message_types.ADMINISTRADOR));
                else out.writeObject(new Geral(Message_types.INVALIDO));
                // aqui verifico se é o admin e ponho o boolean a true ou false
            } else if (o.getTipo() == Message_types.REGISTO) {// Aqui neste caso faltam fazer mais coisas como guardar na base de dados

                RegistoEdicao_Cliente aux=(RegistoEdicao_Cliente)o;
                email= aux.getEmail();
                Utilizador user=new Utilizador(aux.getNome(),aux.getEmail(),aux.getNum_estudante());
                DbManage.Registonovouser(user,aux.getPassword());
                // A implementar
            }else{
                out.writeObject("Comando Invalido para estabelecer conexão");// apenas como exemplo
            }

            if(!isadmin) {
                while (!flagStop) {
                    Geral message = (Geral) in.readObject();
                    switch (message.getTipo()) {
                        case EDITAR_REGISTO ->{
                            RegistoEdicao_Cliente aux=(RegistoEdicao_Cliente)message;
                            Utilizador user=new Utilizador(aux.getNome(), aux.getEmail(), aux.getNum_estudante());
                          if(  DbManage.edita_registo(user,aux.getPassword())){
                              out.writeObject(new Geral(Message_types.VALIDO));
                          }else
                              out.writeObject(new Geral(Message_types.ERRO));


                        }
                        case SUBMICAO_COD -> out.writeObject("Insere dados na database");
                        case CSV_UTILIZADOR -> {}
                        case CONSULTA_PRES_UTILIZADOR -> {}
                        case LOGOUT -> {break;}

                        default -> out.writeObject("Operacao invalida");
                    }
                }
            }else {
                while (!flagStop) {
                    Geral message = (Geral) in.readObject();
                    switch (message.getTipo()) {
                        case GERAR_COD -> out.writeObject("EXEMPLO");
                        case UPDATE_INF -> {}
                        case CRIA_EVENTO -> {}
                        case EDIT_EVENTO -> {}
                        case CONSULTA_EVENTOS -> {}
                        case ELIMINAR_EVENTO -> {}
                        case INSERE_PRES -> {}
                        case ELIMINA_PRES -> {}
                        case CONSULTA_PRES_EVENT -> {
                            Consulta_Elimina_GeraCod_SubmeteCod_Evento aux=(Consulta_Elimina_GeraCod_SubmeteCod_Evento)message;
                            String []res= DbManage.Presencas_evento(aux.getNome());
                            out.writeObject(new ConsultaEventos_EliminaPresencas_InserePresencas(aux.getNome(),Message_types.VALIDO,res));
                        }
                        case CONSULT_EVENT_UTILIZADOR -> {
                           Consulta_Elimina_GeraCod_SubmeteCod_Evento aux=(Consulta_Elimina_GeraCod_SubmeteCod_Evento)message;
                           String []res= DbManage.Presencas_user(aux.getNome());
                            out.writeObject(new ConsultaEventos_EliminaPresencas_InserePresencas(aux.getNome(),Message_types.VALIDO,res));
                        }
                        case CSV_ADMINISTRADOR -> {}
                        case LOGOUT ->{break;}
                        default -> out.writeObject("Operacao invalida");
                    }
                }
            }
            Client.close();



        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(Client.isClosed());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            System.out.println("Cliente "+ email +"terminado");
        }

    }
}

public class ProgServidor {
    List<Socket> clients=new ArrayList<>();
    public void servico() {

            try(ServerSocket socket=new ServerSocket(6001)) {
                while (true){
                    Socket cli=socket.accept();// aceita clientes
                    //cli.setSoTimeout(10000);
                    clients.add(cli);// adiciona cliente conectado a lista de clientes
                    ThreadCliente th=new ThreadCliente(cli);
                    th.run();
                    //new Thread(th).start();
                }
               // clients.clear();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                clients.clear();
            }


    }

}
