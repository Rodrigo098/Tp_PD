package pt.isec.pd.trabalhoPratico.model.classesDados;

import java.io.Serializable;
import java.time.LocalDate;

public record Evento(String criador, String nomeEvento, String local, LocalDate data, int horaInicio, int horaFim) implements Serializable {
    public String getNomeEvento() {
        return nomeEvento;
    }
    public LocalDate getData() {
        return data;
    }
    public int getHoraFim() {
        return horaFim;
    }
    public int getHoraInicio() {
        return horaInicio;
    }
    public String getLocal() {
        return local;
    }
    @Override
    public String toString() {
        return nomeEvento + "; " + local + "; " + data.toString() + "; " + horaInicio;
    }
}
