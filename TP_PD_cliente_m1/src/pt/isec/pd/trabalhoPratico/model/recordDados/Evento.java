package pt.isec.pd.trabalhoPratico.model.recordDados;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public record Evento( String nomeEvento, String local, LocalDate data, int horaInicio, int horaFim) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Override
    public String toString() {
        return nomeEvento + "; " + local + "; " + data.toString() + "; " + horaInicio;
    }
}

