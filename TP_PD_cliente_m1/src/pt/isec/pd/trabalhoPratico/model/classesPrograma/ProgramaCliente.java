package pt.isec.pd.trabalhoPratico.model.classesPrograma;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;
import pt.isec.pd.trabalhoPratico.model.recordDados.Utilizador;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

///////////////////////////////////// PROGRAMA CLIENTE ///////////////////////
public class ProgramaCliente {
    // TEMPO
    private static final int TEMPO_MAXIMO = 10; // 10 segundos
    private final Timer temporizador = new Timer();
    private int contagem = 0;
    private TimerTask tarefa;
    private boolean terminou = false;

    // EVENTOS
    private static final SimpleIntegerProperty atualizacao = new SimpleIntegerProperty(0);
    private static final SimpleIntegerProperty erro = new SimpleIntegerProperty(0);
    private static final SimpleStringProperty logado = new SimpleStringProperty("");

    // COMUNICAÇÃO
    private Socket socket;
    private ObjectOutputStream oout;
    private ObjectInputStream oin;

//-------------------- ATUALIZACAO ASSINCRONA -----------------
static class AtualizacaoAsync implements Runnable {
        private final Socket socketAsync;

        public AtualizacaoAsync(Socket socket) {
            this.socketAsync = socket;
        }

