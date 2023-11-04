package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class EditorEventosUI extends BorderPane {
    protected static SimpleStringProperty opcaoEdicao = new SimpleStringProperty("NADA");
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

        listaPresencas = new ListView<>();
        extrairListaPresencas();

        eventoUI = new EventoUI();

        Label label = new Label("Editar Eventos");
        label.getStyleClass().add("titulo");

        setAlignment(label, javafx.geometry.Pos.CENTER);
        setMargin(listaPresencas, new javafx.geometry.Insets(20, 0, 10, 0));
        this.setTop(label);
        this.setCenter(new StackPane(listaPresencas, eventoUI));
        this.setBottom(flowPane);
    }

    private void registerHandlers() {
        gerarCodigoPresencas.setOnAction(e -> {
            progClienteManager.gerarCodPresenca(ListarEventosUI.eventoSelecionado);
        });
        editarEvento.setOnAction(e -> {
            //faz aparecer textfield e butão
        });
        eliminarEvento.setOnAction(e -> {
            progClienteManager.eliminarEvento(ListarEventosUI.eventoSelecionado);
        });
        obterPresencasCSV.setOnAction(e -> {
            progClienteManager.obterCSV_Admin();
        });
        eliminarPresencas.setOnAction(e -> {
            //faz aparecer textfield e botão
        });
        inserirPresencas.setOnAction(e -> {
            //faz aparecer textfield e botão
        });

        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("EDITOR_EVENTOS"));
    }

    private void extrairListaPresencas() {
        listaPresencas.getItems().clear();
        for (String evento : progClienteManager.consultaPresencasEvento(ListarEventosUI.eventoSelecionado)) {
            listaPresencas.getItems().add(evento);
        }
    }
}
