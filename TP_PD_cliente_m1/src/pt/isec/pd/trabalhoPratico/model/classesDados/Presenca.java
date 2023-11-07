package pt.isec.pd.trabalhoPratico.model.classesDados;

public record Presenca(Evento evento, Utilizador utilizador) {

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
