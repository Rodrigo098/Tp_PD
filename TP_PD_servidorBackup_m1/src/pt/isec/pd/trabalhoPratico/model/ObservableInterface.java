package pt.isec.pd.trabalhoPratico.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObservableInterface extends Remote {
    boolean submitcod(int codigo,String nome_evento,String emailuser) throws RemoteException;
    void executaUpdate(String query) throws RemoteException;
    boolean InserePresencas(String nomeEvento, String[] emails) throws RemoteException;
    boolean EliminaPresencas(String nomeEvento, String [] emails) throws RemoteException;
}
/*
    public void avisaObservables(Geral Msg,int versao) throws RemoteException;
    public boolean RegistoNovoUser(Utilizador user, String password) throws RemoteException;
    public boolean edita_registo( Utilizador user, String pasword ) throws  RemoteException;
    public boolean Cria_evento(Msg_Cria_Evento evento) throws RemoteException;

    public boolean Edita_evento(Msg_Edita_Evento evento) throws RemoteException;
    public boolean Elimina_evento(String nome_evento) throws RemoteException;

*/