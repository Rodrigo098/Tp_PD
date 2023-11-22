package pt.isec.pd.trabalhoPratico.model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiImplementation extends UnicastRemoteObject implements RemoteInterface {
    public RmiImplementation() throws RemoteException {
    }

    @Override
    public void getDB() {
        //TODO
    }
}
