package pt.isec.pd.trabalhoPratico.model;

import pt.isec.pd.trabalhoPratico.model.classesComunication.Geral;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObservableInterface extends Remote {
    public void avisaObservables(Geral Msg,int versao) throws RemoteException;


}
