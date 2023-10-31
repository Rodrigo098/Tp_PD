package pt.isec.pd.trabalhoPratico.model;

import pt.isec.pd.trabalhoPratico.model.data.ProgramaCliente;

import java.io.IOException;
import java.util.List;

public class ProgClienteManager {
    private ProgramaCliente programaCliente;

    public ProgClienteManager() {
        programaCliente = new ProgramaCliente();
    }

    public void login(String email, String password) throws IOException {
        programaCliente.login(email, password);
    }
    public void registar(String nome, String email, String numIdentificacao, String password, String confPass) throws IOException {
        programaCliente.registar(nome, email, numIdentificacao, password, confPass);
    }
    public boolean editarRegisto(){
        return programaCliente.editarRegisto();
    }
    public boolean registarPresença(String codigo){
        return programaCliente.registarPresença(codigo);
    }
    public String[] consultarPresenças(){
        return programaCliente.consultarPresenças();
    }
    public boolean obterFicheiroCSV(){
        return programaCliente.obterFicheiroCSV();
    }
    public void logout() throws IOException {
        programaCliente.logout();
    }

    public void obterCSV() {
    }

    public void eliminarEvento(String evento) {
    }

    public void editarEvento(String evento) {
    }

    public String[] obterListaEventos() {
        return programaCliente.obterListaEventos();
    }

    public String obterEvento(int eventoSelecionado) {
        return programaCliente.obterEvento(eventoSelecionado);
    }

    public boolean handShake(List<String> list) {
        return programaCliente.criaSocket(list);
    }
}
