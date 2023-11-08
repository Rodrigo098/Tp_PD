package pt.isec.pd.trabalhoPratico.model.programs;

import javafx.util.Pair;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;
import pt.isec.pd.trabalhoPratico.model.classesDados.Presenca;
import pt.isec.pd.trabalhoPratico.model.classesDados.Utilizador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

///////////////////////////////////// PROGRAMA CLIENTE ///////////////////////
public class ProgramaCliente {
    private Socket socket;
    private ArrayList<Evento> listaEventos;
    private ArrayList<Presenca> listaPresencas;
    private ArrayList<String> listaResultados;

    public ProgramaCliente() {
        listaEventos = new ArrayList<>();
        listaPresencas = new ArrayList<>();
        listaResultados = new ArrayList<>();

        LocalDate data = LocalDate.now();

        //teste
        Evento e = new Evento("eu", "ola", "aqui", data, 18, 19);
        listaEventos.add(e);
        listaPresencas.add(new Presenca(e, new Utilizador("isa", "isa@isec.pt", "11111")));
        listaResultados.add(e.toString());
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
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())){

            oout.writeObject(dadosLogin);
            oout.flush();

            Geral validacao = (Geral) oin.readObject();

                switch (validacao.getTipo()){
                    case ADMINISTRADOR -> {
                        MainCliente.clienteSBP.set("ADMINISTRADOR");
                        try (Socket socket = new Socket(this.socket.getInetAddress(), this.socket.getPort())) {
                            new Thread(new AtualizacaoAsync(socket)).start();
                        } catch (Exception e) {
                            MainCliente.menuSBP.set("ERRO");
                        }
                    }
                    case UTILIZADOR ->
                        MainCliente.clienteSBP.set("UTILIZADOR");
                    case INVALIDO, ERRO -> {
                        MainCliente.messageBox.set(true);
                        return;
                    }
                }
                MainCliente.menuSBP.set("CONTA");
            }
        }catch (IOException | ClassNotFoundException ignored) {
            MainCliente.messageBox.set(true);
        }*/
        MainCliente.clienteSBP.set("ADMINISTRADOR");
        MainCliente.menuSBP.set("CONTA");
    }

    public void logout() {
        Geral logout = new Geral(Message_types.LOGOUT);

        try (ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {

            oout.writeObject(logout);
            oout.flush();
            MainCliente.menuSBP.set("MENU");
            MainCliente.clienteSBP.set("INDEFINIDO");
        } catch (IOException e) {
            MainCliente.messageBox.set(true);
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public String[] obterListaConsulta(Message_types tipo, String nome, String local, LocalDate limData1, LocalDate limData2, int horaInicio, int horaFim) {

        ConsultaFiltros consultaPresencas = new ConsultaFiltros(tipo, nome, local, limData1, limData2, horaInicio, horaFim);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(consultaPresencas);
            oout.flush();

            EliminaPresencas_InserePresencas lista = (EliminaPresencas_InserePresencas) oin.readObject();
            if(lista.getTipo() == Message_types.VALIDO)
                return lista.getLista();
        } catch (IOException | ClassNotFoundException e) {
            MainCliente.messageBox.set(true);
            return new String[]{"Erro na comunicação com o servidor"};
        }
        return new String[]{"Erro"};
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
                MainCliente.clienteSBP.set("UTILIZADOR");
                MainCliente.menuSBP.set("CONTA");
            }
        } catch (IOException | ClassNotFoundException ignored) {
            MainCliente.messageBox.set(true);
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
            if (validacao.getTipo() == Message_types.ERRO) {
                MainCliente.menuSBP.set("ERRO");
            }
        } catch (IOException | ClassNotFoundException ignored) {
            MainCliente.messageBox.set(true);
        }
        return false;
    }

    public boolean obterCSV_Presencas(String nome) {

        return true;
    }

    public boolean editarRegisto(String nome, String numIdentificacao, String password, String confPass) {

        if (nome == null || nome.isBlank() || password == null || password.isBlank() || !password.equals(confPass) || numIdentificacao == null || numIdentificacao.isBlank())
            return false;

        long numID;
        try {
            numID = Integer.parseInt(numIdentificacao);//?? como é que ponho para long?
        } catch (NumberFormatException e) {
            return false;
        }

        RegistoEdicao_Cliente dadosRegisto = new RegistoEdicao_Cliente(nome, null, password, numID, Message_types.EDITAR_REGISTO);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(dadosRegisto);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return true;
        } catch (ClassNotFoundException | IOException ignored) {
            MainCliente.messageBox.set(true);
        }
        return false;
    }

    /////////////////////////ADMINISTRADOR:
    //CRIAR OU EDITAR EVENTO, O ÚLTIMO PARÂMETRO É PARA SABER SE É PARA CRIAR OU EDITAR
    public boolean criarEditar_Evento(String nome, String local, LocalDate data, int horaInicio, int horaFim, Message_types tipo) {
        if (nome == null || nome.isBlank() || local == null || local.isBlank() || data == null || horaInicio >= horaFim)
            return false;

        LocalDate dataAtual = LocalDate.now();
        if (data.isBefore(dataAtual))
            return false;

        LocalTime horaAtual = LocalTime.now();
        if (horaInicio < horaAtual.getHour())
            return false;

        CriaEdita_evento evento =
                new CriaEdita_evento(new Evento("eu", tipo == Message_types.CRIA_EVENTO ?
                                           nome : nome.substring(0, nome.indexOf(';')).trim(),
                                           local, data, horaInicio, horaFim), tipo);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(evento);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return true;
        } catch (IOException | ClassNotFoundException ignored) {
            MainCliente.messageBox.set(true);
        }
        return false;
    }

    public boolean eliminarEvento(String eventoInfo) {
        Consulta_Elimina_GeraCod_SubmeteCod_Evento evento =
                new Consulta_Elimina_GeraCod_SubmeteCod_Evento(eventoInfo.substring(0, eventoInfo.indexOf(';')).trim(), Message_types.ELIMINAR_EVENTO);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(evento);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao.getTipo() == Message_types.VALIDO)
                return true;
        } catch (IOException | ClassNotFoundException ignored) {
            MainCliente.messageBox.set(true);
        }
        return false;
    }

    public boolean eliminaInserePresencas_Eventos(Message_types tipo, String eventoInfo, String filtros) {
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
                return true;
        } catch (IOException | ClassNotFoundException ignored) {
            MainCliente.messageBox.set(true);
        }
        return false;
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
            MainCliente.messageBox.set(true);
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
                    return lista.getLista();
            } catch (IOException | ClassNotFoundException ignored) {
                MainCliente.messageBox.set(true);
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
                    return lista.getLista();
            } catch (IOException | ClassNotFoundException e) {
                MainCliente.messageBox.set(true);
                return new String[]{"Erro na comunicação com o servidor"};
            }
        }
        return new String[]{"Erro"};
    }

    public boolean obterCSV_Admin(String nome) {
        return false;
    }
}


    /*public String[] obterListaEventos() {
        String [] eventos = new String[listaEventos.size()];
        for(String evento : listaEventos){
            eventos[listaEventos.indexOf(evento)] = evento;//depois põe-se toString
        }
        return eventos;
    }*/

    /*public String obterEvento(int eventoSelecionado) {
        return listaEventos.get(eventoSelecionado);//depois põe-se toString
    }*/
/*
    public String[] consultaPresencasEvento(String nomeEvento){
        //é um botão que vai buscar o nome do evento selecionado logo não é preciso validação
        Consulta_Elimina_GeraCod_SubmeteCod_Evento consulta = new Consulta_Elimina_GeraCod_SubmeteCod_Evento(nomeEvento, Message_types.CONSULTA_PRES_EVENT);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(consulta);
            oout.flush();

            EliminaPresencas_InserePresencas lista = (EliminaPresencas_InserePresencas) oin.readObject();

            if (lista.getTipo() == Message_types.ERRO) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }
            return lista.getLista();
        }catch (IOException | ClassNotFoundException ignored) {
            return new String[]{"Erro"};
        }
    }

*/