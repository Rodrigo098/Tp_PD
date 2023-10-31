package pt.isec.pd.trabalhoPratico.classesComunication;

public class Submissao_codigo extends Geral{

    private long codigo;
  //  private static final long serialVersionUID = 1L;


    public Submissao_codigo(long codigo) {

        this.codigo = codigo;
        tipo=Message_types.SUBMIT_COD;
    }

    public long getCodigo() {
        return codigo;
    }
}
