import pt.isec.pd.trabalhoPratico.model.recordDados.DadosRmi;
import pt.isec.pd.trabalhoPratico.model.recordDados.RemoteInterface;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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


    public void receive(){

    }

    public static void main(String[] args) {

    }
    class Heartbeat extends Thread{
        @Override
        public void run() {
           try(MulticastSocket multicastSocket=new MulticastSocket(portobackup)){
               group=InetAddress.getByName(Heartbeatip);
                multicastSocket.joinGroup(group);
               DatagramPacket packet=new DatagramPacket(new byte[20],20);
               multicastSocket.receive(packet);
               ByteArrayInputStream bye=new ByteArrayInputStream(packet.getData(),0, packet.getLength());
               ObjectInputStream oin=new ObjectInputStream(bye);
               DadosRmi dados= (DadosRmi) oin.readObject();
               if(!conected){
                    registration="rmi://"+dados.Registo()+"/"+dados.nome_servico();
                    rmi= (RemoteInterface) Naming.lookup(registration);
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
