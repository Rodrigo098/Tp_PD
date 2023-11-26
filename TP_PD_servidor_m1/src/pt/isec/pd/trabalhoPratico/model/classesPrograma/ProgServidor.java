package pt.isec.pd.trabalhoPratico.model.classesPrograma;

import pt.isec.pd.trabalhoPratico.model.ObservableInterface;
import pt.isec.pd.trabalhoPratico.dataAccess.BDResposta;
import pt.isec.pd.trabalhoPratico.dataAccess.DbManage;
import pt.isec.pd.trabalhoPratico.model.RemoteInterface;
import pt.isec.pd.trabalhoPratico.model.Utils.Utils;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.recordDados.*;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;


public class ProgServidor implements RemoteInterface {
    // BACKUPS
    public static final int MAX_SIZE = 4000;
    private static final int portobackup = 4444;
    private final String ipMuticastString = "224.0.1.0", Heartbeatip="230.44.44.44", service_name;
    private MulticastSocket multicastSocketBackup;
    private final Timer temporizador;
    private final HeartBeatTask heartBeatTask;
    private List<ObservableInterface> observers;//ArrayList<ObservableInterface> observers;
    private DbManage dbManager;
    private InetAddress heartbeatgroup;
    private Geral Lastupdate;
    private int timerCount = 0;
    
    // CLIENTES
    private final int portoClientes;
    private ServerSocket socketServidor;
    private Boolean pararServidor = false;
    private final GereRecursosClientes gereRecursosClientes;
    private final List<ThreadCliente> threadsClientes;
    private NetworkInterface networkInterface;

    private static HeartBeatTask teste;


    public ProgServidor(int portoClientes, String service_name, String bdLocalDirectory) {
        this.portoClientes = portoClientes;
        this.service_name = service_name;

        gereRecursosClientes = new GereRecursosClientes();
        threadsClientes = new ArrayList<>();

        temporizador = new Timer();
        heartBeatTask = new HeartBeatTask();
        observers = new ArrayList<>();

        dbManager = new DbManage(bdLocalDirectory);
        dbManager.addVersaoListener(event -> envioDeAvisoDeAtualizacao("atualizacao"));
    }

    //////////////////////////////////// MULTICAST ////////////////////////////
    public String setMulticastSocketBackup() {
        try {
            heartbeatgroup = InetAddress.getByName(Heartbeatip);
            multicastSocketBackup = new MulticastSocket(portobackup);

            networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));// replace with your network interface
            System.out.println(networkInterface);
            multicastSocketBackup.setNetworkInterface(networkInterface);
            multicastSocketBackup.joinGroup(new InetSocketAddress(heartbeatgroup, portobackup), networkInterface);

