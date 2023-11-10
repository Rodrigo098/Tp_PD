package pt.isec.pd.trabalhoPratico.model.classesDados;

import java.io.Serializable;

public record Presenca(Evento evento, Utilizador utilizador) implements Serializable {

    public String getEvento() {
        return evento.getNomeEvento();
    }
    public String getUtilizador() {
        return utilizador.getEmail();
    }
    @Override
    public String toString() {
        return utilizador.toString();
    }

    public String getEventoInfo() {
        return evento.toString();
    }
}
