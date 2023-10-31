package pt.isec.pd.trabalhoPratico.classesComunication;

public class Consulta_eventos_criados extends Geral{// esta classe seria para a resposta do servidor ao cliente
    private String nome;
    private Cria_evento eventoConsultar;

    public Consulta_eventos_criados(String nome, Cria_evento eventoConsultar) {
        super(Message_types.CONSULTA_EVENTOS);
        this.nome = nome;
        this.eventoConsultar = eventoConsultar;
    }

    public String getNome() {
        return nome;
    }
}