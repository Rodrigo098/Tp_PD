package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Message_types;


public class CriarEventoUI extends BorderPane {
    private TextField nomeEvento, local;
    private Button confirmar, cancelar;
    ProgClienteManager progClienteManager;

    public CriarEventoUI(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        nomeEvento = new TextField();
        nomeEvento.setPromptText("nome do evento");
        local = new TextField();
        local.setPromptText("local do evento");

        confirmar = new Button("Confirmar");
        confirmar.getStyleClass().add("confirmar");
        cancelar = new Button("Cancelar");
        cancelar.getStyleClass().add("cancelar");

        Label label = new Label("Criar Evento");
        label.getStyleClass().add("titulo");

        VBox vBox = new VBox(new Text("Nome:"), nomeEvento, new Text("Local:"), local);
        vBox.setSpacing(10);

        setMargin(vBox, new Insets(10, 10, 10, 10));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(vBox);
        this.setBottom(new HBox(confirmar, cancelar));
    }
    private void registerHandlers() {
        confirmar.setOnAction( e -> {
            progClienteManager.criarEditar_Evento(nomeEvento.getText(), local.getText(), "12/8/2023", "14", "16", Message_types.CRIA_EVENTO);
        });
        cancelar.setOnAction(e -> {
            ContaAdministradorUI.opcaoAdmin.set("NADA");
        });
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("CRIAR_EVENTO"));
    }
}
