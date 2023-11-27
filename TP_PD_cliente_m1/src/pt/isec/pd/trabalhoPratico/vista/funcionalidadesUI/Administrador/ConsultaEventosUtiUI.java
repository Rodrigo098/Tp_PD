package pt.isec.pd.trabalhoPratico.vista.funcionalidadesUI.Administrador;

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
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Message_types;
import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;

public class ConsultaEventosUtiUI extends BorderPane {
    private TextField utilizador, caminhoCSV;
    private Button obterCSV, listar;
    private ListView<Evento> listaEventos;
    private Text resultado;
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
        caminhoCSV.setPromptText("Caminho CSV");
        obterCSV = new Button("Obter CSV");
        obterCSV.setDisable(true);
        listar = new Button("listar");
        listaEventos = new ListView<>();
        listaEventos.setPlaceholder(new Text("Este utilizador ainda não presenciou nenhum evento"));
        resultado = new Text();

        Label label = new Label("Lista de eventos presenciados por:");
        label.getStyleClass().add("titulo");

        VBox vBox = new VBox(new HBox(utilizador, listar), listaEventos);
        vBox.setSpacing(10);

        setMargin(vBox, new Insets(10, 10, 10, 10));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(vBox);
        this.setBottom(new VBox(new HBox(caminhoCSV, obterCSV), resultado));
    }

    private void registerHandlers() {
        listar.setOnAction(e -> {
            listaEventos.setVisible(extrairListaEventos());
            obterCSV.setDisable(!listaEventos.isVisible());
        });
        obterCSV.setOnAction(e -> {
            resultado.setText(progClienteManager.obterCSV_ListaEventos(caminhoCSV.getText(),"evenosPresenciadosPor_" + utilizador.getText().split("@")[0], Message_types.CSV_PRESENCAS_UTI_NUM_EVENTO));
            caminhoCSV.setText(null);
        });
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
        progClienteManager.addAtualizacaoListener(observable -> {
            if(this.isVisible()) Platform.runLater(this::extrairListaEventos);
        }
        );
    }

    private void update() {
        if(ContaAdministradorUI.opcaoAdmin.get().equals("EVENTOS_PRESENCA_UTI")) {
            resultado.setText("");
            this.setVisible(true);
        }else{
            this.setVisible(false);
        }
        listaEventos.setVisible(false);
    }

    private boolean extrairListaEventos() {
        if(utilizador.getText() == null || utilizador.getText().isBlank())
            return false;
        listaEventos.getItems().clear();
        Evento [] eventos = progClienteManager.consultaEventosUtilizador(utilizador.getText());
        if(eventos == null || eventos.length == 0)
            return false;
        for (Evento evento : eventos) {
            listaEventos.getItems().add(evento);
        }
        return true;
    }
}