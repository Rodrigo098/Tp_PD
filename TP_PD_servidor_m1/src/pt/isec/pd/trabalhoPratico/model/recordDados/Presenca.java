package pt.isec.pd.trabalhoPratico.model.recordDados;

import java.io.Serial;
import java.io.Serializable;

public record Presenca(Evento evento, Utilizador utilizador) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public String getEvento() {
        return evento.nomeEvento();
    }
    public String getUtilizador() {
        return utilizador.email();
    }
    public String getEventoInfo() {
        return evento.toString();
    }
    @Override
    public String toString() {
        return utilizador.toString();
    }
}
