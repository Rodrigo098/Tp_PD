package pt.isec.pd.trabalhoPratico.model.classesComunication;

import java.time.LocalDate;

public class Msg_ConsultaComFiltros extends Geral{
    private String nome, local, limData1, limData2, horaInicio, horaFim;

    public Msg_ConsultaComFiltros(Message_types tipo, String nome, String local, String limData1, String limData2, String horaInicio, String horaFim) {
        super(tipo);
        this.nome = nome;
        this.local = local;
        this.limData1 = limData1;
        this.limData2 = limData2;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public String getNome() {
        return nome;
    }

    public String getLocal() {
        return local;
    }

    public String getLimData1() {
        return limData1;
    }

    public String getLimData2() {
        return limData2;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }
}

