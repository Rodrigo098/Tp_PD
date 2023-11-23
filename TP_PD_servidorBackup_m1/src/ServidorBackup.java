import pt.isec.pd.trabalhoPratico.model.ObservableInterface;
import pt.isec.pd.trabalhoPratico.model.dataAccess.DbManager;
import pt.isec.pd.trabalhoPratico.model.recordDados.DadosRmi;
import pt.isec.pd.trabalhoPratico.model.RemoteInterface;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

public class ServidorBackup extends UnicastRemoteObject implements ObservableInterface {
    private String registration;
    private boolean conected;
    private RemoteInterface rmi;

    private static final int portobackup=4444;
    private final String Heartbeatip="230.44.44.44";
    private InetAddress group;
    private Timer timeoutTimer = new Timer();
    private static String diretoria;
    private static ObservableInterface obs;

    private DbManager dbManager = new DbManager();

    protected ServidorBackup() throws RemoteException {
    }


    public static void main(String[] args)  {


        // Verifica se o número de argumentos é válido
        if (args.length != 1) {
            System.err.println("Sintaxe: ServidorBackup <caminho_da_diretoria>");
            System.exit(1);
        }

        diretoria = args[0];

        // Verifica se o caminho existe
        File caminho = new File(diretoria);

        if (!caminho.exists() || !caminho.isDirectory()) {
            System.err.println("[Erro] O caminho especificado nao corresponde a uma diretoria valida");
            System.exit(1);
        }
/*
        // Verifica se a diretoria está vazia
        if (caminho.list().length > 0) {
            System.err.println("A diretoria nao esta vazia. A encerrar o servidor backup...");
            System.exit(1);
        }
*/
        try {
            ServidorBackup servidorBackup = new ServidorBackup();
            Heartbeat heartbeatThread = servidorBackup.new Heartbeat();
            heartbeatThread.start();
            heartbeatThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    public void receiveDb() {
        try {
            byte[] copiaDb = rmi.getCopiaDb();

            salvarCopiaDb(copiaDb, diretoria +File.separator+ "copiaDb.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void salvarCopiaDb(byte[] copiaDb, String nomeFicheiro) {
        try (FileOutputStream fos = new FileOutputStream(nomeFicheiro)) {
            fos.write(copiaDb);
            System.out.println("Copia da base de dados salva localmente: " + nomeFicheiro);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void avisaObservables() {
        System.out.println("Recebeu notificacao");
    }


    class TimeoutTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("[Timeout] Nenhum heartbeat recebido. A encerrar o servidor backup...");
            System.exit(0); //por enquanto ainda não implementei nada melhor para sair
        }
    }
    class Heartbeat extends Thread{// im not sure pq é que criei este thread  mas agr ta criadad

        @Override
        public void run() {
           try(MulticastSocket multicastSocket=new MulticastSocket(portobackup)){
               group=InetAddress.getByName(Heartbeatip);
               multicastSocket.joinGroup(group);

               DatagramPacket packet=new DatagramPacket(new byte[2024],2024);// aqui tenho de por um valor diferente i guess

               TimeoutTask timeoutTask = new TimeoutTask();

               while (true) {
                   multicastSocket.receive(packet);
                   ByteArrayInputStream bye = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                   ObjectInputStream oin = new ObjectInputStream(bye);
                   DadosRmi dados = (DadosRmi) oin.readObject();


                   if (!conected)
                   {
                       registration = "rmi://" +"localhost"+ "/" + dados.nome_servico();
                       rmi = (RemoteInterface) Naming.lookup(registration);
                       conected = true;

                       System.out.println("Servidor de backup conectado ao servidor principal");


                       receiveDb(); //recebe a copia da base de dados do servidor principal

                      obs=new ServidorBackup();
                       rmi.addObservable(obs);

                   }
                   // Compara a versão da base de dados recebida com a versão local
                   if (dados.versao() != dbManager.getVersaoDb()) {
                       System.out.println("Versao da base de dados diferente. A encerrar o servidor backup...");
                       System.exit(0);
                   }
                   // Reinicia o timer após receber um heartbeat

                   timeoutTimer.cancel();
                   timeoutTimer = new Timer();
                   timeoutTask=new TimeoutTask();
                   timeoutTimer.schedule(timeoutTask, 30000);

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
