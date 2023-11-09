package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;

public class Cria_Evento extends Geral{
    private Evento evento;

    public Cria_Evento(Evento evento) {
        super(Message_types.CRIA_EVENTO);
        this.evento = evento;
    }

    public Evento getEvento() {
        return evento;
    }
}
