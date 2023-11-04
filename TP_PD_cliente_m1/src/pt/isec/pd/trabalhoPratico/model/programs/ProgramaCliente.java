package pt.isec.pd.trabalhoPratico.model.programs;

import javafx.util.Pair;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;
import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;
import pt.isec.pd.trabalhoPratico.model.classesDados.Registo;
import pt.isec.pd.trabalhoPratico.model.classesDados.Utilizador;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


////////////////////////////////// ATUALIZAÇÃO ASSÍNCRONA ////////////////////////////////////////
class AtualizacaoAsync implements Runnable {
    private final ArrayList<Evento> listaEventos;
    private final ArrayList<Registo> listaRegistos;
    private final Socket socket;
    public AtualizacaoAsync(Socket socket, ArrayList<Evento> lista, ArrayList<Registo> listaRegisto) {
        this.socket = socket;
        this.listaEventos = lista;
        this.listaRegistos = listaRegisto;
    }
    @Override
    public void run() {

        do{
            try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream()))
            {
                Object novaLista = oin.readObject();
                if(novaLista instanceof RecebeListaEventos lista){
                    synchronized (listaEventos) {
                        listaEventos.clear();
                        listaEventos.addAll(Arrays.asList(lista.getLista()));
                    }
                } else if(novaLista instanceof RecebeListaRegistos lista)
                {
                    synchronized (listaRegistos) {
                        listaEventos.clear();
                        listaRegistos.addAll(Arrays.asList(lista.getLista()));
                    }
                } else //servidor disse adeus
                    break;
            } catch (IOException | ClassNotFoundException ignored) {
            }
        }while(Thread.currentThread().isAlive());
    }
}

///////////////////////////////////// PROGRAMA CLIENTE ///////////////////////
public class ProgramaCliente {
    private Socket socket;
    private final ArrayList<Evento> listaEventos;
    private final ArrayList<Registo> listaRegistos;
    private final ArrayList<String> listaResultados;

    public ProgramaCliente(){
        listaEventos = new ArrayList<>();
        listaRegistos = new ArrayList<>();
        listaResultados = new ArrayList<>();

        LocalDate data = LocalDate.now();

        //teste
        Evento e = new Evento("ola", "aqui", data, 18, 19);
        listaEventos.add(e);
        listaRegistos.add(new Registo(e, new Utilizador("isa", "isa@isec.pt", "11111")));
        listaResultados.add(e.toString());
    }

    //ver se é email:
    public boolean verificaFormato(String email){
        if(email == null || email.isBlank())
            return true;
        if(email.indexOf('@') <= 0 || !(email.indexOf('@') <= email.indexOf('.') - 2))
            return true;
        return email.split("@|\\.").length != 3;
    }


    ///////////////////////////////////////FUNCIONALIDADES:
    /////////////////////////COMUNS:
    public Pair<Boolean, String> criaSocket(List<String> list) {
        Pair<Boolean, String> pontoSituacao;

        if(list.size() == 2) {
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
        }
        else
            pontoSituacao = new Pair<>(false, "Não foram introduzidos dados suficientes como argumento.");
        return pontoSituacao;
    }

