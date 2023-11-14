package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class Msg_String extends Geral{
    private String conteudo;
    public Msg_String(String conteudo, Message_types tipo) {
        super(tipo);
        this.conteudo = conteudo;
    }

    public String getConteudo() {
        return conteudo;
    }
}
