package pt.isec.pd.trabalhoPratico.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.*;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador.ContaAdministradorUI;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador.ContaUtilizadorUI;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.LoginClienteUI;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador.RegistoUtilizadorUI;

public class RootPane extends BorderPane {
    private Button login, registar;
    ProgClienteManager progClienteManager;

    public RootPane(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        login = new Button("Login");
        registar = new Button("Registar");

        HBox hBox = new HBox(login, registar);
        hBox.getStyleClass().add("conteudo");

        StackPane stackPane = new StackPane(
                new BorderPane(hBox),
                new LoginClienteUI(progClienteManager),
                new RegistoUtilizadorUI(progClienteManager),
                new ContaUtilizadorUI(progClienteManager),
                new ContaAdministradorUI(progClienteManager)
        );
        /*StackPane funcionalidades = new StackPane(
                new BorderPane(hBox),
                new LoginClienteUI(progClienteManager),
                new RegistoUtilizadorUI(progClienteManager),
                new LogoutClienteUI(progClienteManager),
                new ContaUtilizadorUI(progClienteManager)
        );*/

        this.setCenter(stackPane);
    }

    private void registerHandlers() {
        login.setOnAction(e -> {
            MainCliente.menuSBP.set("LOGIN");
            System.out.println("login");
        });
        registar.setOnAction(e -> {
            MainCliente.menuSBP.set("REGISTO");
            System.out.println("registo");
        });
    }

    private void update() {}
}
