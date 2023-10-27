package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.scene.control.ListView;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class ListarEventosUI extends ListView<EventoUI> {
    private ProgClienteManager progClienteManager;
    public ListarEventosUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
    }

    private void registerHandlers() {
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("LISTAR_EVENTOS"));
        extrairListaEventos();
    }

    private void extrairListaEventos() {
        this.getItems().clear();

        for (String evento : progClienteManager.obterListaEventos()) {
            this.getItems().add(new EventoUI(evento));
        }
    }

}
