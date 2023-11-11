package pt.isec.pd.trabalhoPratico.model.classesPrograma;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;
import pt.isec.pd.trabalhoPratico.model.classesDados.Utilizador;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

///////////////////////////////////// PROGRAMA CLIENTE ///////////////////////
public class ProgramaCliente {
    // TEMPO
    private static long parouContagemTempo;
    private static long comecouContagemTempo;
    private static final long TEMPO_MAXIMO = 1000000000L * 10; // 10 segundos

    // EVENTOS
    private static SimpleIntegerProperty atualizacao = new SimpleIntegerProperty(0);
    private static SimpleIntegerProperty erro = new SimpleIntegerProperty(0);
    private static SimpleStringProperty logado = new SimpleStringProperty("ENTRADA");

    // COMUNICAÇÃO
    private Socket socket;

//-------------------- ATUALIZACAO ASSINCRONA -----------------
    class AtualizacaoAsync implements Runnable {
        private final Socket socket;

        public AtualizacaoAsync(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            do {
                try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {
                    Object novaAtualizacao = oin.readObject();
                    if (novaAtualizacao instanceof Geral g)
                        if (g.getTipo() == Message_types.ATUALIZACAO)
                            atualizacao.setValue(atualizacao.getValue() + 1);

                } catch (IOException | ClassNotFoundException ignored) {
                    ProgramaCliente.setErro();
                }
            } while (Thread.currentThread().isAlive());
        }
    }
//-------------------------------------------------------------

//-------------------- VERIFICA LIGACAO -----------------
    class VerificaLigacaoServidor implements Runnable {
        private String estadoAntigo;
        private Timer temporizador;
        private boolean sair = false;

        public VerificaLigacaoServidor() {
            temporizador = new Timer();
            estadoAntigo = logado.getValue();
            logado.addListener(observable -> sair());
        }

        private void sair() {
            sair = logado.getValue().equals("EXCEDEU_TEMPO");
        }
        private TimerTask verificaLigacao() {
            if(estadoAntigo.equals("EXCEDEU_TEMPO") && estadoAntigo.equals(logado.getValue())) {
                sair = false;
                setLogado("EXCEDEU_TEMPO");
            }
            return null;
        }
        @Override
        public void run() {
            do{
                temporizador.schedule(verificaLigacao(), TEMPO_MAXIMO);
            }while (!sair);
        }

    }


//-------------------------------------------------------------

    public ProgramaCliente() {
        new Thread(new VerificaLigacaoServidor()).start();
    }

    ////////////////////////////////////////////////////////////////////
    public void addLogadoListener(InvalidationListener listener) {
        logado.addListener(listener);
    }
    public void addAtualizacaoListener(InvalidationListener listener) {
        atualizacao.addListener(listener);
    }
    public void addErroListener(InvalidationListener listener) {
        erro.addListener(listener);
    }
    protected static synchronized void setErro() {
        System.out.println("ERRO");
        erro.set(erro.getValue() + 1);
    }
    protected static synchronized void setLogado(String estado) {
        logado.set(estado);
    }
    public String getLogado() {
        return logado.getValue();
    }
    public boolean verificaFormato(String email) {
        if (email == null || email.isBlank())
            return true;
        if (email.indexOf('@') <= 0 || !(email.indexOf('@') <= email.indexOf('.') - 2))
            return true;
        return email.split("[@.]").length != 3;
    }

    private static void comecaContarTempo() {
        comecouContagemTempo = System.nanoTime();
    }
    private static void paraContarTempo() {
        parouContagemTempo = System.nanoTime();
    }
    private boolean existeLigacao(){
        paraContarTempo();
        if(parouContagemTempo - comecouContagemTempo > TEMPO_MAXIMO) {
            setLogado("EXCEDEU_TEMPO");
            return false;
        }
        return true;
    }

