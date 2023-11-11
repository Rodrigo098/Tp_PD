package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Message_types;
import pt.isec.pd.trabalhoPratico.model.classesDados.Evento;

public class ConsultaEventosUtiUI extends BorderPane {
    private TextField utilizador, caminhoCSV;
    private Button obterCSV, listar;
    private ListView<Evento> listaEventos;
    private final ProgClienteManager progClienteManager;

    public ConsultaEventosUtiUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        utilizador = new TextField();
        utilizador.setPromptText("Email do utilizador");
        caminhoCSV = new TextField();
        caminhoCSV.setPromptText("Caminho para o ficheiro CSV");
        obterCSV = new Button("Obter CSV");
        obterCSV.setDisable(true);
        listar = new Button("listar");
        listaEventos = new ListView<>();
        listaEventos.setPlaceholder(new Text("Este utilizador ainda não presenciou nenhum evento"));

        Label label = new Label("Lista de eventos presenciados por:");
        label.getStyleClass().add("titulo");

        VBox vBox = new VBox(new HBox(utilizador, listar), listaEventos);
        vBox.setSpacing(10);

        setMargin(vBox, new Insets(10, 10, 10, 10));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(vBox);
        this.setBottom(new HBox(caminhoCSV, obterCSV));
    }

    private void registerHandlers() {
        listar.setOnAction(e -> {
            listaEventos.setVisible(extrairListaEventos());
            obterCSV.setDisable(!listaEventos.isVisible());
        });
        obterCSV.setOnAction(e -> {
            progClienteManager.obterCSV_ListaEventos(caminhoCSV.getText(),"evenosPresenciadosPor_" + utilizador.getText(), Message_types.CSV_PRESENCAS_UTI_NUM_EVENTO);
            obterCSV.setDisable(true);
            caminhoCSV.setText(null);
        });
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
        progClienteManager.addAtualizacaoListener(observable -> Platform.runLater(this::extrairListaEventos));
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("EVENTOS_PRESENCA_UTI"));
        listaEventos.setVisible(false);
    }

    private boolean extrairListaEventos() {
        if(utilizador.getText() == null || utilizador.getText().isBlank())
            return false;
        listaEventos.getItems().clear();
        for (Evento evento : progClienteManager.consultaEventosUtilizador(utilizador.getText())) {
            listaEventos.getItems().add(evento);
        }
        return true;
    }
}