package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

import java.io.IOException;

public class ContaUtilizadorUI extends BorderPane {
    private Button listaPresencas, registarPresenca, editarRegisto, obterCSV, logout;
    private ProgClienteManager progClienteManager;
    public ContaUtilizadorUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        listaPresencas = new Button("Ver Lista de Presenças ");
        registarPresenca = new Button("Registar Presença");
        editarRegisto = new Button("Editar Registo");
        obterCSV = new Button("Obter CSV");
        logout = new Button("Logout");
        logout.getStyleClass().add("btnLogout");

        VBox vBox = new VBox(listaPresencas, registarPresenca, editarRegisto, obterCSV);

        this.setStyle("-fx-background-color: #E8EAF6; -fx-padding: 30 30 0 30;");
        this.setMargin(logout, new Insets(10));
        this.setBottom(logout);
        this.setCenter(vBox);
        this.setFocusTraversable(true);
    }

    private void registerHandlers() {
        listaPresencas.setOnAction(e -> {
            //progClienteManager.consultarPresenças();
            //mostrar lista de presenças
        });
        registarPresenca.setOnAction(e -> {
            //mostrar form registar presença
            //o progClienteManager.registarPresença() chama-se na submissão do registo
        });
        editarRegisto.setOnAction(e -> {
            MainCliente.menuSBP.set("EDITAR_REGISTO");
        });
        obterCSV.setOnAction(e -> {
            progClienteManager.obterCSV();
        });
        logout.setOnAction(e -> {
            try {
                progClienteManager.logout();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        MainCliente.administradorSBP.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(MainCliente.administradorSBP.get().equals("UTILIZADOR"));
    }
}