    ///////////////////////////////////////FUNCIONALIDADES:
    /////////////////////////COMUNS:
    public Pair<Boolean, String> criaSocket(List<String> list) {
        Pair<Boolean, String> pontoSituacao;

        if (list.size() == 2) {
            this.socket = new Socket();
            pontoSituacao = new Pair<>(true, "Ocorreu uma exceção I/O na criação do socket.");
        /*
            try (Socket socket = new Socket(InetAddress.getByName(list.get(0)), Integer.parseInt(list.get(1)))) {
                this.socket = socket;
                pontoSituacao = new Pair<>(true, "Conexão bem sucedida");
                comecaContarTempo();
            } catch (IllegalArgumentException e) {
                pontoSituacao = new Pair<>(false, "Introduziu um porto inválido.");
            } catch (NullPointerException e) {
                pontoSituacao = new Pair<>(false, "Introduziu um endereço inválido.");
            } catch (IOException e) {
                pontoSituacao = new Pair<>(false, "Ocorreu uma exceção I/O na criação do socket.");
            }*/
        } else
            pontoSituacao = new Pair<>(false, "Não foram introduzidos dados suficientes como argumento.");
        return pontoSituacao;
    }

    public void login(String email, String password) {
        if(!existeLigacao())
            return;

        if (password == null || password.isBlank() || verificaFormato(email))
            return;
        /*
        Msg_Login dadosLogin = new Msg_Login(email, password);
        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream()))
        {

            oout.writeObject(dadosLogin);
            oout.flush();

            Msg_String validacao = (Msg_String) oin.readObject();

                switch (validacao.getTipo()){
                    case ADMINISTRADOR -> {
                        logado.set("ADMINISTRADOR");
                        try (Socket socketThread = new Socket(Msg_String.getConteudo(), this.socket.getPort())) {
                            new Thread(new AtualizacaoAsync(socketThread)).start();
                        } catch (Exception e) {
                            setErro();
                        }
                    }
                    case UTILIZADOR ->
                        logado.set("UTILIZADOR");
                    case INVALIDO, ERRO -> {
                        setErro();
                        //sairApp();
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }*/
        logado.set("UTILIZADOR");
    }

    public void logout() {
        comecaContarTempo();

        /*
        Geral logout = new Geral(Message_types.LOGOUT);
        try (ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {

            oout.writeObject(logout);
            oout.flush();
            logado.set("ENTRADA");
        } catch (IOException e) {
            setErro();
        }*/
        logado.set("ENTRADA");
    }

    public Evento[] obterListaConsultaEventos(Message_types tipo, String nome, String local, LocalDate limData1, LocalDate limData2, int horaInicio, int horaFim) {
        if(nome != null && !nome.isBlank() && local != null && !local.isBlank() && limData1 != null && limData2 != null && horaInicio >= horaFim)
        {
            return new Evento[]{new Evento("ola", "HelloMate", "ISEC", LocalDate.now(), 11,12)};
        /*
        Msg_ConsultaComFiltros consultaEventos = new Msg_ConsultaComFiltros(tipo, nome, local, limData1, limData2, horaInicio, horaFim);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(consultaEventos);
            oout.flush();

            Msg_EliminaInsere_Presencas lista = (Msg_EliminaInsere_Presencas) oin.readObject();
            if(lista.getTipo() == Message_types.VALIDO)
                return lista.getLista();
        } catch (IOException | ClassNotFoundException e) {
            setErro();
        */}
        return new Evento[]{};
    }

