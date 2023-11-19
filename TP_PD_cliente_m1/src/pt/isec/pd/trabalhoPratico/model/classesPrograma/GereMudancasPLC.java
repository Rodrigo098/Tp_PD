package pt.isec.pd.trabalhoPratico.model.classesPrograma;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

enum EstadoNaAplicacao {
    ENTRADA, ADMINISTRADOR, UTILIZADOR, EXCEDEU_TEMPO, SAIR, FIM, NADA;
}
public class GereMudancasPLC {
    public static String PROP_ATUALIZACAO = "atualizacao", PROP_ERRO = "erro", PROP_ESTADO = "estado";
    private int numAtualizacoes, erros;
    private EstadoNaAplicacao estadoNaAplicacao;

    private PropertyChangeSupport suporteAtualizacao;

    public GereMudancasPLC() {
        estadoNaAplicacao = EstadoNaAplicacao.NADA;
        suporteAtualizacao = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(String propriedade, PropertyChangeListener novoListener) {
        suporteAtualizacao.addPropertyChangeListener(propriedade, novoListener);
    }

    public void removePropertyChangeListener(String propriedade, PropertyChangeListener listener) {
        suporteAtualizacao.removePropertyChangeListener(propriedade, listener);
    }

    public void setNovaAtualizacao() {
        numAtualizacoes++;
        suporteAtualizacao.firePropertyChange(PROP_ATUALIZACAO, null, null);
    }

    public void setEstadoNaAplicacao(EstadoNaAplicacao novoEstado) {
        estadoNaAplicacao = novoEstado;
        suporteAtualizacao.firePropertyChange(PROP_ESTADO, null, null);
    }

    public void setErros() {
        erros++;
        suporteAtualizacao.firePropertyChange(PROP_ERRO, null, null);
    }

    public EstadoNaAplicacao getEstadoNaAplicacao() {
        return estadoNaAplicacao;
    }
}
