package pt.isec.pd.trabalhoPratico.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.*;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.*;

public class RootPane extends BorderPane {
    private Button login, registar;

    public RootPane() {
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        login = new Button("Login");
        registar = new Button("Registar");

        HBox hBox = new HBox(login, registar);
        BorderPane borderPane = new BorderPane(hBox);
        borderPane.setFocusTraversable(true);

        StackPane funcionalidades = new StackPane(
                borderPane,
                new LoginUtilizador(),
                new RegistoUtilizador(),
                new LogoutUtilizador(),
                new ContaUtilizador()
        );
        //funcionalidades.setFocusTraversable(true);
        //this.setFocusTraversable(true);
        this.setCenter(funcionalidades);
    }

    private void registerHandlers() {
        login.setOnAction(e -> {
            MainCliente.menuSBP.set("LOGIN");
            System.out.println("login");
        });
        registar.setOnAction(e -> {
            MainCliente.menuSBP.set("REGISTO");
            System.out.println("rgisto");
        });
    }

    private void update() {}
}
