package pt.isec.pd.trabalhoPratico.model.programs;

import javafx.beans.property.SimpleIntegerProperty;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Geral;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Message_types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
public class AtualizacaoAsync implements Runnable {
    public static SimpleIntegerProperty atualizacao = new SimpleIntegerProperty(0);
    private final Socket socket;
    public AtualizacaoAsync(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {

        do{
            try(ObjectInputStream oin = new ObjectInputStream(socket.getInputStream()))
            {
                Object novaAtualizacao = oin.readObject();
                if(novaAtualizacao instanceof Geral g)
                    if(g.getTipo() == Message_types.ATUALIZACAO)
                        atualizacao.setValue(atualizacao.getValue() + 1);
            } catch (IOException | ClassNotFoundException ignored) {
            }
        }while(Thread.currentThread().isAlive());
    }
}