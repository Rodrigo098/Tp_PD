package pt.isec.pd.trabalhoPratico.model.classesComunication;

import java.time.LocalTime;
import java.util.Date;

public class Cria_evento extends Geral{
    private String nome, local, data, horaInicio, horaFim;//int não seria mais fácil??

    public Cria_evento(String nome, String local, String data, String horaInicio, String horaFim, Message_types tipo) {
        super(tipo);
        this.nome = nome;
        this.local = local;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public String getNome() {
        return nome;
    }

    public String getLocal() {
        return local;
    }

    public String getData() {
        return data;
    }

    public String getHorainicio() {
        return horaInicio;
    }

    public String getHorafim() {
        return horaFim;
    }
}
