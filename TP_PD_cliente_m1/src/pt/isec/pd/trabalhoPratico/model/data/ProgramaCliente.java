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


    public void mainMethod() throws IOException {

        if(args.length != 2){
            System.out.println("Sintaxe: java TcpSerializedTimeClientIncomplete serverAddress serverUdpPort");
            return;
        }
//se se criar os objetos na mesma forma cria deadlock por isso se no servidor o input está primeiro e o output
// está em segundo então no cliente o output deve estar em primeiro e o input em segundo
        try(Socket socket = new Socket(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
            ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream())){

            socket.setSoTimeout(TIMEOUT*1000);

            //Serializa a string TIME_REQUEST para o OutputStream associado a socket
            oout.writeObject(TIME_REQUEST);
            oout.flush();

            //Deserializa a resposta recebida em socket
            response = (Time) oin.readObject();

            if(response == null){
                System.out.println("O servidor nao enviou qualquer respota antes de"
                        + " fechar aligacao TCP!");
            }else{
                System.out.println("Hora indicada pelo servidor: " + response.getHora() + ":" + response.getMinuto() + ":" + response.getSegundo());
            }

        }catch(Exception e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }
    }

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


    //FUNCIONALIDADES:
    //COMUNS:
    public void login(String email, String password) throws IOException {
        if(password == null || verificaFormato(email))
            return;

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

    //UTILIZADOR:
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

    public boolean editarRegisto() {
        return false;
    }

    public boolean registarPresença() {
        //se o utilizador já registou presença
            //return false;
        return false;
    }

    public String[] consultarPresenças() {
        return new String[0];
    }

    public boolean obterFicheiroCSV() {
        //se há presenças registadas
            //cria ficheiro CSV
            //return true;
        //senão
            //return false;
        return false;
    }

    public void logout() {
        //desligar comunicação com o servidor
        System.out.println("até à próxima!");
    }


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
}
