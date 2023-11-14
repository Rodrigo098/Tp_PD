package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;

import java.time.LocalDate;

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

    public String getNome() {
        return evento.nomeEvento();
    }

    public String getLocal() {
        return evento.local();
    }
    public LocalDate getData() {
        return evento.data();
    }
    public int getHoreInicio() {
        return evento.horaInicio();
    }
    public int getHoraFim() {
        return evento.horaFim();
    }
}