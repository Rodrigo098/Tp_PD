package pt.isec.pd.trabalhoPratico.model.dataAccess;


import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class DbManager {
    private static final String dbAdress = "Base de Dados/copiaDb.db";
    private static final String dbUrl= "jdbc:sqlite:"+dbAdress;
    public static String getDbAdress() {
        return dbAdress;
    }

    public int getVersaoDb(){
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
}


