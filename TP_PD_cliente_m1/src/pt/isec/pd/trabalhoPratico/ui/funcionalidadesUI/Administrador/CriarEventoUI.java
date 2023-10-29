package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;


public class CriarEventoUI extends BorderPane {
    ProgClienteManager progClienteManager;

    public CriarEventoUI(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        Label label = new Label("Criar Evento");
        label.getStyleClass().add("titulo");
        this.setCenter(new VBox());
        this.setTop(label);
    }
    private void registerHandlers() {
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("CRIAR_EVENTO"));
    }
}
