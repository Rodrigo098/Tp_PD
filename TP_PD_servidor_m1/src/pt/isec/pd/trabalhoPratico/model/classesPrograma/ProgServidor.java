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
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class ProgServidor  extends UnicastRemoteObject implements RemoteInterface {
    // BACKUPS
    public static final int MAX_SIZE = 4000;
    private static final int portobackup = 4444;
    public static final String SERVICE_NAME = "servidor";
    private final String ipMuticastString = "224.0.1.0";
    private final String Heartbeatip="230.44.44.44";
    private RemoteInterface rmi;
    private MulticastSocket multicastSocketBackup;
    private final Timer temporizador;
    private final HeartBeatTask heartBeatTask;
    List<ObservableInterface> observers=new ArrayList<>();
    private DbManage dbManager;
    private DatagramPacket heartBeatPacket;
    private int timerCount;
    private InetAddress heartbeatgroup;
    private Geral  Lastupdate;
    
    // CLIENTES
    private final int portoClientes;
    private ServerSocket socketServidor;
    private Boolean pararServidor = false;
    private final GereRecursosClientes gereRecursosClientes;
    private final List<ThreadCliente> threadsClientes;


    public ProgServidor(int portoClientes) throws RemoteException {
        this.portoClientes = portoClientes;
        gereRecursosClientes = new GereRecursosClientes();
        threadsClientes = new ArrayList<>();
        temporizador = new Timer();
        heartBeatTask = new HeartBeatTask();
        dbManager = new DbManage();
        dbManager.addVersaoListener(event -> envioDeAvisoDeAtualizacao("atualizacao"));
    }

    //////////////////////////////////// SERVIÇO /////////////////////////////
    public void servico() {
        new Thread(new ThreadLeLinhaComandos()).start();
        try
        {
            socketServidor = new ServerSocket(portoClientes);
            try {
                System.setProperty("java.rmi.server.hostname", "192.168.56.1");
                LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                System.out.println("\n<SERVIDOR> Registry lancado");
                heartbeatgroup = InetAddress.getByName(Heartbeatip);
                multicastSocketBackup = new MulticastSocket(portobackup);

                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName("192.168.56.1"));// replace with your network interface

               multicastSocketBackup.joinGroup(new InetSocketAddress(heartbeatgroup, portobackup),networkInterface);
               // multicastSocketBackup.joinGroup(heartbeatgroup);
               // System.out.println(heartbeatgroup.getHostAddress());
               // multicastSocketBackup.setInterface(heartbeatgroup);
                rmi = this ;//???
                String myIpIdress = "192.168.43.48";//InetAddress.getLocalHost().getHostAddress();
                Naming.rebind("rmi://"+myIpIdress+"/"+SERVICE_NAME,rmi);


                DadosRmi data = new DadosRmi(myIpIdress, SERVICE_NAME,dbManager.getVersao());// nao tenho a certeza se seria este o IP

                ByteArrayOutputStream help = new ByteArrayOutputStream(); //for real "help"
                ObjectOutputStream objout = new ObjectOutputStream(help);
                objout.writeObject(data);

                heartBeatPacket = new DatagramPacket(help.toByteArray(),help.toByteArray().length,heartbeatgroup,portobackup);
                temporizador.schedule(heartBeatTask,0,1000);
            } catch (SocketException | UnknownHostException e) {
                throw new RuntimeException("\n<SERVIDOR> Nao foi possivel criar o socket para multicast, erro [" + e + "]");
            }catch (RemoteException e){
                System.out.println("\n<SERVIDOR> Registry ja em execucao");
            }
            // ACEITA CLIENTES
            while(!pararServidor){
                try {
                    Socket cli = socketServidor.accept();
                    BufferedReader inTipo = new BufferedReader(new InputStreamReader(cli.getInputStream()));
                    String tipoSocket = inTipo.readLine();

                    if (tipoSocket.contains("socketAtualizacao")) {
                        String ip = ((InetSocketAddress) cli.getRemoteSocketAddress()).getAddress().getHostAddress();
                        gereRecursosClientes.setClienteDadosAtualizacao(ip, cli, new PrintStream(cli.getOutputStream()), tipoSocket.split(" ")[1]);
                        System.out.println("\n<SERVIDOR> Socket do cliente [" + tipoSocket.split(" ")[1] + "] para atualização conectado.");
                    } else if (tipoSocket.equals("socketPedidos")) {
                        ThreadCliente novaTh = new ThreadCliente(gereRecursosClientes.novaLigacao(((InetSocketAddress) cli.getRemoteSocketAddress()).getAddress().getHostAddress(), cli));
                        novaTh.start();
                        threadsClientes.add(novaTh);
                    }
                } catch (IOException e) {
                    System.out.println("\n<SERVIDOR> já não se aceitam mais clientes.");
                }
            }
        } catch (IOException e) {
            System.out.println("\n<SERVIDOR> Excecao a criar socket para o Servidor.");
        }
        if(!threadsClientes.isEmpty()){
            for(ThreadCliente t : threadsClientes){
                String nomeCli = t.getCliente();
                try {
                    t.join();
                    System.out.println("\n<SERVIDOR> Thread para atendimento do cliente [" + nomeCli + "] terminada.");
                } catch (InterruptedException e) {
                    System.out.println("\n<SERVIDOR> Excecao ao esperar que a thread do cliente [" + nomeCli + "] terminasse.");
                }
            }
        }
    }

    ////////////////////////////// ATUALIZAÇÃO ASSÍNCRONA /////////////////////////////
    public synchronized void envioDeAvisoDeAtualizacao(String msgAtaulizacao) {

/*        try {
            //aviso de atualizacao aos backups
            byte[] help= getDados();
            synchronized (heartBeatPacket){
                heartBeatPacket = new DatagramPacket(help,help.length,heartbeatgroup,portobackup);
            }
            synchronized (multicastSocketBackup){
                enviatodosobv(Lastupdate);
                multicastSocketBackup.send(heartBeatPacket);
            }
            System.out.println("Aviso de atualizacao enviado aos clientes...");
        } catch (IOException e) {
            throw new RuntimeException("Nao foi possivel informar da atualizacao ao clientes...");
        }*/

        //aviso de atualizacao aos clientes
        System.out.println("\n------------------------------------------");
        System.out.println("<SERVIDOR> Aviso de atualizacao enviado a:");
        gereRecursosClientes.enviaAvisoAtualizacao(msgAtaulizacao);
        System.out.println("\n------------------------------------------");
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

    ////////////////////////////////// THREAD HEART BEAT /////////////////////////////
    class HeartBeatTask extends TimerTask {
        @Override
        public void run() {
            timerCount++;
            if(timerCount == 10) {
                timerCount = 0;
                try {
                    System.out.println(multicastSocketBackup.getLocalPort());
                    synchronized (multicastSocketBackup) {
                        multicastSocketBackup.send(heartBeatPacket);
                    }
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());}
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
                gereRecursosClientes.terminarLigacoes();
            } catch (IOException e) {
                throw new RuntimeException("\n<SERVIDOR> Erro a fechar sockets");
            }
        }
    }


    ////////////////////////////////// THREAD CLIENTE /////////////////////////////
    class ThreadCliente extends Thread {
        boolean isadmin, logado, stopthreadCliente;
        String ipCliente, email;

        List <Evento> eventosPresencasUser = new ArrayList<>();
        List <Evento> eventosPresencasAdmin = new ArrayList<>();
        List <Utilizador> utilizadoresEvento = new ArrayList<>();

        public ThreadCliente(String ipCliente) {
            this.ipCliente = ipCliente;
            isadmin = false;
            stopthreadCliente = false;
        }
        public String getCliente() {
            return email;
        }
        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(gereRecursosClientes.getClienteSocketPedidos(ipCliente).getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(gereRecursosClientes.getClienteSocketPedidos(ipCliente).getInputStream())
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
                                                    Utilizador user = new Utilizador(aux.getNome(), aux.getEmail(), aux.getNum_estudante());
                                                    Lastupdate = aux;
                                                    if (dbManager.edita_registo(user, aux.getPassword())) {
                                                        out.writeObject(new Geral(Message_types.VALIDO));

                                                    } else
                                                        out.writeObject(new Geral(Message_types.ERRO));
                                                }
                                                case SUBMICAO_COD -> {
                                                    //vai à BD verificar -> têm de fazer, por enquanto está inválido
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
                                                        eventosAssistidos.clear();
                                                        eventosPresencasUser.addAll(eventosAssistidos); //vou utilizar o eventos presenças user para fazer o ficheiro csv do utilizador
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
                                                    if (dbManager.Cria_evento(evento)) {//.getNome(), evento.getLocal(), evento.getData(), evento.getHoreInicio(), evento.getHoraFim())){
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
                    gereRecursosClientes.removeLigacao(ipCliente);
                    System.out.println("\n<SERVIDOR> Cliente [" + email + "] terminado.");
                }
        }
    }
    // funcoes uteis que são usadas pelo servidor
    public synchronized void enviatodosobv(Geral msg){
        Lastupdate=msg;
        for (ObservableInterface obv:observers) {
            try {
                System.out.println(dbManager.getVersao());
                obv.avisaObservables(Lastupdate, dbManager.getVersao());
            } catch (RemoteException e) {
                observers.remove(obv);// se o o observer nao tiver a funcionar removo o
            }
        }
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
    private byte[] getDados(){// como vamos ter duas versos de dados diferentes temis

        try {
            DadosRmi data = new DadosRmi(InetAddress.getLocalHost().getHostAddress(), SERVICE_NAME,dbManager.getVersao());
            ByteArrayOutputStream helpi = new ByteArrayOutputStream(); //for real "help"
            ObjectOutputStream objout = new ObjectOutputStream(helpi);
            objout.writeObject(data);
            return helpi.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new byte[0];
        }
    }
}





