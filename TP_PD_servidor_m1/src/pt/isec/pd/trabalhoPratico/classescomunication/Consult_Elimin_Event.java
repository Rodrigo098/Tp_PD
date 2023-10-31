package pt.isec.pd.trabalhoPratico.classescomunication;

public class Consult_Elimin_Event extends Geral{
    // Esta classe seria só usada para enviar o nome do evento para dps o servidor responder conforme a operação
  private   String nome;
    public Consult_Elimin_Event(String nome, Message_types tipo) {
        this.nome = nome;
        this.tipo=tipo;
    }

    public String getNome() {
        return nome;
    }
}
