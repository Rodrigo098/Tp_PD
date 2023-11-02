package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class ContaUtilizadorUI extends BorderPane {
    static protected SimpleStringProperty opcaoUti = new SimpleStringProperty("NADA");
    private Button listaPresencas, marcarPresenca, editarRegisto, obterCSV, logout;
    private ProgClienteManager progClienteManager;
    public ContaUtilizadorUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        listaPresencas = new Button("Ver Lista de Presenças ");
        marcarPresenca = new Button("Registar Presença");
        editarRegisto = new Button("Editar Registo");
        obterCSV = new Button("Obter CSV");
        logout = new Button("Logout");
        logout.getStyleClass().add("btnLogout");

        HBox hBox = new HBox(listaPresencas, marcarPresenca, editarRegisto, obterCSV);
        StackPane stackPane = new StackPane(new ListarPresencasUI(progClienteManager), new MarcarPresencaUI(progClienteManager), new EditarRegistoUI(progClienteManager));

        stackPane.setMaxSize(500, 250);
        this.setStyle("-fx-background-color: #E8EAF6; -fx-padding: 30 30 0 30;");
        this.setTop(hBox);
        this.setCenter(stackPane);
        this.setBottom(logout);
        this.setFocusTraversable(true);
    }

    private void registerHandlers() {
        listaPresencas.setOnAction(e -> {
            MainCliente.menuSBP.set("LISTAR_PRESENCAS");
        });
        marcarPresenca.setOnAction(e -> {
            MainCliente.menuSBP.set("MARCAR_PRES");
        });
        editarRegisto.setOnAction(e -> {
            MainCliente.menuSBP.set("EDITAR_REGISTO");
        });
        logout.setOnAction(e -> {
            progClienteManager.logout();
            MainCliente.menuSBP.set("MENU");
            MainCliente.clienteSBP.set("INDEFINIDO");
        });
        MainCliente.clienteSBP.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(MainCliente.clienteSBP.get().equals("UTILIZADOR"));
    }
}
