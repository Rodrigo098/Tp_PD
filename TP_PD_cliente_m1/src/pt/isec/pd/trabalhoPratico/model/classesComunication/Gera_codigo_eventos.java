package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class Gera_codigo_eventos extends Geral{
    private String nomeEvento;

    public Gera_codigo_eventos(String nomeEvento){
        super(Message_types.GERAR_COD);
        this.nomeEvento = nomeEvento;
    }

    public String getNomevento() {
        return nomeEvento;
    }
}
