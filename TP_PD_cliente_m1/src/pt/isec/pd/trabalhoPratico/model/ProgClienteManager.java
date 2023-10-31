package pt.isec.pd.trabalhoPratico.model;

import pt.isec.pd.trabalhoPratico.model.data.ProgramaCliente;

import java.io.IOException;
import java.util.List;

public class ProgClienteManager {
    private ProgramaCliente programaCliente;

    public ProgClienteManager() {
        programaCliente = new ProgramaCliente();
    }

    //COMUM:
    public void login(String email, String password) throws IOException {
        programaCliente.login(email, password);
    }
    public void logout() throws IOException {
        programaCliente.logout();
    }

    //UTILIZADOR:
    public void registar(String nome, String email, String numIdentificacao, String password, String confPass) throws IOException {
        programaCliente.registar(nome, email, numIdentificacao, password, confPass);
    }
    public boolean editarRegisto(String nome, String email, String numIdentificacao, String password, String confPass) throws IOException {
        return programaCliente.editarRegisto(nome, email, numIdentificacao, password, confPass);
    }
    public boolean registarPresença(String codigo){
        return programaCliente.registarPresença(codigo);
    }
    public String[] consultarPresençasUti(){
        return programaCliente.consultarPresençasUti();
    }
    public boolean obterFicheiroCSV(){
        return programaCliente.obterFicheiroCSV();
    }

    //ADMINISTRADOR:
    public void obterCSV() {
    }

    public void eliminarEvento(String evento) {
    }

    public void editarEvento(String evento) {
    }

    public String[] consultarPresenças() {
        return programaCliente.consultarPresençasUti();
    }

    public boolean criaSocket(List<String> list) {
        return programaCliente.criaSocket(list);
    }
}
