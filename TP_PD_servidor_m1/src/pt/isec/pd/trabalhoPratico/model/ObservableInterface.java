package pt.isec.pd.trabalhoPratico.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObservableInterface extends Remote {
    public void avisaObservables() throws RemoteException;
}
