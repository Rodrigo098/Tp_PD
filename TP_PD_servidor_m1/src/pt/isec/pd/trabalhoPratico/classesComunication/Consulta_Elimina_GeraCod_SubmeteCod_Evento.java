package pt.isec.pd.trabalhoPratico.classesComunication;

import java.util.*;

public class Consulta_Elimina_GeraCod_SubmeteCod_Evento extends Geral{
    // Esta classe seria só usada para enviar o nome do evento para dps o servidor responder conforme a operação
  private   String nome;
  private static final ArrayList<Message_types> acepted=new ArrayList<>( // usavamos nesta variavel os tipos que eram aceitados e assim impedia se que o tipo fosse errado com o objeto assim evitava mos de andar com instance of no codigo
            Arrays.asList(Message_types.GERAR_COD, Message_types.SUBMICAO_COD,
                    Message_types.ELIMINAR_EVENTO,Message_types.CONSULTA_EVENTOS,
                    Message_types.CONSULTA_PRES_UTILIZADOR)
            );
    public Consulta_Elimina_GeraCod_SubmeteCod_Evento(String nome, Message_types tipo) {
        super(tipo);
        if(!acepted.contains(tipo))
            throw new RuntimeException("Tipo invalido");
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
