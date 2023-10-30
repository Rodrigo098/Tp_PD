package pt.isec.pd.trabalhoPratico.classescomunication;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class Cria_evento extends Geral{
   private String nome,local;
    private Date data;
    private LocalTime horainicio,horafim;

    public Cria_evento(String nome, String local, Date data, LocalTime horainicio, LocalTime horafim) {
        this.nome = nome;
        this.local = local;
        this.data = data;
        this.horainicio = horainicio;
        this.horafim = horafim;
        Calendar cal=Calendar.getInstance();
        tipo=Message_types.CRIA_EVENTO;

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
        return horainicio;
    }

    public LocalTime getHorafim() {
        return horafim;
    }
}
