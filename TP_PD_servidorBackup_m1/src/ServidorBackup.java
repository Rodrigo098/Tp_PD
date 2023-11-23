import pt.isec.pd.trabalhoPratico.model.recordDados.DadosRmi;
import pt.isec.pd.trabalhoPratico.model.RemoteInterface;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class ServidorBackup {
    private String registration;
    private boolean conected;
    private RemoteInterface rmi;

    private static final int portobackup=4444;
    private final String Heartbeatip="230.44.44.44";
    private InetAddress group;


    public static void main(String[] args) {
        ServidorBackup servidorBackup = new ServidorBackup();
        Heartbeat heartbeatThread = servidorBackup.new Heartbeat();

        heartbeatThread.start();

        try {
            heartbeatThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void receiveDb() {
        try {
            String serviceURL = "rmi://" + "localhost"+ "/" + "servidor";
            RemoteInterface rmi = (RemoteInterface) Naming.lookup(serviceURL);

            byte[] copiaDb = rmi.getCopiaDb();
            salvarCopiaDb(copiaDb, "copiaDb.db");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void salvarCopiaDb(byte[] copiaDb, String nomeFicheiro) {
        try (FileOutputStream fos = new FileOutputStream(nomeFicheiro)) {
            fos.write(copiaDb);
            System.out.println("Cópia da base de dados salva localmente: " + nomeFicheiro);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Heartbeat extends Thread{// im not sure pq é que criei este thread  mas agr ta criadad
        @Override
        public void run() {
           try(MulticastSocket multicastSocket=new MulticastSocket(portobackup)){
               group=InetAddress.getByName(Heartbeatip);
                multicastSocket.joinGroup(group);



               DatagramPacket packet=new DatagramPacket(new byte[2024],2024);// aqui tenho de por um valor diferente i guess
               multicastSocket.receive(packet);
               ByteArrayInputStream bye=new ByteArrayInputStream(packet.getData(),0, packet.getLength());
               ObjectInputStream oin=new ObjectInputStream(bye);
               DadosRmi dados= (DadosRmi) oin.readObject();

               if(!conected){
                    registration="rmi://"+dados.Registo()+"/"+dados.nome_servico();
                    rmi= (RemoteInterface) Naming.lookup(registration);
                    conected=true;
                    System.out.println("Servidor de backup conectado ao servidor principal");

                   receiveDb(); //recebe a copia da base de dados do servidor principal

                   String backupIpAddress = InetAddress.getLocalHost().getHostAddress();
                   String backupServiceURL = "rmi://" + backupIpAddress + "/" + dados.nome_servico();
                   // Registra o servidor de backup para callbacks (rever isso ???)
                   rmi.registaBackupServers(backupServiceURL);

               }

           } catch (IOException e) {
               throw new RuntimeException(e);
           } catch (ClassNotFoundException e) {
               throw new RuntimeException(e);
           } catch (NotBoundException e) {
               throw new RuntimeException(e);
           }
        }
    }
}
