package pt.isec.pd.trabalhoPratico.model.classesDados;

public record Utilizador(String nome, String email, String numIdentificacao) {
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
        return nome + ";" + numIdentificacao + ";" + email;
    }
}
