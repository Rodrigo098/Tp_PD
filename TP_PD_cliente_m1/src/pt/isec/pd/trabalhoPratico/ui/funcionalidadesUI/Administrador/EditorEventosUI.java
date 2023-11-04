package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import com.sun.scenario.effect.impl.prism.PrImage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Message_types;

public class EditorEventosUI extends BorderPane {
    protected static SimpleStringProperty opcaoEdicao = new SimpleStringProperty("NADA");
    private int indice = 0;
    private final String[] opcoes = {"LISTA", "EDITAR", "ELI_PRESENCA", "INS_PRESENCA"};
    private Label mais;
    private Button editarEvento, eliminarEvento, gerarCodigoPresencas, obterPresencasCSV, eliminarPresencas, inserirPresencas;
    private ListView<String> listaPresencas;
    private EventoUI eventoUI;
    private final ProgClienteManager progClienteManager;

    public EditorEventosUI(ProgClienteManager progClienteManager){
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        gerarCodigoPresencas = new Button("Gerar Código");
        gerarCodigoPresencas.getStyleClass().add("eventoButton");

        editarEvento = new Button("Editar");
        editarEvento.getStyleClass().add("eventoButton");

        eliminarEvento = new Button("Eliminar");
        eliminarEvento.getStyleClass().add("eventoButton");

        obterPresencasCSV = new Button("CSV");
        obterPresencasCSV.getStyleClass().add("eventoButton");

        eliminarPresencas = new Button("Eliminar Presenças");
        eliminarPresencas.getStyleClass().add("eventoButton");

        inserirPresencas = new Button("Inserir Presenças");
        inserirPresencas.getStyleClass().add("eventoButton");

        FlowPane flowPane = new FlowPane(eliminarPresencas, inserirPresencas, gerarCodigoPresencas, editarEvento, eliminarEvento, obterPresencasCSV);
        flowPane.setVgap(10);
        flowPane.setHgap(20);

        mais = new Label("+");
        mais.getStyleClass().add("eventos");
        listaPresencas = new ListView<>();
        extrairListaPresencas();

        eventoUI = new EventoUI();

        Label label = new Label("Editar Eventos");
        label.getStyleClass().add("titulo");


        setAlignment(label, javafx.geometry.Pos.CENTER);
        setMargin(listaPresencas, new javafx.geometry.Insets(20, 0, 10, 0));
        this.setTop(label);
        this.setCenter(new HBox(new StackPane(listaPresencas, eventoUI), mais));
        this.setBottom(flowPane);
    }

    private void registerHandlers() {
        mais.setOnMouseClicked(e -> {
            opcaoEdicao.set(opcoes[indice + 1 >= opcoes.length - 1 ? indice = 0 : indice++]);
        });
        gerarCodigoPresencas.setOnAction(e -> {
            progClienteManager.gerarCodPresenca(ListarEventosUI.eventoSelecionado);
        });
        editarEvento.setOnAction(e -> {
            progClienteManager.criarEditar_Evento(eventoUI.nomeEvento.getText(), eventoUI.local.getText(), eventoUI.data.getValue(), eventoUI.horaInicio.getValue(), eventoUI.horaFim.getValue(), Message_types.EDIT_EVENTO);
        });
        eliminarEvento.setOnAction(e -> {
            progClienteManager.eliminarEvento(ListarEventosUI.eventoSelecionado);
        });
        obterPresencasCSV.setOnAction(e -> {
            progClienteManager.obterCSV_Admin();
        });
        eliminarPresencas.setOnAction(e -> {
            progClienteManager.eliminaInsere_Eventos(Message_types.ELIMINA_PRES, ListarEventosUI.eventoSelecionado, "OLA");
        });
        inserirPresencas.setOnAction(e -> {
            progClienteManager.eliminaInsere_Eventos(Message_types.INSERE_PRES, ListarEventosUI.eventoSelecionado, "OLA");
        });

        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
        opcaoEdicao.addListener(observable -> update2());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("EDITOR_EVENTOS"));
    }

    private void update2() {
        editarEvento.setDisable(opcaoEdicao.get().equals("EDITAR"));
        eliminarPresencas.setDisable(opcaoEdicao.get().equals("ELI_PRESENCA"));
        inserirPresencas.setDisable(opcaoEdicao.get().equals("INS_PRESENCA"));
    }

    private void extrairListaPresencas() {
        listaPresencas.getItems().clear();
        for (String evento : progClienteManager.consultaPresencasEvento(ListarEventosUI.eventoSelecionado)) {
            listaPresencas.getItems().add(evento);
        }
    }
}
