package pt.isec.pd.trabalhoPratico.model;

import pt.isec.pd.trabalhoPratico.ObservableInterface;

import java.rmi.Remote;

public interface RemoteInterface extends Remote {
        public void getDB();
        public void addObservable(ObservableInterface obv);
        public void RemoveObservable(ObservableInterface obv);

}
