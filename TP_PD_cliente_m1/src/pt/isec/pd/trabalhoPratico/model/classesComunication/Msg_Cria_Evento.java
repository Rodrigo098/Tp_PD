package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;

public class Msg_Cria_Evento extends Geral{
    private Evento evento;

    public Msg_Cria_Evento(Evento evento) {
        super(Message_types.CRIA_EVENTO);
        this.evento = evento;
    }

    public Evento getEvento() {
        return evento;
    }
}
