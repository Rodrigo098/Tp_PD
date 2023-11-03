package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class MarcarPresencaUI extends BorderPane {
    private TextField codigo;
    private Button submeter, cancelar;
    private final ProgClienteManager progClienteManager;

    public MarcarPresencaUI(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        codigo = new TextField();
        codigo.setPromptText("codigo do evento");

        submeter = new Button("Submeter");
        cancelar = new Button("Cancelar");

        VBox vBox = new VBox(new Label("Código do evento:"), codigo);

        this.setStyle("-fx-background-color: #E8aFF6;");
        this.setCenter(vBox);
        this.setBottom(new HBox(submeter, cancelar));
    }

    private void registerHandlers() {
        submeter.setOnAction( e -> {
            progClienteManager.marcarPresenca(codigo.getText());
        });
        cancelar.setOnAction(e -> {
            ContaUtilizadorUI.opcaoUti.set("NADA");
        });
        ContaUtilizadorUI.opcaoUti.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaUtilizadorUI.opcaoUti.get().equals("MARCAR_PRES"));
    }

}
