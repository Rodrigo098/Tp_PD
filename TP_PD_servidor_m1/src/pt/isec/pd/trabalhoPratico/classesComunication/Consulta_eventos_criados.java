package pt.isec.pd.trabalhoPratico.classesComunication;

public class Consulta_eventos_criados extends Geral{// esta classe seria para a resposta do servidor ao cliente
    private String nome;
    private String [] filtros;

    public Consulta_eventos_criados(String nome, String ... filtros) {
        super(Message_types.CONSULTA_EVENTOS);
        this.nome = nome;
        this.filtros = filtros;
    }

    public String getNome() {
        return nome;
    }
    public String [] getFiltros() {
        return filtros;
    }
}