package pt.isec.pd.trabalhoPratico;

import pt.isec.pd.trabalhoPratico.classesComunication.*;

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
            Geral o =(Geral) in.readObject();
            if(o.getTipo() == Message_types.LOGIN){// para descobrir qual a classe estava a pensar em algo para o processamento depois dos dados
                Login aux = (Login) o;
                String password=aux.getPassword();
                email=aux.getEmail();
                // aqui verifico se é o admin e ponho o boolean a true ou false
            } else if (o.getTipo() == Message_types.REGISTO) {// Aqui neste caso faltam fazer mais coisas como guardar na base de dados

                RegistoEdicao_Cliente aux=(RegistoEdicao_Cliente) o;
                email= aux.getEmail();
                // A implementar
            }else{
                out.writeObject("Comando Invalido para estabelecer conexão");// apenas como exemplo
            }

            if(!isadmin) {
                while (!flagStop) {
                    Geral message = (Geral) in.readObject();
                    switch (message.getTipo()) {
                        case EDITAR_REGISTO ->{
                            out.writeObject("Altera dados na database");
                            RegistoEdicao_Cliente aux=(RegistoEdicao_Cliente) message;
                        }
                        case SUBMICAO_COD -> out.writeObject("Insere dados na database");
                        case CSV_UTILIZADOR -> {}
                        case CONSULTA_PRES_UTILIZADOR -> {}
                        case REGISTO -> {}

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
                        case CONSULTA_PRES_EVENT -> {}
                        case CONSULT_EVENT_UTILIZADOR -> {}
                        case CSV_ADMINISTRADOR -> {}
                        default -> out.writeObject("Operacao invalida");
                    }
                }
            }
            Client.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}

public class ProgServidor {
    List<Socket> clients=new ArrayList<>();
    public void serviço() {

            try(ServerSocket socket=new ServerSocket()) {
                while (true){
                    Socket cli=socket.accept();// aceita clientes
                    clients.add(cli);// adiciona cliente conectado a lista de clientes
                    ThreadCliente th=new ThreadCliente(cli);
                    th.run();
                }
               // clients.clear();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                clients.clear();
            }


    }

}
