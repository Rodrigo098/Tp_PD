package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class ContaUtilizadorUI extends BorderPane {
    static protected SimpleStringProperty opcaoUti = new SimpleStringProperty("NADA");
    private Button listaPresencas, marcarPresenca, editarRegisto, logout, voltar;
    private HBox funcionalidades;
    private ProgClienteManager progClienteManager;
    public ContaUtilizadorUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update1();
        update2();
    }

    private void createViews() {
        voltar = new Button("Voltar");
        listaPresencas = new Button("Ver Lista de Presenças ");
        marcarPresenca = new Button("Registar Presença");
        editarRegisto = new Button("Editar Registo");
        logout = new Button("Logout");
        logout.getStyleClass().add("btnLogout");

        VBox vBox = new VBox(listaPresencas, marcarPresenca, editarRegisto, logout);
        vBox.getStyleClass().add("sombreamentoBox");

        funcionalidades =  new HBox(voltar, new StackPane(new ListarPresencasUI(progClienteManager), new MarcarPresencaUI(progClienteManager), new EditarRegistoUI(progClienteManager)));
        funcionalidades.setFocusTraversable(true);
        StackPane stackPane = new StackPane(vBox, funcionalidades);

        this.setStyle("-fx-background-color: #80CBC4;");
        this.setCenter(stackPane);
        this.setFocusTraversable(true);
    }

    private void registerHandlers() {
        voltar.setOnAction(e -> {
            opcaoUti.set("NADA");
        });
        listaPresencas.setOnAction(e -> {
            opcaoUti.set("LISTAR_PRESENCAS");
        });
        marcarPresenca.setOnAction(e -> {
            opcaoUti.set("MARCAR_PRES");
        });
        editarRegisto.setOnAction(e -> {
            opcaoUti.set("EDITAR_REGISTO");
        });
        logout.setOnAction(e -> {
            progClienteManager.logout();
            MainCliente.menuSBP.set("MENU");
            MainCliente.clienteSBP.set("INDEFINIDO");
        });
        MainCliente.clienteSBP.addListener(observable -> update1());
        opcaoUti.addListener(observable -> update2());
    }

    private void update1() {
        this.setVisible(MainCliente.clienteSBP.get().equals("UTILIZADOR"));
    }
    private void update2() {
        funcionalidades.setVisible(!opcaoUti.get().equals("NADA"));
    }
}
