package pt.isec.trabalhoPratico.classesComunication;

import java.util.Arrays;

public class ConsultaEventos_EliminaPresencas_InserePresencas extends Geral{// podiamos usar esta classe tmb para inserir presenças
    private  String []lista;
    private String nome_evento;

    public ConsultaEventos_EliminaPresencas_InserePresencas(String nome_evento, Message_types tipo, String ... emails) {// assim podem escrever quantos emails quiserem
       super(tipo);
       this.nome_evento = nome_evento;
       this.lista = emails;

    }

    public String[] getEmails() {

        return lista;//devolve um array por questões de segurança para nao tar a devolver a referencia da lisraa
    }

    public String getNome_evento() {
        return nome_evento;
    }
}
