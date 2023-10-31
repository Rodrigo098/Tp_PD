package pt.isec.pd.trabalhoPratico.classesComunication;

public class Gera_codigo_eventos extends Geral{
   private String nomevento;
    private int validade;

    public Gera_codigo_eventos(String nomevento, int validade) {
        this.nomevento = nomevento;
        this.validade = validade;
        tipo=Message_types.GERA_COD;
    }

    public String getNomevento() {
        return nomevento;
    }

    public int getValidade() {
        return validade;
    }
}
