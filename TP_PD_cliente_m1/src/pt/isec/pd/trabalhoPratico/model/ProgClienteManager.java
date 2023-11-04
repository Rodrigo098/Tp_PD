package pt.isec.pd.trabalhoPratico.model;

import javafx.util.Pair;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Message_types;
import pt.isec.pd.trabalhoPratico.model.programs.ProgramaCliente;

import java.io.IOException;
import java.time.LocalDate;
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

    public boolean marcarPresenca(String codigo){
        return programaCliente.registarPresenca(codigo);
    }

    public String[] consultarPresencasUti(){
        return programaCliente.consultarPresencasUti();
    }
    public boolean obterCSV_Presencas(String nome) {
        return programaCliente.obterCSV_Presencas(nome);
    }

    public boolean editarRegisto(String nome, String numIdentificacao, String password, String confPass) {
        return programaCliente.editarRegisto(nome, numIdentificacao, password, confPass);
    }

    //ADMINISTRADOR:
    public boolean criarEditar_Evento(String nome, String local, LocalDate data, int horaInicio, int horaFim, Message_types tipo){
        return programaCliente.criarEditar_Evento(nome, local, data, horaInicio, horaFim, tipo);
    }

    public boolean eliminarEvento(int indiceEvento) {
        return programaCliente.eliminarEvento(indiceEvento);
    }

    public boolean eliminaInsere_Eventos(Message_types tipo, int indiceEvento, String filtros) {
        return programaCliente.eliminaInsere_Eventos(tipo, indiceEvento, filtros);
    }

    public String gerarCodPresenca(int indiceEvento) {
        return programaCliente.gerarCodPresenca(indiceEvento);
    }

    public ArrayList<String> getListaEventos() {
        return programaCliente.getListaEventos();
    }
    public ArrayList<String> consultaEventosFiltros(String nome, String local, String data, String horaInicio, String horaFim){
        return programaCliente.consultaEventosFiltros(nome, local, data, horaInicio, horaFim);
    }

    public ArrayList<String> consultaPresencasEvento(int indiceEvento){
        return programaCliente.consultaPresencasEvento(indiceEvento);
    }

    public ArrayList<String> consultaEventosUtilizador(String utilizador){
        return programaCliente.consultaEventosUtilizador(utilizador);
    }
    public void obterCSV_Admin() {
        programaCliente.obterCSV_Admin();
    }
}
