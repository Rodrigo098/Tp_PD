package pt.isec.pd.trabalhoPratico.model.recordDados;

import java.io.Serializable;

public record Presenca(Evento evento, Utilizador utilizador) implements Serializable {

    public String getEvento() {
        return evento.nomeEvento();
    }
    public String getUtilizador() {
        return utilizador.email();
    }
    @Override
    public String toString() {
        return utilizador.toString();
    }

    public String getEventoInfo() {
        return evento.toString();
    }
}
