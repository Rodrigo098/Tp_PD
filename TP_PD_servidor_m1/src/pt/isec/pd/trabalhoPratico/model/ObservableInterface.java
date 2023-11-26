package pt.isec.pd.trabalhoPratico.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObservableInterface extends Remote {
    boolean submitcod(int codigo,String nome_evento,String emailuser) throws RemoteException;
    void executaUpdate(String query) throws RemoteException;
    boolean InserePresencas(String nomeEvento, String[] emails) throws RemoteException;
    boolean EliminaPresencas(String nomeEvento, String [] emails) throws RemoteException;
}
