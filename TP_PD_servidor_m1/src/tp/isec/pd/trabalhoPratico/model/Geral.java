package tp.isec.pd.trabalhoPratico.model;



import java.io.Serializable;

public class Geral implements Serializable {
    static final long serialVersionUID = 1L;
    private Message_types tipo;

    public Geral(Message_types tipo){
        this.tipo = tipo;
    }
    public Message_types getTipo() {
        return tipo;
    }


    // criar um enum e defino aqui o tipo de mensagem
//Comuns:
    //login(email,password)
    //logout

//Cliente:
    //registo(nome,numero_estudante,email(vai servir de username/id),password)
    //editar(campo a editar, email)
    //submissão(codigo,email)
    //envio_tabela_de_eventos(...)
    //pedido_consulta(int flag)

//Administrador:
    //cria_evento(nome, local, data de realização,hora de início ,hora de fim)
    //editar_evento(nome)
    //elimina_evento(nome)
    //envia_tabela_de_eventos
    //envio_registo_presenças()
    //envio_codigo_presenças(codigo)
    //consultar_eventos
    //eliminar_presenças
    //inserir_presenças
    //Atualização assíncrona sempre que se alterar dados na bd

}
