package pt.isec.pd.trabalhoPratico.model.classesComunication;

import pt.isec.pd.trabalhoPratico.model.recordDados.Utilizador;

public class Msg_ListaRegistos extends Geral{
    private final Utilizador[] lista;

    public Msg_ListaRegistos(Message_types tipo, Utilizador... lista) {
        super(tipo);
        this.lista = lista;
    }

    public Utilizador[] getLista() {
        return lista;
    }
}