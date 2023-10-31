package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class Consulta_Elimina_GeraCod_SubmeteCod_Evento extends Geral{
    // Esta classe seria só usada para enviar o nome do evento para dps o servidor responder conforme a operação
    private   String nome;
    public Consulta_Elimina_GeraCod_SubmeteCod_Evento(String nome, Message_types tipo) {
        super(tipo);
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
