package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class ConsultaEventos_EliminaPresencas extends Geral{// podiamos usar esta classe tmb para inserir presenças
    private String [] lista;
    private String nome_evento;

    public ConsultaEventos_EliminaPresencas(String nome_evento, Message_types tipo, String ... lista) {// assim podem escrever quantos emails quiserem
       super(tipo);
       this.nome_evento = nome_evento;
       this.lista = lista;;
    }

    public String[] getLista() {
        return lista;//devolve um array por questões de segurança para nao tar a devolver a referencia da lisraa
    }

    public String getNome_evento() {
        return nome_evento;
    }
}
