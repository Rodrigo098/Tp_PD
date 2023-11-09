package pt.isec.pd.trabalhoPratico.model.classesComunication;

import java.time.LocalDate;

public class Msg_ConsultaComFiltros extends Geral{
    private String nome, local;
    private LocalDate limData1, limData2;
    private int horaInicio, horaFim;

    public Msg_ConsultaComFiltros(Message_types tipo, String nome, String local, LocalDate limData1, LocalDate limData2, int horaInicio, int horaFim) {
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

    public LocalDate getLimData1() {
        return limData1;
    }

    public LocalDate getLimData2() {
        return limData2;
    }

    public int getHoraInicio() {
        return horaInicio;
    }

    public int getHoraFim() {
        return horaFim;
    }
}

