package pt.isec.trabalhoPratico.classesComunication;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class Cria_evento extends Geral{
    private String nome, local;
    private Date data;//string não seria mais fácil??
    private LocalTime horaInicio, horaFim;//int não seria mais fácil??

    public Cria_evento(String nome, String local, Date data, LocalTime horaInicio, LocalTime horaFim) {
        super(Message_types.CRIA_EVENTO);
        this.nome = nome;
        this.local = local;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        //Calendar cal = Calendar.getInstance();
    }

    public String getNome() {
        return nome;
    }

    public String getLocal() {
        return local;
    }

    public Date getData() {
        return data;
    }

    public LocalTime getHorainicio() {
        return horaInicio;
    }

    public LocalTime getHorafim() {
        return horaFim;
    }
}
