package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class Msg_String_Int extends Geral{
    private final String conteudo;
    private int numero;
    public Msg_String_Int(String conteudo, int numero, Message_types tipo) {
        super(tipo);
        this.conteudo = conteudo;
        this.numero = numero;
    }

    public String getConteudo() {
        return conteudo;
    }
    public int getNumero() {
        return numero;
    }
}