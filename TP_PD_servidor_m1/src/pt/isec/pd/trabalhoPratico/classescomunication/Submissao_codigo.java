package pt.isec.pd.trabalhoPratico.classescomunication;

public class Submissao_codigo extends Geral{

    private long codigo;
  //  private static final long serialVersionUID = 1L;


    public Submissao_codigo(long codigo) {

        this.codigo = codigo;
    }

    public long getCodigo() {
        return codigo;
    }
}
