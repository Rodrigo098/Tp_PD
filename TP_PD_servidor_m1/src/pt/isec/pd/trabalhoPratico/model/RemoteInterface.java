package pt.isec.pd.trabalhoPratico.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote {
        public byte[] getCopiaDb() throws RemoteException;
        void registaBackupServers(String backupServiceURL) throws RemoteException;
        public void addObservable(ObservableInterface obv) throws RemoteException;
        public void RemoveObservable(ObservableInterface obv) throws RemoteException;

}
