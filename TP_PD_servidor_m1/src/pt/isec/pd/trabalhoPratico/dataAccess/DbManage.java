package pt.isec.pd.trabalhoPratico.dataAccess;

import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;
import pt.isec.pd.trabalhoPratico.model.classesDados.Utilizador;

import java.sql.*;

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
          //  int num_est=Integer.parseInt(user.getNumIdentificacao());
            //Somente para teste de ligação a base de dados
            /*String createEntryQuery = "INSERT INTO Codigo_Registo (n_codigo_registo,nome_evento) VALUES ('"
                    + codigo_registo+"','" + nome_evento+ "')";*/
            //String mail= user.getEmail();
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
    public static boolean submitcod(String codigo,String nome_evento){
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



}
