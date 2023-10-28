package pt.isec.pd.trabalhoPratico;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;



class threadCliente implements Runnable {
    Socket Client;
    boolean flagStop;

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


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}

public class ProgServidor {
    ServerSocket socket;

    public static void main(String[] args) {

    }

}
