package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class LogoutUtilizador extends BorderPane {
    private Button confirmar, cancelar;
    ProgClienteManager progClienteManager;
    public LogoutUtilizador(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        Label label = new Label("Quer mesmo sair da sua conta?");
        confirmar = new Button("Sair");
        cancelar = new Button("Cancelar");

        HBox hBox = new HBox(cancelar, confirmar);
        VBox vBox = new VBox(label, hBox);

        this.setStyle("-fx-background-color: #FFF3E0; -fx-padding: 30;");
        this.setCenter(vBox);
        this.setFocusTraversable(true);
    }

    private void registerHandlers() {
        cancelar.setOnAction(e -> {
            MainCliente.menuSBP.set("CONTA");
        });
        confirmar.setOnAction(e -> {
            progClienteManager.logout();
            MainCliente.menuSBP.set("MENU");
        });
        MainCliente.menuSBP.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(MainCliente.menuSBP.get().equals("LOGOUT"));
    }
}
