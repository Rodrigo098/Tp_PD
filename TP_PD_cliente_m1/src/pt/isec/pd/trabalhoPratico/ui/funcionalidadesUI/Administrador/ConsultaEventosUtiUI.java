package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class ConsultaEventosUtiUI extends BorderPane {
    private TextField utilizador;
    private Button obterCSV, listar;
    private ListView<String> listaEventos;
    private ProgClienteManager progClienteManager;

    public ConsultaEventosUtiUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        utilizador = new TextField();
        utilizador.setPromptText("Email do utilizador");
        obterCSV = new Button("Obter CSV");
        obterCSV.setDisable(true);
        listar = new Button("listar");
        listaEventos = new ListView<>();
        extrairListaEventos();

        Label label = new Label("Lista de eventos presenciados por:");
        label.getStyleClass().add("titulo");

        VBox vBox = new VBox(new HBox(utilizador, listar), listaEventos);
        vBox.setSpacing(10);

        setMargin(vBox, new Insets(10, 10, 10, 10));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(vBox);
        this.setBottom(obterCSV);
    }

    private void registerHandlers() {
        listar.setOnAction(e -> {
            extrairListaEventos();
            listaEventos.setVisible(true);
            obterCSV.setDisable(false);
        });
        obterCSV.setOnAction(e -> {
            progClienteManager.obterCSV_Admin();
            obterCSV.setDisable(true);
        });
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("EVENTOS_PRESENCA_UTI"));
        listaEventos.setVisible(false);
    }

    private boolean extrairListaEventos() {
        if(utilizador.getText() == null || utilizador.getText().isBlank())
            return false;
        listaEventos.getItems().clear();
        for (String evento : progClienteManager.consultaEventosUtilizador(utilizador.getText())) {
            listaEventos.getItems().add(evento);
        }
        return true;
    }
}