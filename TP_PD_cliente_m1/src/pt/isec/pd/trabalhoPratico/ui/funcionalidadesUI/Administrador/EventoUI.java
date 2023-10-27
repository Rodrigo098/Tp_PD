package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class EventoUI extends VBox {
    private Button editarEvento, eliminarEvento, gerarCodigoPresencas, listarPresencas, obterPresencasCSV, eliminarPresencas, inserirPresencas;
    private String nomeEvento;
    public EventoUI(String nomeEvento){
        this.nomeEvento = nomeEvento;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {

        Label eventoNome = new Label("evento -" + nomeEvento);
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

        this.setStyle("-fx-border-color: black");
        this.getChildren().addAll(eventoNome, flowPane);
    }
    private void registerHandlers() {
        gerarCodigoPresencas.setOnAction(e -> {
            //progClienteManager.gerarCodigoPresencas(nomeEvento);
        });
        editarEvento.setOnAction(e -> {
            //progClienteManager.editarEvento(nomeEvento);
        });
        eliminarEvento.setOnAction(e -> {
            //progClienteManager.eliminarEvento(nomeEvento);
        });
        listarPresencas.setOnAction(e -> {});
        obterPresencasCSV.setOnAction(e -> {
            //progClienteManager.obterPresencasCSV(nomeEvento);
        });
        eliminarPresencas.setOnAction(e -> {
            //progClienteManager.eliminarPresencas(nomeEvento);
        });
        inserirPresencas.setOnAction(e -> {
            //progClienteManager.inserirPresencas(presencasAInserir.getText(), nomeEvento);
        });
    }

    private void update() {
        //evento que mudou
    }
}
