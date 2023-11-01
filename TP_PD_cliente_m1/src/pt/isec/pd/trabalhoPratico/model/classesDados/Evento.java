package pt.isec.pd.trabalhoPratico.model.classesDados;

public class Evento {
    private final String nome, local, data, horaInicio, horaFim;

    public Evento(String nome, String local, String data, String horaInicio, String horaFim) {
        this.nome = nome;
        this.local = local;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }
    public String getNome() {
        return nome;
    }
    public String getData() {
        return data;
    }
    public String getHoraFim() {
        return horaFim;
    }
    public String getHoraInicio() {
        return horaInicio;
    }
    public String getLocal() {
        return local;
    }
    @Override
    public String toString() {
        return "Evento " + nome + ": localizado em " + local + " no dia " + data + " das " + horaInicio + " às " + horaFim;
    }
}
