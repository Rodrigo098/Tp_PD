package pt.isec.pd.trabalhoPratico.model.data;

import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProgramaCliente {
    public static final int TIMEOUT = 10; //segundos
    private Socket socket;
    //public ArrayList<String> listaEventos;//obviamente que não é string mas meanwhile yes

    public ProgramaCliente(){
        //vai
        //listaEventos = new ArrayList<>();
        //listaEventos.add("Evento 1");
        //listaEventos.add("Evento 2");
        //listaEventos.add("Evento 3");
    }

    //temos de pôr uma thread que atualiza o arraylist de eventos

    //ver se é email:
    public boolean verificaFormato(String email){
        return email == null || email.split("@|\\.").length != 3;
    }


    ///////////////////////////////////////FUNCIONALIDADES:
    /////////////////////////COMUNS:
    public boolean criaSocket(List<String> list) {
        if(list.size() != 2){
            return false;
        }
        try(Socket socket = new Socket(InetAddress.getByName(list.get(0)), Integer.parseInt(list.get(1))))
        {
            this.socket = socket;
            return true;
        }catch(Exception e){
            return false;
        }
    }
    public void login(String email, String password) {
        if(password == null || verificaFormato(email))
            return;

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
                    case ADMINISTRADOR ->
                        MainCliente.administradorSBP.set("ADMINISTRADOR");
                    case UTILIZADOR ->
                        MainCliente.administradorSBP.set("UTILIZADOR");
                    case INVALIDO, ERRO -> {
                        MainCliente.menuSBP.set("ERRO");
                        socket.close();
                        return;
                    }
                }
                MainCliente.menuSBP.set("CONTA");
            }
        }catch (IOException | ClassNotFoundException ignored) {
        }
    }
    public void logout() {
        Geral logout = new Geral(Message_types.LOGOUT);

        try(ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())){

            oout.writeObject(logout);
            oout.flush();
            MainCliente.menuSBP.set("MENU");
            MainCliente.administradorSBP.set("INDEFINIDO");
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
                    MainCliente.administradorSBP.set("UTILIZADOR");
                    MainCliente.menuSBP.set("CONTA");
                } else {
                    MainCliente.menuSBP.set("ERRO");
                    socket.close();
                }
            }
        }catch (IOException | ClassNotFoundException ignored) {
        }
    }

    public boolean registarPresença(String codigoEvento) {
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

    public String[] consultarPresençasUti() {
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

    public boolean editarRegisto(String nome, String email, String numIdentificacao, String password, String confPass) {

        if(nome == null || password == null || !password.equals(confPass) || verificaFormato(email) || numIdentificacao == null)
            return false;

        long numID;
        try {
            numID = Integer.parseInt(numIdentificacao);//?? como é que ponho para long?
        } catch (NumberFormatException e) {
            return false;
        }

        RegistoEdicao_Cliente dadosRegisto = new RegistoEdicao_Cliente(nome, email, password, numID, Message_types.EDITAR_REGISTO);

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

    public boolean obterFicheiroCSV() {
        //se há presenças registadas
            //cria ficheiro CSV
            //return true;
        //senão
            //return false;

        return false;
    }


    /////////////////////////ADMINISTRADOR:
    //CRIAR OU EDITAR EVENTO, O ÚLTIMO PARÂMETRO É PARA SABER SE É PARA CRIAR OU EDITAR
    public boolean criarEditar_Evento(String nome, String local, String data, String horaInicio, String horaFim, Message_types tipo) {
        if(nome == null || local == null || data == null || horaInicio == null || horaFim == null)
            return false;

        //Data: como será com data picker não será necessária
        //Hora: como será com select não será necessário

        Cria_evento evento = new Cria_evento(nome, local, data, horaInicio, horaFim, tipo);

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

    public boolean eliminarEvento(String nomeEvento) {
        if(nomeEvento == null || nomeEvento.isBlank())
            return false;

        Consulta_Elimina_GeraCod_SubmeteCod_Evento evento =
                new Consulta_Elimina_GeraCod_SubmeteCod_Evento(nomeEvento, Message_types.ELIMINAR_EVENTO);

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

    public String[] consultaEventos(String filtros) {
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

    public boolean eliminaInsere_Eventos(Message_types tipo, String nome, String filtros) {
        //o nome do evento é o primeiro filtro
        ArrayList<String> emails = new ArrayList<>();
        for (String email : filtros.trim().split(" ")) {
            if (verificaFormato(email))
                emails.add(email);
        }

        ConsultaEventos_EliminaPresencas_InserePresencas interacao =
                new ConsultaEventos_EliminaPresencas_InserePresencas(tipo, nome, emails.toArray(new String[0]));

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

    public String gerarCodPresenca(String evento) {
        //nao é preciso validar o evento porque é um botão que vai buscar o nome do evento selecionado

        Consulta_Elimina_GeraCod_SubmeteCod_Evento geraCod = new Consulta_Elimina_GeraCod_SubmeteCod_Evento(evento, Message_types.GERAR_COD);

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