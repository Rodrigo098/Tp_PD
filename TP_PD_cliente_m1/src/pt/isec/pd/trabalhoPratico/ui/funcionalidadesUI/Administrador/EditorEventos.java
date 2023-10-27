package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class EditorEventos extends BorderPane {
    private Button editarEvento, eliminarEvento, gerarCodigoPresencas, listarPresencas, obterPresencasCSV, eliminarPresencas, inserirPresencas;
    private ProgClienteManager progClienteManager;

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

        listarPresencas = new Button("Ver Presenças");
        listarPresencas.getStyleClass().add("eventoButton");

        eliminarPresencas = new Button("Eliminar Presenças");
        eliminarPresencas.getStyleClass().add("eventoButton");

        inserirPresencas = new Button("Inserir Presenças");
        inserirPresencas.getStyleClass().add("eventoButton");

        FlowPane flowPane = new FlowPane(gerarCodigoPresencas, editarEvento, eliminarEvento, listarPresencas, obterPresencasCSV, eliminarPresencas, inserirPresencas);

        flowPane.setVgap(8);
        flowPane.setHgap(4);

        Label label = new Label("Editor de Eventos");
        label.getStyleClass().add("titulo");

        VBox vBox = new VBox(new Label("Evento Selecionado: " + progClienteManager.obterEvento(ListarEventosUI.eventoSelecionado)), flowPane);
        this.setCenter(vBox);
        this.setTop(label);
    }

    private void registerHandlers() {
        gerarCodigoPresencas.setOnAction(e -> {
            //progClienteManager.gerarCodigoPresencas(ListarEventosUI.eventoSelecionado);
        });
        editarEvento.setOnAction(e -> {
            //progClienteManager.editarEvento(ListarEventosUI.eventoSelecionado);
        });
        eliminarEvento.setOnAction(e -> {
            //progClienteManager.eliminarEvento(ListarEventosUI.eventoSelecionado);
        });
        listarPresencas.setOnAction(e -> {});
        obterPresencasCSV.setOnAction(e -> {
            //progClienteManager.obterPresencasCSV(ListarEventosUI.eventoSelecionado);
        });
        eliminarPresencas.setOnAction(e -> {
            //progClienteManager.eliminarPresencas(ListarEventosUI.eventoSelecionado);
        });
        inserirPresencas.setOnAction(e -> {
            //progClienteManager.inserirPresencas(presencasAInserir.getText(), ListarEventosUI.eventoSelecionado);
        });

        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("EDITOR_EVENTOS"));
    }
}
