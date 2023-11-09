package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;

public class Msg_ListaEventos extends Geral{
    private final Evento[] lista;

    public Msg_ListaEventos(Message_types tipo, Evento ... lista) {
        super(tipo);
        this.lista = lista;
    }

    public Evento[] getLista() {
        return lista;
    }
}
