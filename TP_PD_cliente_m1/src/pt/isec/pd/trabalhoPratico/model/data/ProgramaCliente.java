package pt.isec.pd.trabalhoPratico.model.data;

import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.classesComunication.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
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
    public void login(String email, String password) throws IOException {
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
        }catch (Exception e) {
            MainCliente.menuSBP.set("ERRO");
            socket.close();
        }
    }
    public void logout() throws IOException {
        Geral logout = new Geral(Message_types.LOGOUT);

        try(ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())){

            oout.writeObject(logout);
            oout.flush();
            MainCliente.menuSBP.set("MENU");
            MainCliente.administradorSBP.set("INDEFINIDO");
        }catch (Exception e) {
            MainCliente.menuSBP.set("ERRO");
        }
        socket.close();
    }

    /////////////////////////UTILIZADOR:
    public void registar(String nome, String email, String numIdentificacao, String password, String confPass) throws IOException {
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
        }catch (Exception e) {
            MainCliente.menuSBP.set("ERRO");
            socket.close();
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

            return validacao.getTipo() == Message_types.VALIDO;
        }catch (Exception e) {
            return false;
        }
    }

    public String[] consultarPresençasUti() {
        Geral consultaPresencas = new Geral(Message_types.CONSULTA_PRES_UTILIZADOR);

        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream()))
        {
            oout.writeObject(consultaPresencas);
            oout.flush();

            ConsultaEventos_EliminaPresencas lista = (ConsultaEventos_EliminaPresencas) oin.readObject();

            return lista.getLista();
        }catch (Exception e) {
            return new String[]{"Erro na comunicação com o servidor"};
        }
    }

    public boolean editarRegisto(String nome, String email, String numIdentificacao, String password, String confPass) throws IOException {

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
        }catch (Exception e) {
            MainCliente.menuSBP.set("ERRO");
            socket.close();
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
    //////////////////////// FALTA ADMINISTRADOR

    //Faço amanhã desculpem o atraso :(

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