package pt.isec.pd.trabalhoPratico.model;

import javafx.beans.InvalidationListener;
import javafx.util.Pair;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Message_types;
import pt.isec.pd.trabalhoPratico.model.classesPrograma.ProgramaCliente;

import java.time.LocalDate;
import java.util.List;

public class ProgClienteManager {
    private ProgramaCliente programaCliente;

    public ProgClienteManager() {
        programaCliente = new ProgramaCliente();
    }

    ////////////////////////////////////////////////////////////////////
    public void addLogadoListener(InvalidationListener listener) {
        programaCliente.addLogadoListener(listener);
    }
    public void addAtualizacaoListener(InvalidationListener listener) {
        programaCliente.addAtualizacaoListener(listener);
    }
    public void addErroListener(InvalidationListener listener) {
        programaCliente.addErroListener(listener);
    }
    public String getLogado(){
        return programaCliente.getLogado();
    }
    ////////////////////////////////////////////////////////////////////

    //COMUM:
    public Pair<Boolean, String> criaSocket(List<String> list) {
        return programaCliente.criaSocket(list);
    }
    public void login(String email, String password) {
        programaCliente.login(email, password);
    }
    public void logout() {
        programaCliente.logout();
    }

    public String[] obterListaConsulta(Message_types tipo, String nome, String local, LocalDate limData1, LocalDate limData2, int horaInicio, int horaFim){
        return programaCliente.obterListaConsulta(tipo, nome, local, limData1, limData2, horaInicio, horaFim);
    }

    //UTILIZADOR:
    public void registar(String nome, String email, String numIdentificacao, String password, String confPass) {
        programaCliente.registar(nome, email, numIdentificacao, password, confPass);
    }
    public boolean registarPresenca(String codigo){
        return programaCliente.registarPresenca(codigo);
    }
    public boolean obterCSV_Presencas(String nome) {
        return programaCliente.obterCSV_Presencas(nome);
    }

    public boolean editarRegisto(String nome, String numIdentificacao, String password, String confPass) {
        return programaCliente.editarRegisto(nome, numIdentificacao, password, confPass);
    }

    //ADMINISTRADOR:
    public boolean criar_Evento(String nome, String local, LocalDate data, int horaInicio, int horaFim){
        return programaCliente.criar_Evento(nome, local, data, horaInicio, horaFim);
    }
    public String editar_Evento(String evento, String novoNome, String local, LocalDate data, int horaInicio, int horaFim){
        return programaCliente.editar_Evento(evento, novoNome,local, data, horaInicio, horaFim);
    }

    public String eliminarEvento(String nomeEvento) {
        return programaCliente.eliminarEvento(nomeEvento);
    }

    public String eliminaInsere_Eventos(Message_types tipo, String nomeEvento, String filtros) {
        return programaCliente.eliminaInserePresencas_Eventos(tipo, nomeEvento, filtros);
    }

    public String gerarCodPresenca(String nomeEvento) {
        return programaCliente.gerarCodPresenca(nomeEvento);
    }

    public String[] consultaPresencasEvento(String nomeEvento){
        return programaCliente.consultaPresencasEvento(nomeEvento);
    }
    public String[] consultaEventosUtilizador(String utilizador){
        return programaCliente.consultaEventosDeUmUtilizador(utilizador);
    }
    public void obterCSV_Admin(String nome) {
        programaCliente.obterCSV_Admin(nome);
    }
}
