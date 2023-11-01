package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.classesDados.Registo;

public class RecebeListaRegistos extends Geral{
    private final Registo[] lista;

    public RecebeListaRegistos(Message_types tipo, Registo ... lista) {
        super(tipo);
        this.lista = lista;
    }

    public Registo[] getLista() {
        return lista;
    }
}
