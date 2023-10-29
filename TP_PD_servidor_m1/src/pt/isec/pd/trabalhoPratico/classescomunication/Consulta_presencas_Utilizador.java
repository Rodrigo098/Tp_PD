package pt.isec.pd.trabalhoPratico.classescomunication;

public class Consulta_presencas_Utilizador extends Geral{
    private String email,nome_evento;

    public Consulta_presencas_Utilizador(String email, String nome_evento) {
        this.email = email;
        this.nome_evento = nome_evento;
    }

    public String getEmail() {
        return email;
    }

    public String getNome_evento() {
        return nome_evento;
    }
}
