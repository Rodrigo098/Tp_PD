package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;

public class Msg_Edita_Evento extends Geral{
    private Evento evento;
    private String novoNome;
    public Msg_Edita_Evento(Evento evento, String novoNome) {
        super(Message_types.EDIT_EVENTO);
        this.evento = evento;
        this.novoNome = novoNome;
    }
    public String getNovoNome() {
        return novoNome;
    }
    public Evento getEvento() {
        return evento;
    }
}