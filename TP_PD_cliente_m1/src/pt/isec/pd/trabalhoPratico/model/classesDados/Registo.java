package pt.isec.pd.trabalhoPratico.model.classesDados;

public class Registo {
    private final Evento evento;
    private final Utilizador utilizador;

    public Registo(Evento evento, Utilizador utilizador) {
        this.evento = evento;
        this.utilizador = utilizador;
    }
    public String getEvento() {
        return evento.getNome();
    }
    public String getUtilizador() {
        return utilizador.getEmail();
    }
    @Override
    public String toString() {
        return evento.toString() +  "Presenças:\n" + utilizador.toString();
    }
}
