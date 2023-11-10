package pt.isec.pd.trabalhoPratico.model.classesDados;

import java.io.Serializable;

public record Utilizador(String nome, String email, String numIdentificacao) implements Serializable {
    @Override
    public String toString() {
        return nome + ";" + numIdentificacao + ";" + email;
    }
}
