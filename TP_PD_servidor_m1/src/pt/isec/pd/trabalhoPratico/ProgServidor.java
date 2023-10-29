package pt.isec.pd.trabalhoPratico;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class threadCliente implements Runnable {
    Socket Client;
    boolean flagStop;// flag para terminar a thread

    public threadCliente(Socket client) {
        Client = client;
        flagStop=false;
    }


    @Override
    public void run() {
        try(ObjectOutputStream out=new ObjectOutputStream(Client.getOutputStream());
            ObjectInputStream in=new ObjectInputStream(Client.getInputStream())
        ) {
            while (!flagStop){
                    Object o =in.readObject();
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
                    threadCliente th=new threadCliente(cli);
                    th.run();
                }
               // clients.clear();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


    }

}
