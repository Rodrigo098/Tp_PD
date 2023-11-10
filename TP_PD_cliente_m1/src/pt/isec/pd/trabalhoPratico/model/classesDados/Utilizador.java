package pt.isec.pd.trabalhoPratico.model.classesDados;

import java.io.Serializable;

public record Utilizador(String nome, String email, String numIdentificacao) implements Serializable {
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
