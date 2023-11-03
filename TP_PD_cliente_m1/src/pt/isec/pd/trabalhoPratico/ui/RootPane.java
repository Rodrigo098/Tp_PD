package pt.isec.pd.trabalhoPratico.ui;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador.ContaAdministradorUI;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador.ContaUtilizadorUI;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador.RegistoUtilizadorUI;

import java.io.IOException;

public class RootPane extends BorderPane {
    private Button login;
    private Text registar;
    private TextField username, password;
    ProgClienteManager progClienteManager;

    public RootPane(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();

        update();
    }

    private void createViews() {
        username = new TextField();
        password = new TextField();
        username.setPromptText("Username");
        password.setPromptText("Password");

        login = new Button("Login");
        registar = new Text("Registar");
        registar.getStyleClass().add("links");

        VBox vBox = new VBox(username, password, login, registar);

        StackPane stackPane = new StackPane(
                new BorderPane(vBox),
                new RegistoUtilizadorUI(progClienteManager),
                //new ContaUtilizadorUI(progClienteManager)
               new ContaAdministradorUI(progClienteManager)
        );
        stackPane.setMaxHeight(400);
        this.setCenter(stackPane);
    }

    private void registerHandlers() {
        login.setOnAction(e -> {
            try {
                progClienteManager.login(username.getText(), password.getText());
            } catch (Exception ex) {
                MainCliente.menuSBP.set("ERRO");
            }
            username.setText(null);
            password.setText(null);
        });
        registar.setOnMouseClicked( e -> {
            MainCliente.menuSBP.set("REGISTO");
        });
        MainCliente.menuSBP.addListener(observable -> update());
    }

    private void update() {
    }
}
