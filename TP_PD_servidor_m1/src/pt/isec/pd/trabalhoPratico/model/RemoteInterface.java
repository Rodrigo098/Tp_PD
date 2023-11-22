package pt.isec.pd.trabalhoPratico.model;

import pt.isec.pd.trabalhoPratico.ObservableInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote {
        public void getDB() throws RemoteException;
        public void addObservable(ObservableInterface obv) throws RemoteException;
        public void RemoveObservable(ObservableInterface obv) throws RemoteException;

}
