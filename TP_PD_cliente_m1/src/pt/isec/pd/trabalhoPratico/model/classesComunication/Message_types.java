package pt.isec.pd.trabalhoPratico.model.classesComunication;

public enum Message_types {// varios tipos de mensagens que serão usados, se quiserem podem adicionar mais ou remover(mas remover atenção que pode causar problemas noutras partes do codigo)
    //COMUM
    LOGIN, LOGOUT,

    //UTILIZADOR
    REGISTO, EDITAR_REGISTO, SUBMICAO_COD, CONSULTA_PRES_UTILIZADOR, CSV_UTILIZADOR,

    //ADMINISTRADOR
    CRIA_EVENTO, EDIT_EVENTO, ELIMINAR_EVENTO, CONSULTA_EVENTOS,
    GERAR_COD, CONSULTA_PRES_EVENT, CSV_ADMINISTRADOR,
    CONSULT_EVENT_UTILIZADOR,
    ELIMINA_PRES, INSERE_PRES, UPDATE_INF,

    //CONFIRMACOES
    ERRO, INVALIDO, VALIDO, ADMINISTRADOR, UTILIZADOR,

    //ASYNC
    UPDATE_EVENTOS, UPDATE_REGISTOS
}
