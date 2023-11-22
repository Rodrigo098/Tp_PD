package pt.isec.pd.trabalhoPratico.model.recordDados;

import java.io.Serializable;

public record DadosRmi(String Registo, String nome_servico, int versao) implements Serializable {

}
