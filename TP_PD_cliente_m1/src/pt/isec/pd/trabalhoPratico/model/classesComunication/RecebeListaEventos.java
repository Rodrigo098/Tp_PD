package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;

public class RecebeListaEventos extends Geral{
    private final Evento[] lista;

    public RecebeListaEventos(Message_types tipo, Evento ... lista) {
        super(tipo);
        this.lista = lista;
    }

    public Evento[] getLista() {
        return lista;
    }
}
