package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class ListarEventosUI extends BorderPane {
    protected static int eventoSelecionado;
    private Button obterCSV;
    private ListView listaEventos;
    private ProgClienteManager progClienteManager;

    public ListarEventosUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        obterCSV = new Button("Obter CSV");
        listaEventos = new ListView<String>();
        extrairListaEventos();

        Label label = new Label("Lista de Eventos");
        label.getStyleClass().add("titulo");
        VBox vBox = new VBox(listaEventos, obterCSV);
        this.setCenter(vBox);
        this.setTop(label);
    }

    private void registerHandlers() {
        listaEventos.setOnMouseClicked( mouseEvent -> {
            if(mouseEvent.getClickCount() == 2){
                eventoSelecionado = listaEventos.getSelectionModel().getSelectedIndex();
                ContaAdministradorUI.opcaoAdmin.set("EDITOR_EVENTOS");
            }
        });
        obterCSV.setOnAction(e -> {
            progClienteManager.obterCSV();
        });
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("LISTAR_EVENTOS"));
        extrairListaEventos();
    }

    private void extrairListaEventos() {
        listaEventos.getItems().clear();
        for (String evento : progClienteManager.()) {
            listaEventos.getItems().add(new String(evento));
        }
    }
}
