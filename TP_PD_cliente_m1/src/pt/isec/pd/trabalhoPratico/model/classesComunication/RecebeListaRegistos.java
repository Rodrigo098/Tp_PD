package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.classesDados.Presenca;

public class RecebeListaRegistos extends Geral{
    private final Presenca[] lista;

    public RecebeListaRegistos(Message_types tipo, Presenca... lista) {
        super(tipo);
        this.lista = lista;
    }

    public Presenca[] getLista() {
        return lista;
    }
}
