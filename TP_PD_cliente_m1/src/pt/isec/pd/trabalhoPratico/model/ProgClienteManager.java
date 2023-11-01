package pt.isec.pd.trabalhoPratico.model;

import javafx.util.Pair;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Message_types;
import pt.isec.pd.trabalhoPratico.model.programs.ProgramaCliente;

import java.io.IOException;
import java.util.ArrayList;
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

    public Pair<Boolean, String> criaSocket(List<String> list) {
        return programaCliente.criaSocket(list);
    }

    //UTILIZADOR:
    public void registar(String nome, String email, String numIdentificacao, String password, String confPass) {
        programaCliente.registar(nome, email, numIdentificacao, password, confPass);
    }

    public boolean registarPresenca(String codigo){
        return programaCliente.registarPresenca(codigo);
    }

    public String[] consultarPresencasUti(){
        return programaCliente.consultarPresencasUti();
    }
    public boolean obterFicheiroCSV(){
        return programaCliente.obterFicheiroCSV();
    }

    public boolean editarRegisto(String nome, String email, String numIdentificacao, String password, String confPass) {
        return programaCliente.editarRegisto(nome, email, numIdentificacao, password, confPass);
    }

    //ADMINISTRADOR:
    public boolean criarEditar_Evento(String nome, String local, String data, String horaInicio, String horaFim, Message_types tipo){
        return programaCliente.criarEditar_Evento(nome, local, data, horaInicio, horaFim, tipo);
    }

    public boolean eliminarEvento(String nomeEvento) {
        return programaCliente.eliminarEvento(nomeEvento);
    }

    public boolean eliminaInsere_Eventos(Message_types tipo, String nome, String filtros) {
        return programaCliente.eliminaInsere_Eventos(tipo, nome, filtros);
    }

    public String gerarCodPresenca(String evento) {
        return programaCliente.gerarCodPresenca(evento);
    }

    public ArrayList<String> consultaEventosFiltros(String nome, String local, String data, String horaInicio, String horaFim){
        return programaCliente.consultaEventosFiltros(nome, local, data, horaInicio, horaFim);
    }

    public ArrayList<String> consultaPresencasEvento(String nomeEvento){
        return programaCliente.consultaPresencasEvento(nomeEvento);
    }

    public ArrayList<String> consultaEventosUtilizador(String utilizador){
        return programaCliente.consultaEventosUtilizador(utilizador);
    }
    public void obterCSV() {
    }
}
