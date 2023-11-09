package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;

public class Edita_Evento extends Geral{
    private Evento evento;
    private String novoNome;

    public Edita_Evento(Evento evento, String novoNome) {
        super(Message_types.EDIT_EVENTO);
        this.evento = evento;
    }
    public String getNovoNome() {
        return novoNome;
    }
    public Evento getEvento() {
        return evento;
    }
}