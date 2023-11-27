package pt.isec.pd.trabalhoPratico.model.recordDados;

import java.io.Serial;
import java.io.Serializable;

public record Utilizador(String nome, String email, int numIdentificacao) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Override
    public String toString() {
        return "Nome: " + nome + "; Número de identificacao: " + numIdentificacao + "; Email: " + email;
    }
}