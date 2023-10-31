package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class Submissao_codigo extends Geral{

// se trocar para String consigo eliminar esta Classe tmb

    private long codigo;

    public Submissao_codigo(long codigo) {
        super(Message_types.SUBMICAO_COD);
        this.codigo = codigo;
    }

    public long getCodigo() {
        return codigo;
    }
}
