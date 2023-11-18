package pt.isec.pd.trabalhoPratico.model.classesPrograma;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

enum EstadoNaAplicacao {
    ENTRADA, ADMINISTRADOR, UTILIZADOR, EXCEDEU_TEMPO, SAIR, FIM;
}
public class GereMudancasPLC {
    public static String PROP_ATUALIZACAO = "atualizacao", PROP_ERRO = "erro", PROP_ESTADO = "estado";
    private int numAtualizacoes, erros;
    private EstadoNaAplicacao estadoNaAplicacao;

    private PropertyChangeSupport suporteAtualizacao;

    public GereMudancasPLC() {
        suporteAtualizacao = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(String propriedade, PropertyChangeListener novoListener) {
        suporteAtualizacao.addPropertyChangeListener(propriedade, novoListener);
    }

    public void removePropertyChangeListener(String propriedade, PropertyChangeListener listener) {
        suporteAtualizacao.removePropertyChangeListener(propriedade, listener);
    }

    public void setNovaAtualizacao() {
        suporteAtualizacao.firePropertyChange(PROP_ATUALIZACAO, null, null);
        numAtualizacoes++;
    }

    public void setEstadoNaAplicacao(EstadoNaAplicacao novoEstado) {
        suporteAtualizacao.firePropertyChange(PROP_ESTADO, null, null);
        estadoNaAplicacao = novoEstado;
    }

    public void setErros() {
        suporteAtualizacao.firePropertyChange(PROP_ERRO, null, null);
        erros++;
    }

    public EstadoNaAplicacao getEstadoNaAplicacao() {
        return estadoNaAplicacao;
    }
}