            temporizador.schedule(teste, 0, 1000);
        } catch (SocketException e) {
            return "<SERVIDOR> Excecao na criacao do socket para heartBeat.";
        } catch (UnknownHostException e) {
            return "<SERVIDOR> Excecao ao obter o host necessario hearBeat.";
        } catch (IOException e) {
            return "<SERVIDOR> Excecao IO na criacao do socket para hearBeat.";
        }
        return null;
    }
    //////////////////////////////////// SERVIDOR /////////////////////////////
    public void servidorMainFunction() {
        try
        {
            socketServidor = new ServerSocket(portoClientes);

            // ACEITA CLIENTES
            while(!pararServidor){
                try {
                    Socket cli = socketServidor.accept();
                    BufferedReader inTipo = new BufferedReader(new InputStreamReader(cli.getInputStream()));
                    String tipoSocket = inTipo.readLine();

                    if (tipoSocket.contains("socketAtualizacao")) {
                        gereRecursosClientes.setClienteDadosAtualizacao(tipoSocket.split(" ")[1], cli, new PrintStream(cli.getOutputStream()), tipoSocket.split(" ")[2]);
                        System.out.println("\n<SERVIDOR> Socket do cliente [" + tipoSocket.split(" ")[2] + "] para atualização conectado.");
                    } else if (tipoSocket.contains("socketPedidos")) {
                        ThreadCliente novaTh = new ThreadCliente(gereRecursosClientes.novaLigacao(tipoSocket.split(" ")[1], cli));
                        novaTh.start();
                        threadsClientes.add(novaTh);
                    }
                } catch (IOException e) {
                    System.out.println("\n<SERVIDOR> já não se aceitam mais clientes.");
                }
            }
        } catch (IOException e) {
            System.out.println("\n<SERVIDOR> Excecao a criar socket para comunicacao com clientes.");
        }
        System.out.println("\n<SERVIDOR> A terminar threads para atendimento de clientes.");
        if(!threadsClientes.isEmpty()){
            for(ThreadCliente t : threadsClientes){
                String nomeCli = t.getCliente();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    System.out.println("\n<SERVIDOR> Excecao ao esperar que a thread do cliente [" + nomeCli + "] terminasse.");
                }
            }
        }
    }

    ////////////////////////////// ATUALIZAÇÃO ASSÍNCRONA /////////////////////////////
    public synchronized void envioDeAvisoDeAtualizacao(String msgAtaulizacao) {
        observers.forEach(observableInterface -> {
            try {
                observableInterface.setVersao(dbManager.getVersao());
                observableInterface.executaUpdate(Lastupdate);
            } catch (RemoteException e) {
                System.out.println(e.getMessage());
            }
        });
        /*switch(queryAFazer) {
            case REGISTO -> {

                synchronized (observers) {
                    observers.forEach(observableInterface -> {
                        try {
                            observableInterface.RegistoNovoUser(user, aux.getPassword());
                        } catch (RemoteException e) {
                            System.out.println("<SERVIDOR> Excecao: " + e.getMessage());
                        }
                    });
                }
            }
            case INSERE_PRES -> {
                observers.forEach(observableInterface -> {
                    try {
                        observableInterface.InserePresencas(aux.getNome_evento(), aux.getLista());
                    } catch (RemoteException e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
            case ELIMINA_PRES -> {
                observers.forEach(observableInterface -> {
                    try {
                        observableInterface.EliminaPresencas(aux.getNome_evento(), aux.getLista());
                    } catch (RemoteException e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
            case ELIMINAR_EVENTO -> {
                observers.forEach(observableInterface -> {
                    try {
                        observableInterface.Elimina_evento(aux.getConteudo());
                    } catch (RemoteException e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
            case EDIT_EVENTO -> {
                observers.forEach(observableInterface -> {
                    try {
                        observableInterface.Edita_evento(evento);
                    } catch (RemoteException e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
            case CRIA_EVENTO -> {
                observers.forEach(observableInterface -> {
                    try {
                        observableInterface.Cria_evento(evento);
                    } catch (RemoteException e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
            case EDITAR_REGISTO -> {
                observers.forEach(observableInterface -> {
                    try {
                        observableInterface.edita_registo(user, aux.getPassword());
                    } catch (RemoteException e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
            case SUBMICAO_COD -> {
                observers.forEach(observableInterface -> {
                    try {
                        observableInterface.submitcod(aux.getNumero(), aux.getConteudo(), email);
                    } catch (RemoteException e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
        }*/
        System.out.println("\n-------------------- ATUALIZACAO ----------------------");
        //aviso de atualizacao aos backups
        System.out.println("<SERVIDOR> HeartBeat de atualizacao enviado aos servidores backup");
        enviatodosobv(Lastupdate);
        enviaHeartBeat();

        //aviso de atualizacao aos clientes
        System.out.println("<SERVIDOR> Aviso de atualizacao enviado a:");
        gereRecursosClientes.enviaAvisoAtualizacao(msgAtaulizacao);
        System.out.println("\n--------------------------------------------------------");
    }

    ////////////////////////////////// OBSERVABLE /////////////////////////////
    @Override
    public byte[] getCopiaDb() throws RemoteException {

        try {
            //Para evitar que sejam feitas alterações enquanto a cópia é feita
            synchronized (this){
                return Files.readAllBytes(Paths.get(DbManage.getDbAdress()));
            }
        } catch (Exception e) {
            throw new RemoteException("Erro ao obter cópia da base de dados", e);
        }

    }

    @Override
    public void registaBackupServers(String backupServerURL) throws RemoteException {

    }

    @Override
    public void addObservable(ObservableInterface obv) throws RemoteException {
        synchronized (observers){
      if(!observers.contains(obv)){
          observers.add(obv);
          System.out.println("\n<SERVIDOR> Observable adicionado");
      }
        }

    }

    @Override
    public  void RemoveObservable(ObservableInterface obv) throws RemoteException {
        synchronized (observers){
        observers.remove(obv);
            System.out.println("\n<SERVIDOR> Observable Removido");
        }
    }

    ////////////////////////////////// HEART BEAT /////////////////////////////
    private synchronized void enviaHeartBeat() {
        DatagramPacket heartBeat;

            try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(bout))
            {
                oout.writeObject(new DadosRmi(InetAddress.getLocalHost().getHostAddress(), service_name, dbManager.getVersao()));
                oout.flush();
                heartBeat = new DatagramPacket(bout.toByteArray(), bout.size(), heartbeatgroup, portobackup);
                System.out.println( multicastSocketBackup.getNetworkInterface());
                multicastSocketBackup.send(heartBeat);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

    }


    class HeartBeatTask extends TimerTask {
        @Override
        public void run() {
            timerCount++;
            if(timerCount == 10 && !pararServidor) {
                timerCount = 0;
                enviaHeartBeat();
                System.out.println("[<SERVIDOR> Heartbeat enviado]");
            }
        }
    }

    ////////////////////////////////// THREAD LINHA DE COMANDOS /////////////////////////////
    class ThreadLeLinhaComandos extends Thread {
        @Override
        public void run(){
            String inserido;
            do{
                Scanner linhaComandos = new Scanner(System.in);
                System.out.println("\n<SERVIDOR> Escreva \"sair\" para terminar o servidor");
                inserido = linhaComandos.nextLine();
                if(inserido.equals("atua"))
                  envioDeAvisoDeAtualizacao("atualizacao");
            }while (!inserido.equals("sair"));
            pararServidor = true;
            temporizador.cancel();
            heartBeatTask.cancel();
            envioDeAvisoDeAtualizacao("fimServidor");
            try {
                socketServidor.close();
                multicastSocketBackup.close();
                gereRecursosClientes.terminarLigacoes();
            } catch (IOException e) {
                throw new RuntimeException("\n<SERVIDOR> Erro a fechar sockets");
            }
        }
    }

    ////////////////////////////////// THREAD CLIENTE /////////////////////////////
    class ThreadCliente extends Thread {
        boolean isadmin, logado, stopthreadCliente;
        String idCliente, email;

        List <Evento> eventosPresencasUser = new ArrayList<>();
        List <Evento> eventosPresencasAdmin = new ArrayList<>();
        List <Utilizador> utilizadoresEvento = new ArrayList<>();

        public ThreadCliente(String idCliente) {
            this.idCliente = idCliente;
            isadmin = false;
            stopthreadCliente = false;
        }
        public String getCliente() {
            return email;
        }
        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(gereRecursosClientes.getClienteSocketPedidos(idCliente).getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(gereRecursosClientes.getClienteSocketPedidos(idCliente).getInputStream())
                ) {
                    // ciclo 1 - enquanto a app do cliente estiver ligada
                    while (!pararServidor && !stopthreadCliente) {

                        // primeira interação com o cliente
                        Object interacao = in.readObject();

                        if (interacao instanceof Geral loginRegisto) {
                            switch (loginRegisto.getTipo()) {
                                case LOGIN -> {
                                    Msg_Login aux = (Msg_Login) interacao;
                                    BDResposta resposta = dbManager.autentica_user(aux.getEmail(), aux.getPassword());

                                    if (!resposta.resultado()) {
                                        out.writeObject(new Geral(resposta.mensagem().contains("Password") ? Message_types.WRONG_PASS : resposta.conteudo() ? Message_types.INVALIDO : Message_types.ERRO));
                                    } else {
                                        email = aux.getEmail();
                                        logado = true;
                                        isadmin = resposta.conteudo();

                                        out.writeObject(new Geral(resposta.conteudo() ? Message_types.ADMINISTRADOR : Message_types.UTILIZADOR));
                                    }
                                    System.out.println("\n<SERVIDOR> [OPERACAO DE LOGIN] -> " + resposta.mensagem());
                                }
                                case REGISTO -> {
                                    Mgs_RegistarEditar_Conta aux = (Mgs_RegistarEditar_Conta) interacao;
                                    Utilizador user = new Utilizador(aux.getNome(), aux.getEmail(), aux.getNum_estudante());
                                    Lastupdate = aux;
                                    BDResposta resposta = dbManager.RegistoNovoUser(user, aux.getPassword());
                                    if (resposta.resultado()) {
                                        email = aux.getEmail();
                                        logado = true;
                                        isadmin = resposta.conteudo();
                                        out.writeObject(new Geral(Message_types.UTILIZADOR));
                                    } else {
                                        out.writeObject(new Geral(resposta.conteudo() ? Message_types.INVALIDO : Message_types.ERRO));
                                    }

                                    System.out.println("\n<SERVIDOR> [OPERACAO DE REGISTO] -> " + resposta.mensagem());
                                }
                                case FECHOU_APP -> {
                                    System.out.println("\n<SERVIDOR> [CLIENTE SAIU DA APP] -> " + email );
                                    stopthreadCliente = true;
                                    logado = false;
                                }
                                default -> out.writeObject(new Geral(Message_types.FAZER_LOGIN));
                            }
                            //Pedidos para clientes do tipo UTILIZADOR
                            if (logado) {
                                if (!isadmin) {
                                    while (logado) {
                                        Object message = in.readObject();
                                        if (message instanceof Geral geral) {
                                            switch (geral.getTipo()) {
                                                case EDITAR_REGISTO -> {
                                                    Mgs_RegistarEditar_Conta aux = (Mgs_RegistarEditar_Conta) message;
                                                    Utilizador user = new Utilizador(aux.getNome(), email, aux.getNum_estudante());
                                                    Lastupdate = aux;
                                                    if (dbManager.edita_registo(user, aux.getPassword())) {
                                                        out.writeObject(new Geral(Message_types.VALIDO));
                                                    } else
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                }
                                                case SUBMICAO_COD -> {
                                                    Msg_String_Int aux = (Msg_String_Int) message;
                                                    Lastupdate = new Msg_Sub_Cod(aux.getTipo(), email, aux.getConteudo(), aux.getNumero());
                                                    if (!dbManager.submitcod(aux.getNumero(), aux.getConteudo(), email)) {
                                                        out.writeObject(new Geral(Message_types.INVALIDO));
                                                    } else {
                                                        out.writeObject(new Geral(Message_types.VALIDO));
                                                    }
                                                }
                                                case CONSULTA_PRES_UTILIZADOR -> {
                                                    Msg_ConsultaComFiltros aux = (Msg_ConsultaComFiltros) message;

                                                    List<Evento> eventosAssistidos = dbManager.ConsultaPresencas_user(email, aux);
                                                    if (eventosAssistidos.isEmpty())
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                    else {
                                                        eventosPresencasUser.clear();
                                                        eventosPresencasUser.addAll(eventosAssistidos);
                                                        Evento[] res = eventosAssistidos.toArray(new Evento[0]);
                                                        out.writeObject(new Msg_ListaEventos(Message_types.VALIDO, res));
                                                    }
                                                }
                                                case CSV_UTILIZADOR -> {
                                                    File file = new File("minhasPresencas.csv");
                                                    if (!file.exists()) {
                                                        if (!file.createNewFile())
                                                            out.writeObject(new Geral(Message_types.ERRO));
                                                    }

                                                    Utils.presencasUtilizadorCSV(eventosPresencasUser, file);
                                                    sendfile(out, file);
                                                }
                                                case LOGOUT -> {
                                                    System.out.println("\n<SERVIDOR> [OPERACAO DE LOGOUT] -> " + email);
                                                    logado = false;
                                                }
                                                case FECHOU_APP -> {
                                                    System.out.println("\n<SERVIDOR> [CLIENTE SAIU DA APP] -> " + email);
                                                    stopthreadCliente = true;
                                                    logado = false;
                                                }
                                                default -> out.writeObject(new Geral(Message_types.INVALIDO));
                                            }
                                        } else
                                            out.writeObject(new Geral(Message_types.INVALIDO));
                                    }
                                } else {
                                    //Pedidos para clientes to tipo UTILIZADOR
                                    while (logado) {
                                        Object message = in.readObject();
                                        if (message instanceof Geral geral) {
                                            switch (geral.getTipo()) {
                                                case CRIA_EVENTO -> {
                                                    Msg_Cria_Evento evento = (Msg_Cria_Evento) message;
                                                    Lastupdate = evento;
                                                    if (dbManager.Cria_evento(evento)) {
                                                        out.writeObject(new Geral(Message_types.VALIDO));
                                                    } else
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                }
                                                case EDIT_EVENTO -> {
                                                    Msg_Edita_Evento evento = (Msg_Edita_Evento) message;
                                                    Lastupdate = evento;
                                                    if (dbManager.Edita_evento(evento)) {
                                                        out.writeObject(new Geral(Message_types.VALIDO));
                                                    } else
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                }
                                                case ELIMINAR_EVENTO -> {
                                                    Msg_String aux = (Msg_String) message;
                                                    Lastupdate = aux;
                                                    if (dbManager.Elimina_evento(aux.getConteudo())) {
                                                        out.writeObject(new Geral(Message_types.VALIDO));
                                                    } else
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                }
                                                case CONSULTA_EVENTOS -> {
                                                    Msg_ConsultaComFiltros aux = (Msg_ConsultaComFiltros) message;

                                                    List<Evento> eventosConsultados = dbManager.Consulta_eventos(aux);
                                                    if (eventosConsultados.isEmpty())
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                    else {
                                                        eventosConsultados.clear();
                                                        eventosPresencasUser.addAll(eventosConsultados); //vou utilizar o eventos presenças user para fazer o ficheiro csv do utilizador
                                                        Evento[] res = eventosConsultados.toArray(new Evento[0]);
                                                        out.writeObject(new Msg_ListaEventos(Message_types.VALIDO, res));
                                                    }
                                                }
                                                case GERAR_COD -> {
                                                    Msg_String_Int aux = (Msg_String_Int) message;
                                                    int code = dbManager.GeraCodigoRegisto(aux.getConteudo(), aux.getNumero());
                                                    Lastupdate = aux;
                                                    if (code != 0) {
                                                         out.writeObject(new Msg_String(Integer.toString(code), Message_types.VALIDO));
                                                    } else
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                }
                                                case CONSULTA_PRES_EVENT -> {
                                                    Msg_String aux = (Msg_String) message;
                                                    utilizadoresEvento = dbManager.Presencas_evento(aux.getConteudo());
                                                    if (utilizadoresEvento != null) {
                                                        Utilizador[] res = new Utilizador[utilizadoresEvento.size()];
                                                        for (int i = 0; i < utilizadoresEvento.size(); i++) {
                                                            res[i] = utilizadoresEvento.get(i);
                                                        }
                                                        out.writeObject(new Msg_ListaRegistos(Message_types.VALIDO, res));
                                                    } else
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                }
                                                case CSV_PRESENCAS_DO_EVENTO -> {
                                                    File file = new File("presencasEvento.csv");
                                                    if (!file.exists()) {
                                                        if (!file.createNewFile())
                                                            out.writeObject(new Geral(Message_types.ERRO));
                                                    }
                                                    Utils.eventosPresencasCSV(utilizadoresEvento, file);
                                                    sendfile(out, file);
                                                }
                                                case CONSULTA_PRES_UTILIZADOR -> {
                                                    Msg_String aux = (Msg_String) message;
                                                    eventosPresencasAdmin = dbManager.ConsultaPresencas_User_Admin(aux.getConteudo());
                                                    if (eventosPresencasAdmin != null) {
                                                        Evento[] res = eventosPresencasAdmin.toArray(new Evento[0]);
                                                        out.writeObject(new Msg_ListaEventos(Message_types.VALIDO, res));
                                                    } else
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                }

                                                case CSV_PRESENCAS_UTI_NUM_EVENTO -> {
                                                    //NÃO DEVIAM ESTAR A FAZER ISTO, VÃO BUSCAR OS DADOS E CRIAR O CSV DESTE LADO
                                                    File file = new File("Presencas.csv");// talvez o nome do ficheiro seja outro ,idk
                                                    if (!file.exists()) {
                                                        if (!file.createNewFile())
                                                            out.writeObject(new Geral(Message_types.ERRO));
                                                    }
                                                    Utils.presencasUtilizadorCSV(eventosPresencasUser, file);// not sure se e esta a funcao
                                                    // Aqui colocar a funcao que vamos chamar na db
                                                    sendfile(out, file);
                                                }

                                                case ELIMINA_PRES -> {
                                                    Msg_EliminaInsere_Presencas aux = (Msg_EliminaInsere_Presencas) message;
                                                    Lastupdate = aux;
                                                    if (dbManager.EliminaPresencas(aux.getNome_evento(), aux.getLista())) {
                                                        out.writeObject(new Geral(Message_types.VALIDO));
                                                    } else
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                }

                                                case INSERE_PRES -> {
                                                    Msg_EliminaInsere_Presencas aux = (Msg_EliminaInsere_Presencas) message;
                                                    Lastupdate = aux;
                                                    if (dbManager.InserePresencas(aux.getNome_evento(), aux.getLista())) {
                                                        out.writeObject(new Geral(Message_types.VALIDO));
                                                    } else
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                }
                                                case LOGOUT -> {
                                                    System.out.println("\n<SERVIDOR> [OPERACAO DE LOGOUT] -> " + email);
                                                    logado = false;
                                                }
                                                case FECHOU_APP -> {
                                                    System.out.println("\n<SERVIDOR> [CLIENTE SAIU DA APP] -> " + email);
                                                    stopthreadCliente = true;
                                                    logado = false;
                                                }
                                                default -> out.writeObject(new Geral(Message_types.INVALIDO));
                                            }
                                        } else
                                            out.writeObject(new Geral(Message_types.INVALIDO));
                                    }
                                }
                                gereRecursosClientes.removeLogado(email);
                            }
                        } else
                            out.writeObject(new Geral(Message_types.INVALIDO));
                    }
                } catch (IOException | ClassNotFoundException e) {
                    //System.out.println("\n<SERVIDOR> Ocorreu um erro na thread que atenderia um cliente :(");
                } finally {
                    gereRecursosClientes.removeLigacao(idCliente);
                    System.out.println("\n<SERVIDOR> Cliente [" + email + "] terminado.");
                }
        }
    }
    // funcoes uteis que são usadas pelo servidor
    public synchronized void enviatodosobv(Geral msg){
        Lastupdate=msg;

    }

    public synchronized void sendfile(OutputStream out, File file){
        byte []fileChunk = new byte[ProgServidor.MAX_SIZE];
        try (FileInputStream filereader=new FileInputStream(file)){
            int nbytes;
            do{
                nbytes = filereader.read(fileChunk);
                if(nbytes!=-1){
                    out.write(fileChunk,0,nbytes);
                    out.flush();
                }
            }while (nbytes>0);
            System.out.println("File sended");

        } catch (IOException e) {
            System.out.println(""+e.getCause());
        }
    }

}





