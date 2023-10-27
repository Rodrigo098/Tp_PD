package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class LoginUtilizador extends BorderPane {
    private Button entrar, voltar;
    private TextField username, password;
    private ProgClienteManager progClienteManager;

    public LoginUtilizador(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        username = new TextField("Username");
        password = new TextField("Password");

        entrar = new Button("Entrar");
        voltar = new Button("Voltar");

        HBox hBox = new HBox(voltar, entrar);
        VBox vBox = new VBox(username, password, hBox);

        this.setStyle("-fx-background-color: #E0F2F1; -fx-padding: 30;");
        this.getStyleClass().add("extendBorderPane");
        this.setCenter(vBox);
        this.setFocusTraversable(true);
    }

    private void registerHandlers() {
        voltar.setOnAction(e -> {
            MainCliente.menuSBP.set("MENU");
        });
        entrar.setOnAction(e -> {
            progClienteManager.login(username.getText(), password.getText());
            username.setText("");
            password.setText("");
        });
        MainCliente.menuSBP.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(MainCliente.menuSBP.get().equals("LOGIN"));
    }
}
