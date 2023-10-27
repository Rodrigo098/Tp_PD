package pt.isec.pd.trabalhoPratico.model.data;

import pt.isec.pd.trabalhoPratico.MainCliente;

import java.net.Socket;

public class ProgramaCliente {
    private Socket socket;
    public ProgramaCliente(){
    }

    //ver se é email:
    public boolean verificaFormato(String email){
        String [] verificaFormatoEmail = email.split("@|\\.");
        return (verificaFormatoEmail.length != 3 && verificaFormatoEmail[2].length() > 3);
    }


    //FUNCIONALIDADES:
    //COMUNS:
    public boolean login(String email, String password){
        if(email == null || password == null || verificaFormato(email))
            return false;

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
            MainCliente.administradorSBP.set(true);
        }
        else {
            MainCliente.administradorSBP.set(false);
        }

        MainCliente.menuSBP.set("CONTA");
        return true;
    }

    //UTILIZADOR:
    public boolean registar(String nome, String password, String email, String confPass, String numIdentificacao){
        if(nome == null || password == null || !password.equals(confPass) || email == null || verificaFormato(email) || numIdentificacao == null)
                return false;
        int numID;
        try {
            numID = Integer.parseInt(numIdentificacao);
        } catch (NumberFormatException e) {
            return false;
        }

        //CÓDIGO A IMPLEMENTAR
            //verifica se o email já existe na BD
                //se existir
                    //return false;
                //senão
                    //insere na BD
        MainCliente.menuSBP.set("CONTA");
        return true;
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


}
