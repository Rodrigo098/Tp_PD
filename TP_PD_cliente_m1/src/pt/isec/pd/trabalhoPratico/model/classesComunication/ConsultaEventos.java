package pt.isec.pd.trabalhoPratico.model.classesComunication;

public class ConsultaEventos extends Geral{
    // esta classe seria para a resposta do servidor ao cliente dos eventos que ele criou ou que que o uti foi
    private String nome;
    private String [] filtros;

    public ConsultaEventos(String nome, String ... filtros) {
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