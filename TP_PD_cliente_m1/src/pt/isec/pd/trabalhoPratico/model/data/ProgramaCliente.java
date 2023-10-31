package pt.isec.pd.trabalhoPratico.model.data;

import pt.isec.pd.trabalhoPratico.MainCliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ProgramaCliente {
    public static final int TIMEOUT = 10; //segundos
    private Socket socket;
    public ArrayList<String> listaEventos;//obviamente que não é string mas meanwhile yes

    public ProgramaCliente(){
        //vai
        listaEventos = new ArrayList<>();
        listaEventos.add("Evento 1");
        listaEventos.add("Evento 2");
        listaEventos.add("Evento 3");
    }

    //temos de pôr uma thread que atualiza o arraylist de eventos

    //ver se é email:
    public boolean verificaFormato(String email){
        return email == null || email.split("@|\\.").length != 3;
    }


    ///////////////////////////////////////FUNCIONALIDADES:
    /////////////////////////COMUNS:
    public boolean handShake(List<String> list) {
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

        cria o objeto da classe mensagem:
            tipo - login;
            conteudo - email, password;

        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())){

            oout.writeObject(dadosLogin);
            oout.flush();

            validacao = (Resposta) oin.readObject();

            if(validacao == null){
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }else{
                switch (validacao.getTipo()){
                    case "ADMINISTRADOR" ->
                        MainCliente.administradorSBP.set("ADMINISTRADOR");
                    case "UTILIZADOR" ->
                        MainCliente.administradorSBP.set("UTILIZADOR");
                    case "FALHA_AUTENTICACAO" -> {
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
    public void logout() {
        //desligar comunicação com o servidor
        System.out.println("até à próxima!");
    }

    /////////////////////////UTILIZADOR:
    public void registar(String nome, String email, String numIdentificacao, String password, String confPass) throws IOException {
        if(nome == null || password == null || !password.equals(confPass) || verificaFormato(email) || numIdentificacao == null)
                return;
        int numID;
        try {
            numID = Integer.parseInt(numIdentificacao);
        } catch (NumberFormatException e) {
            return;
        }
        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream()))
        {
            oout.writeObject(dadosRegisto);
            oout.flush();
            validacao = (Resposta) oin.readObject();

            if(validacao == null){
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }else{
                if(validacao.getTipo() == "NOVO_REGISTO") {
                    MainCliente.administradorSBP.set("UTILIZADOR");
                    MainCliente.menuSBP.set("CONTA");
                }else if(validacao.getTipo() == "FALHA_REGISTO") {
                        MainCliente.menuSBP.set("ERRO");
                        socket.close();
                    }
                }
            }catch (Exception e) {
                MainCliente.menuSBP.set("ERRO");
                socket.close();
            }
            /*
            //CÓDIGO A IMPLEMENTAR
            //procura na BD se existe um username correspondente
            //se não existir
            //return false;

            //se existir
            //verifica a password
            //se for diferente
            //return false;

            //se existir email e pass for correspondente:
            if(email.equals("admin@isec.pt")) {//verificaFormatoEmail[0].equals("admin") é só por agora, depois vai ser o que corresponde na BD
                MainCliente.administradorSBP.set("ADMINISTRADOR");
            }
            else {
                MainCliente.administradorSBP.set("UTILIZADOR");
            }
            MainCliente.menuSBP.set("CONTA");*/
    }

    public boolean registarPresença(String evento) {
        if(evento == null || evento.isBlank())
            return false;

        cria o objeto da classe mensagem:
        tipo - registoPresenca;
        conteudo - codigoEvento;

        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream()))
        {
            oout.writeObject(registoPresenca);
            oout.flush();
            validacao = (Resposta) oin.readObject();

            if(validacao == null || validacao.getTipo() == "FALHA_REGISTO"){
                return false;
            }
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public String[] consultarPresenças() {
        cria o objeto da classe resposta:
        tipo - consultarPresencas;

        try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream()))
        {
            oout.writeObject(consultaPresencas);
            oout.flush();
            lista = (Mensagem) oin.readObject();

            // não é preciso porque lá será preenchido que não há registos -> if(lista == null || lista.getTipo() == "FALHA_PROCURA")

            return lista.trim(";");
        }catch (Exception e) {
            return new String[]{"Erro na comunicação com o servidor"};
        }
    }


    //////////////////////// FALTA UTILIZADOR:
    public boolean editarRegisto() {
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
    //////////////////////// FALTA ADMINISTRADOR:
    public String[] obterListaEventos() {
        String [] eventos = new String[listaEventos.size()];
        for(String evento : listaEventos){
            eventos[listaEventos.indexOf(evento)] = evento;//depois põe-se toString
        }
        return eventos;
    }

    public String obterEvento(int eventoSelecionado) {
        return listaEventos.get(eventoSelecionado);//depois põe-se toString
    }
}