    public String obterCSV(String caminhoCSV, String nomeFicheiro, Message_types tipoCSV) {
        if(caminhoCSV == null || caminhoCSV.isBlank() || nomeFicheiro == null || nomeFicheiro.isBlank())
            return "É necessário inserir um caminho para guardar o ficheiro!";

        String localCSVCaminho;
        File destinoCSV = new File(caminhoCSV);
        byte []fileChunk = new byte[4000];
        int nbytes;
        int nCiclos = 0, totalBytes=0;

        if(!destinoCSV.exists()){
            System.out.println();
            return "A directoria inserida [" + caminhoCSV + "] não existe!";
        }

        if(!destinoCSV.isDirectory()){
            return "O caminho [" + caminhoCSV + "] não é uma diretoria!";
        }

        if(!destinoCSV.canWrite()){
            return "Não pode guardar o .csv em: " + destinoCSV;
        }

        try{
            localCSVCaminho = destinoCSV.getCanonicalPath()+File.separator + nomeFicheiro + ".csv";
        }catch(IOException e){
            return "Ocorreu um erro ao gerar o csv!";
        }
        /*
        Msg_String csv = new Msg_String(nomeFicheiro, tipoCSV);

        try (FileOutputStream localFileOutputStream = new FileOutputStream(localCSVCaminho);
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {

            oout.writeObject(csv);
            oout.flush();

            InputStream inStream = socket.getInputStream();

            while((nbytes = inStream.read(fileChunk)) > 0){
                totalBytes += nbytes;
                localFileOutputStream.write(fileChunk, 0, nbytes);
            }
            return "CSV gerado com sucesso guardado em: " + localCSVCaminho;
        }catch (IOException e){
            setErro();
        }*/
        return "Erro ao gerar CSV";
    }

    /////////////////////////UTILIZADOR:
    public Pair<String, Boolean> registarConta(String nome, String email, String numIdentificacao, String password, String confPass) {
        if(!existeLigacao())
            return new Pair<>("Tempo excedido", false);

        if (nome == null || nome.isBlank() || password == null || password.isBlank() || confPass == null || confPass.isBlank() || !password.equals(confPass) || verificaFormato(email) || numIdentificacao == null || numIdentificacao.isBlank())
            return new Pair<>("Os dados inseriados são inválidos :(", false);

        long numID;
        try {
            numID = Integer.parseInt(numIdentificacao);//?? como é que ponho para long?
        } catch (NumberFormatException e) {
            return new Pair<>("Insira um número de identificação válido!", false);
        }
        /*
        Mgs_RegistarEditar_Conta dadosRegisto = new Mgs_RegistarEditar_Conta(nome, email, password, numID, Message_types.REGISTO);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {

            oout.writeObject(dadosRegisto);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO) {
                logado.set("UTILIZADOR");
                return new Pair<>("", true);
            }
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }*/
        return new Pair<>("Erro ao registar conta!", false);
    }

    public boolean registarPresenca(String codigoEvento) {
        if (codigoEvento == null || codigoEvento.isBlank())
            return false;
        /*
        Msg_String registoPresenca = new Msg_String(codigoEvento, Message_types.SUBMICAO_COD);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(registoPresenca);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return true;
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }*/
        return false;
    }

    public String editarRegisto(String nome, String numIdentificacao, String password, String confPass) {
        if (nome == null || nome.isBlank() || password == null || password.isBlank() || !password.equals(confPass) || numIdentificacao == null || numIdentificacao.isBlank())
            return "Dados de input inválidos :(";

        long numID;
        try {
            numID = Integer.parseInt(numIdentificacao);//?? como é que ponho para long?
            if(numID < 0)
                return "O teu número acho que não é negativo...";
        } catch (NumberFormatException e) {
            return "O teu número deve ser inteiro!";
        }
        /*
        Mgs_RegistarEditar_Conta dadosRegisto = new Mgs_RegistarEditar_Conta(nome, null, password, numID, Message_types.EDITAR_REGISTO);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(dadosRegisto);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return "Registo editado com sucesso!";
        } catch (ClassNotFoundException | IOException ignored) {
            setErro();
        }*/
        return "Erro";
    }

    /////////////////////////ADMINISTRADOR:
    //CRIAR OU EDITAR EVENTO, O ÚLTIMO PARÂMETRO É PARA SABER SE É PARA CRIAR OU EDITAR
    public String criar_Evento(String nome, String local, LocalDate data, int horaInicio, int horaFim) {
        if (nome == null || nome.isBlank() || local == null || local.isBlank() || data == null || horaInicio >= horaFim)
            return "Dados de input inválidos :(";

        LocalDate dataAtual = LocalDate.now();
        LocalTime horaAtual = LocalTime.now();

        if (data.isBefore(dataAtual) || horaInicio < horaAtual.getHour())
            return "A data não pode estar no passadooo!";
        /*
        Msg_Cria_Evento evento =
                new Msg_Cria_Evento(new Evento("eu",nome, local, data, horaInicio, horaFim));

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(evento);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return "Evento criado com sucesso!";
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }*/
        return "Erro";
    }

