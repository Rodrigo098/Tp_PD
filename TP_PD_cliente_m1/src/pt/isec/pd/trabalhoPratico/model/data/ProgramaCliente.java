package pt.isec.pd.trabalhoPratico.model.data;

import pt.isec.pd.trabalhoPratico.MainCliente;

import java.net.Socket;
import java.util.ArrayList;

public class ProgramaCliente {
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


    //FUNCIONALIDADES:
    //COMUNS:
    public void login(String email, String password){
        if(password == null || verificaFormato(email))
            return;

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
        MainCliente.menuSBP.set("CONTA");
    }

    //UTILIZADOR:
    public void registar(String nome, String email, String numIdentificacao, String password, String confPass){
        if(nome == null || password == null || !password.equals(confPass) || verificaFormato(email) || numIdentificacao == null)
                return;
        int numID;
        try {
            numID = Integer.parseInt(numIdentificacao);
        } catch (NumberFormatException e) {
            return;
        }

        //CÓDIGO A IMPLEMENTAR
            //verifica se o email já existe na BD
                //se existir
                    //return false;
                //senão
                    //insere na BD
        MainCliente.menuSBP.set("CONTA");
        MainCliente.administradorSBP.set("UTILIZADOR");
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
}
