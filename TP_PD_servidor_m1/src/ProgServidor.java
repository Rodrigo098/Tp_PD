import pt.isec.pd.trabalhoPratico.ObservableInterface;
import pt.isec.pd.trabalhoPratico.dataAccess.DbManage;
import pt.isec.pd.trabalhoPratico.model.RemoteInterface;
import pt.isec.pd.trabalhoPratico.model.Utils.Utils;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.recordDados.*;

import java.io.*;
import java.net.*;
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
    List<ObservableInterface> observers;
    private DbManage dbManager;
    
    // CLIENTES (e ip heartbeat)
    private InetAddress grupoMulticast, heartbeatgroup;
    private DatagramSocket socketMulticastClientes;
    private final int portoClientes;
    private ServerSocket socketServidor;
    List<Socket> clients = new ArrayList<>();
    private Boolean pararServidor = false;

    public ProgServidor(int portoClientes) throws RemoteException {
        this.portoClientes = portoClientes;
        observers = new ArrayList<>();

    }

    public void setDbManager(DbManage dbManager) {
        this.dbManager = dbManager;
    }

    //////////////////////////////////// SERVIÇO /////////////////////////////
    public void servico() {
        new Thread(new ThreadLeLinhaComandos()).start();
        try
        {
            socketServidor = new ServerSocket(portoClientes);
            try {
                // multicast clientes
                socketMulticastClientes = new DatagramSocket();
                this.grupoMulticast = InetAddress.getByName(ipMuticastString);

                LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                System.out.println("Registry lancado");
                heartbeatgroup = InetAddress.getByName(Heartbeatip);
                multicastSocketBackup = new MulticastSocket(portobackup);
                multicastSocketBackup.joinGroup(heartbeatgroup);
                rmi = new ProgServidor(portoClientes); //???
                String myIpIdress=InetAddress.getLocalHost().getHostAddress();
                Naming.rebind("rmi://"+myIpIdress+"/"+SERVICE_NAME,rmi);

                new Thread(new ThreadHeartbeat()).start();
            } catch (SocketException | UnknownHostException e) {
                throw new RuntimeException("Nao foi possivel criar o socket para multicast, erro [" + e + "]");
            }catch (RemoteException e){
                System.out.println("Registy ja em execucao");
            }

            while(!pararServidor){
                Socket cli = socketServidor.accept();// aceita clientes
                //cli.setSoTimeout(10000);
                clients.add(cli);// adiciona cliente conectado a lista de clientes
                new Thread(new ThreadCliente(cli)).start();
            }
        } catch (IOException e) {
            System.out.println("<SERVIDOR> já não se aceitam mais clientes.");
        }
    }

    ////////////////////////////// ATUALIZAÇÃO ASSÍNCRONA /////////////////////////////
    public synchronized void envioDeAvisoDeAtualizacao(String msgAtaulizacao) {

        try {
            //aviso de atualizacao aos clientes
            byte[] msg = msgAtaulizacao.getBytes();
            DatagramPacket atualizacaoPacket = new DatagramPacket(msg, msg.length, grupoMulticast, portoClientes);
            socketMulticastClientes.send(atualizacaoPacket);

            //aviso de atualizacao aos backups
            DadosRmi data = new DadosRmi(InetAddress.getLocalHost().getHostAddress(), SERVICE_NAME,dbManager.getVersao());
            ByteArrayOutputStream help = new ByteArrayOutputStream(); //for real "help"
            ObjectOutputStream objout = new ObjectOutputStream(help);
            objout.writeObject(data);
            DatagramPacket backupPacket = new DatagramPacket(help.toByteArray(),help.toByteArray().length,heartbeatgroup,portobackup);
            multicastSocketBackup.send(backupPacket);

            System.out.println("Aviso de atualizacao enviado aos clientes...");
        } catch (IOException e) {
            throw new RuntimeException("Nao foi possivel informar da atualizacao ao clientes...");
        }
    }

    @Override
    public void getDB() {
        for (ObservableInterface obv:observers
             ) {
            obv.avisaobservables();// acho que seria algo assim que usariamos para atualizar os observables, depois chamavamos esta funcao nos outros metodos

        }
    }

    @Override
    public synchronized void addObservable(ObservableInterface obv) {
      if(!observers.contains(obv))
          observers.add(obv);
    }

    @Override
    public synchronized   void RemoveObservable(ObservableInterface obv) {
        observers.remove(obv);
    }

    class ThreadHeartbeat extends Thread{

        @Override
        public void run() {

          try
          {
             DadosRmi data = new DadosRmi(InetAddress.getLocalHost().getHostAddress(), SERVICE_NAME,dbManager.getVersao());// nao tenho a certeza se seria este o IP

             ByteArrayOutputStream help = new ByteArrayOutputStream(); //for real "help"
             ObjectOutputStream objout = new ObjectOutputStream(help);
             objout.writeObject(data);

             DatagramPacket packet = new DatagramPacket(help.toByteArray(),help.toByteArray().length,heartbeatgroup,portobackup);
                 while (!pararServidor){
                     Timer temporizador=new Timer();
                     TimerTask timerTask=new TimerTask() {
                         int timer;
                         @Override
                         public void run() {
                            timer++;
                            if(timer == 10) {
                                timer = 0;
                                try {
                                    multicastSocketBackup.send(packet);}
                                catch (IOException e) {
                                    System.out.println(e.getMessage());}
                            }
                         }
                     };
                     temporizador.schedule(timerTask,0,1000);
             }
          } catch (IOException e) {
              throw new RuntimeException(e);
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
                System.out.println("<SERVIDOR> Escreva \"sair\" para terminar o servidor");
                inserido = linhaComandos.nextLine();
                //if(inserido.equals("atua"))
                  //  envioDeAvisoDeAtualizacao("atualizacao");
            }while (!inserido.equals("sair"));
            envioDeAvisoDeAtualizacao("fimServidor");
            try {
                socketServidor.close();
                socketMulticastClientes.close();
                clients.clear();
            } catch (IOException e) {
                throw new RuntimeException("erro a fechar sockets");
            }
            pararServidor = true;
        }
    }
    ////////////////////////////////// THREAD CLIENTE /////////////////////////////
    class ThreadCliente extends Thread {
        boolean flagStop, isadmin, logado,stopthread;
        Socket client;
        Timerask timerask;
        String email;

        List <Evento> eventosPresencasUser = new ArrayList<>();
        List <Evento> eventosPresencasAdmin = new ArrayList<>();
        List<Utilizador> utilizadoresEvento = new ArrayList<>();

        public ThreadCliente(Socket cli) {
            client = cli;
            flagStop = false;
            isadmin = false;
            stopthread=false;
        }

        @Override
        public void run() {
            try(ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client.getInputStream())
            ) {


                while(!pararServidor&& !stopthread) {

                    Geral msgRecebida = (Geral) in.readObject();
                    System.out.println("Teve aqui");
                    //Pedidos de Registo e Login
                    if(msgRecebida.getTipo() == Message_types.LOGIN){// para descobrir qual a classe estava a pensar em algo para o processamento depois dos dados
                        Msg_Login aux = (Msg_Login) msgRecebida;
                        email = aux.getEmail();
                        System.out.println(email);

                        Boolean [] resposta = dbManager.autentica_user(email, aux.getPassword());

                        if(!resposta[0])
                            out.writeObject(new Geral(Message_types.INVALIDO));
                        else {
                            logado = true;
                            //System.out.println(resposta[0]);
                            if(resposta[1]) {
                                isadmin = true;
                                out.writeObject(new Msg_String(ipMuticastString, Message_types.ADMINISTRADOR));
                            } else
                                out.writeObject(new Msg_String(ipMuticastString, Message_types.UTILIZADOR));
                        }

                    }
                    else if (msgRecebida.getTipo() == Message_types.REGISTO) {


                        Mgs_RegistarEditar_Conta aux=(Mgs_RegistarEditar_Conta)msgRecebida;
                        email = aux.getEmail();
                        Utilizador user = new Utilizador(aux.getNome(),aux.getEmail(),aux.getNum_estudante());

                           if( dbManager.RegistoNovoUser(user,aux.getPassword())){
                        out.writeObject(new Msg_String(ipMuticastString, Message_types.UTILIZADOR));
                           logado=true;
                           }else {
                               out.writeObject(new Geral(Message_types.ERRO));
                           }

                    }else{
                        out.writeObject(new Geral(Message_types.FAZER_LOGIN));
                    }
                    if(timerask!=null && logado){
                        timerask.cancel();
                        System.out.println("Parou o timing");
                        flagStop=false;
                    }
                    //Pedidos para clientes do tipo UTILIZADOR
                    if(!isadmin && logado) {
                        while (!flagStop ) {
                            Geral message = (Geral) in.readObject();
                            switch (message.getTipo()) {
                                case EDITAR_REGISTO ->{
                                    Mgs_RegistarEditar_Conta aux = (Mgs_RegistarEditar_Conta)message;
                                    Utilizador user = new Utilizador(aux.getNome(), aux.getEmail(), aux.getNum_estudante());

                                    if(dbManager.edita_registo(user,aux.getPassword())){
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                    }else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case SUBMICAO_COD -> {
                                    //vai à BD verificar -> têm de fazer, por enquanto está inválido
                                    Msg_String_Int aux= (Msg_String_Int)message;
                                 if(!dbManager.submitcod(aux.getNumero(),aux.getConteudo(),email)){
                                     out.writeObject(new Geral(Message_types.INVALIDO));
                                 }else{
                                    out.writeObject(new Geral(Message_types.VALIDO));}
                                /*se for valido fazer

                                 */
                                }
                                case CONSULTA_PRES_UTILIZADOR -> {
                                    Msg_ConsultaComFiltros aux = (Msg_ConsultaComFiltros) message;

                                    //Depois temos que mandar aí a classe com os critérios/filtros e substituir esses parametros extras

                                    List<Evento> eventosAssistidos = dbManager.ConsultaPresencas_user(email, aux);
                                    if(eventosAssistidos.isEmpty())
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
                                    if(!file.exists()) {
                                        if(!file.createNewFile())
                                            out.writeObject(new Geral(Message_types.ERRO));
                                    }

                                    Utils.presencasUtilizadorCSV(eventosPresencasUser,file);
                                    sendfile(out,file);
                                }
                                case LOGOUT -> {
                                    /* Tive a fzr experiencias caso quisessem por um timer no logout
                                    Timerask timerask =new Timerask(in);
                                    Timer usar=new Timer();
                                    usar.schedule(timerask,0,1000);
                                    //flagStop = true;*/
                                    flagStop=true;
                                    logado=false;
                                    Timer startcount=new Timer();
                                    timerask=new Timerask(in);
                                    startcount.schedule(timerask,0,1000);



                                }
                                default -> out.writeObject(new Geral(Message_types.INVALIDO));
                            }
                        }
                    } else if(logado){
                        //Pedidos para clientes to tipo UTILIZADOR
                        while (!flagStop ) {
                            Geral message = (Geral) in.readObject();
                            switch (message.getTipo()) {
                                case CRIA_EVENTO -> {
                                    Msg_Cria_Evento evento = (Msg_Cria_Evento) message;
                                    if(dbManager.Cria_evento(evento)) {//.getNome(), evento.getLocal(), evento.getData(), evento.getHoreInicio(), evento.getHoraFim())){
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case EDIT_EVENTO -> {
                                    Msg_Edita_Evento evento = (Msg_Edita_Evento) message;
                                    if(dbManager.Edita_evento(evento)) {
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case ELIMINAR_EVENTO -> {
                                    Msg_String aux = (Msg_String)message;
                                    if(dbManager.Elimina_evento(aux.getConteudo())) {
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case CONSULTA_EVENTOS -> {
                                    Msg_ConsultaComFiltros aux = (Msg_ConsultaComFiltros) message;

                                    List <Evento> eventosConsultados = dbManager.Consulta_eventos(aux);
                                    if(eventosConsultados.isEmpty())
                                        out.writeObject(new Geral(Message_types.ERRO));
                                    else {
                                        eventosConsultados.clear();
                                        eventosPresencasUser.addAll(eventosConsultados); //vou utilizar o eventos presenças user para fazer o ficheiro csv do utilizador
                                        Evento[] res = eventosConsultados.toArray(new Evento[0]);
                                        out.writeObject(new Msg_ListaEventos(Message_types.VALIDO, res));
                                    }
                                }
                                case GERAR_COD ->{
                                    Msg_String_Int aux = (Msg_String_Int)message;
                                    int  code = dbManager.GeraCodigoRegisto(aux.getConteudo(), aux.getNumero());
                                    if(code != 0)
                                        out.writeObject(new Msg_String(Integer.toString(code), Message_types.VALIDO));
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case CONSULTA_PRES_EVENT -> {
                                    Msg_String aux = (Msg_String)message;
                                     utilizadoresEvento = dbManager.Presencas_evento(aux.getConteudo());
                                    if(utilizadoresEvento != null) {
                                        Utilizador[] res = new Utilizador[utilizadoresEvento.size()];
                                        for (int i = 0; i <utilizadoresEvento.size() ; i++) {
                                            res[i]= utilizadoresEvento.get(i);
                                        }
                                        out.writeObject(new Msg_ListaRegistos(Message_types.VALIDO, res));
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }
                                case CSV_PRESENCAS_DO_EVENTO -> {
                                    File file=new File("presencasEvento.csv");
                                    if(!file.exists()) {
                                        if(!file.createNewFile())
                                            out.writeObject(new Geral(Message_types.ERRO));
                                    }
                                    Utils.eventosPresencasCSV(utilizadoresEvento,file);
                                    sendfile(out,file);
                                }
                                case CONSULTA_PRES_UTILIZADOR -> {

                                    Msg_String aux = (Msg_String) message;
                                   eventosPresencasAdmin= dbManager.ConsultaPresencas_User_Admin(aux.getConteudo());
                                   if(eventosPresencasAdmin!=null){
                                   Evento[]res=eventosPresencasAdmin.toArray(new Evento[0]);
                                   out.writeObject(new Msg_ListaEventos(Message_types.VALIDO,res));}
                                   else
                                       out.writeObject(new Geral(Message_types.ERRO));
                                }

                                case CSV_PRESENCAS_UTI_NUM_EVENTO -> {
                                    //NÃO DEVIAM ESTAR A FAZER ISTO, VÃO BUSCAR OS DADOS E CRIAR O CSV DESTE LADO
                                    File file = new File("Presencas.csv");// talvez o nome do ficheiro seja outro ,idk
                                    if(!file.exists()) {
                                        if(!file.createNewFile())
                                            out.writeObject(new Geral(Message_types.ERRO));
                                    }
                                    Utils.presencasUtilizadorCSV(eventosPresencasUser, file);// not sure se e esta a funcao
                                    // Aqui colocar a funcao que vamos chamar na db
                                    sendfile(out,file);
                                }


                                case ELIMINA_PRES -> {
                                    Msg_EliminaInsere_Presencas aux = (Msg_EliminaInsere_Presencas)message;
                                    if(dbManager.EliminaPresencas(aux.getNome_evento(),aux.getLista())) {
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }


                                case INSERE_PRES ->  {
                                    Msg_EliminaInsere_Presencas aux = (Msg_EliminaInsere_Presencas)message;
                                    if(dbManager.InserePresencas(aux.getNome_evento(),aux.getLista())) {
                                        out.writeObject(new Geral(Message_types.VALIDO));
                                    }
                                    else
                                        out.writeObject(new Geral(Message_types.ERRO));
                                }


                                case LOGOUT -> {
                                    flagStop=true;
                                    logado=false;
                                    Timer startcount=new Timer();
                                    timerask=new Timerask(in);
                                    startcount.schedule(timerask,0,1000);
                                }
                                default -> out.writeObject(new Geral(Message_types.INVALIDO));
                            }
                        }
                    }
                }

            } catch (IOException | ClassNotFoundException ignored) {

            } finally {
                try {
                    client.close();
                } catch (IOException ignored) {
                }
                if(timerask!=null)timerask.cancel();
                clients.remove(client);
                System.out.println("Cliente: "+ email +"terminado");
            }
        }
        class Timerask extends TimerTask{
            private int contador;
            private final ObjectInputStream oin;


            public Timerask(ObjectInputStream oin) {
                this.oin=oin;
            }



            @Override
            public void run() {
                System.out.println("Fez "+contador);
                contador++;

                if(contador==60){
                    stopthread=true;
                    try {

                        oin.close();

                    } catch (IOException e) {
                        throw new RuntimeException(e);

                    }
                    this.cancel();
                }
            }
        }
    }
    public  void sendfile(OutputStream out, File file){
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





