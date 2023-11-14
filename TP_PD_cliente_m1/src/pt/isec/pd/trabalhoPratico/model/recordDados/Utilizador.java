package pt.isec.pd.trabalhoPratico.model.recordDados;

import java.io.Serializable;

public record Utilizador(String nome, String email, int numIdentificacao) implements Serializable {
    @Override
    public String toString() {
        return nome + ";" + numIdentificacao + ";" + email;
    }
}
