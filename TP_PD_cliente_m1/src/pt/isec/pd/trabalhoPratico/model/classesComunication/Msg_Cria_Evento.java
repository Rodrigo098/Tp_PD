package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;

import java.time.LocalDate;

public class Msg_Cria_Evento extends Geral{
    private Evento evento;

    public Msg_Cria_Evento(Evento evento) {
        super(Message_types.CRIA_EVENTO);
        this.evento = evento;
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