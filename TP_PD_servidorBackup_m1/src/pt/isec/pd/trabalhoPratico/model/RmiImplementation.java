package pt.isec.pd.trabalhoPratico.model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiImplementation extends UnicastRemoteObject implements RemoteInterface {
    public RmiImplementation() throws RemoteException {
    }

    @Override
    public byte[] getCopiaDb() throws RemoteException {
        return new byte[0];
    }

    @Override
    public void registaBackupServers(String backupServiceURL) throws RemoteException {

    }
}

