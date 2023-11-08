package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;

public class CriaEdita_evento extends Geral{
    private Evento evento;

    public CriaEdita_evento(Evento evento, Message_types tipo) {
        super(tipo);
        this.evento = evento;
    }

    public Evento getEvento() {
        return evento;
    }
}
