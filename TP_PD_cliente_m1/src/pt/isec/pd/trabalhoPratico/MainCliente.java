package pt.isec.pd.trabalhoPratico;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.ui.MainClienteJFX;

public class MainCliente {
    public static ProgClienteManager progClienteManager;

    public static SimpleStringProperty menuSBP = new SimpleStringProperty("MENU");
    static {
        progClienteManager = new ProgClienteManager();
    }
    public static void main(String[] args) {
        Application.launch(MainClienteJFX.class, args);
    }
}
