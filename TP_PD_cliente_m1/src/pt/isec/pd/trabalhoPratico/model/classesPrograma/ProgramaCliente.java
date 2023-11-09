package pt.isec.pd.trabalhoPratico.model.classesPrograma;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

///////////////////////////////////// ATUALIZACAO ASSINCRONA ///////////////////////
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
                        ProgramaCliente.atualizacao.setValue(ProgramaCliente.atualizacao.getValue() + 1);
            } catch (IOException | ClassNotFoundException ignored) {
                synchronized (ProgramaCliente.erro) {
                    ProgramaCliente.setErro();
                }
            }
        } while (Thread.currentThread().isAlive());
    }
}

///////////////////////////////////// PROGRAMA CLIENTE ///////////////////////
public class ProgramaCliente {
    protected static SimpleIntegerProperty atualizacao = new SimpleIntegerProperty(0);
    private final SimpleStringProperty logado = new SimpleStringProperty("ENTRADA");
    protected static final SimpleIntegerProperty erro = new SimpleIntegerProperty(0);
    private Socket socket;

    public ProgramaCliente() {
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
    public String getLogado() {
        return logado.getValue();
    }
    //ver se é email:
    public boolean verificaFormato(String email) {
        if (email == null || email.isBlank())
            return true;
        if (email.indexOf('@') <= 0 || !(email.indexOf('@') <= email.indexOf('.') - 2))
            return true;
        return email.split("@|\\.").length != 3;
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

            } catch (IllegalArgumentException e) {
                pontoSituacao = new Pair<>(false, "Introduziu um porto inválido.");
            } catch (NullPointerException e) {
                pontoSituacao = new Pair<>(false, "Introduziu um endereço inválido.");
            } catch (IOException e) {
                this.socket = new Socket();
                pontoSituacao = new Pair<>(true, "Ocorreu uma exceção I/O na criação do socket.");
                pontoSituacao = new Pair<>(false, "Ocorreu uma exceção I/O na criação do socket.");
            }*/
        } else
            pontoSituacao = new Pair<>(false, "Não foram introduzidos dados suficientes como argumento.");
        return pontoSituacao;
    }

    public void login(String email, String password) {
        if (password == null || password.isBlank() || verificaFormato(email))
            return;
/*
        Login dadosLogin = new Login(email, password);
        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream()))
        {

            oout.writeObject(dadosLogin);
            oout.flush();

            Geral validacao = (Geral) oin.readObject();

                switch (validacao.getTipo()){
                    case ADMINISTRADOR -> {
                        logado.set("ADMINISTRADOR");
                        try (Socket socket = new Socket(this.socket.getInetAddress(), this.socket.getPort())) {
                            new Thread(new AtualizacaoAsync(socket)).start();
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
        logado.set("ADMINISTRADOR");
    }


    public void logout() {
        /*Geral logout = new Geral(Message_types.LOGOUT);
        try (ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {

            oout.writeObject(logout);
            oout.flush();
            logado = "ENTRADA";
        } catch (IOException e) {
            setErro();
        }*/
        logado.set("ENTRADA");
    }

    public String[] obterListaConsulta(Message_types tipo, String nome, String local, LocalDate limData1, LocalDate limData2, int horaInicio, int horaFim) {
        return new String[]{"ola; aqui; 2021-05-05; 18; 19", "ola; aqui; 2021-05-05; 18; 19"};
        /*
        ConsultaFiltros consultaPresencas = new ConsultaFiltros(tipo, nome, local, limData1, limData2, horaInicio, horaFim);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(consultaPresencas);
            oout.flush();

            EliminaPresencas_InserePresencas lista = (EliminaPresencas_InserePresencas) oin.readObject();
            if(lista.getTipo() == Message_types.VALIDO)
                return  lista.getLista().length == 0 ? new String[]{"Sem registos"} : lista.getLista();
        } catch (IOException | ClassNotFoundException e) {
            setErro();
            return new String[]{"Erro na comunicação com o servidor"};
        }
        return new String[]{"Erro"};*/
    }

    /////////////////////////UTILIZADOR:
    public void registar(String nome, String email, String numIdentificacao, String password, String confPass) {

        if (nome == null || password == null || !password.equals(confPass) || verificaFormato(email) || numIdentificacao == null)
            return;
        long numID;
        try {
            numID = Integer.parseInt(numIdentificacao);//?? como é que ponho para long?
        } catch (NumberFormatException e) {
            return;
        }
        RegistoEdicao_Cliente dadosRegisto = new RegistoEdicao_Cliente(nome, email, password, numID, Message_types.REGISTO);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(dadosRegisto);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO) {
                logado.set("UTILIZADOR");
            }
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }
    }

    public boolean registarPresenca(String codigoEvento) {
        if (codigoEvento == null || codigoEvento.isBlank())
            return false;

        Consulta_Elimina_GeraCod_SubmeteCod_Evento registoPresenca = new Consulta_Elimina_GeraCod_SubmeteCod_Evento(codigoEvento, Message_types.SUBMICAO_COD);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(registoPresenca);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return true;
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
        }
        return false;
    }

    public boolean obterCSV_Presencas(String nome) {

        return true;
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

        RegistoEdicao_Cliente dadosRegisto = new RegistoEdicao_Cliente(nome, null, password, numID, Message_types.EDITAR_REGISTO);

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

        Cria_Evento evento =
                new Cria_Evento(new Evento("eu",nome, local, data, horaInicio, horaFim));

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
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
    public String editar_Evento(String eventoInfo, String novoNome, String local, LocalDate data, int horaInicio, int horaFim) {

        LocalDate dataAtual = LocalDate.now();
        LocalTime horaAtual = LocalTime.now();

        if ((data != null && data.isBefore(dataAtual)) || (horaInicio < horaAtual.getHour() || horaInicio >= horaFim))
            return "Evento não editado!";

        Edita_Evento evento =
                new Edita_Evento(new Evento("eu", eventoInfo.substring(0, eventoInfo.indexOf(';')).trim(),
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
        }
        return "Evento não editado!";
    }

    public String eliminarEvento(String eventoInfo) {
        Consulta_Elimina_GeraCod_SubmeteCod_Evento evento =
                new Consulta_Elimina_GeraCod_SubmeteCod_Evento(eventoInfo.substring(0, eventoInfo.indexOf(';')).trim(), Message_types.ELIMINAR_EVENTO);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
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

    public String eliminaInserePresencas_Eventos(Message_types tipo, String eventoInfo, String filtros) {
        ArrayList<String> emails = new ArrayList<>();
        for (String email : filtros.trim().split(" ")) {
            if (!verificaFormato(email))
                emails.add(email);
        }

        EliminaPresencas_InserePresencas interacao =
                new EliminaPresencas_InserePresencas(tipo, eventoInfo.substring(0, eventoInfo.indexOf(';')).trim(), emails.toArray(new String[0]));

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
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

    public String gerarCodPresenca(String eventoInfo) {
        Consulta_Elimina_GeraCod_SubmeteCod_Evento geraCod =
                new Consulta_Elimina_GeraCod_SubmeteCod_Evento(eventoInfo.substring(0, eventoInfo.indexOf(';')).trim(), Message_types.GERAR_COD);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(geraCod);
            oout.flush();

            Consulta_Elimina_GeraCod_SubmeteCod_Evento codigo = (Consulta_Elimina_GeraCod_SubmeteCod_Evento) oin.readObject();

            if (codigo.getTipo() == Message_types.VALIDO) {
                return codigo.getNome();
            }
        } catch (IOException | ClassNotFoundException ignored) {
            setErro();
            return "Erro de comunicação com o servidor";
        }
        return "Erro";
    }

    public String[] consultaPresencasEvento(String eventoInfo) {
        if(eventoInfo != null) {
            Consulta_Elimina_GeraCod_SubmeteCod_Evento consulta =
                    new Consulta_Elimina_GeraCod_SubmeteCod_Evento(eventoInfo.substring(0, eventoInfo.indexOf(';')).trim(), Message_types.CONSULTA_PRES_EVENT);

            try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
                oout.writeObject(consulta);
                oout.flush();

                EliminaPresencas_InserePresencas lista = (EliminaPresencas_InserePresencas) oin.readObject();

                if (lista.getTipo() == Message_types.VALIDO)
                    return lista.getLista().length == 0 ? new String[]{"Sem registos"} : lista.getLista();
            } catch (IOException | ClassNotFoundException ignored) {
                setErro();
                return new String[]{"Erro na comunicação com o servidor"};
            }
        }
        return new String[]{"Erro"};
    }

    public String[] consultaEventosDeUmUtilizador(String utilizador) {
        if (!verificaFormato(utilizador)) {
            Consulta_Elimina_GeraCod_SubmeteCod_Evento consulta =
                    new Consulta_Elimina_GeraCod_SubmeteCod_Evento(utilizador, Message_types.CONSULTA_EVENTOS);

            try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
                oout.writeObject(consulta);
                oout.flush();

                EliminaPresencas_InserePresencas lista = (EliminaPresencas_InserePresencas) oin.readObject();

                if (lista.getTipo() == Message_types.VALIDO)
                    return  lista.getLista().length == 0 ? new String[]{"Sem registos"} : lista.getLista();
            } catch (IOException | ClassNotFoundException e) {
                setErro();
                return new String[]{"Erro na comunicação com o servidor"};
            }
        }
        return new String[]{"Erro"};
    }

    public boolean obterCSV_Admin(String nome) {
        return false;
    }
}
/*
        listaEventos = new ArrayList<>();
        listaPresencas = new ArrayList<>();
        listaResultados = new ArrayList<>();

        LocalDate data = LocalDate.now();

        //teste
        Evento e = new Evento("eu", "ola", "aqui", data, 18, 19);
        listaEventos.add(e);
        listaPresencas.add(new Presenca(e, new Utilizador("isa", "isa@isec.pt", "11111")));
        listaResultados.add(e.toString());*/