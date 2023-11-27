import pt.isec.pd.trabalhoPratico.model.ObservableInterface;
import pt.isec.pd.trabalhoPratico.model.dataAccess.DbManager;
import pt.isec.pd.trabalhoPratico.model.RemoteInterface;
import pt.isec.pd.trabalhoPratico.model.recordDados.DadosRmi;

import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;

public class ServidorBackup extends UnicastRemoteObject implements ObservableInterface {
    private static final String dbAdress = "Base de Dados/copiaDb.db";
    private static final String dbUrl= "jdbc:sqlite:"+dbAdress;
    private static String registration;
    private static boolean sair;
    private static boolean recebeuHeartBeat;
    private static RemoteInterface rmi;
    private static MulticastSocket multicastSocket;
    private static int versao;

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
            sair("<SERVIDOR BACKUP> Sintaxe: ServidorBackup <caminho_da_diretoria>");
        }

        diretoria = args[0];
        File destinoBDSB = new File(diretoria);

        if (!destinoBDSB.exists()) {
            sair("A directoria inserida [" + diretoria + "] não existe!");
        }

        if (!destinoBDSB.isDirectory()) {
            sair("O caminho [" + diretoria + "] não é uma diretoria!");
        }

        if (!destinoBDSB.canWrite()) {
            sair("Não tem permissoes para: " + destinoBDSB);
        }

        try {
            diretoria = destinoBDSB.getCanonicalPath() + File.separator + "copiaDb.db";
        } catch (IOException e) {
            sair("Excecao no acesso a ficheiro para armazenar BD!");
        }

        if(new File(diretoria).exists()){
            sair("<SERVIDOR BACKUP> Já existe uma base de dados guarda na diretoria.");
        }

        ThreadLeLinhaComandos linhaComandos = new ServidorBackup().new ThreadLeLinhaComandos();
        linhaComandos.start();

        try
        {   multicastSocket = new MulticastSocket(portobackup);
            multicastSocket.setSoTimeout(30000);
            group = InetAddress.getByName(Heartbeatip);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));// replace with your network interface
            multicastSocket.joinGroup(new InetSocketAddress(group,portobackup),networkInterface);
            DatagramPacket heartBeat;

            heartBeat = new DatagramPacket(new byte[2024],2024);
            multicastSocket.receive(heartBeat);

            try(ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(heartBeat.getData()))) {
                // PRIMEIRO HEATBEAT PARA REGISTO
                Object object = oin.readObject();
                if (object instanceof DadosRmi dados) {
                    registration = "rmi://" + dados.Registo() + "/" + dados.nome_servico();
                    rmi = (RemoteInterface) Naming.lookup(registration);
                    //conected = true;

                    System.out.println("Servidor de backup conectado ao servidor principal");

                    receiveDb(); //recebe a copia da base de dados do servidor principal

                    obs = new ServidorBackup();
                    rmi.addObservable(obs);
                }
            } catch (NotBoundException e) {
                throw new RuntimeException(e);
            }

            //CICLO HEARTBEAT
                do {
                    heartBeat = new DatagramPacket(new byte[2024], 2024);
                    multicastSocket.receive(heartBeat);
                    try(ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(heartBeat.getData())))
                    {
                        Object o = oin.readObject();
                        if (o instanceof DadosRmi dados) {
                            recebeuHeartBeat = true;
                            System.out.println("<SERVIDOR BACKUP> Recebeu HeartBeat");
                            // Compara a versão da base de dados recebida com a versão local
                            if (dados.versao() != dbManager.getVersaoDb()) {
                                System.out.println("<INFO> Dados " + dados.versao() + " Manager:" + dbManager.getVersaoDb());
                                System.out.println("<SERVIDOR BACKUP> Versao da base de dados diferente.");
                                sair = true;

                            }
                        }
                    }
                } while (!sair);
        } catch (SocketTimeoutException e) {
            System.out.println("<SERVIDOR BACKUP> Nao foi detetado nenhum HearBeat nos ultimos 30 segundos.");
            sair = true;
        } catch (IOException e) {
            System.out.println("\n<SERVIDOR BACKUP> Erro ao criar socket multicast: " + e.getMessage());
        } catch (ClassNotFoundException e) {//NotBoundException |
            System.out.println("<SERVIDOR BACKUP> Info: " + e.getMessage());
        }
        System.out.println("<SERVIDOR BACKUP> A encerrar ...");
        sair = true;
        try {
            rmi.RemoveObservable(obs);
            multicastSocket.leaveGroup(InetAddress.getByName(Heartbeatip));
            multicastSocket.close();
            linhaComandos.join();
        } catch (InterruptedException | RuntimeException | IOException e) {
            System.out.println("<SERVIDOR BACKUP>");;
        }
        System.out.println("\n-----------------------------------------------");
        System.out.println("<SERVIDOR BACKUP> A encerrar o servidor backup...");
        System.exit(0);
    }

    public static void receiveDb() {
        try {
           byte[] copiaDb = rmi.getCopiaDb();
            salvarCopiaDb(copiaDb);
        } catch (RemoteException e) {
          e.printStackTrace();
        }

    }

    private static void salvarCopiaDb(byte[] copiaDb) {
        try (FileOutputStream fos = new FileOutputStream(diretoria)) {
            fos.write(copiaDb);
            System.out.println("<SERVIDOR BACKUP> Copia da base de dados salva localmente: " + diretoria);
            versao = DbManager.getVersaoDb();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setVersao(int versao) {
        try (Connection connection=DriverManager.getConnection(dbUrl)){
            String UpdateVersao="UPDATE Versao SET versao_id=? where versao_id=?;";
            PreparedStatement statement=connection.prepareStatement(UpdateVersao);
            statement.setInt(1,ServidorBackup.versao);
            statement.setInt(2,ServidorBackup.versao-1);
            if( statement.executeUpdate()<1)
                System.out.println("Erro a atualizar a versao");
            else{

                System.out.println("Versao atualizada com sucesso");}
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public boolean submitcod(int codigo, String nome_evento, String emailuser) throws RemoteException {
        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement())
        {
            System.out.println("Recebeu");
            String GetQuery = "SELECT * FROM Codigo_Registo where nome_evento=? AND validade>?;";
            PreparedStatement getquery=connection.prepareStatement(GetQuery);
            getquery.setString(1,nome_evento);
            getquery.setLong(2,0);
            ResultSet rs=getquery.executeQuery();

            if(rs.isBeforeFirst())
            {   rs.next();
                java.util.Date Data=new Date();
                long datamili=Data.getTime();
                if(rs.getTimestamp("validade").getTime()<datamili){
                    System.out.println("Fora de validade");
                    String EliminaCodigosAnterioresQuery = "UPDATE Codigo_Registo SET validade=0 WHERE nome_evento = ?";//
                    PreparedStatement expiraStatement = connection.prepareStatement(EliminaCodigosAnterioresQuery);
                    expiraStatement.setString(1, nome_evento); // Define o valor do nome_evento para o ? da query
                    expiraStatement.executeUpdate();// se existirem codigos antigos são eliminados se nao existirem nao acontece nada
                    return false;
                }
                    String createEntryQuery = "INSERT INTO Assiste (nome_evento,email) VALUES ('"
                            + nome_evento+"','" +emailuser+"')";// qual o valor que é suposto colocar no idassiste??

               statement.executeUpdate(createEntryQuery);
               connection.close();
               setVersao(versao++);
            }
        } catch (SQLException e) {

            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }

    @Override
    public boolean InserePresencas(String nomeEvento, String[] emails) throws RemoteException {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            for (String emailEstudante : emails) {
                    String inserePresencaQuery = "INSERT INTO assiste (nome_evento, email) VALUES (?, ?);";
                    PreparedStatement presencaStatement = connection.prepareStatement(inserePresencaQuery);
                    presencaStatement.setString(1, nomeEvento);
                    presencaStatement.setString(2, emailEstudante);
                    presencaStatement.executeUpdate();
            }
            connection.close();
            setVersao(versao++);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean EliminaPresencas(String nomeEvento, String[] emails) throws RemoteException {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {

            for (String emailEstudante : emails) {
                String eliminaPresencaQuery = "DELETE FROM assiste WHERE nome_evento = ? AND email = ?;";
                PreparedStatement eliminaPresencaStatement = connection.prepareStatement(eliminaPresencaQuery);
                eliminaPresencaStatement.setString(1, nomeEvento);
                eliminaPresencaStatement.setString(2, emailEstudante);
                eliminaPresencaStatement.executeUpdate();
            }
            connection.close();
            setVersao(versao++);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void executaUpdate(String query) throws RemoteException {
        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement= connection.createStatement();
        ) {
            System.out.println("Chega ca");
            statement.executeUpdate(query);
            connection.close();
            setVersao(versao++);
        } catch (SQLException e) {
            System.out.println("<SERVIDOR BACKUP> Excepcao ao executar um update da base de dados [" + e + "]");
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
            sair = true;
            multicastSocket.close();
        }
    }

    public static void sair(String msg) {
        System.out.println(msg);
        System.exit(1);
    }
}
/*

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

    public boolean RegistoNovoUser(Utilizador user, String password) throws RemoteException {
        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement())
        {String createEntryQuery = "INSERT INTO Utilizador (email,nome,numero_estudante,palavra_passe,tipo_utilizador) VALUES ('"
                    + user.email() + "','" + user.nome() + "','" + user.numIdentificacao() + "','" + password +"','" + "cliente" +"')";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??
           connection.close();
            setVersao(versao++);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean edita_registo(Utilizador user, String pasword) throws RemoteException {
        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement()){
                String updateQuery = "UPDATE Utilizador SET nome=?, numero_estudante=?, palavra_passe=? WHERE email=?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, user.nome());
                preparedStatement.setInt(2, user.numIdentificacao());
                preparedStatement.setString(3, pasword);
                preparedStatement.setString(4, user.email());
                preparedStatement.executeUpdate();
                connection.close();
                setVersao(versao++);
                return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean Cria_evento(Msg_Cria_Evento evento) throws RemoteException {
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dataString = dateFormat.format(evento.getData()); //Alterei isso para termos a data nesse formato, facilita os testes

            String createEntryQuery = "INSERT INTO Evento (nome_evento,local,data_realizacao,hora_inicio,hora_fim) VALUES ('"
                    + evento.getNome() +"','" + evento.getLocal() +"','" + dataString +"','" + evento.getHoreInicio() +"','" + evento.getHoraFim() +"')";
          statement.executeUpdate(createEntryQuery);
          connection.close();
          setVersao(versao++);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean Edita_evento(Msg_Edita_Evento evento) throws RemoteException {
        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dataString = dateFormat.format(evento.getData()); //Alterei isso para termos a data nesse formato, facilita os testes mas sempre se pode alterar
            String updateEventQuery = "UPDATE Evento SET data_realizacao = '" + dataString + "', hora_inicio = '" + evento.getHoreInicio() + "', hora_fim = '" + evento.getHoraFim() + "', nome_evento = '" + evento.getNome() + "', local = '" + evento.getLocal()+ "' WHERE nome_evento = '" + evento.getNome()
                    + "'";
          statement.executeUpdate(updateEventQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    public boolean Elimina_evento(String nome_evento) throws RemoteException {
        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()){
        String deleteEventQuery = "DELETE FROM Evento WHERE nome_evento = '" + nome_evento + "'";
        statement.executeUpdate(deleteEventQuery);
        connection.close();
        return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

 */