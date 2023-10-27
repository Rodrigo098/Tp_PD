package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
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

        Label eventoNome = new Label("Criar Evento");
        this.setTop(eventoNome);

    }
    private void registerHandlers() {
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("CRIAR_EVENTO"));
    }
}
