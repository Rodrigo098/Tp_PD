package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class ListarEventosUI extends FlowPane {
    private FlowPane eventosBoard;
    private Button refresh;
    private ProgClienteManager progClienteManager;
    public ListarEventosUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        Label eventoNome = new Label("Eventos");
        refresh = new Button("Refresh");
        eventosBoard = extrairListaEventos();

        this.getChildren().addAll(eventoNome, eventosBoard);
    }

    private void registerHandlers() {
        refresh.setOnAction(e -> {
            eventosBoard = extrairListaEventos();
        });
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("LISTAR_EVENTOS"));
    }

    private FlowPane extrairListaEventos() {
        FlowPane flowPane = new FlowPane();
        for (String evento : progClienteManager.obterListaEventos()) {
            flowPane.getChildren().add(new EventoUI(evento));
        }
        return flowPane;
    }

}