    public void login(String email, String password) {
        if(password == null || password.isBlank() || verificaFormato(email))
            return;
/*
        Login dadosLogin = new Login(email, password);

        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())){

            oout.writeObject(dadosLogin);
            oout.flush();

            Geral validacao = (Geral) oin.readObject();

            if(validacao == null){
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }else{
                switch (validacao.getTipo()){
                    case ADMINISTRADOR -> {
                        MainCliente.clienteSBP.set("ADMINISTRADOR");
                        try (Socket socket = new Socket(this.socket.getInetAddress(), this.socket.getPort())) {
                            new Thread(new AtualizacaoAsync(socket, listaEventos, listaRegistos)).start();
                        } catch (Exception e) {
                            MainCliente.menuSBP.set("ERRO");
                            socket.close();
                        }
                    }
                    case UTILIZADOR ->
                        MainCliente.clienteSBP.set("UTILIZADOR");
                    case INVALIDO, ERRO -> {
                        MainCliente.menuSBP.set("ERRO");
                        socket.close();
                        return;
                    }
                }
                MainCliente.menuSBP.set("CONTA");
            }
        }catch (IOException | ClassNotFoundException ignored) {
            MainCliente.menuSBP.set("ERRO");
        }*/
        MainCliente.clienteSBP.set("UTILIZADOR");
        MainCliente.menuSBP.set("CONTA");
    }
    public void logout() {
        Geral logout = new Geral(Message_types.LOGOUT);

        try(ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())){

            oout.writeObject(logout);
            oout.flush();
            MainCliente.menuSBP.set("MENU");
            MainCliente.clienteSBP.set("INDEFINIDO");
        }catch (IOException e) {
            MainCliente.menuSBP.set("ERRO");
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    /////////////////////////UTILIZADOR:
    public void registar(String nome, String email, String numIdentificacao, String password, String confPass) {
        if(nome == null || password == null || !password.equals(confPass) || verificaFormato(email) || numIdentificacao == null)
                return;
        long numID;
        try {
            numID = Integer.parseInt(numIdentificacao);//?? como é que ponho para long?
        } catch (NumberFormatException e) {
            return;
        }

        RegistoEdicao_Cliente dadosRegisto = new RegistoEdicao_Cliente(nome, email, password, numID, Message_types.REGISTO);

        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(dadosRegisto);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao == null) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            } else {
                if (validacao.getTipo() == Message_types.VALIDO) {
                    MainCliente.clienteSBP.set("UTILIZADOR");
                    MainCliente.menuSBP.set("CONTA");
                } else {
                    MainCliente.menuSBP.set("ERRO");
                    socket.close();
                }
            }
        }catch (IOException | ClassNotFoundException ignored) {
        }
    }

    public boolean registarPresenca(String codigoEvento) {
        if(codigoEvento == null || codigoEvento.isBlank())
            return false;

        Consulta_Elimina_GeraCod_SubmeteCod_Evento registoPresenca = new Consulta_Elimina_GeraCod_SubmeteCod_Evento(codigoEvento, Message_types.SUBMICAO_COD);

        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream()))
        {
            oout.writeObject(registoPresenca);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if(validacao.getTipo() == Message_types.VALIDO)
                return true;
            if (validacao.getTipo() == Message_types.ERRO) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return false;
    }

    public String[] consultarPresencasUti() {
        Geral consultaPresencas = new Geral(Message_types.CONSULTA_PRES_UTILIZADOR);

        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream()))
        {
            oout.writeObject(consultaPresencas);
            oout.flush();

            ConsultaEventos_EliminaPresencas_InserePresencas lista = (ConsultaEventos_EliminaPresencas_InserePresencas) oin.readObject();

            return lista.getLista();
        } catch (IOException | ClassNotFoundException e) {
            return new String[]{"Erro na comunicação com o servidor"};
        }
    }

    public boolean obterCSV_Uti() {
        //se há presenças registadas
        //cria ficheiro CSV
        //return true;
        //senão
        //return false;

        return false;
    }

    public boolean editarRegisto(String nome, String numIdentificacao, String password, String confPass) {

        if(nome == null || password == null || !password.equals(confPass) || numIdentificacao == null)
            return false;

        long numID;
        try {
            numID = Integer.parseInt(numIdentificacao);//?? como é que ponho para long?
        } catch (NumberFormatException e) {
            return false;
        }

        RegistoEdicao_Cliente dadosRegisto = new RegistoEdicao_Cliente(nome, null, password, numID, Message_types.EDITAR_REGISTO);

        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(dadosRegisto);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if (validacao == null) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            } else {
                if(validacao.getTipo() == Message_types.VALIDO)
                    return true;
                if (validacao.getTipo() == Message_types.ERRO) {
                    MainCliente.menuSBP.set("ERRO");
                    socket.close();
                }
            }
        }catch (ClassNotFoundException | IOException ignored) {
        }
        return false;
    }

    /////////////////////////ADMINISTRADOR:
    //CRIAR OU EDITAR EVENTO, O ÚLTIMO PARÂMETRO É PARA SABER SE É PARA CRIAR OU EDITAR
    public boolean criarEditar_Evento(String nome, String local, LocalDate data, int horaInicio, int horaFim, Message_types tipo) {
        if(nome == null || local == null || data == null || horaInicio >= horaFim)
            return false;

        LocalDate dataAtual = LocalDate.now();
        if(data.isBefore(dataAtual))
            return false;

        LocalTime horaAtual = LocalTime.now();
        if(horaInicio < horaAtual.getHour())
            return false;

        Cria_evento evento = new Cria_evento(new Evento(nome, local, data, horaInicio, horaFim), tipo);

        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream()))
        {
            oout.writeObject(evento);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if(validacao.getTipo() == Message_types.VALIDO)
                return true;
            if (validacao.getTipo() == Message_types.ERRO) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return false;
    }

    public boolean eliminarEvento(int indiceEvento) {
        Consulta_Elimina_GeraCod_SubmeteCod_Evento evento =
                new Consulta_Elimina_GeraCod_SubmeteCod_Evento(listaEventos.get(indiceEvento).getNome(), Message_types.ELIMINAR_EVENTO);

        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream()))
        {
            oout.writeObject(evento);
            oout.flush();
            Geral validacao = (Geral) oin.readObject();

            if(validacao.getTipo() == Message_types.VALIDO)
                return true;
            if (validacao.getTipo() == Message_types.ERRO) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }
        }catch (IOException | ClassNotFoundException ignored) {
        }
        return false;
    }

    public boolean eliminaInsere_Eventos(Message_types tipo, int indiceEvento, String filtros) {
        //o nome do evento é o primeiro filtro
        ArrayList<String> emails = new ArrayList<>();
        for (String email : filtros.trim().split(" ")) {
            if (!verificaFormato(email))
                emails.add(email);
        }

        ConsultaEventos_EliminaPresencas_InserePresencas interacao =
                new ConsultaEventos_EliminaPresencas_InserePresencas(tipo, listaEventos.get(indiceEvento).getNome(), emails.toArray(new String[0]));

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(interacao);
            oout.flush();

            Geral validacao = (Geral) oin.readObject();

            if(validacao.getTipo() == Message_types.VALIDO)
                return true;
            if (validacao.getTipo() == Message_types.ERRO) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }
        }catch (IOException | ClassNotFoundException ignored) {
        }
        return false;
    }

    public String gerarCodPresenca(int indiceEvento) {
        Consulta_Elimina_GeraCod_SubmeteCod_Evento geraCod =
        new Consulta_Elimina_GeraCod_SubmeteCod_Evento(listaEventos.get(indiceEvento).getNome(), Message_types.GERAR_COD);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(geraCod);
            oout.flush();

            Consulta_Elimina_GeraCod_SubmeteCod_Evento codigo = (Consulta_Elimina_GeraCod_SubmeteCod_Evento) oin.readObject();

            if (codigo.getTipo() == Message_types.ERRO) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }
            return codigo.getNome();
        }catch (IOException | ClassNotFoundException ignored) {
            return "Erro";
        }
    }

    public ArrayList<String> getListaEventos() {
        listaResultados.clear();
        synchronized (listaEventos) {
            for (Evento e : listaEventos) {
                listaResultados.add(e.toString());
            }
        }
        return listaResultados;
    }

    public ArrayList<String> consultaEventosFiltros(String nome, String local, String data, String horaInicio, String horaFim) {
        listaResultados.clear();
        synchronized(listaEventos){
            for(Evento e : listaEventos){
                if(nome.contains(e.getNome()))
                    listaResultados.add(e.toString());
                if(local.contains(e.getLocal()))
                    listaResultados.add(e.toString());
                // ver filtros por data
                // ver filtros por hora
            }
        }
        return listaResultados;
    }

    public ArrayList<String> consultaPresencasEvento(int indiceEvento){
        String nomeEvento = listaEventos.get(indiceEvento).getNome();
        listaResultados.clear();
        synchronized(listaRegistos){
            for(Registo r : listaRegistos){
                if(nomeEvento.equalsIgnoreCase(r.getEvento()))
                    listaResultados.add(r.toString());
            }
        }
        return listaResultados;
    }

    public ArrayList<String> consultaEventosUtilizador(String utilizador) {
        if(verificaFormato(utilizador))
            return null;
        listaResultados.clear();
        synchronized(listaRegistos){
            for(Registo r : listaRegistos){
                if(r.getUtilizador().equals(utilizador))
                    listaResultados.add(r.toString());
            }
        }
        return listaResultados;
    }

    public void obterCSV_Admin(){
        //cria ficheiro usando listaResultados
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

            ConsultaEventos_EliminaPresencas_InserePresencas lista = (ConsultaEventos_EliminaPresencas_InserePresencas) oin.readObject();

            if (lista.getTipo() == Message_types.ERRO) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }
            return lista.getLista();
        }catch (IOException | ClassNotFoundException ignored) {
            return new String[]{"Erro"};
        }
    }
    public String[] consultaEventosUtilizador(String utilizador) {
        if(verificaFormato(utilizador))
            return new String[]{"Erro"};

        Consulta_Elimina_GeraCod_SubmeteCod_Evento consulta =
                new Consulta_Elimina_GeraCod_SubmeteCod_Evento(utilizador, Message_types.CONSULTA_EVENTOS);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(consulta);
            oout.flush();

            ConsultaEventos_EliminaPresencas_InserePresencas lista = (ConsultaEventos_EliminaPresencas_InserePresencas) oin.readObject();

            if(lista.getTipo() == Message_types.VALIDO)
                return lista.getLista();
            if (lista.getTipo() == Message_types.ERRO) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            return new String[]{"Erro na comunicação com o servidor"};
        }
        return new String[]{"Erro"};
    }
*/
  /*  public String[] consultaEventosFiltros(String filtros) {
        ArrayList<String> filtrosArray = new ArrayList<>();
        Collections.addAll(filtrosArray, filtros.trim().split(" "));

        ConsultaEventos_EliminaPresencas_InserePresencas interacao =
                new ConsultaEventos_EliminaPresencas_InserePresencas(Message_types.CONSULTA_EVENTOS, filtros);

        try (ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())) {
            oout.writeObject(interacao);
            oout.flush();

            ConsultaEventos_EliminaPresencas_InserePresencas lista = (ConsultaEventos_EliminaPresencas_InserePresencas) oin.readObject();

            if(lista.getTipo() == Message_types.VALIDO)
                return lista.getLista();
            if (lista.getTipo() == Message_types.ERRO) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            return new String[]{"Erro na comunicação com o servidor"};
        }
        return new String[]{"Erro"};
    }
*/
