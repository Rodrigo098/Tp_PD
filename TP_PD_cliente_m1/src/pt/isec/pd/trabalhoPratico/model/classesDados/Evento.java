package pt.isec.pd.trabalhoPratico.model.classesDados;

import java.time.LocalDate;

public class Evento {
    private final String nome, local;
    private final int horaInicio, horaFim;
    private final LocalDate data;

    public Evento(String nome, String local, LocalDate data, int horaInicio, int horaFim) {
        this.nome = nome;
        this.local = local;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }
    public String getNome() {
        return nome;
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
        return "Evento " + nome + ": localizado em " + local + " no dia " + data.toString() + " das " + horaInicio + " às " + horaFim;
    }
}
