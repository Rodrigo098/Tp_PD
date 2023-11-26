package pt.isec.pd.trabalhoPratico.dataAccess;

import pt.isec.pd.trabalhoPratico.model.ObservableInterface;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Msg_ConsultaComFiltros;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Msg_Cria_Evento;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Msg_Edita_Evento;
import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;
import pt.isec.pd.trabalhoPratico.model.recordDados.Utilizador;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class DbManage implements Serializable {
    private static String dbAdress = "databasePD.db";
    private static final String dbUrl= "jdbc:sqlite:"+dbAdress;
    private int versao;

    private List<ObservableInterface>observables;
    private PropertyChangeSupport versaoSuporte;

    public DbManage(List<ObservableInterface> obv) {
        if(!DbManage.existeDb())
            criarDb();
        this.observables=obv;
        versao = getversaobd();
        versaoSuporte = new PropertyChangeSupport(this);
        int codigo_registo = 1;
        String nome_evento = "Evento1";
        String email="email";
    }

    public void addObserver(ObservableInterface o){

            if(!observables.contains(o))
                observables.add(o);

    }
    public void removeObserver(ObservableInterface o){
            if(observables.contains(o))
                observables.remove(o);


    }

    public static String getDbAdress() {
        return dbAdress;
    }

    //Função para verificar se a bd existe
    private static boolean existeDb() {
        try {
            File dbFile = new File(dbAdress);
            return dbFile.exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//Função para criar uma nova db vazia
    private void criarDb() {
        try {
            Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement();

            // Tabela Assiste
            String criaAssiste = "CREATE TABLE Assiste ("
                    + "assiste_id INTEGER PRIMARY KEY,"
                    + "nome_evento TEXT,"
                    + "email TEXT,"
                    + "FOREIGN KEY(email) REFERENCES Utilizador(email),"
                    + "FOREIGN KEY(nome_evento) REFERENCES Evento(nome_evento)"
                    + ");";
            statement.executeUpdate(criaAssiste);

            // Tabela Codigo_Registo
            String criaCodigoRegisto = "CREATE TABLE Codigo_Registo ("
                    + "n_codigo_registo INTEGER PRIMARY KEY,"
                    + "nome_evento TEXT,"
                    + "validade TIMESTAMP,"
                    + "FOREIGN KEY(nome_evento) REFERENCES Evento(nome_evento)"
                    + ");";
            statement.executeUpdate(criaCodigoRegisto);

            // Tabela Evento
            String criaEvento = "CREATE TABLE Evento ("
                    + "nome_evento TEXT PRIMARY KEY,"
                    + "local TEXT,"
                    + "data_realizacao DATE,"
                    + "hora_inicio TIME,"
                    + "hora_fim TIME"
                    + ");";
            statement.executeUpdate(criaEvento);

            // Tabela Utilizador
            String criaUtilizador = "CREATE TABLE Utilizador ("
                    + "email TEXT PRIMARY KEY,"
                    + "nome TEXT,"
                    + "numero_estudante INTEGER,"
                    + "palavra_passe TEXT,"
                    + "tipo_utilizador TEXT"
                    + ");";
            statement.executeUpdate(criaUtilizador);

            // Tabela Versao
            String criaVersao = "CREATE TABLE Versao ("
                    + "versao_id INTEGER PRIMARY KEY,"
                    + "descricao TEXT"
                    + ");";
            statement.executeUpdate(criaVersao);

            // versao_id = 0
            String insereVersao = "INSERT INTO Versao (versao_id, descricao) VALUES (0, 'Versão Inicial');";
            statement.executeUpdate(insereVersao);

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addVersaoListener(PropertyChangeListener novoListener) {
        versaoSuporte.addPropertyChangeListener(novoListener);
    }

    public void removeVersaoChangeListener(PropertyChangeListener novoListener) {
        versaoSuporte.removePropertyChangeListener(novoListener);
    }
    private int getversaobd(){
        try(Connection connection=DriverManager.getConnection(dbUrl)) {
            String GetQuery="Select versao_id FROM VERSAO;";
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

    public void setVersao() {
        versao++;
        try (Connection connection=DriverManager.getConnection(dbUrl)){
            String UpdateVersao="UPDATE Versao SET versao_id=? where versao_id=?;";
            PreparedStatement statement=connection.prepareStatement(UpdateVersao);
            statement.setInt(1,versao);
            statement.setInt(2,versao-1);
           if( statement.executeUpdate()<1)
               System.out.println("<BD> Erro na atualizacao da versao da BAse de Dados");
           else{
               versaoSuporte.firePropertyChange("versao", null, null);
               System.out.println("<BD> Versao de Base de Dados atualizada com sucesso");
           }

           statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getVersao() {
        return versao;
    }

    public BDResposta RegistoNovoUser(Utilizador user, String password){
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement())
        {
            String createEntryQuery = "INSERT INTO Utilizador (email,nome,numero_estudante,palavra_passe,tipo_utilizador) VALUES ('"
                    + user.email() + "','" + user.nome() + "','" + user.numIdentificacao() + "','" + password +"','" + "cliente" +"');";

            if(statement.executeUpdate(createEntryQuery)<1){
                return new BDResposta(false, "<BD>Falha na inserção de novo utilizador", true);
            }
            else{
                connection.close();
                for (ObservableInterface obv:observables) {
                    obv.executaUpdate("INSERT INTO Utilizador (email,nome,numero_estudante,palavra_passe,tipo_utilizador) VALUES ('"
                            + user.email() + "','" + user.nome() + "','" + user.numIdentificacao() + "','" + password +"','" + "cliente" +"')");

                }
                setVersao();
                return new BDResposta(true, "<BD>Insercao de novo utilizador com sucesso # " + user.nome() + "," + user.numIdentificacao(), false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new BDResposta(false, "<BD>Erro no acesso a base de dados", false);
        } catch (RemoteException e) {
            System.out.println("<Rmi>Excecao ao atualizar o backup: "+e);
            return new BDResposta(true, "<RMI>Erro no acesso a base de dados", false);
        }
    }

    public BDResposta autentica_user(String user, String password){

        try(Connection connection = DriverManager.getConnection(dbUrl))
        {
            String verificaEstudanteQuery = "SELECT * FROM Utilizador WHERE email = ?;";
            PreparedStatement alunoStatement = connection.prepareStatement(verificaEstudanteQuery);
            alunoStatement.setString(1, user);
            ResultSet rs=alunoStatement.executeQuery();

            if(rs.isBeforeFirst())
            {   rs.next();
                if(rs.getString("palavra_passe").equals(password)) {
                    if(rs.getString("tipo_utilizador").equals("administrador"))
                        return new BDResposta(true, "<BD>Entrou um administrador", true);
                    else {
                        String dados = rs.getString("nome") + "," + rs.getString("numero_estudante");
                        return new BDResposta(true, "<BD>Entrou um cliente # " + dados, false);
                    }
                }
                return new BDResposta(false, "<BD>Password errada", false);
            }
            else{
                return new BDResposta(false, "<BD>Não encontrou nenhum utilizador", true);
            }
        } catch (SQLException ignored) {
            return new BDResposta(false, "<BD>Erro no acesso a Base de Dados", false);
        }
    }
    public boolean edita_registo( Utilizador user, String pasword ){
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){

            String GetQuery = "SELECT * FROM Utilizador where email='" + user.email() + "';";// CHELSEA SERIA ASSIM QUE ADICIONAVAMOS OUTROS VALORES??
            ResultSet rs = statement.executeQuery(GetQuery);

            if(rs.isBeforeFirst())
            {   rs.next();
                String updateQuery = "UPDATE Utilizador SET nome=?, numero_estudante=?, palavra_passe=? WHERE email=?;";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, user.nome());
                preparedStatement.setInt(2, user.numIdentificacao());
                preparedStatement.setString(3, pasword);
                preparedStatement.setString(4, user.email());
                preparedStatement.executeUpdate();
                connection.close();
                for (ObservableInterface obv:observables) {
                    obv.executaUpdate("UPDATE Utilizador SET nome='" + user.nome() + "', numero_estudante=" + user.numIdentificacao() + ", palavra_passe=" + pasword + " WHERE email="+ user.email() + ";");
                }
                setVersao();
                return true;
            }
            else{
                System.out.println("<BD> Nao foi encontrado nenhum utilizador com email [" + user.email() + "]");
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (RemoteException e) {
            System.out.println("<Rmi>Excecao ao atualizar o backup: "+e);
            return true;
        }

    }
    public boolean submitcod(int codigo,String nome_evento,String emailuser){
        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement())
        {
            String verificaEstudanteQuery = "SELECT COUNT(*) FROM Utilizador WHERE email = ?;";
            PreparedStatement alunoStatement = connection.prepareStatement(verificaEstudanteQuery);
            alunoStatement.setString(1, emailuser);
            int estudantesEncontrados = alunoStatement.executeQuery().getInt(1);
            if(estudantesEncontrados<1)
            {
                System.out.println("<BD> Nenhum utilizador [" + emailuser + "] encontrado");
                return false;
            }

            String verificasejaseincreveuQuery="SELECT COUNT(*) FROM Assiste where nome_evento=? AND email=?;";
            PreparedStatement verstatemente=connection.prepareStatement(verificasejaseincreveuQuery);
            verstatemente.setString(1,nome_evento);
            verstatemente.setString(2,emailuser);
            if(verstatemente.executeQuery().getInt(1)>=1){

                System.out.println("<DB> Tentativa multipla de registo no evento [" + nome_evento + "] por [" + emailuser + "]");
                return false;
            }


            String GetQuery = "SELECT * FROM Codigo_Registo where nome_evento=? AND validade>?;";
            PreparedStatement getquery=connection.prepareStatement(GetQuery);
            getquery.setString(1,nome_evento);
            getquery.setLong(2,0);
            ResultSet rs=getquery.executeQuery();

            if(rs.isBeforeFirst())
            {   rs.next();
                Date Data = new Date();
                long datamili = Data.getTime();
                if(rs.getTimestamp("validade").getTime()<datamili){
                    System.out.println("<BD> Tentativa de registo no evento [" + nome_evento + "] com codigo invalido por [" + emailuser + "]");
                    String EliminaCodigosAnterioresQuery = "UPDATE Codigo_Registo SET validade=0 WHERE nome_evento = ?;";//
                    PreparedStatement expiraStatement = connection.prepareStatement(EliminaCodigosAnterioresQuery);
                    expiraStatement.setString(1, nome_evento); // Define o valor do nome_evento para o ? da query
                    expiraStatement.executeUpdate();// se existirem codigos antigos são eliminados se nao existirem nao acontece nada
                        return false;
                }

                if(rs.getInt("n_codigo_registo")==codigo  ){
                    String createEntryQuery = "INSERT INTO Assiste (nome_evento,email) VALUES ('"
                            + nome_evento+"','" +emailuser+"');";// qual o valor que é suposto colocar no idassiste??

                    if(statement.executeUpdate(createEntryQuery)<1){
                        System.out.println("<BD> Erro na insercao da presenca de [" + emailuser + "] no evento [" + nome_evento + "]");
                        return false;
                    }
                    else{
                        System.out.println("<BD> Nova presenca registada de [" + emailuser + "] no evento [" + nome_evento + "]");
                        connection.close();
                        for (ObservableInterface obv:observables) {
                            obv.submitcod(codigo,nome_evento,emailuser);
                            }
                        setVersao();
                        return true;
                    }

                }else{
                    System.out.println("<BD> Codigo invalido para registo no evento [" + nome_evento + "] de [" + emailuser + "]");
                    return false;
                }
            }
            else{
                System.out.println("<BD> Nenhum evento corresponde a submicao de codigo por [" + emailuser + "]");
                return false;
            }
        } catch (SQLException e) {

            System.out.println(e.getMessage());
            return false;
        } catch (RemoteException e) {
            System.out.println("<Rmi>Excecao ao atualizar o backup: "+e);
            return true;
        }
    }

    public List<Evento> ConsultaPresencas_User_Admin(String email_utilizador){
        List<Evento> eventos_assistidos = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(dbUrl)){
            String GetQuery = "SELECT * FROM EVENTO INNER JOIN ASSISTE ON EVENTO.nome_evento=ASSISTE.nome_evento where ASSISTE.email= ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(GetQuery);
            preparedStatement.setString(1, email_utilizador);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.isBeforeFirst()){
                while (rs.next()) {
                    String nomeEvento = rs.getString("nome_evento");
                    String localEvento = rs.getString("local");
                    String dataRealizacao = rs.getString("data_realizacao");
                    String horaInicioEvento = rs.getString("hora_inicio");
                    String horaFimEvento = rs.getString("hora_fim");

                    Evento evento = new Evento( nomeEvento,localEvento, dataRealizacao, horaInicioEvento, horaFimEvento );
                    eventos_assistidos.add(evento);
                }
                return eventos_assistidos;

            }else{
                System.out.println("<BD> Nenhum evento encontrado");
                return eventos_assistidos;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return eventos_assistidos;
        }

    }

    //VERRRR
    public List <Evento> ConsultaPresencas_user(String email_utilizador, Msg_ConsultaComFiltros filtros){
        List<Evento> eventosAssistidos = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement()) {
            String FiltroEventosUser = "SELECT * FROM Evento " +
                    "INNER JOIN Assiste ON Evento.nome_evento = Assiste.nome_evento " +
                    "WHERE Assiste.email = '" + email_utilizador + "' ;";

            //ResultSet rs = statement.executeQuery(FiltroEventosUser);

            // nome evento
            if (filtros.getNome() != null && !filtros.getNome().isEmpty()) {
                FiltroEventosUser += "AND Evento.nome_evento LIKE '%" + filtros.getNome() + "%' ";
            }

            // local
            if (filtros.getLocal() != null && !filtros.getLocal().isEmpty()) {
                FiltroEventosUser += "AND Evento.local LIKE '%" + filtros.getLocal() + "%' ";
            }

            // data
            if (filtros.getLimData1() != null && filtros.getLimData2() != null) {
                FiltroEventosUser += "AND date(Evento.data_realizacao) BETWEEN '" + filtros.getLimData1() + "' AND '" + filtros.getLimData2() + "' ";
            }
            else if (filtros.getLimData1() != null)
                FiltroEventosUser += "AND date(Evento.data_realizacao) > '" + filtros.getLimData1() + "' ";
            else if (filtros.getLimData2() != null)
                FiltroEventosUser += "AND date(Evento.data_realizacao) < '" + filtros.getLimData2() + "' ";

            // hora
            if (filtros.getHoraInicio() != null && filtros.getHoraFim() != null) {
                FiltroEventosUser += " AND time(Evento.hora_inicio) BETWEEN '" + filtros.getHoraInicio() + "' AND '" + filtros.getHoraFim() + "' ";
            }
            else if (filtros.getHoraInicio() != null) {
                FiltroEventosUser += " AND time(Evento.hora_inicio) > '" + filtros.getHoraInicio() + "' ";
            }
            else if (filtros.getHoraFim() != null) {
                FiltroEventosUser += " AND time(Evento.hora_inicio) < '" + filtros.getHoraFim() + "' ";
            }

            ResultSet rs = statement.executeQuery(FiltroEventosUser);


            while (rs.next()){
                String nomeEvento = rs.getString("nome_evento");
                String localEvento = rs.getString("local");
                String dataRealizacao = rs.getString("data_realizacao");
                String horaInicioEvento = rs.getString("hora_inicio");
                String horaFimEvento = rs.getString("hora_fim");

                Evento evento = new Evento( nomeEvento,localEvento, dataRealizacao, horaInicioEvento, horaFimEvento );
                eventosAssistidos.add(evento);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return eventosAssistidos;

    }
    public List<Utilizador> Presencas_evento(String nome_evento){
        List<Utilizador> res = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(dbUrl);
            ){
            String GetQuery = "SELECT * FROM UTILIZADOR INNER JOIN ASSISTE ON UTILIZADOR.EMAIL=ASSISTE.EMAIL where ASSISTE.nome_evento= ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(GetQuery);
            preparedStatement.setString(1, nome_evento);
            ResultSet rs = preparedStatement.executeQuery();
            if(!rs.isBeforeFirst())
            {
                System.out.println("<BD> Nenhum evento [" + nome_evento + "] encontrado");
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
    public boolean Cria_evento(Msg_Cria_Evento evento){
        try(Connection connection = DriverManager.getConnection(dbUrl);

            Statement statement = connection.createStatement()){
            String createEntryQuery = "INSERT INTO Evento (nome_evento,local,data_realizacao,hora_inicio,hora_fim) VALUES ('"
                    + evento.getNome() +"','" + evento.getLocal() +"','" + evento.getData() +"','" + evento.getHoreInicio() +"','" + evento.getHoraFim() +"');";

            if(statement.executeUpdate(createEntryQuery)<1){
                System.out.println("<BD> Erro na criacao do evento [" + evento.getNome() +"]");
                return false;
            }
            else{
                System.out.println("<BD> Evento [" + evento +"] criado com sucesso");
                connection.close();
                for (ObservableInterface obv:observables) {
                 obv.executaUpdate("INSERT INTO Evento (nome_evento,local,data_realizacao,hora_inicio,hora_fim) VALUES ('"
                         + evento.getNome() +"','" + evento.getLocal() +"','" + evento.getData() +"','" + evento.getHoreInicio() +"','" + evento.getHoraFim() +"')");
                }
                setVersao();
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (RemoteException e) {
            System.out.println("<Rmi>Excecao ao atualizar o backup: "+e);
        }
        return false;
    }

    public boolean Edita_evento(Msg_Edita_Evento evento) {
        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {

            // Estou a verificar se há presenças na tabela assiste para o evento (pelo seu nomeantigo que é o seu id)
            String checkAssisteQuery = "SELECT COUNT(*) FROM assiste WHERE nome_evento = '" + evento.getNome() + "';";
            ResultSet resultSet = statement.executeQuery(checkAssisteQuery);
            resultSet.next();
            int presencas = resultSet.getInt(1);

            if(presencas == 0){
                // Se não houver presenças edita todos os campos
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dataString = dateFormat.format(evento.getData()); //Alterei isso para termos a data nesse formato, facilita os testes mas sempre se pode alterar
                String updateEventQuery = "UPDATE Evento SET data_realizacao = '" + dataString + "', hora_inicio = '" + evento.getHoreInicio() + "', hora_fim = '" +
                        evento.getHoraFim() + "', nome_evento = '" + evento.getNome() + "', local = '" + evento.getLocal()+ "' WHERE nome_evento = '" + evento.getNome() + "';";

                if (statement.executeUpdate(updateEventQuery) < 1) {
                    System.out.println("<BD> Erro na edição do evento [" + evento.getNome() + "]");
                    return false;
                } else {
                    System.out.println("<BD> Evento [" + evento + "] editado com sucesso");
                    connection.close();
                    for (ObservableInterface obv:observables) {
                      obv.executaUpdate("UPDATE Evento SET data_realizacao = '" + dataString + "', hora_inicio = '"
                              + evento.getHoreInicio() + "', hora_fim = '" + evento.getHoraFim() + "', nome_evento = '"
                              + evento.getNome() + "', local = '" + evento.getLocal()+ "' WHERE nome_evento = '"
                              + evento.getNome() + "'");
                    }
                    setVersao();
                    }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (RemoteException e) {
            System.out.println("<Rmi>Excecao ao atualizar o backup: "+e);
        }
        return true;
    }

    public boolean Elimina_evento(String nome_evento) {
        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {

            // Verifico se há presenças na tabela "assiste" para o evento
            String checkAssisteQuery = "SELECT COUNT(*) FROM assiste WHERE nome_evento = '" + nome_evento + "';";
            ResultSet resultSet = statement.executeQuery(checkAssisteQuery);
            resultSet.next();
            int presencas = resultSet.getInt(1);

            if (presencas > 0) {
                System.out.println("<BD> Nao e possível eliminar o evento [" + nome_evento + "], pois o mesmo contem presencas.");
                return false;
            } else {
                // Se não houver presenças, elimina o evento
                String deleteEventQuery = "DELETE FROM Evento WHERE nome_evento = '" + nome_evento + "';";

                if (statement.executeUpdate(deleteEventQuery) < 1) {
                    System.out.println("<BD> Erro na eliminacao do evento [" + nome_evento + "]");
                    return false; // erro na eliminação do evento
                } else {
                    System.out.println("<BD> Evento ["+ nome_evento +"] eliminado com sucesso");
                    connection.close();
                    for (ObservableInterface obv:observables) {
                        obv.executaUpdate("DELETE FROM Evento WHERE nome_evento = '" + nome_evento + "'");
                    }
                    setVersao();
                    }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (RemoteException e) {
            System.out.println("<Rmi>Excecao ao atualizar o backup: "+e);
        }
        return false;
    }

// VERRRRRR
    public List <Evento> Consulta_eventos(Msg_ConsultaComFiltros filtros) {
        List<Evento> eventos = new ArrayList<>();


        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {

            String filtroEvento = "SELECT * FROM Evento WHERE 1=1;"; // Começa com "1=1" para mostrar todos resultados
                                                                  // A medida que campos forem não null ele inclui na pesquisa

            if (filtros.getNome() != null && !filtros.getNome().isEmpty()) {
                filtroEvento += " AND nome_evento LIKE '%" + filtros.getNome()  + "%'";
            }

            if (filtros.getLocal() != null) {
                filtroEvento += " AND local LIKE '%" + filtros.getLocal() + "'";
            }

            // data
            if (filtros.getLimData1() != null && filtros.getLimData2() != null) {
                filtroEvento += "AND date(Evento.data_realizacao) BETWEEN '" + filtros.getLimData1() + "' AND '" + filtros.getLimData2() + "' ";
            }
            else if (filtros.getLimData1() != null)
                filtroEvento += "AND date(Evento.data_realizacao) > '" + filtros.getLimData1() + "' ";
            else if (filtros.getLimData2() != null)
                filtroEvento += "AND date(Evento.data_realizacao) < '" + filtros.getLimData2() + "' ";

            // hora
            if (filtros.getHoraInicio() != null && filtros.getHoraFim() != null) {
                filtroEvento += " AND time(Evento.hora_inicio) BETWEEN '" + filtros.getHoraInicio() + "' AND '" + filtros.getHoraFim() + "' ";
            }
            else if (filtros.getHoraInicio() != null) {
                filtroEvento += " AND time(Evento.hora_inicio) > '" + filtros.getHoraInicio() + "' ";
            }
            else if (filtros.getHoraFim() != null) {
                filtroEvento += " AND time(Evento.hora_inicio) < '" + filtros.getHoraFim() + "' ";
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

    public boolean InserePresencas(String nomeEvento, String[] emails) {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {

            //Att: Vi na net que para cenas na bd que envolvam duas chaves primárias deve se utilizar esse PreparedStatement
            //É rezar que funcione bem e que seja assim mesmo

            // Verificar se o evento existe
            String verificaEventoQuery = "SELECT COUNT(*) FROM Evento WHERE nome_evento = ?;";
            PreparedStatement eventoStatement = connection.prepareStatement(verificaEventoQuery); //Para preparar a consulta
            eventoStatement.setString(1, nomeEvento); //Para substituir o ? pelo nome do evento, ou seja indexar o nome do evento
            int eventosEncontrados = eventoStatement.executeQuery().getInt(1); //Para executar a consulta e devolver o resultado

            if(eventosEncontrados == 1) {
                for (String emailEstudante : emails) {
                    // Verificar se os estudantes da lista existem na db
                    String verificaEstudanteQuery = "SELECT COUNT(*) FROM Utilizador WHERE email = ?;";
                    PreparedStatement alunoStatement = connection.prepareStatement(verificaEstudanteQuery);
                    alunoStatement.setString(1, emailEstudante);
                    int estudantesEncontrados = alunoStatement.executeQuery().getInt(1);

                    if (estudantesEncontrados == 1) {
                        // Se o evento e o aluno existirem insere a presença
                        String inserePresencaQuery = "INSERT INTO assiste (nome_evento, email) VALUES (?, ?);";
                        PreparedStatement presencaStatement = connection.prepareStatement(inserePresencaQuery);
                        presencaStatement.setString(1, nomeEvento);
                        presencaStatement.setString(2, emailEstudante);

                        int rowsAffected = presencaStatement.executeUpdate();

                        if (rowsAffected == 1) {
                            System.out.println("<BD> A presenca do estudante " + emailEstudante + " no evento " + nomeEvento + " foi registada com sucesso");
                        } else {
                            System.out.println("<BD> Erro ao registar a presença do estudante " + emailEstudante + ".");
                        }
                    } else {
                        System.out.println("<BD> Utilizador [" + emailEstudante + "] nao existe.");
                    }
                }
            }
            else System.out.println("<BD> Evento [" + nomeEvento +"] nao existe.");
            connection.close();
            for (ObservableInterface obv:observables) {obv.InserePresencas(nomeEvento,emails);}
            setVersao();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (RemoteException e) {
            System.out.println("<Rmi>Excecao ao atualizar o backup: "+e);
            return true;
        }
    }

    public boolean EliminaPresencas(String nomeEvento, String [] emails) {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            for (String emailEstudante : emails) {
                String eliminaPresencaQuery = "DELETE FROM assiste WHERE nome_evento = ? AND email = ?;";
                PreparedStatement eliminaPresencaStatement = connection.prepareStatement(eliminaPresencaQuery);
                eliminaPresencaStatement.setString(1, nomeEvento);
                eliminaPresencaStatement.setString(2, emailEstudante);

                int rowsAffected = eliminaPresencaStatement.executeUpdate();

                if (rowsAffected == 1) {
                    System.out.println("<BD> A Presença do estudante " + emailEstudante +" do evento " + nomeEvento + " foi eliminada com sucesso.");
                    } else {
                    System.out.println("<BD> Nao foi encontrada a presenca do estudante " + emailEstudante + " no evento " + nomeEvento + ".");
                }
            }
            connection.close();
            setVersao();
            for (ObservableInterface obv:observables) {obv.EliminaPresencas(nomeEvento,emails);}
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (RemoteException e) {
            System.out.println("<Rmi>Excecao ao atualizar o backup: "+e);
            return true;
        }
    }

//Gerar codigo
    public int GeraCodigoRegisto(String evento, int validadeMinutos) {
        //Estou a utilizar o PreparedStatement pq é necessário para passar valores dinâmicos por parametros (para consultas)
        String verificaEventoQuery = "SELECT data_realizacao, hora_inicio, hora_fim FROM Evento WHERE nome_evento = ?;";

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
/*
                System.out.println(dataAtualMillis);
                System.out.println(dataInicioMillis);
                System.out.println(dataFimMillis);*/

                //Se estiver dentro do intervalo de tempo, então o evento se encontra a decorrer
                if (dataAtualMillis >= dataInicioMillis && dataAtualMillis <= dataFimMillis) {


                    // Eu alterei para passar a eliminar os codigos antigos e substituir pelos novos, nao me parece muito logico guarda-los, dps diz me o que achas
                    String EliminaCodigosAnterioresQuery = "DELETE  FROM Codigo_Registo  WHERE nome_evento = ?;";//
                    PreparedStatement expiraStatement = connection.prepareStatement(EliminaCodigosAnterioresQuery);
                    expiraStatement.setString(1, evento); // Define o valor do nome_evento para o ? da query
                    expiraStatement.executeUpdate();// se existirem codigos antigos são eliminados se nao existirem nao acontece nada

                    // Depois de expirar os codigos anteriores, ele vai gerar um novo código
                    int codigo = geraCodigoAleatorio();

                   //Calcula o tempo de validade para o sistema saber quando deve expirar o código (a informacao não fica armazenada apenas com os minutos dados pelo utilizador
                    //Armazena então com o tipo Timestamp, e alterei assim a bd
                    long validadeMillis = validadeMinutos * 60 * 1000;
                    Timestamp horarioValidade = new Timestamp(dataAtualMillis + validadeMillis);

                    String insereCodigoQuery = "INSERT INTO Codigo_Registo (n_codigo_registo, nome_evento, validade) VALUES (?, ?, ?);";
                    PreparedStatement insereStatement = connection.prepareStatement(insereCodigoQuery);
                    insereStatement.setInt(1, codigo);
                    insereStatement.setString(2, evento);
                    insereStatement.setTimestamp(3, horarioValidade); //Estou a salvar em TimeStamp porque é melhor para verificar a validade do codigo
                    insereStatement.executeUpdate();
                    System.out.println("<BD> Novo codigo para o evento [" + evento + "]");
                    connection.close();
                    setVersao();
                  //  observables.get(0).insert("INSERT INTO Codigo_Registo (n_codigo_registo, nome_evento, validade) VALUES (?, ?, ?)")
                        return codigo;
                } else {
                    System.out.println("<BD> O evento [" + evento +"] não esta a decorrer no momento");
                    return 0;
                }
            } else {
                System.out.println("<BD> O Evento [" + evento + "] nao foi encontrado");
                return 0;
            }
        } catch (SQLException | ParseException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    private int geraCodigoAleatorio() {
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


