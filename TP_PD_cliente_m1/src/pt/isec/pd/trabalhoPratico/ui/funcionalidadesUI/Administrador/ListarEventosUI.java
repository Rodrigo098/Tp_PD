package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Message_types;
import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.PersonalNodes.FiltrosUI;

public class ListarEventosUI extends BorderPane {
    protected static Evento eventoSelecionado;
    private FiltrosUI filtros;
    private ListView<Evento> listaEventos;
    private final ProgClienteManager progClienteManager;

    public ListarEventosUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        filtros = new FiltrosUI();
        listaEventos = new ListView<>();
        listaEventos.setPlaceholder(new Text("Não há eventos registados"));

        Label label = new Label("Lista de Eventos");
        label.getStyleClass().add("titulo");

        setMargin(listaEventos, new Insets(20, 0, 5, 0));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(new VBox(filtros, listaEventos));
    }

    private void registerHandlers() {
        listaEventos.setOnMouseClicked( mouseEvent -> {
            if(mouseEvent.getClickCount() == 2){
                eventoSelecionado = listaEventos.getSelectionModel().getSelectedItem();
                ContaAdministradorUI.opcaoAdmin.set("EDITOR_EVENTOS");
            }
        });
        filtros.procurar.setOnAction( e -> extrairListaEventos());
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
        progClienteManager.addAtualizacaoListener(observable -> Platform.runLater(this::extrairListaEventos));
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("LISTAR_EVENTOS"));
        if(this.isVisible()) extrairListaEventos();
    }

    private void extrairListaEventos() {
        listaEventos.getItems().clear();
        for (Evento evento : progClienteManager.obterListaConsulta(Message_types.CONSULTA_EVENTOS, filtros.getNomeEvento(), filtros.getLocal(), filtros.getLimData1(), filtros.getLimData2(), filtros.getHoraInicio(), filtros.getHoraFim())) {
            listaEventos.getItems().add(evento);
        }
    }
}
