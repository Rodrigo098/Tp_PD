package pt.isec.pd.trabalhoPratico.model.classesComunication;

public enum Message_types {// varios tipos de mensagens que serão usados, se quiserem podem adicionar mais ou remover(mas remover atenção que pode causar problemas noutras partes do codigo)
    //COMUM
    LOGIN, LOGOUT,

    //UTILIZADOR
    REGISTO, EDITAR_REGISTO, SUBMICAO_COD, CONSULTA_PRES_UTILIZADOR, CSV_UTILIZADOR,

    //ADMINISTRADOR
    CRIA_EVENTO, EDIT_EVENTO, ELIMINAR_EVENTO, CONSULTA_EVENTOS,
    GERAR_COD, CONSULTA_PRES_EVENT, CSV_PRESENCAS_UTI_NUM_EVENTO,
    CSV_PRESENCAS_DO_EVENTO,
    ELIMINA_PRES, INSERE_PRES,

    //RESULTADOS
    ERRO, INVALIDO, VALIDO, ADMINISTRADOR, UTILIZADOR,

    //ASYNC
    ATUALIZACAO
}
