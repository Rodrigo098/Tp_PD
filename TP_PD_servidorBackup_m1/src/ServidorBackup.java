import pt.isec.pd.trabalhoPratico.model.ObservableInterface;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.dataAccess.DbManager;
import pt.isec.pd.trabalhoPratico.model.recordDados.DadosRmi;
import pt.isec.pd.trabalhoPratico.model.RemoteInterface;
import pt.isec.pd.trabalhoPratico.model.recordDados.Utilizador;

import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class ServidorBackup extends UnicastRemoteObject implements ObservableInterface {
    private static String registration;
    private static boolean sair;
    private static boolean recebeuHeartBeat;
    private static RemoteInterface rmi;
    private static MulticastSocket multicastSocket;

    private static final int portobackup = 4444;
    private static final String Heartbeatip = "230.44.44.44";
    private static InetAddress group;
    private Timer timeoutTimer = new Timer();
    private static String diretoria;
    private static ObservableInterface obs;

    private static final DbManager dbManager = new DbManager();

    protected ServidorBackup() throws RemoteException {
    }

    public static void main(String[] args) throws RemoteException {

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
        ThreadLeLinhaComandos linhaComandos = new ServidorBackup().new ThreadLeLinhaComandos();
        linhaComandos.start();

        try(MulticastSocket mSocket = new MulticastSocket(portobackup)){
            multicastSocket = mSocket;
            multicastSocket.setSoTimeout(30000);
            group = InetAddress.getByName(Heartbeatip);
            multicastSocket.joinGroup(group);

            DatagramPacket packet = new DatagramPacket(new byte[2024],2024);// aqui tenho de por um valor diferente i guess

            multicastSocket.receive(packet);
            ByteArrayInputStream bye = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
            ObjectInputStream oin = new ObjectInputStream(bye);
            DadosRmi dados = (DadosRmi) oin.readObject();

            registration = "rmi://" + dados.Registo()+ "/" + dados.nome_servico();
            rmi = (RemoteInterface) Naming.lookup(registration);
            //conected = true;

            System.out.println("Servidor de backup conectado ao servidor principal");

            receiveDb(); //recebe a copia da base de dados do servidor principal

            obs = new ServidorBackup();
            rmi.addObservable(obs);

            while (!sair) {
                recebeuHeartBeat = false;
                DatagramPacket heartBeat = new DatagramPacket(new byte[2024],2024);// aqui tenho de por um valor diferente i guess
                multicastSocket.receive(heartBeat);
                bye = new ByteArrayInputStream(heartBeat.getData(), 0, heartBeat.getLength());
                oin = new ObjectInputStream(bye);
                dados = (DadosRmi) oin.readObject();

                // Compara a versão da base de dados recebida com a versão local
                if (dados.versao() != dbManager.getVersaoDb()) {
                    System.out.println("Dados " + dados.versao() + " Manager:" + dbManager.getVersaoDb());
                    System.out.println("Versao da base de dados diferente. A encerrar o servidor backup...");
                    sair = true;
                }
            }
        } catch (SocketTimeoutException e) {
            System.out.println("<SERVIDOR BACKUP> Nao foi detetado nenhum HearBeat nos ultimos 30 segundos.");
            sair = true;
        } catch (IOException e) {
            System.out.println("\n<SERVIDOR BACKUP> Erro ao criar socket multicast: " + e.getMessage());
        } catch (NotBoundException | ClassNotFoundException e) {
            System.out.println("<SERVIDOR BACKUP> Info: " + e.getMessage());
        }
        try {
            linhaComandos.join();
            multicastSocket.leaveGroup(InetAddress.getByName(Heartbeatip));
            multicastSocket.close();
        } catch (IOException e) {
            System.out.println("\n<SERVIDOR BACKUP> Info: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("\n<SERVIDOR BACKUP> ---");
        }
        System.out.println("\n##################################################");
        System.out.println("<SERVIDOR BACKUP> A encerrar o servidor backup...");
        System.exit(0);
    }

    public static void receiveDb() {
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
    public void avisaObservables(Geral Msg, int versao) {
        System.out.println("Recebeu notificacao");
        DbManager.setVersao(versao);
        System.out.println(versao);
        switch (Msg.getTipo()){
            case REGISTO ->{
                Mgs_RegistarEditar_Conta mg=(Mgs_RegistarEditar_Conta) Msg;
                Utilizador aux=new Utilizador(mg.getNome(),mg.getEmail(), mg.getNum_estudante());
                DbManager.RegistoNovoUser(aux,mg.getPassword());
            }
            case EDITAR_REGISTO -> {
                Mgs_RegistarEditar_Conta mg=(Mgs_RegistarEditar_Conta) Msg;
                Utilizador aux=new Utilizador(mg.getNome(),mg.getEmail(), mg.getNum_estudante());
                DbManager.edita_registo(aux,mg.getPassword());
            }
            case SUBMICAO_COD -> {
                Msg_Sub_Cod mg=(Msg_Sub_Cod) Msg;
                DbManager.submitcod(mg.getNumero(),mg.getConteudo(),mg.getEmail());
            }
            case CRIA_EVENTO -> {
                Msg_Cria_Evento mg=(Msg_Cria_Evento) Msg;
                DbManager.Cria_evento(mg);
            }
            case EDIT_EVENTO -> {
                Msg_Edita_Evento mg=(Msg_Edita_Evento) Msg;
                DbManager.Edita_evento(mg);
            }
            case ELIMINAR_EVENTO -> {
                Msg_String mg=(Msg_String) Msg;
                DbManager.Elimina_evento(mg.getConteudo());
            }
            case GERAR_COD ->
                System.out.println("Por fazer");

            case INSERE_PRES -> {
                Msg_EliminaInsere_Presencas mg=(Msg_EliminaInsere_Presencas) Msg;
                DbManager.InserePresencas(mg.getNome_evento(),mg.getLista());
            }
            case ELIMINA_PRES -> {
                Msg_EliminaInsere_Presencas mg=(Msg_EliminaInsere_Presencas) Msg;
                DbManager.EliminaPresencas(mg.getNome_evento(),mg.getLista());
            }

        }
    }
    class ThreadLeLinhaComandos extends Thread {
        @Override
        public void run(){
            String inserido;
            do{
                Scanner linhaComandos = new Scanner(System.in);
                System.out.println("<SERVIDOR BACKUP> Escreva \"sair\" para terminar.");
                inserido = linhaComandos.nextLine();
            }while (!sair && !inserido.equals("sair"));
                //if(inserido.equals("atua"))
                //  envioDeAvisoDeAtualizacao("atualizacao");
            sair = true;
            multicastSocket.close();
        }
    }
/*
    class Heartbeat extends Thread{// im not sure pq é que criei este thread  mas agr ta criadad

        @Override
        public void run() {
            try(MulticastSocket mSocket = new MulticastSocket(portobackup)){
                multicastSocket = mSocket;
                multicastSocket.setSoTimeout(30000);
                group = InetAddress.getByName(Heartbeatip);
                multicastSocket.joinGroup(group);

                DatagramPacket packet = new DatagramPacket(new byte[2024],2024);// aqui tenho de por um valor diferente i guess

                multicastSocket.receive(packet);
                ByteArrayInputStream bye = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                ObjectInputStream oin = new ObjectInputStream(bye);
                DadosRmi dados = (DadosRmi) oin.readObject();

                registration = "rmi://" + dados.Registo()+ "/" + dados.nome_servico();
                rmi = (RemoteInterface) Naming.lookup(registration);
                //conected = true;

                System.out.println("Servidor de backup conectado ao servidor principal");

                receiveDb(); //recebe a copia da base de dados do servidor principal

                obs = new ServidorBackup();
                rmi.addObservable(obs);

                while (!sair) {
                    recebeuHeartBeat = false;
                    DatagramPacket heartBeat = new DatagramPacket(new byte[2024],2024);// aqui tenho de por um valor diferente i guess
                    multicastSocket.receive(heartBeat);
                    bye = new ByteArrayInputStream(heartBeat.getData(), 0, heartBeat.getLength());
                    oin = new ObjectInputStream(bye);
                    dados = (DadosRmi) oin.readObject();

                    // Compara a versão da base de dados recebida com a versão local
                    if (dados.versao() != dbManager.getVersaoDb()) {
                        System.out.println("Dados " + dados.versao() + " Manager:" + dbManager.getVersaoDb());
                        System.out.println("Versao da base de dados diferente. A encerrar o servidor backup...");
                        sair = true;
                    }
                }
            } catch (SocketTimeoutException e) {
                System.out.println("<SERVIDOR BACKUP> Nao foi detetado nenhum HearBeat nos ultimos 30 segundos.");
                System.out.println("<SERVIDOR BACKUP> Prime [enter] para terminar.");
                sair = true;
            } catch (IOException e) {
                System.out.println("\n<SERVIDOR BACKUP> Erro ao criar socket multicast: " + e.getMessage());
            } catch (NotBoundException | ClassNotFoundException e) {
                System.out.println("<SERVIDOR BACKUP> Info: " + e.getMessage());
            }
            try {
                multicastSocket.leaveGroup(InetAddress.getByName(Heartbeatip));
                multicastSocket.close();
            } catch (IOException e) {
                System.out.println("\n<SERVIDOR BACKUP> Info: " + e.getMessage());
            }
        }
    }*/
}