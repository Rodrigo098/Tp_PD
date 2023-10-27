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
        gerarCodigoPresencas = new Button("Gerar Código Presenças");
        editarEvento = new Button("Editar Evento");
        eliminarEvento = new Button("Eliminar Evento");
        listarPresencas = new Button("Ver Lista de Presenças");
        obterPresencasCSV = new Button("Obter CSV");
        eliminarPresencas = new Button("Eliminar Presenças");
        inserirPresencas = new Button("Inserir Presenças");

        FlowPane flowPane = new FlowPane(gerarCodigoPresencas, editarEvento, eliminarEvento, listarPresencas, obterPresencasCSV, eliminarPresencas, inserirPresencas);

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
