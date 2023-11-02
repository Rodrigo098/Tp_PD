package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;


public class CriarEventoUI extends BorderPane {
    private TextField nomeEvento, local;
    ProgClienteManager progClienteManager;

    public CriarEventoUI(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        nomeEvento = new TextField();
        nomeEvento.setPromptText("nome do evento");
        local = new TextField();
        local.setPromptText("local do evento");

        Label label = new Label("Criar Evento");
        label.getStyleClass().add("titulo");

        this.setCenter(new VBox(new Label("Nome:"), nomeEvento, new Label("Local:"), local));
        this.setTop(label);
    }
    private void registerHandlers() {
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("CRIAR_EVENTO"));
    }
}
