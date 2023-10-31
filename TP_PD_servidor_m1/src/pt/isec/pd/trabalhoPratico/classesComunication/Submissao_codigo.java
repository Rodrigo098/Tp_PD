package pt.isec.pd.trabalhoPratico.classesComunication;

public class Submissao_codigo extends Geral{
    private long codigo;

    public Submissao_codigo(long codigo) {
        super(Message_types.SUBMICAO_COD);
        this.codigo = codigo;
    }

    public long getCodigo() {
        return codigo;
    }
}
