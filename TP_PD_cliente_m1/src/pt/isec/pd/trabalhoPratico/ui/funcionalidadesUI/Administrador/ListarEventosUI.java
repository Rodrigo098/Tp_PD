package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

import java.util.ArrayList;

public class ListarEventosUI extends BorderPane {
    protected static int eventoSelecionado;
    private ListView<String> listaEventos;
    private final ProgClienteManager progClienteManager;

    public ListarEventosUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        listaEventos = new ListView<>();
        extrairListaEventos();

        Label label = new Label("Lista de Eventos");
        label.getStyleClass().add("titulo");

        setMargin(listaEventos, new Insets(20, 0, 5, 0));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(listaEventos);
    }

    private void registerHandlers() {
        listaEventos.setOnMouseClicked( mouseEvent -> {
            if(mouseEvent.getClickCount() == 2){
                eventoSelecionado = listaEventos.getSelectionModel().getSelectedIndex();
                ContaAdministradorUI.opcaoAdmin.set("EDITOR_EVENTOS");
            }
        });
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("LISTAR_EVENTOS"));
        extrairListaEventos();
    }

    private void extrairListaEventos() {
        listaEventos.getItems().clear();
        for (String evento : progClienteManager.getListaEventos()) {
            listaEventos.getItems().add(evento);
        }
    }
}
