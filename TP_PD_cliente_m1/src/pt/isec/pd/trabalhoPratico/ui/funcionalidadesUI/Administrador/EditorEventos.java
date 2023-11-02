package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class EditorEventos extends BorderPane {
    private Button editarEvento, eliminarEvento, gerarCodigoPresencas, obterPresencasCSV, eliminarPresencas, inserirPresencas;
    private final ProgClienteManager progClienteManager;
    private ListView<String> listaPresencas;

    public EditorEventos(ProgClienteManager progClienteManager){
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

        FlowPane flowPane = new FlowPane(gerarCodigoPresencas, editarEvento, eliminarEvento, obterPresencasCSV, eliminarPresencas, inserirPresencas);
        flowPane.setVgap(8);
        flowPane.setHgap(8);

        listaPresencas = new ListView<>();
        //extrairListaPresencas();

        this.setCenter(listaPresencas);
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
