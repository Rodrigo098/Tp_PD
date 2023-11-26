package pt.isec.pd.trabalhoPratico.vista.funcionalidadesUI.Utilizador;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class MarcarPresencaUI extends BorderPane {
    private TextField codigo, evento;
    private Button submeter, cancelar;
    private Text resultado;
    private final ProgClienteManager progClienteManager;

    public MarcarPresencaUI(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        evento = new TextField();
        evento.setPromptText("nome do evento");
        codigo = new TextField();
        codigo.setPromptText("codigo do evento");

        resultado = new Text();

        submeter = new Button("Submeter");
        submeter.getStyleClass().add("confirmar");
        cancelar = new Button("Cancelar");
        cancelar.getStyleClass().add("cancelar");

        VBox vBox = new VBox(new Label("Evento a marcar presença:"), evento, new Label("Código do evento:"), codigo);

        Label label = new Label("Marcar Presença");
        label.getStyleClass().add("titulo");

        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(vBox);
        this.setBottom(new VBox(new HBox(submeter, cancelar), resultado));
    }

    private void registerHandlers() {
        submeter.setOnAction( e -> {
            resultado.setText(progClienteManager.registarPresenca(evento.getText(), codigo.getText()));
        });

        cancelar.setOnAction(e -> {
            ContaUtilizadorUI.opcaoUti.set("NADA");
        });

        ContaUtilizadorUI.opcaoUti.addListener(observable -> update());
    }

    private void update() {
        if(ContaUtilizadorUI.opcaoUti.get().equals("MARCAR_PRES")) {
            limparTextFields();
            this.setVisible(true);
        }
        else this.setVisible(false);
    }

    private void limparTextFields() {
        resultado.setText("");
        evento.clear();
        codigo.clear();
    }
}