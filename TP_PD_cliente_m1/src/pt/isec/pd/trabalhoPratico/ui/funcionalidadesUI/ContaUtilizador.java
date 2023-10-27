package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class ContaUtilizador extends BorderPane {
    private Button lista, registar_presenca, voltar, logout;
    private ProgClienteManager progClienteManager;
    public ContaUtilizador(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        lista = new Button("Ver Lista de Presenças ");
        registar_presenca = new Button("Registar Presença");
        voltar = new Button("Voltar");
        logout = new Button("Logout");
        logout.getStyleClass().add("btnLogout");

        VBox vBox = new VBox(lista, registar_presenca, voltar);

        this.setStyle("-fx-background-color: #E8EAF6; -fx-padding: 30 30 0 30;");
        this.setMargin(logout, new Insets(10));
        this.setBottom(logout);
        this.setCenter(vBox);
        this.setFocusTraversable(true);
    }

    private void registerHandlers() {
        voltar.setOnAction(e -> {
            MainCliente.menuSBP.set("MENU");
        });
        lista.setOnAction(e -> {
            //progClienteManager.consultarPresenças();
            //mostrar lista de presenças
        });
        registar_presenca.setOnAction(e -> {
            //mostrar form registar presença
            //o progClienteManager.registarPresença() chama-se na submissão do registo
        });
        logout.setOnAction(e -> {
            progClienteManager.logout();
            MainCliente.menuSBP.set("LOGOUT");
        });
        MainCliente.menuSBP.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(MainCliente.menuSBP.get().equals("CONTA"));
    }
}
