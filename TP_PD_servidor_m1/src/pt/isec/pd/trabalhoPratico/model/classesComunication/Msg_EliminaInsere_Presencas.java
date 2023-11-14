package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class Msg_EliminaInsere_Presencas extends Geral{// podiamos usar esta classe tmb para inserir presenças
    private String [] lista;
    private String nome_evento;

    public Msg_EliminaInsere_Presencas(Message_types tipo, String nome, String ... lista) {// assim podem escrever quantos emails quiserem
       super(tipo);
       this.lista = lista;
       nome_evento = nome;
    }

    public String[] getLista() {
        return lista;//devolve um array por questões de segurança para nao tar a devolver a referencia da lisraa
    }

    public String getNome_evento() {
        return nome_evento;
    }
}
