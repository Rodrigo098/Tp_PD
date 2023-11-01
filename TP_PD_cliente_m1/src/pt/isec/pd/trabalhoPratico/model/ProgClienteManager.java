package pt.isec.pd.trabalhoPratico.model;

import javafx.util.Pair;
import pt.isec.pd.trabalhoPratico.model.programs.ProgramaCliente;

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
    public void logout() {
        programaCliente.logout();
    }

    //UTILIZADOR:
    public void registar(String nome, String email, String numIdentificacao, String password, String confPass) {
        programaCliente.registar(nome, email, numIdentificacao, password, confPass);
    }
    public boolean editarRegisto(String nome, String email, String numIdentificacao, String password, String confPass) {
        return programaCliente.editarRegisto(nome, email, numIdentificacao, password, confPass);
    }
    public boolean registarPresenca(String codigo){
        return programaCliente.registarPresença(codigo);
    }
    public String[] consultarPresencasUti(){
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

    public String[] consultarPresencas() {
        return programaCliente.consultarPresençasUti();
    }

    public Pair<Boolean, String> criaSocket(List<String> list) {
        return programaCliente.criaSocket(list);
    }
}
