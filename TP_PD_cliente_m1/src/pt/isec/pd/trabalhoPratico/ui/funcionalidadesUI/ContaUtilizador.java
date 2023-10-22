package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.MainCliente;

public class ContaUtilizador extends BorderPane {
    private Button lista, registar_presenca, voltar, logout;
    public ContaUtilizador() {
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        lista = new Button("Ver Lista de Presenças ");
        registar_presenca = new Button("Registar Presença");
        voltar = new Button("Voltar");
        logout = new Button("Logout");

        HBox hBox = new HBox(voltar, lista, registar_presenca);
        this.setBottom(logout);
        this.setCenter(hBox);
        this.setFocusTraversable(true);
    }

    private void registerHandlers() {
        voltar.setOnAction(e -> {
            MainCliente.menuSBP.set("MENU");
        });
        lista.setOnAction(e -> {
            //... manager
            //mostrar lista de presenças
        });
        registar_presenca.setOnAction(e -> {
            //mostrar form registar presença
        });
        logout.setOnAction(e -> {
            //... manager
            MainCliente.menuSBP.set("LOGOUT");
        });
        MainCliente.menuSBP.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(MainCliente.menuSBP.get().equals("CONTA"));
    }
}
