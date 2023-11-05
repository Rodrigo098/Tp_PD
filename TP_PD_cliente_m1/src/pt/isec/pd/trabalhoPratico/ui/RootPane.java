package pt.isec.pd.trabalhoPratico.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

        Label label = new Label("Entrar na aplicação");
        label.getStyleClass().add("titulo");

        VBox vBox = new VBox(label, new HBox(new Text("Nome de utilizador: "), username), new HBox(new Text("Password: "), password), login, registar);
        vBox.getStyleClass().add("sombreamentoBox");
        VBox.setMargin(label, new Insets(0, 10, 30, 10));

        StackPane stackPane = new StackPane(
                new BorderPane(vBox),
                new ContaUtilizadorUI(progClienteManager),
                new RegistoUtilizadorUI(progClienteManager),
                new ContaAdministradorUI(progClienteManager)
        );
        this.getStyleClass().add("entradaPane");
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
