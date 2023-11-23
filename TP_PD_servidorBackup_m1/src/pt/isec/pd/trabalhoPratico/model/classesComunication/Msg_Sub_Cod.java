package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class Msg_Sub_Cod extends Geral{
    public String email,conteudo;
    public int numero;
    public Msg_Sub_Cod(Message_types tipo) {
        super(tipo);
    }

    public String getEmail() {
        return email;
    }

    public String getConteudo() {
        return conteudo;
    }

    public int getNumero() {
        return numero;
    }
}
