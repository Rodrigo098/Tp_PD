package pt.isec.pd.trabalhoPratico.model.classesDados;

public class Utilizador {
    private final String nome, email, numIdentificacao;

    public Utilizador(String nome, String email, String numIdentificacao) {
        this.nome = nome;
        this.email = email;
        this.numIdentificacao = numIdentificacao;
    }
    public String getNome() {
        return nome;
    }
    public String getEmail() {
        return email;
    }
    public String getNumIdentificacao() {
        return numIdentificacao;
    }
    @Override
    public String toString() {
        return "Utilizador: " + nome + " (" + email + "), nº " + numIdentificacao;
    }
}
