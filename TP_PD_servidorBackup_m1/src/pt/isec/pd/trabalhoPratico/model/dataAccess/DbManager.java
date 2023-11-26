package pt.isec.pd.trabalhoPratico.model.dataAccess;


import pt.isec.pd.trabalhoPratico.model.classesComunication.Msg_Cria_Evento;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Msg_Edita_Evento;
import pt.isec.pd.trabalhoPratico.model.recordDados.Utilizador;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DbManager {
    private static final String dbAdress = "Base de Dados/copiaDb.db";
    private static final String dbUrl= "jdbc:sqlite:"+dbAdress;
    public static String getDbAdress() {
        return dbAdress;
    }
    private int versao;


    public static int getVersaoDb(){
        try(Connection connection=DriverManager.getConnection(dbUrl)) {
            String GetQuery="Select versao_id FROM VERSAO";
            PreparedStatement statement=connection.prepareStatement(GetQuery);
            ResultSet rs= statement.executeQuery();
            if(rs.isBeforeFirst()){
                rs.next();
                return rs.getInt("versao_id");

            }else{
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean RegistoNovoUser(Utilizador user, String password){
        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement())
        {
            String createEntryQuery = "INSERT INTO Utilizador (email,nome,numero_estudante,palavra_passe,tipo_utilizador) VALUES ('"
                    + user.email() + "','" + user.nome() + "','" + user.numIdentificacao() + "','" + password +"','" + "cliente" +"')";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??

            if(statement.executeUpdate(createEntryQuery)<1){
                return false;
            }
            else{
                return true;
            }
        } catch (SQLException e) {
         return false;
        }
    }
    public static boolean edita_registo( Utilizador user, String pasword ){
        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement()){

            //Somente para teste de ligação a base de dados
            /*String createEntryQuery = "INSERT INTO Codigo_Registo (n_codigo_registo,nome_evento) VALUES ('"
                    + codigo_registo+"','" + nome_evento+ "')";*/
            String GetQuery = "SELECT * FROM Utilizador where email='" + user.email() + "';";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??
            ResultSet rs=statement.executeQuery(GetQuery);

            if(rs.isBeforeFirst())
            {   rs.next();
                System.out.println(rs.getString("email"));
                String updateQuery = "UPDATE Utilizador SET nome=?, numero_estudante=?, palavra_passe=? WHERE email=?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, user.nome());
                preparedStatement.setInt(2, user.numIdentificacao());
                preparedStatement.setString(3, pasword);
                preparedStatement.setString(4, user.email());
                preparedStatement.executeUpdate();

                return true;
            }
            else{
                System.out.println("Nao foi encontrado nenhum utilizador com esse email");
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public static void setVersao(int versao) {

        try (Connection connection=DriverManager.getConnection(dbUrl)){
            String UpdateVersao="UPDATE Versao SET versao_id=? where versao_id=?";
            PreparedStatement statement=connection.prepareStatement(UpdateVersao);
            statement.setInt(1,versao);
            statement.setInt(2,versao-1);
            if( statement.executeUpdate()<1)
                System.out.println("Erro a atualizar a versao");
            else{

                System.out.println("Versao atualizada com sucesso");}
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static boolean Cria_evento(Msg_Cria_Evento evento){//String nome, String local, Date data, LocalTime horainicio, LocalTime horafim) {
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dataString = dateFormat.format(evento.getData()); //Alterei isso para termos a data nesse formato, facilita os testes

            String createEntryQuery = "INSERT INTO Evento (nome_evento,local,data_realizacao,hora_inicio,hora_fim) VALUES ('"
                    + evento.getNome() +"','" + evento.getLocal() +"','" + dataString +"','" + evento.getHoreInicio() +"','" + evento.getHoraFim() +"')";

            if(statement.executeUpdate(createEntryQuery)<1){
                System.out.println("Erro na criacao do evento");
                return false;
            }
            else{
                System.out.println("Evento criado com sucesso");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public static boolean submitcod(int codigo,String nome_evento,String emailuser){
        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement())
        {


            String GetQuery = "SELECT * FROM Codigo_Registo where nome_evento=? AND validade>?";
            PreparedStatement getquery=connection.prepareStatement(GetQuery);
            getquery.setString(1,nome_evento);
            getquery.setLong(2,0);
            ResultSet rs=getquery.executeQuery();

            if(rs.isBeforeFirst())
            {   rs.next();
                Date Data=new Date();
                long datamili=Data.getTime();
                System.out.println("o data atual e "+datamili);
                System.out.println(rs.getTimestamp("validade").getTime());
                if(rs.getTimestamp("validade").getTime()<datamili){
                    System.out.println("Fora de validade");
                    String EliminaCodigosAnterioresQuery = "UPDATE Codigo_Registo SET validade=0 WHERE nome_evento = ?";//
                    PreparedStatement expiraStatement = connection.prepareStatement(EliminaCodigosAnterioresQuery);
                    expiraStatement.setString(1, nome_evento); // Define o valor do nome_evento para o ? da query
                    expiraStatement.executeUpdate();// se existirem codigos antigos são eliminados se nao existirem nao acontece nada
                    return false;
                }

                if(rs.getInt("n_codigo_registo")==codigo  ){
                    String createEntryQuery = "INSERT INTO Assiste (nome_evento,email) VALUES ('"
                            + nome_evento+"','" +emailuser+"')";// qual o valor que é suposto colocar no idassiste??

                    if(statement.executeUpdate(createEntryQuery)<1){
                        System.out.println("Entry insertion or update failed");
                        return false;
                    }
                    else{
                        System.out.println("Entry insertion succeeded");

                        return true;
                    }

                }else{
                    System.out.println("Codigo invalido");
                    return false;
                }
            }
            else{
                System.out.println("Nenhum item corresponde a pesquisa");
                return false;
            }
        } catch (SQLException e) {

            System.out.println(e.getMessage());
            return false;
        }
    }
    public static boolean Edita_evento(Msg_Edita_Evento evento) {
        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {

            // Estou a verificar se há presenças na tabela assiste para o evento (pelo seu nomeantigo que é o seu id)
            String checkAssisteQuery = "SELECT COUNT(*) FROM assiste WHERE nome_evento = '" + evento.getNome() + "'";
            ResultSet resultSet = statement.executeQuery(checkAssisteQuery);
            resultSet.next();
            int presencas = resultSet.getInt(1);



                // Se não houver presenças edita todos os campos
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String dataString = dateFormat.format(evento.getData()); //Alterei isso para termos a data nesse formato, facilita os testes mas sempre se pode alterar
                String updateEventQuery = "UPDATE Evento SET data_realizacao = '" + dataString + "', hora_inicio = '" + evento.getHoreInicio() + "', hora_fim = '" + evento.getHoraFim() + "', nome_evento = '" + evento.getNome() + "', local = '" + evento.getLocal()+ "' WHERE nome_evento = '" + evento.getNome()
                        + "'";

                if (statement.executeUpdate(updateEventQuery) < 1) {
                    System.out.println("Erro na edição do evento");
                    return false;
                } else {
                    System.out.println("Evento editado com sucesso");

                }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    public static boolean Elimina_evento(String nome_evento) {
        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {

            // Verifico se há presenças na tabela "assiste" para o evento
            String checkAssisteQuery = "SELECT COUNT(*) FROM assiste WHERE nome_evento = '" + nome_evento + "'";
            ResultSet resultSet = statement.executeQuery(checkAssisteQuery);
            resultSet.next();
            int presencas = resultSet.getInt(1);

            if (presencas > 0) {
                System.out.println("Nao e possível eliminar o evento, pois o mesmo contem presencas.");
                return false;
            } else {
                // Se não houver presenças, elimina o evento
                String deleteEventQuery = "DELETE FROM Evento WHERE nome_evento = '" + nome_evento + "'";

                if (statement.executeUpdate(deleteEventQuery) < 1) {
                    System.out.println("Erro na eliminacao do evento");
                    return false; // erro na eliminação do evento
                } else {
                    System.out.println("Evento eliminado com sucesso");

                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public static boolean InserePresencas(String nomeEvento, String[] emails) {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {

            //Att: Vi na net que para cenas na bd que envolvam duas chaves primárias deve se utilizar esse PreparedStatement
            //É rezar que funcione bem e que seja assim mesmo

            // Verificar se o evento existe
            String verificaEventoQuery = "SELECT COUNT(*) FROM Evento WHERE nome_evento = ?";
            PreparedStatement eventoStatement = connection.prepareStatement(verificaEventoQuery); //Para preparar a consulta
            eventoStatement.setString(1, nomeEvento); //Para substituir o ? pelo nome do evento, ou seja indexar o nome do evento
            int eventosEncontrados = eventoStatement.executeQuery().getInt(1); //Para executar a consulta e devolver o resultado

            for (String emailEstudante : emails) {
                // Verificar se os estudantes da lista existem na db

                String verificaEstudanteQuery = "SELECT COUNT(*) FROM Utilizador WHERE email = ?";
                PreparedStatement alunoStatement = connection.prepareStatement(verificaEstudanteQuery);
                alunoStatement.setString(1, emailEstudante);
                int estudantesEncontrados = alunoStatement.executeQuery().getInt(1);

                if (eventosEncontrados == 1 && estudantesEncontrados == 1) {
                    // Se o evento e o aluno existirem insere a presença
                    String inserePresencaQuery = "INSERT INTO assiste (nome_evento, email) VALUES (?, ?)";
                    PreparedStatement presencaStatement = connection.prepareStatement(inserePresencaQuery);
                    presencaStatement.setString(1, nomeEvento);
                    presencaStatement.setString(2, emailEstudante);

                    int rowsAffected = presencaStatement.executeUpdate();

                    if (rowsAffected == 1) {
                        System.out.println("A presenca do estudante " + emailEstudante + " no evento " + nomeEvento + " foi registada com sucesso");
                    } else {
                        System.out.println("Erro ao registar a presença do estudante " + emailEstudante + ".");
                        //return false;
                    }
                } else {
                    System.out.println("Evento e/ou aluno nao existem.");
                    //return false;
                }
            }

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean EliminaPresencas(String nomeEvento, String [] emails) {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {

            for (String emailEstudante : emails) {
                String eliminaPresencaQuery = "DELETE FROM assiste WHERE nome_evento = ? AND email = ?";
                PreparedStatement eliminaPresencaStatement = connection.prepareStatement(eliminaPresencaQuery);
                eliminaPresencaStatement.setString(1, nomeEvento);
                eliminaPresencaStatement.setString(2, emailEstudante);

                int rowsAffected = eliminaPresencaStatement.executeUpdate();

                if (rowsAffected == 1) {
                    System.out.println("A Presença do estudante " + emailEstudante +" do evento " + nomeEvento + " foi eliminada com sucesso.");
                } else {
                    System.out.println("Nao foi encontrada a presenca do estudante " + emailEstudante + " no evento " + nomeEvento + ".");
                }
            }

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


}


