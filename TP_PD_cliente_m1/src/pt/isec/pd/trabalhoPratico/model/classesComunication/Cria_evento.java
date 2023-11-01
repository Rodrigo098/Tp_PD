package pt.isec.pd.trabalhoPratico.model.classesComunication;

import java.time.LocalTime;
import java.util.Date;

public class Cria_evento extends Geral{
    private Evento evento;

    public Cria_evento(Evento evento, Message_types tipo) {
        super(tipo);
        this.evento = evento;
    }

    public Evento getEvento() {
        return evento;
    }
}
