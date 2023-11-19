package pt.isec.pd.trabalhoPratico.dataAccess;

import pt.isec.pd.trabalhoPratico.model.classesComunication.Msg_ConsultaComFiltros;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Msg_Cria_Evento;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Msg_Edita_Evento;
import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;
import pt.isec.pd.trabalhoPratico.model.recordDados.Utilizador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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


    public static boolean RegistoNovoUser(Utilizador user, String password){
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){
            String createEntryQuery = "INSERT INTO Utilizador (email,nome,numero_estudante,palavra_passe) VALUES ('"
                    + user.email() + "','" + user.nome() + "','" + user.numIdentificacao() + "','" + password +"')";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??

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
    public static Boolean[] autentica_user(String user, String password){

        // processar adnistrador
        Boolean[]res={false,false};//o primeiro é se a o user ta certo e a segunda é se é Admin ou não
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

                if( rs.getString("palavra_passe").equals(password))
                    res[0]=true;
                if(rs.getString("tipo_utilizador").equals("admin"))
                    res[1]=true;


                return res;// devolve true se a password for a mesma
            }
            else{ System.out.println("Não encontrou nenhum utilizador");
                return res;

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
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

    //Alterei esta classe para retornar os eventos em que o utilizador tem presenças registadas
    //E para conter já os filtros
    public static List <Evento> ConsultaPresencas_user(String email_utilizador, Msg_ConsultaComFiltros filtros){
        List<Evento> eventosAssistidos = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement()) {
            String FiltroEventosUser = "SELECT * FROM Evento " +
                    "INNER JOIN Assiste ON Evento.nome_evento = Assiste.nome_evento " +
                    "WHERE Assiste.email = '" + email_utilizador + "' ";

            //ResultSet rs = statement.executeQuery(FiltroEventosUser);

            if (filtros.getNome() != null && !filtros.getNome().isEmpty()) {
                FiltroEventosUser += "AND Evento.nome_evento LIKE '%" + filtros.getNome() + "%' ";
            }

            if (filtros.getLocal() != null && !filtros.getLocal().isEmpty()) {
                FiltroEventosUser += "AND Evento.local LIKE '%" + filtros.getLocal() + "%' ";
            }

            //DATASSSSS LIM 1 E LIM 2
            /*if (data != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String dataString = dateFormat.format(data); //Alterei isso para termos a data nesse formato, facilita os testes mas sempre se pode alterar
                FiltroEventosUser += " AND data_realizacao = '" + dataString + "'";
            }*/

            if (filtros.getHoraInicio() != 0) {
                FiltroEventosUser += " AND hora_inicio = '" + filtros.getHoraInicio() + "'";
            }

            if (filtros.getHoraFim() != 0) {
                FiltroEventosUser += " AND hora_fim = '" + filtros.getHoraFim() + "'";
            }

            ResultSet rs = statement.executeQuery(FiltroEventosUser);


            while (rs.next()){
                String nomeEvento = rs.getString("nome_evento");
                String localEvento = rs.getString("local");
                String dataRealizacao = rs.getString("data_realizacao");
                int horaInicioEvento = rs.getInt("hora_inicio");
                int horaFimEvento = rs.getInt("hora_fim");
                LocalDate date=LocalDate.parse(dataRealizacao);

                Evento evento = new Evento( nomeEvento,localEvento, date, horaInicioEvento, horaFimEvento );
                eventosAssistidos.add(evento);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return eventosAssistidos;

    }
    public static List<Utilizador> Presencas_evento(String nome_evento){
        List<Utilizador> res = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(dbUrl);
            ){
            String GetQuery = "SELECT * FROM UTILIZADOR INNER JOIN ASSISTE ON UTILIZADOR.EMAIL=ASSISTE.EMAIL where ASSISTE.nome_evento= ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(GetQuery);
            preparedStatement.setString(1, nome_evento);
            ResultSet rs = preparedStatement.executeQuery();
            if(!rs.isBeforeFirst())
            {
                System.out.println("Nenhum evento encontrado");
                return null;
            }
            while (rs.next()){
                Utilizador aux = new Utilizador(rs.getString("nome"),rs.getString("email"),rs.getInt("num_estudante"));
                res.add(aux);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

//----------------------------------------------------------------------------Novas funções para o Admin
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
    return true;
    }

    public static boolean Edita_evento(Msg_Edita_Evento evento) {
        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {

            // Estou a verificar se há presenças na tabela assiste para o evento (pelo seu nomeantigo que é o seu id)
            String checkAssisteQuery = "SELECT COUNT(*) FROM assiste WHERE nome_evento = '" + evento.getNome() + "'";
            ResultSet resultSet = statement.executeQuery(checkAssisteQuery);
            resultSet.next();
            int presencas = resultSet.getInt(1);

            if (presencas > 0) {
                // Se houver presenças registadas,apenas permite editar o nome e o local (??)
                String updateEventQuery = "UPDATE Evento SET nome_evento = '" + evento.getNovoNome() + "', local = '" + evento.getLocal() + "' WHERE nome_evento = '" + evento.getNome() + "'";

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
                String updateEventQuery = "UPDATE Evento SET data_realizacao = '" + dataString + "', hora_inicio = '" + evento.getHoreInicio() + "', hora_fim = '" + evento.getHoraFim() + "', nome_evento = '" + evento.getNome() + "', local = '" + evento.getLocal()+ "' WHERE nome_evento = '" + evento.getNome()
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

    public static List <Evento> Consulta_eventos(Msg_ConsultaComFiltros evento) {
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

            //DATAAAAAAAS LIM1 E LIM2
            /*if (evento.getData() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String dataString = dateFormat.format(evento.getData()); //Alterei isso para termos a data nesse formato, facilita os testes mas sempre se pode alterar
                filtroEvento += " AND data_realizacao = '" + dataString + "'";
            }*/

            if (evento.getHoraInicio() != 0) {
                filtroEvento += " AND hora_inicio = '" + evento.getHoraInicio() + "'";
            }

            if (evento.getHoraFim() != 0) {
                filtroEvento += " AND hora_fim = '" + evento.getHoraFim() + "'";
            }

            ResultSet resultSet = statement.executeQuery(filtroEvento);

            while (resultSet.next()) {
                //Para extrair a data e hora do sqlLite tem que ser com .getstring e também é o que temos na classe evento
                String nome = resultSet.getString("nome_evento");
                String local = resultSet.getString("local");
                LocalDate data_realizacao = resultSet.getDate("data_realizacao").toLocalDate();
                int horaInicio =  resultSet.getInt("hora_inicio");
                int horaFim = resultSet.getInt("hora_fim");

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

//Gerar codigo
    public static int GeraCodigoRegisto(String evento, int validadeMinutos) {
        //Estou a utilizar o PreparedStatement pq é necessário para passar valores dinâmicos por parametros (para consultas)
        String verificaEventoQuery = "SELECT data_realizacao, hora_inicio, hora_fim FROM Evento WHERE nome_evento = ?";

        try (Connection connection = DriverManager.getConnection(dbUrl);
         PreparedStatement eventoStatement = connection.prepareStatement(verificaEventoQuery)) {
            //Statement statement = connection.createStatement();

            // Para verificar se o evento  se encontra a decorrer
            eventoStatement.setString(1, evento);
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
                    String EliminaCodigosAnterioresQuery = "DELETE  FROM Codigo_Registo  WHERE nome_evento = ?";//
                    PreparedStatement expiraStatement = connection.prepareStatement(EliminaCodigosAnterioresQuery);
                    expiraStatement.setString(1, evento); // Define o valor do nome_evento para o ? da query
                    expiraStatement.executeUpdate();// se existirem codigos antigos são eliminados se nao existirem nao acontece nada

                    // Depois de expirar os codigos anteriores, ele vai gerar um novo código
                    int codigo = geraCodigoAleatorio();

                   //Calcula o tempo de validade para o sistema saber quando deve expirar o código (a informacao não fica armazenada apenas com os minutos dados pelo utilizador
                    //Armazena então com o tipo Timestamp, e alterei assim a bd
                    long validadeMillis = validadeMinutos * 60 * 1000;
                    Timestamp horarioValidade = new Timestamp(dataAtualMillis + validadeMillis);

                    String insereCodigoQuery = "INSERT INTO Codigo_Registo (n_codigo_registo, nome_evento, validade) VALUES (?, ?, ?)";
                    PreparedStatement insereStatement = connection.prepareStatement(insereCodigoQuery);
                    insereStatement.setInt(1, codigo);
                    insereStatement.setString(2, evento);
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

}


