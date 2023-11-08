package pt.isec.pd.trabalhoPratico.dataAccess;

import pt.isec.pd.trabalhoPratico.model.classesComunication.Cria_evento;
import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;
import pt.isec.pd.trabalhoPratico.model.classesDados.Utilizador;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    }
    public static boolean Registonovouser(Utilizador user,String password){
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){
            int num_est=Integer.parseInt(user.getNumIdentificacao());
            String createEntryQuery = "INSERT INTO Utilizador (email,nome,numero_estudante,palavra_passe) VALUES ('"
                    + user.getEmail()+"','" + user.getNome()+"','" +num_est+"','" +password+"')";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??

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
    public static boolean autentica_user(String user, String password){
        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement())
        {
            String verificaEstudanteQuery = "SELECT * FROM Utilizador WHERE email = ?";
            PreparedStatement alunoStatement = connection.prepareStatement(verificaEstudanteQuery);
           alunoStatement.setString(1, user);
            ResultSet rs=alunoStatement.executeQuery();

            if(rs.isBeforeFirst())
            {   rs.next();
                System.out.println(rs.getString("email"));
                System.out.println(rs.getString("palavra_passe"));
                return rs.getString("palavra_passe").equals(password);// devolve true se a password for a mesma
            }
            else{ System.out.println("Couldn't find any user");
                return false;

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

            if(rs.isBeforeFirst())
            {   rs.next();
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
            else{
               System.out.println("Couldn't find the any user");
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }
    public static boolean submitcod(int codigo,String nome_evento,String emailuser){
        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement())
        {
            String verificaEstudanteQuery = "SELECT COUNT(*) FROM Utilizador WHERE email = ?";
            PreparedStatement alunoStatement = connection.prepareStatement(verificaEstudanteQuery);
            alunoStatement.setString(1, emailuser);
            int estudantesEncontrados = alunoStatement.executeQuery().getInt(1);
            if(estudantesEncontrados<1)
            {
                System.out.println("Estudante inexistente");
                return false;
            }

            String verificasejaseincreveuQuery="SELECT COUNT(*) FROM Assiste where nome_evento=? AND email=?";
            PreparedStatement verstatemente=connection.prepareStatement(verificasejaseincreveuQuery);
            verstatemente.setString(1,nome_evento);
            verstatemente.setString(2,emailuser);
            if(verstatemente.executeQuery().getInt(1)>=1){

                System.out.println("[Erro] Aluno ja inscrito");
                return false;
            }


            String GetQuery = "SELECT * FROM Codigo_Registo where nome_evento='" + nome_evento + "';";
            ResultSet rs=statement.executeQuery(GetQuery);


            if(rs.isBeforeFirst())
            {   rs.next();
                if(rs.getInt("n_codigo_registo")==codigo && rs.getTimestamp("validade").getTime()>0 ){
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
                System.out.println("Nao ha nenhum evento com esse nome");
                return false;
            }
        } catch (SQLException e) {

            System.out.println(e.getMessage());
            return false;
        }


    }
    public static boolean CriaEvento(Evento evento){
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){


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


    //Alterei esta classe para retornar os eventos em que o utilizador tem presenças registadas
    //E para conter já os filtros
    public static List <Evento> ConsultaPresencas_user(String nome_utilizador,String nome_evento,String local, Date data,LocalTime horaInicio, LocalTime horaFim){
        List<Evento> eventosAssistidos =new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement()) {
            String FiltroEventosUser = "SELECT * FROM Evento " +
                    "INNER JOIN Assiste ON Evento.nome_evento = Assiste.nome_evento " +
                    "WHERE Assiste.email = '" + nome_utilizador + "' ";

            //ResultSet rs = statement.executeQuery(FiltroEventosUser);

            if (nome_evento != null && !nome_evento.isEmpty()) {
                FiltroEventosUser += "AND Evento.nome_evento LIKE '%" + nome_evento + "%' ";
            }

            if (local != null && !local.isEmpty()) {
                FiltroEventosUser += "AND Evento.local LIKE '%" + local + "%' ";
            }

            if (data != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String dataString = dateFormat.format(data); //Alterei isso para termos a data nesse formato, facilita os testes mas sempre se pode alterar
                FiltroEventosUser += " AND data_realizacao = '" + dataString + "'";
            }

            if (horaInicio != null) {
                FiltroEventosUser += " AND hora_inicio = '" + horaInicio + "'";
            }

            if (horaFim!= null) {
                FiltroEventosUser += " AND hora_fim = '" + horaFim + "'";
            }

            ResultSet rs = statement.executeQuery(FiltroEventosUser);



            while (rs.next()){
                String nomeEvento = rs.getString("nome_evento");
                String localEvento = rs.getString("local");
                String dataRealizacao = rs.getString("data_realizacao");
                String horaInicioEvento = rs.getString("hora_inicio");
                String horaFimEvento = rs.getString("hora_fim");

                Evento evento = new Evento(nomeEvento,localEvento, dataRealizacao, horaInicioEvento, horaFimEvento );
                eventosAssistidos.add(evento);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return eventosAssistidos;

    }
    public static List<String>Presencas_evento(String nome_evento){
        List<String> res = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){
            String GetQuery = "SELECT * FROM Assiste where nome_evento='" + nome_evento + "';";
            ResultSet rs=statement.executeQuery(GetQuery);
            if(!rs.isBeforeFirst())
            {
                System.out.println("Nenhum evento encontrado");
                return null;

            }

            while (rs.next()){
                res.add(rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }




        return res ;
    }

//----------------------------------------------------------------------------Novas funções para o Admin
    public static boolean Cria_evento(String nome, String local, Date data, LocalTime horainicio, LocalTime horafim) {
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dataString = dateFormat.format(data); //Alterei isso para termos a data nesse formato, facilita os testes

            String createEntryQuery = "INSERT INTO Evento (nome_evento,local,data_realizacao,hora_inicio,hora_fim) VALUES ('"
                    + nome+"','" + local+"','" +dataString+"','" +horainicio+"','" +horafim+"')";

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
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String dataString = dateFormat.format(evento.getData()); //Alterei isso para termos a data nesse formato, facilita os testes mas sempre se pode alterar
                String updateEventQuery = "UPDATE Evento SET data_realizacao = '" + dataString + "', hora_inicio = '" + evento.getHorainicio() + "', hora_fim = '" + evento.getHorafim() + "', nome_evento = '" + evento.getNome() + "', local = '" + evento.getLocal()+ "' WHERE nome_evento = '" + antigoNome
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

    public static List <Evento> Consulta_eventos(Cria_evento evento) {
        List<Evento> eventos = new ArrayList<>();


        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {

            String filtroEvento = "SELECT * FROM Evento WHERE 1=1"; // Começa com "1=1" para mostrar todos resultados
                                                                  // A medida que campos forem não null ele inclui na pesquisa

            if (evento.getNome() != null && !evento.getNome().isEmpty()) {
                filtroEvento += " AND nome_evento LIKE '%" + evento.getNome()  + "%'";
            }

            if (evento.getLocal() != null) {
                filtroEvento += " AND local LIKE '%" + evento.getLocal() + "'";
            }

            if (evento.getData() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String dataString = dateFormat.format(evento.getData()); //Alterei isso para termos a data nesse formato, facilita os testes mas sempre se pode alterar
                filtroEvento += " AND data_realizacao = '" + dataString + "'";
            }

            if (evento.getHorainicio() != null) {
                filtroEvento += " AND hora_inicio = '" + evento.getHorainicio() + "'";
            }

            if (evento.getHorafim()!= null) {
                filtroEvento += " AND hora_fim = '" + evento.getHorafim() + "'";
            }

            ResultSet resultSet = statement.executeQuery(filtroEvento);

            while (resultSet.next()) {
                //Para extrair a data e hora do sqlLite tem que ser com .getstring e também é o que temos na classe evento
                String nome = resultSet.getString("nome_evento");
                String local = resultSet.getString("local");
                String data_realizacao = resultSet.getString("data_realizacao");
                String horaInicio = resultSet.getString("hora_inicio");
                String horaFim = resultSet.getString("hora_fim");

                Evento evento_result = new Evento(nome,local, data_realizacao, horaInicio, horaFim);
                eventos.add(evento_result);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return eventos;
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

//Gerar codigo (Este é complicadinho =D)

    public static int GeraCodigoRegisto(Cria_evento evento, int validadeMinutos) {
        //Estou a utilizar o PreparedStatement pq é necessário para passar valores dinâmicos por parametros (para consultas)
        String verificaEventoQuery = "SELECT data_realizacao, hora_inicio, hora_fim FROM Evento WHERE nome_evento = ?";

        try (Connection connection = DriverManager.getConnection(dbUrl);
         PreparedStatement eventoStatement = connection.prepareStatement(verificaEventoQuery)) {
            //Statement statement = connection.createStatement();

            // Para verificar se o evento  se encontra a decorrer
            eventoStatement.setString(1, evento.getNome());
            ResultSet resultSet = eventoStatement.executeQuery();


            if (resultSet.next()) {
                String dataRealizacao = resultSet.getString("data_realizacao");
                String horaInicio = resultSet.getString("hora_inicio");
                String horaFim = resultSet.getString("hora_fim");

                // Estou a criar um objeto SimpleDateFormat para analisar a data e hora no formato correto
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date dataRealizacaoInicio = dateFormat.parse(dataRealizacao + " "+ horaInicio);
                Date dataRealizacaoDateFim = dateFormat.parse(dataRealizacao + " "+ horaFim);

                // Estou a combinar a data do evento com a hora de início e fim para obter o horário de início e fim do evento
                long dataInicioMillis = dataRealizacaoInicio.getTime();
                long dataFimMillis = dataRealizacaoDateFim.getTime();

                //Estou a obter a data e hora atual para comparar se o evento se encontra a decorrer
                Date dataAtual = new Date();
                long dataAtualMillis = dataAtual.getTime();

                System.out.println(dataAtualMillis);
                System.out.println(dataInicioMillis);
                System.out.println(dataFimMillis);

                //Se estiver dentro do intervalo de tempo, então o evento se encontra a decorrer
                if (dataAtualMillis >= dataInicioMillis && dataAtualMillis <= dataFimMillis) {


                    // Eu alterei para passar a eliminar os codigos antigos e substituir pelos novos, nao me parece muito logico guarda-los, dps diz me o que achas
                    String EliminaCodigosAnterioresQuery = "Delete From Codigo_Registo WHERE nome_evento = ?";
                    PreparedStatement expiraStatement = connection.prepareStatement(EliminaCodigosAnterioresQuery);
                    expiraStatement.setString(1, evento.getNome()); // Define o valor do nome_evento para o ? da query
                    expiraStatement.executeUpdate();

                    // Depois de expirar os codigos anteriores, ele vai gerar um novo código
                    int codigo = geraCodigoAleatorio();

                   //Calcula o tempo de validade para o sistema saber quando deve expirar o código (a informacao não fica armazenada apenas com os minutos dados pelo utilizador
                    //Armazena então com o tipo Timestamp, e alterei assim a bd
                    long validadeMillis = validadeMinutos * 60 * 1000;
                    Timestamp horarioValidade = new Timestamp(dataAtualMillis + validadeMillis);

                    String insereCodigoQuery = "INSERT INTO Codigo_Registo (n_codigo_registo, nome_evento, validade) VALUES (?, ?, ?)";
                    PreparedStatement insereStatement = connection.prepareStatement(insereCodigoQuery);
                    insereStatement.setInt(1, codigo);
                    insereStatement.setString(2, evento.getNome());
                    insereStatement.setTimestamp(3, horarioValidade); //Estou a salvar em TimeStamp porque é melhor para verificar a validade do codigo
                    insereStatement.executeUpdate();
                    System.out.println("Inseriu o codigo na database");

                    return codigo;

                } else {
                    System.out.println("O evento não esta a decorrer no momento");
                    return 0;
                }
            } else {
                System.out.println("O Evento nao foi encontrado");
                return 0;
            }
        } catch (SQLException | ParseException e) {
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

    //Ficheiro CSV
    public static void PresencasCSV(List<Evento> eventos,String csvFile ) {
        String csvSplit = ","; // Delimitador!!

        try (FileWriter writer = new FileWriter(csvFile)) {
            // Escrita do cabeçalho:
             writer.append("Nome do Evento");
             writer.append(csvSplit);
             writer.append("Local");
             writer.append(csvSplit);
             writer.append("Data de Realizacao");
             writer.append(csvSplit);
             writer.append("Hora de Inicio");
             writer.append(csvSplit);
             writer.append("Hora de Fim");
             writer.append("\n");

             //Escrita dos dados obtidos da base de dados:
            for (Evento evento : eventos) {
                writer.append(evento.getNome());
                writer.append(csvSplit);
                writer.append(evento.getLocal());
                writer.append(csvSplit);
                writer.append(evento.getData());
                writer.append(csvSplit);
                writer.append(evento.getHoraInicio());
                writer.append(csvSplit);
                writer.append(evento.getHoraFim());
                writer.append("\n");
            }

            System.out.println("Ficheiro CSV criado com sucesso");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


