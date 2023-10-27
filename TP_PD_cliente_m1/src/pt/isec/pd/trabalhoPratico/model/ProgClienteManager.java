package pt.isec.pd.trabalhoPratico.model;

import pt.isec.pd.trabalhoPratico.model.data.ProgramaCliente;

public class ProgClienteManager {
    private ProgramaCliente programaCliente;

    public ProgClienteManager() {
        programaCliente = new ProgramaCliente();
    }

    public void login(String email, String password) {
        programaCliente.login(email, password);
    }
    public void registar(String nome, String email, String numIdentificacao, String password, String confPass) {
        programaCliente.registar(nome, email, numIdentificacao, password, confPass);
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

    public void obterCSV() {
    }

    public void eliminarEvento(String evento) {
    }

    public void editarEvento(String evento) {
    }

    public String[] obterListaEventos() {
        return new String[0];
    }
}
