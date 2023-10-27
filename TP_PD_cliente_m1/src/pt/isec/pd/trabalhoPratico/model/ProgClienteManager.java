package pt.isec.pd.trabalhoPratico.model;

import pt.isec.pd.trabalhoPratico.model.data.ProgramaCliente;

public class ProgClienteManager {
    private ProgramaCliente programaCliente;

    public ProgClienteManager() {
        programaCliente = new ProgramaCliente();
    }

    public boolean login(String email, String password) {
        return programaCliente.login(email, password);
    }
    public void registar(String nome, String password, String confPass, String email, String numIdentificacao) {
        programaCliente.registar(nome, password, confPass, email, numIdentificacao);
    }
    public boolean editarRegisto(){
        return programaCliente.editarRegisto();
    }
    public boolean registarPresença(){
        return programaCliente.registarPresença();
    }
    public String[] consultarPresenças(){
        return programaCliente.consultarPresenças();
    }
    public boolean obterFicheiroCSV(){
        return programaCliente.obterFicheiroCSV();
    }
    public void logout(){
        programaCliente.logout();
    }
}
