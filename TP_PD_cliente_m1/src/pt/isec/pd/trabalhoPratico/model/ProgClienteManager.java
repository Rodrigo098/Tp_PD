package pt.isec.pd.trabalhoPratico.model;

import pt.isec.pd.trabalhoPratico.model.classesComunication.Message_types;
import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;
import pt.isec.pd.trabalhoPratico.model.recordDados.Utilizador;
import pt.isec.pd.trabalhoPratico.model.classesPrograma.*;

import java.beans.PropertyChangeListener;
import java.util.List;

public class ProgClienteManager {
    private final ProgramaCliente programaCliente;

    public ProgClienteManager() {
        programaCliente = new ProgramaCliente();
    }

    public String getNomeCliente() {
        return programaCliente.getNomeCliente();
    }
    public String getEmailCliente() {
        return programaCliente.getEmailCliente();
    }
    public String getNumeroCliente() {
        return programaCliente.getNumeroCliente();
    }
    //////////////////////////// PROPRIEDADE LISTENERS ////////////////////////////////////
    public void addLogadoListener(PropertyChangeListener listener) {
        programaCliente.addPropertyChangeListener(GereMudancasPLC.PROP_ESTADO ,listener);
    }
    public void addAtualizacaoListener(PropertyChangeListener listener) {
        programaCliente.addPropertyChangeListener(GereMudancasPLC.PROP_ATUALIZACAO ,listener);
    }
    public void addErroListener(PropertyChangeListener listener) {
        programaCliente.addPropertyChangeListener(GereMudancasPLC.PROP_ERRO ,listener);
    }

    public String getLogado(){
        return programaCliente.getEstadoNaAplicacao();
    }
    ////////////////////////////////////////////////////////////////////

    //COMUM:
    public ParResposta criaSocket(List<String> list) {
        return programaCliente.criaSocket(list);
    }

    public ParResposta login(String email, String password) {
        return programaCliente.login(email, password);
    }

    public void logout(String fonte) {
        programaCliente.logout(fonte);
    }

    public Evento[] obterListaConsulta(Message_types tipo, String nome, String local, String limData1, String limData2, String horaInicio, String horaFim){
        return programaCliente.obterListaConsultaEventos(tipo, nome, local, limData1, limData2, horaInicio, horaFim);
    }

    public String obterCSV_ListaEventos(String caminhoCSV,String nomeFicheiro, Message_types tipoCSV) {
        return programaCliente.obterCSV(caminhoCSV, nomeFicheiro, tipoCSV);
    }

    //UTILIZADOR:
    public ParResposta registar(String nome, String email, String numIdentificacao, String password, String confPass) {
        return programaCliente.registarConta(nome, email, numIdentificacao, password, confPass);
    }
    public String registarPresenca(String evento, String codigo){
        return programaCliente.registarPresenca(evento, codigo);
    }
    public String editarRegisto(String nome, String numIdentificacao, String password, String confPass) {
        return programaCliente.editarRegisto(nome, numIdentificacao, password, confPass);
    }

    //ADMINISTRADOR:
    public String criar_Evento(String nome, String local, String data, String horaInicio, String horaFim){
        return programaCliente.criar_Evento(nome, local, data, horaInicio, horaFim);
    }
    public String editar_Evento(String evento, String novoNome, String local, String data, String horaInicio, String horaFim){
        return programaCliente.editar_Evento(evento, novoNome,local, data, horaInicio, horaFim);
    }
    public String eliminarEvento(String nomeEvento) {
        return programaCliente.eliminarEvento(nomeEvento);
    }

    public String eliminaInsere_Eventos(Message_types tipo, String nomeEvento, String filtros) {
        return programaCliente.eliminaInserePresencas_Eventos(tipo, nomeEvento, filtros);
    }
    public String gerarCodPresenca(String nomeEvento, String tempoValido) {
        return programaCliente.gerarCodPresenca(nomeEvento, tempoValido);
    }

    public Utilizador[] consultaPresencasEvento(String nomeEvento){
        return programaCliente.consultaPresencasEvento(nomeEvento);
    }
    public Evento[] consultaEventosUtilizador(String utilizador){
        return programaCliente.consultaEventosDeUmUtilizador(utilizador);
    }
}