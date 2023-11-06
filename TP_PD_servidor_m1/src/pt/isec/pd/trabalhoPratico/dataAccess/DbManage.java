package pt.isec.pd.trabalhoPratico.dataAccess;

import pt.isec.pd.trabalhoPratico.model.classesComunication.Cria_evento;
import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;
import pt.isec.pd.trabalhoPratico.model.classesDados.Utilizador;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DbManage {
    private static final String dbAdress = "databasePD.db";
    private static final String dbUrl= "jdbc:sqlite:"+dbAdress;

    public DbManage() {
        int codigo_registo = 1;
        String nome_evento = "Evento1";
        String email="email";
/*
        try(Connection connection = DriverManager.getConnection(dbUrl);

         Statement statement = connection.createStatement()){

            //Somente para teste de ligação a base de dados
            /*String createEntryQuery = "INSERT INTO Codigo_Registo (n_codigo_registo,nome_evento) VALUES ('"
                    + codigo_registo+"','" + nome_evento+ "')";
            String createEntryQuery = "INSERT INTO Versao (versao_id,descricao) VALUES ('"
                    + codigo_registo+"','" + email+"')";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??

            if(statement.executeUpdate(createEntryQuery)<1){
                System.out.println("Entry insertion or update failed");
            }
            else{
                System.out.println("Entry insertion succeeded");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

    }
    public static void Registonovouser(Utilizador user,String password){
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){
            int num_est=Integer.parseInt(user.getNumIdentificacao());
            //Somente para teste de ligação a base de dados
            /*String createEntryQuery = "INSERT INTO Codigo_Registo (n_codigo_registo,nome_evento) VALUES ('"
                    + codigo_registo+"','" + nome_evento+ "')";*/
            String createEntryQuery = "INSERT INTO Utilizador (email,nome,numero_estudante,palavra_passe) VALUES ('"
                    + user.getEmail()+"','" + user.getNome()+"','" +num_est+"','" +password+"')";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??

            if(statement.executeUpdate(createEntryQuery)<1){
                System.out.println("Entry insertion or update failed");
            }
            else{
                System.out.println("Entry insertion succeeded");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    public static boolean autentica_user(String user, String password){
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){
            String GetQuery = "SELECT * FROM Utilizador where email='" + user + "';";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??
            ResultSet rs=statement.executeQuery(GetQuery);


            if(!rs.next())
            {
                System.out.println("Couldn't find the value");
                return false;
            }
            else{
                System.out.println(rs.getString("email"));
                System.out.println(rs.getString("palavra_passe"));
                return rs.getString("palavra_passe").equals(password);// devolve true se a password for a mesma
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public static boolean edita_registo( Utilizador user, String pasword){
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){

            //Somente para teste de ligação a base de dados
            /*String createEntryQuery = "INSERT INTO Codigo_Registo (n_codigo_registo,nome_evento) VALUES ('"
                    + codigo_registo+"','" + nome_evento+ "')";*/
            String mail= user.getEmail();
            String GetQuery = "SELECT * FROM Utilizador where email='" + mail + "';";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??
            ResultSet rs=statement.executeQuery(GetQuery);

            if(!rs.next())
            {
                System.out.println("Couldn't find the value");

                return false;
            }
            else{
                System.out.println(rs.getString("email"));
                int num_est=Integer.parseInt(rs.getString("numero_estudante"));
                String updateQuery = "UPDATE Utilizador SET nome=?, numero_estudante=?, palavra_passe=? WHERE email=?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, user.getNome());
                preparedStatement.setInt(2, Integer.parseInt(user.getNumIdentificacao()));
                preparedStatement.setString(3, pasword);
                preparedStatement.setString(4, user.getEmail());
                preparedStatement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }
    public static boolean submitcod(String codigo,String nome_evento,String emailuser){
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){


            String GetQuery = "SELECT * FROM Codigo_Registo where nome_evento='" + nome_evento + "';";
            ResultSet rs=statement.executeQuery(GetQuery);
            int idassiste=0;//eu nao sei o que por aqui;
            if(!rs.next())
            {
                if(rs.getInt("n_codigo_registo")==Integer.parseInt(codigo)){
                    String createEntryQuery = "INSERT INTO Assiste (assiste_id,nome_evento,email) VALUES ('"
                            + idassiste+"','" + nome_evento+"','" +emailuser+"')";// qual o valor que é suposto colocar no idassiste??

                    if(statement.executeUpdate(createEntryQuery)<1){
                        System.out.println("Entry insertion or update failed");
                        return true;
                    }
                    else{
                        System.out.println("Entry insertion succeeded");
                        return false;
                    }

                }
            }
            else{
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;

    }
    public static boolean CriaEvento(Evento evento){
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){

            //Somente para teste de ligação a base de dados
            /*String createEntryQuery = "INSERT INTO Codigo_Registo (n_codigo_registo,nome_evento) VALUES ('"
                    + codigo_registo+"','" + nome_evento+ "')";*/
            String createEntryQuery = "INSERT INTO Evento (nome_evento,local,data_realizacao,hora_inicio,hora_fim) VALUES ('"
                    + evento.getNome()+"','" + evento.getLocal()+"','" +evento.getData()+"','" +evento.getHoraInicio()+"','" +evento.getHoraFim()+"')";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??

            if(statement.executeUpdate(createEntryQuery)<1){
                System.out.println("Entry insertion or update failed");
                return false;
            }
            else{
                System.out.println("Entry insertion succeeded");
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }


    }
    public static String[] Presencas_user(String nome_utilizador){
        List<String> res=new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){
            String GetQuery = "SELECT * FROM Assiste where email='" + nome_utilizador + "';";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??
            ResultSet rs=statement.executeQuery(GetQuery);


            while (rs.next()){
             res.add(   rs.getString("nome_evento"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        String []devolve= (String[]) res.toArray();

        return devolve;

    }
    public static String[] Presencas_evento(String nome_evento){
        List<String> res = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){
            String GetQuery = "SELECT * FROM Assiste where nome_evento='" + nome_evento + "';";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??
            ResultSet rs=statement.executeQuery(GetQuery);


            while (rs.next()){
                res.add(rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        String []devolve= (String[]) res.toArray();

        return devolve;
    }

//----------------------------------------------------------------------------Novas funções para o Admin
    public static boolean Cria_evento(String nome, String local, Date data, LocalTime horainicio, LocalTime horafim) {
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){

            String createEntryQuery = "INSERT INTO Evento (nome_evento,local,data_realizacao,hora_inicio,hora_fim) VALUES ('"
                    + nome+"','" + local+"','" +data+"','" +horainicio+"','" +horafim+"')";

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
    return true;
    }


    public static boolean Edita_evento(Cria_evento evento, String antigoNome) {
        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {

            // Estou a verificar se há presenças na tabela assiste para o evento (pelo seu nomeantigo que é o seu id)
            String checkAssisteQuery = "SELECT COUNT(*) FROM assiste WHERE nome_evento = '" + antigoNome + "'";
            ResultSet resultSet = statement.executeQuery(checkAssisteQuery);
            resultSet.next();
            int presencas = resultSet.getInt(1);

            if (presencas > 0) {
                // Se houver presenças registadas,apenas permite editar o nome e o local (??)
                String updateEventQuery = "UPDATE Evento SET nome_evento = '" + evento.getNome() + "', local = '" + evento.getLocal() + "' WHERE nome_evento = '" + antigoNome + "'";

                if (statement.executeUpdate(updateEventQuery) < 1) {
                    System.out.println("Erro na edição do evento");
                    return false;
                } else {
                    System.out.println("Nome e local do evento editados com sucesso.");
                }
            } else {
                // Se não houver presenças edita todos os campos
                String updateEventQuery = "UPDATE Evento SET data_realizacao = '" + evento.getData() + "', hora_inicio = '" + evento.getHorainicio() + "', hora_fim = '" + evento.getHorafim() + "', nome_evento = '" + evento.getNome() + "', local = '" + evento.getLocal()+ "' WHERE nome_evento = '" + antigoNome
                        + "'";

                if (statement.executeUpdate(updateEventQuery) < 1) {
                    System.out.println("Erro na edição do evento");
                    return false;
                } else {
                    System.out.println("Evento editado com sucesso");
                }
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
        return true;
    }

    public static String[] Consulta_eventos(Cria_evento evento) {
        List<Evento> eventos = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {

            String filtroEvento = "SELECT * FROM Evento WHERE 1=1"; // Começa com "1=1" para mostrar todos resultados
                                                                  // A medida que campos forem não null ele inclui na pesquisa

            if (evento.getNome() != null && !evento.getNome() .isEmpty()) {
                filtroEvento += " AND nome_evento LIKE '%" + evento.getNome()  + "%'";
            }

            if (evento.getLocal() != null) {
                filtroEvento += " AND local LIKE '%" + evento.getLocal() + "'";
            }

            if (evento.getData() != null) {
                filtroEvento += " AND data_realizacao = '" + evento.getData() + "'";
            }

            if (evento.getHorainicio() != null) {
                filtroEvento += " AND hora_inicio = '" + evento.getHorainicio() + "'";
            }

            if (evento.getHorafim() != null) {
                filtroEvento += " AND hora_fim = '" + evento.getHorafim() + "'";
            }

            ResultSet resultSet = statement.executeQuery(filtroEvento);

            while (resultSet.next()) {
                String nome = resultSet.getString("nome_evento");
                String local = resultSet.getString("local");
                Date dataRealizacao = resultSet.getDate("data_realizacao");
                LocalTime horaInicio = resultSet.getTime("hora_inicio").toLocalTime();
                LocalTime horaFim = resultSet.getTime("hora_fim").toLocalTime();

                //Acabei por converter os campos de data e hora para string pq vi que está assim na classe, mas precisamos de ver isso
                Evento evento_result = new Evento(nome,local, dataRealizacao.toString(), horaInicio.toString(), horaFim.toString());
                eventos.add(evento_result);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        String [] res = (String[]) eventos.toArray();
        return res;
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

                String verificaEstudanteQuery = "SELECT COUNT(*) FROM Usuario WHERE email = ?";
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
                        System.out.println("Presenca do estudante " + emailEstudante + "no evento" + nomeEvento + " registada com sucesso");
                    } else {
                        System.out.println("Erro ao registar a presença do estudante " + emailEstudante + ".");
                        return false;
                    }
                } else {
                    System.out.println("Evento e/ou aluno nao existem.");
                    return false;
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
                    System.out.println("A Presença do estudante " + emailEstudante +"do evento" + nomeEvento + " foi eliminada com sucesso.");
                } else {
                    System.out.println("Nao foi encontrada a presenca do estudante" + emailEstudante + "no evento " + nomeEvento + ".");
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

//Gerar codigo (Este é complicadinho =D)

    public static int GeraCodigoRegisto(Cria_evento evento, int validadeMinutos) {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            Statement statement = connection.createStatement();

            // Para verificar se o evento  se encontra a decorrer
            String verificaEventoQuery = "SELECT data_realizacao, hora_inicio, hora_fim FROM Evento WHERE nome_evento = " + evento.getNome();
            ResultSet eventoResult = statement.executeQuery(verificaEventoQuery);

            if (eventoResult.next()) {
                Date dataRealizacao = eventoResult.getDate("data_realizacao");
                Date horaInicio = eventoResult.getTime("hora_inicio");
                Date horaFim = eventoResult.getTime("hora_fim");

                // Estou a combinar a data do evento com a hora de início e fim para obter o horário de início e fim do evento
                long dataInicioMillis = dataRealizacao.getTime() + horaInicio.getTime();
                long dataFimMillis = dataRealizacao.getTime() + horaFim.getTime();

                //Estou a obter a data atual
                Date dataAtual = new Date();
                long dataAtualMillis = dataAtual.getTime();

                //Se estiver dentro do intervalo de tempo, então o evento se encontra a decorrer
                if (dataAtualMillis >= dataInicioMillis && dataAtualMillis <= dataFimMillis) {

                    // Se tiver códigos anteriores para o evento, então ele vai colocar a validade desses à 0
                    String expiraCodigosAnterioresQuery = "UPDATE Codigo_Registo SET validade = 0 WHERE nome_evento = " + evento.getNome();
                    statement.executeUpdate(expiraCodigosAnterioresQuery);

                    // Depois de expirar os codigos anteriores, ele vai gerar um novo código
                    int codigo = geraCodigoAleatorio();


                   //Calcula o tempo de validade para o sistema saber quando deve expirar o código (a informacao não fica armazenada apenas com os minutos dados pelo utilizador
                    //Armazena então com o tipo Timestamp, e alterei assim a bd
                    long validadeMillis = validadeMinutos * 60 * 1000;
                    Timestamp horarioValidade = new Timestamp(dataAtualMillis + validadeMillis);

                    String insereCodigoQuery = "INSERT INTO Codigo_Registo (n_codigo_registo, nome_evento, validade) VALUES ('" + codigo + "', " + evento.getNome() + ", '" + horarioValidade + "')";
                    statement.executeUpdate(insereCodigoQuery);

                    return codigo;
                } else {
                    System.out.println("O evento não esta a decorrer no momento");
                    return 0;
                }
            } else {
                System.out.println("Evento nao encontrado");
                return 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    private static int geraCodigoAleatorio() {
        int tamanhoCodigo = 6;

        // O código vai ter 6 digitos, então o valor mínimo é 100000 e o máximo é 999999
        int minimo = (int) Math.pow(10, tamanhoCodigo - 1);
        int maximo = (int) Math.pow(10, tamanhoCodigo) - 1;

        Random rand = new Random();
        //Depois disso gera um codigo aleatorio que esteja dentro desse intervalo
        int cod= rand.nextInt(maximo - minimo + 1) + minimo;
        return cod;
    }


}