    public String editar_Evento(String eventoNomeAntigo, String novoNome, String local, LocalDate data, int horaInicio, int horaFim) {

        LocalDate dataAtual = LocalDate.now();
        LocalTime horaAtual = LocalTime.now();

        if (eventoNomeAntigo == null || eventoNomeAntigo.isBlank() || novoNome == null || novoNome.isBlank() ||
            local == null || local.isBlank() || data == null || data.isBefore(dataAtual) ||
            horaInicio < horaAtual.getHour() || horaInicio >= horaFim)
            return "Dados inválidos para criação de evento!";
        /*
        Msg_Edita_Evento evento =
                new Msg_Edita_Evento(new Evento("eu", eventoNomeAntigo,
                                           local, data, horaInicio, horaFim), novoNome);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(evento);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return "Evento editado com sucesso!";
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }*/
        return "Evento não editado!";
    }

    public String eliminarEvento(String eventoNome) {
        if(eventoNome == null || eventoNome.isBlank())
            return "Evento inexistente...";
        /*
        Msg_String evento =
                new Msg_String(eventoNome, Message_types.ELIMINAR_EVENTO);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(evento);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return "Evento eliminado com sucesso!";
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }*/
        return "Evento não eliminado!";
    }

    public String eliminaInserePresencas_Eventos(Message_types tipo, String evento, String emailsP) {
        if(evento == null || evento.isBlank() || emailsP == null || emailsP.length() == 0 || emailsP.isBlank())
            return "Não foram inseridos emails!";

        ArrayList<String> emails = new ArrayList<>();
        for (String email : emailsP.trim().split(" ")) {
            if (!verificaFormato(email))
                emails.add(email);
        }
        /*
        Msg_EliminaInsere_Presencas interacao =
                new Msg_EliminaInsere_Presencas(tipo, evento, emails.toArray(new String[0]));

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(interacao);
            oout.flush();

            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return tipo == Message_types.ELIMINA_PRES ? "Presenças eliminadas com sucesso!" : "Presenças inseridas com sucesso!";
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }*/
        return "Presenças não inseridas!";
    }

    public String gerarCodPresenca(String evento) {
        if(evento == null || evento.isBlank())
            return "Evento inexistente...";
        /*
        Msg_String geraCod =
                new Msg_String(evento, Message_types.GERAR_COD);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(geraCod);
            oout.flush();

            Msg_String codigo = (Msg_String) oin.readObject();

            if (codigo.getTipo() == Message_types.VALIDO) {
                return codigo.getConteudo();
            }
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }*/
        return "Erro";
    }

    public Utilizador[] consultaPresencasEvento(String evento) {
        if(evento != null && !evento.isBlank()) {
           /*
            Msg_String consulta =
                    new Msg_String(evento, Message_types.CONSULTA_PRES_EVENT);

            try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
                oout.writeObject(consulta);
                oout.flush();

                Msg_ListaRegistos lista = (Msg_ListaRegistos) oin.readObject();

                if (lista.getTipo() == Message_types.VALIDO)
                    return lista.getLista();
            } catch (IOException | ClassNotFoundException ignored) {
                setErro();
            }*/
        }
        return new Utilizador[]{};
    }

    public Evento[] consultaEventosDeUmUtilizador(String utilizador) {
        if (!verificaFormato(utilizador)) {
            /*
            Msg_String consulta =
                    new Msg_String(utilizador, Message_types.CONSULTA_EVENTOS);

            try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
                oout.writeObject(consulta);
                oout.flush();

                Msg_ListaEventos lista = (Msg_ListaEventos) oin.readObject();

                if (lista.getTipo() == Message_types.VALIDO)
                    return lista.getLista();
            } catch (IOException | ClassNotFoundException e) {
                setErro();
            }*/
        }
        return new Evento[]{};
    }
}