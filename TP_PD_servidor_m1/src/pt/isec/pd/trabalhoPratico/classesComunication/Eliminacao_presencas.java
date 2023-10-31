package pt.isec.pd.trabalhoPratico.classesComunication;

import java.util.Arrays;
import java.util.List;

public class Eliminacao_presencas extends Geral{// podiamos usar esta classe tmb para inserir presenças
    private List<String> emails;
    private String nome_evento;

    public Eliminacao_presencas(String nome_evento,String ... emails) {// assim podem escrever quantos emails quiserem
       super(Message_types.ELIMINA_PRES);
       this.nome_evento = nome_evento;
       this.emails = Arrays.stream(emails).toList();;
    }

    public String[] getEmails() {
        String [] emails = new String[this.emails.size()];
        for (String email : this.emails){
            emails[this.emails.indexOf(email)] = email;
        }
        return emails;//devolve um array por questões de segurança para nao tar a devolver a referencia da lisraa
    }

    public String getNome_evento() {
        return nome_evento;
    }
}
