package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class RecebeListas extends Geral{
    private final Evento [] lista;

    public RecebeListas(Message_types tipo, Evento ... lista) {
        super(tipo);
        this.lista = lista;
    }

    public Evento[] getLista() {
        return lista;
    }
}
