package pt.isec.pd.trabalhoPratico.model.classesComunication;

import java.io.Serializable;

public class Geral implements Serializable {
    static final long serialVersionUID = 1L;
    private final Message_types tipo;

    public Geral(Message_types tipo){
        this.tipo = tipo;
    }
    public Message_types getTipo() {
        return tipo;
    }
}
