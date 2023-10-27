package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class RegistoUtilizador extends BorderPane {
    private Button registar, voltar;
    private TextField username, email, password, confirmar_password;
    private ProgClienteManager progClienteManager;
    public RegistoUtilizador(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        username = new TextField("Username");
        password = new TextField("Password");
        confirmar_password = new TextField("confirmar Password");

        registar = new Button("Criar registo");
        voltar = new Button("Voltar");

        HBox hBox = new HBox(voltar, registar);
        VBox vBox = new VBox(username, email, password, confirmar_password, hBox);

        this.setStyle("-fx-background-color: #78909C; -fx-padding: 30;");
        this.setCenter(vBox);
        this.setFocusTraversable(true);
    }

    private void registerHandlers() {
        voltar.setOnAction(e -> {
            MainCliente.menuSBP.set("MENU");
        });
        registar.setOnAction(e -> {
            progClienteManager.registar(username.getText(), password.getText(), confirmar_password.getText(), email.getText(), "123");
        });
        MainCliente.menuSBP.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(MainCliente.menuSBP.get().equals("REGISTO"));
    }
}