        @Override
        public void run() {
            do {
                try (ObjectInputStream oin = new ObjectInputStream(socketAsync.getInputStream())) {
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

//-------------------- VERIFICA LIGACAO -----------------
    class VerificaLigacao extends TimerTask {
    @Override
    public void run() {
            contagem++;
        System.out.println(contagem);
            if (contagem == TEMPO_MAXIMO && logado.getValue().equals("ENTRADA")) {
                setLogado("EXCEDEU_TEMPO");
                termina();
                System.out.println("tempo excedido task");
            }
        }
    }

//-------------------------------------------------------------

    public ProgramaCliente() {
        logado.addListener(observable -> verificacaoLigacao());
        logado.set("ENTRADA");
    }
    private void termina() {
        terminou = true;
        temporizador.cancel();
        tarefa.cancel();
        logado.removeListener(observable -> verificacaoLigacao());
        try {
            if(socket != null)
                socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void verificacaoLigacao() {
        switch (logado.getValue()) {
            case "ENTRADA" -> {
                tarefa = new VerificaLigacao();
                temporizador.schedule(tarefa, 0, 1000);
            }
            case "SAIR" -> {
                if(!terminou)
                    termina();
                System.out.println("uiTermina");
            }
            case "ADMINISTRADOR", "UTILIZADOR" -> {
                tarefa.cancel();
                contagem = 0;
            }
        }
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
    public synchronized void setLogado(String valor) {
        logado.set(valor);
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

    ///////////////////////////////////////FUNCIONALIDADES:
    /////////////////////////COMUNS:
    public Pair<Boolean, String> criaSocket(List<String> list) {
        Pair<Boolean, String> pontoSituacao = new Pair<>(false, "Erro na criação do socket");
        if (list.size() == 2) {
            try
            {
                socket = new Socket(InetAddress.getByName(list.get(0)), Integer.parseInt(list.get(1)));
                if(socket.isConnected()) {
                    oin = new ObjectInputStream(socket.getInputStream());
                    oout = new ObjectOutputStream(socket.getOutputStream());
                    pontoSituacao = new Pair<>(true, "Conexão bem sucedida");
                }
            } catch (IllegalArgumentException e) {
                pontoSituacao = new Pair<>(false, "Introduziu um porto inválido.");
            } catch (NullPointerException e) {
                pontoSituacao = new Pair<>(false, "Introduziu um endereço inválido.");
            } catch (IOException e) {
                pontoSituacao = new Pair<>(false, "Ocorreu uma exceção I/O na criação do socket.");
            }
        } else
            pontoSituacao = new Pair<>(false, "Não foram introduzidos dados suficientes como argumento.");
        return pontoSituacao;
    }

    public void login(String email, String password) {

        if (password == null || password.isBlank() || verificaFormato(email))
            return;
/*
        Msg_Login dadosLogin = new Msg_Login(email, password);
        try
        {
            oout.writeObject(dadosLogin);
            oout.flush();

            Msg_String validacao = (Msg_String) oin.readObject();

                if(validacao.getTipo() == Message_types.ADMINISTRADOR || validacao.getTipo() == Message_types.UTILIZADOR) {
                    logado.set(validacao.getTipo() == Message_types.ADMINISTRADOR ? "ADMINISTRADOR" : "UTILIZADOR");
                    try (Socket socketThread = new Socket(validacao.getConteudo(), this.socket.getPort())) {
                        new Thread(new AtualizacaoAsync(socketThread)).start();
                    } catch (Exception e) {
                        setErro();
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                setErro();
            }
*/
        logado.set("ADMINISTRADOR");
    }

    public void logout() {
        if(logado.getValue().equals("ADMINISTRADOR") || logado.getValue().equals("UTILIZADOR")) {
            /*
            Geral logout = new Geral(Message_types.LOGOUT);
            try {
                oout.writeObject(logout);
                oout.flush();
                logado.set("ENTRADA");
            } catch (IOException e) {
                setErro();
            }*/
            logado.set("ENTRADA");
        }
    }

    public Evento[] obterListaConsultaEventos(Message_types tipo, String nome, String local, LocalDate limData1, LocalDate limData2, int horaInicio, int horaFim) {
        return new Evento[]{new Evento("ola", "HelloMate", LocalDate.now(), 11, 12)};
/*
        if(nome != null && !nome.isBlank() && local != null && !local.isBlank() && limData1 != null && limData2 != null && horaInicio >= horaFim) {
            //return new Evento[]{new Evento("ola", "HelloMate", "ISEC", LocalDate.now(), 11, 12)};

            Msg_ConsultaComFiltros consultaEventos = new Msg_ConsultaComFiltros(tipo, nome, local, limData1, limData2, horaInicio, horaFim);

            try {
                oout.writeObject(consultaEventos);
                oout.flush();

                Msg_ListaEventos lista = (Msg_ListaEventos) oin.readObject();
                if (lista.getTipo() == Message_types.VALIDO)
                    return lista.getLista();
            } catch (IOException | ClassNotFoundException e) {
                setErro();
            }
        }
        return new Evento[]{};*/
    }

    public String obterCSV(String caminhoCSV, String nomeFicheiro, Message_types tipoCSV) {
        if(caminhoCSV == null || caminhoCSV.isBlank() || nomeFicheiro == null || nomeFicheiro.isBlank())
            return "É necessário inserir um caminho para guardar o ficheiro!";

        String localCSVCaminho;
        File destinoCSV = new File(caminhoCSV);
        byte []fileChunk = new byte[4000];
        int nbytes;

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

        Msg_String csv = new Msg_String(nomeFicheiro, tipoCSV);

        try (FileOutputStream localFileOutputStream = new FileOutputStream(localCSVCaminho)) {

            oout.writeObject(csv);
            oout.flush();

            InputStream inStream = socket.getInputStream();

            while((nbytes = inStream.read(fileChunk)) > 0){
                localFileOutputStream.write(fileChunk, 0, nbytes);
            }
            return "CSV gerado com sucesso guardado em: " + localCSVCaminho;
        }catch (IOException e){
            setErro();
        }
        return "Erro ao gerar CSV";
    }

    /////////////////////////UTILIZADOR:
    public Pair<String, Boolean> registarConta(String nome, String email, String numIdentificacao, String password, String confPass) {

        if (nome == null || nome.isBlank() || password == null || password.isBlank() || confPass == null || confPass.isBlank() || !password.equals(confPass) || verificaFormato(email) || numIdentificacao == null || numIdentificacao.isBlank())
            return new Pair<>("Os dados inseriados são inválidos :(", false);

        long numID;
        try {
            numID = Integer.parseInt(numIdentificacao);//?? como é que ponho para long?
        } catch (NumberFormatException e) {
            return new Pair<>("Insira um número de identificação válido!", false);
        }

        Mgs_RegistarEditar_Conta dadosRegisto = new Mgs_RegistarEditar_Conta(nome, email, password, numID, Message_types.REGISTO);

        try {
            oout.writeObject(dadosRegisto);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO) {
                logado.set("UTILIZADOR");
                return new Pair<>("", true);
            }
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }
        return new Pair<>("Erro ao registar conta!", false);
    }

    public boolean registarPresenca(String evento, String codigoEvento) {
        if (evento == null || evento.isBlank() || codigoEvento == null || codigoEvento.isBlank())
            return false;

        try{
            int codigo = Integer.parseInt(codigoEvento);
            Msg_String_Int registoPresenca = new Msg_String_Int(evento, codigo, Message_types.SUBMICAO_COD);

            try {
                oout.writeObject(registoPresenca);
                oout.flush();
                Geral validacao = (Geral) oin.readObject();

                if (validacao.getTipo() == Message_types.VALIDO)
                    return true;
            } catch (IOException | ClassNotFoundException ignored) {
                setErro();
            }
        } catch (NumberFormatException ignored) {}

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
        }
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

        Msg_Cria_Evento evento =
                new Msg_Cria_Evento(new Evento(nome, local, data, horaInicio, horaFim));

        try {
            oout.writeObject(evento);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return "Evento criado com sucesso!";
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }
        return "Erro";
    }

    public String editar_Evento(String eventoNomeAntigo, String novoNome, String local, LocalDate data, int horaInicio, int horaFim) {

        LocalDate dataAtual = LocalDate.now();
        LocalTime horaAtual = LocalTime.now();

        if (eventoNomeAntigo == null || eventoNomeAntigo.isBlank() || novoNome == null || novoNome.isBlank() ||
            local == null || local.isBlank() || data == null || data.isBefore(dataAtual) ||
            horaInicio < horaAtual.getHour() || horaInicio >= horaFim)
            return "Dados inválidos para criação de evento!";

        Msg_Edita_Evento evento =
                new Msg_Edita_Evento(new Evento( eventoNomeAntigo, local, data, horaInicio, horaFim), novoNome);

        try {
            oout.writeObject(evento);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return "Evento editado com sucesso!";
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }
        return "Evento não editado!";
    }

    public String eliminarEvento(String eventoNome) {
        if(eventoNome == null || eventoNome.isBlank())
            return "Evento inexistente...";

        Msg_String evento =
                new Msg_String(eventoNome, Message_types.ELIMINAR_EVENTO);

        try {
            oout.writeObject(evento);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return "Evento eliminado com sucesso!";
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }
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

        Msg_EliminaInsere_Presencas interacao =
                new Msg_EliminaInsere_Presencas(tipo, evento, emails.toArray(new String[0]));

        try {
            oout.writeObject(interacao);
            oout.flush();

            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return tipo == Message_types.ELIMINA_PRES ? "Presenças eliminadas com sucesso!" : "Presenças inseridas com sucesso!";
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }
        return "Presenças não inseridas!";
    }

    public String gerarCodPresenca(String evento, String tempoValido) {
        if(evento == null || evento.isBlank())
            return "Evento inexistente...";
        if(tempoValido == null || tempoValido.isBlank())
            return "Tempo inválido!";

        int tempo;
        try{
            tempo = Integer.parseInt(tempoValido);
            if(tempo < 0)
                return "O tempo não pode ser negativo!";
        } catch (NumberFormatException e) {
            return "Tempo deve ser numérico!";
        }

        Msg_String_Int geraCod =
                new Msg_String_Int(evento, tempo, Message_types.GERAR_COD);

        try {
            oout.writeObject(geraCod);
            oout.flush();

            Msg_String codigo = (Msg_String) oin.readObject();

            if (codigo.getTipo() == Message_types.VALIDO) {
                return codigo.getConteudo();
            }
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }
        return "Erro";
    }

    public Utilizador[] consultaPresencasEvento(String evento) {
        if(evento != null && !evento.isBlank()) {

            Msg_String consulta =
                    new Msg_String(evento, Message_types.CONSULTA_PRES_EVENT);

            try {
                oout.writeObject(consulta);
                oout.flush();

                Msg_ListaRegistos lista = (Msg_ListaRegistos) oin.readObject();

                if (lista.getTipo() == Message_types.VALIDO)
                    return lista.getLista();
            } catch (IOException | ClassNotFoundException ignored) {
                setErro();
            }
        }
        return new Utilizador[]{};
    }

    public Evento[] consultaEventosDeUmUtilizador(String utilizador) {
        if (!verificaFormato(utilizador)) {

            Msg_String consulta =
                    new Msg_String(utilizador, Message_types.CONSULTA_EVENTOS);

            try {
                oout.writeObject(consulta);
                oout.flush();

                Msg_ListaEventos lista = (Msg_ListaEventos) oin.readObject();

                if (lista.getTipo() == Message_types.VALIDO)
                    return lista.getLista();
            } catch (IOException | ClassNotFoundException e) {
                setErro();
            }
        }
        return new Evento[]{};
    }
}