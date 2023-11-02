package pt.isec.pd.trabalhoPratico;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import pt.isec.pd.trabalhoPratico.ui.MainClienteJFX;

public class MainCliente {
    public static SimpleStringProperty menuSBP = new SimpleStringProperty("MENU");
    public static SimpleStringProperty clienteSBP = new SimpleStringProperty("INDEFINIDO");
    public static void main(String[] args) {
        Application.launch(MainClienteJFX.class, args);
    }
}
