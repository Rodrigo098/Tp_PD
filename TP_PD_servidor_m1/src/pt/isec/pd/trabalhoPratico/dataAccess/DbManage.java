package pt.isec.pd.trabalhoPratico.dataAccess;

import java.sql.*;

public class DbManage extends Thread{
    private static final String dbAdress = "databasePD.db";
    private static final String dbUrl= "jdbc:sqlite:"+dbAdress;

    public DbManage() {
        int codigo_registo = 1;
        String nome_evento = "Evento1";
        String email="email";

        try(Connection connection = DriverManager.getConnection(dbUrl);

         Statement statement = connection.createStatement()){

            //Somente para teste de ligação a base de dados
            /*String createEntryQuery = "INSERT INTO Codigo_Registo (n_codigo_registo,nome_evento) VALUES ('"
                    + codigo_registo+"','" + nome_evento+ "')";*/
            String createEntryQuery = "INSERT INTO Assiste (assiste_id,nome_evento,email) VALUES ('"
                    + codigo_registo+"','" + nome_evento+ "','"+email+"')";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??

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


    @Override
    public void run() {
        super.run();
    }

}
