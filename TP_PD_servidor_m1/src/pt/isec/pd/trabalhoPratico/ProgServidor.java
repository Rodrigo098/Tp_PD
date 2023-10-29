package pt.isec.pd.trabalhoPratico;

import pt.isec.pd.trabalhoPratico.classescomunication.Geral;
import pt.isec.pd.trabalhoPratico.classescomunication.Login;
import pt.isec.pd.trabalhoPratico.classescomunication.Registo_Cliente;
import pt.isec.pd.trabalhoPratico.classescomunication.Submissao_codigo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ThreadCliente implements Runnable {
    Socket Client;
    boolean flagStop;// flag para terminar a thread
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
            if(o instanceof Login){// para descobrir qual a classe estava a pensar em algo para o processamento depois dos dados
                Login aux=(Login) o;
                String password=aux.getPassword();
                email=aux.getEmail();
            } else if (o instanceof Registo_Cliente) {// Aqui neste caso faltam fazer mais coisas como guardar na base de dados
                Registo_Cliente aux=(Registo_Cliente) o;
                email= aux.getEmail();
            }else{
                out.writeObject("Comando Invalido para estabelecer conexão");
            }

            while (!flagStop){
                    Geral message =(Geral) in.readObject();
                    if(message instanceof Login){// para descobrir qual a classe estava a pensar em algo para o processamento depois dos dados
                        Login aux=(Login) message;
                        String password=aux.getPassword();
                        email=aux.getEmail();
                    }
                    out.writeObject("ola");
                    out.flush();

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
