package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import com.sun.scenario.effect.impl.prism.PrImage;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.model.classesComunication.Message_types;

public class EditorEventosUI extends BorderPane {
    protected static SimpleStringProperty opcaoEdicao = new SimpleStringProperty("NADA");
    private int indice = 0;
    private final String[] opcoes = {"LISTA", "EDITAR", "ELI_PRESENCA", "INS_PRESENCA"};
    private Label mais;
    private TextField emailsTextField, nomeFicheiro, resultado;
    private Button editarEvento, eliminarEvento, gerarCodigoPresencas, obterPresencasCSV, eliminarPresencas, inserirPresencas;
    private ListView<String> listaPresencas;
    private EventoUI eventoUI;
    private VBox eliminarInserirPresencas;
    private final ProgClienteManager progClienteManager;

    public EditorEventosUI(ProgClienteManager progClienteManager){
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
        update2();
    }

    private void createViews() {
        gerarCodigoPresencas = new Button("Gerar Código");
        gerarCodigoPresencas.getStyleClass().add("eventoButton");

        editarEvento = new Button("Editar");
        editarEvento.getStyleClass().add("eventoButton");

        eliminarEvento = new Button("Eliminar");
        eliminarEvento.getStyleClass().add("eventoButton");

        nomeFicheiro = new TextField();
        nomeFicheiro.setPromptText("Nome csv");
        obterPresencasCSV = new Button("CSV");
        obterPresencasCSV.getStyleClass().add("eventoButton");

        eliminarPresencas = new Button("Eliminar Presenças");
        eliminarPresencas.getStyleClass().add("eventoButton");

        inserirPresencas = new Button("Inserir Presenças");
        inserirPresencas.getStyleClass().add("eventoButton");

        FlowPane flowPane = new FlowPane(gerarCodigoPresencas, editarEvento, eliminarEvento,
                                         eliminarPresencas, inserirPresencas,
                                         nomeFicheiro, obterPresencasCSV);
        flowPane.setVgap(10);
        flowPane.setHgap(30);
        flowPane.setPadding(new Insets(20, 50, 0, 0));

        mais = new Label("+");
        mais.getStyleClass().add("eventoButton");

        listaPresencas = new ListView<>();

        resultado = new TextField();

        eventoUI = new EventoUI();

        emailsTextField = new TextField();
        emailsTextField.setPromptText("Isepare os emails por \" \"");
        eliminarInserirPresencas = new VBox(new Text("Insira os emails pretendidos:"), emailsTextField);
        eliminarInserirPresencas.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox centralNode = new HBox(new VBox(new StackPane(listaPresencas, eventoUI, eliminarInserirPresencas), resultado), mais);
        centralNode.setSpacing(0);

        Label label = new Label("Editar Eventos");
        label.getStyleClass().add("titulo");

        setAlignment(label, javafx.geometry.Pos.CENTER);
        setMargin(centralNode, new javafx.geometry.Insets(20, 0, 5, 0));
        this.setTop(label);
        this.setCenter(centralNode);
        this.setBottom(flowPane);
    }

    private void registerHandlers() {
        mais.setOnMouseClicked(e -> {
            resultado.setText("");
            opcaoEdicao.set(opcoes[indice++ % opcoes.length]);
        });
        gerarCodigoPresencas.setOnAction(e -> {
            resultado.setText(progClienteManager.gerarCodPresenca(listaPresencas.getSelectionModel().getSelectedItem()));
        });
        editarEvento.setOnAction(e -> {
            resultado.setText(progClienteManager.editar_Evento(ListarEventosUI.eventoSelecionado ,eventoUI.getNomeEvento(), eventoUI.getLocal(),
                              eventoUI.getData(), eventoUI.getHoraInicio(), eventoUI.getHoraFim()) ?
                              "Evento editado com sucesso!" : "Evento não editado!");
        });
        eliminarEvento.setOnAction(e -> {
            resultado.setText(progClienteManager.eliminarEvento(ListarEventosUI.eventoSelecionado) ?
                              "Evento eliminado com sucesso!" : "Evento não eliminado!");
        });
        obterPresencasCSV.setOnAction(e -> {
            progClienteManager.obterCSV_Admin(nomeFicheiro.getText());
        });
        eliminarPresencas.setOnAction(e -> {
            resultado.setText(progClienteManager.eliminaInsere_Eventos(Message_types.ELIMINA_PRES,
                              ListarEventosUI.eventoSelecionado, emailsTextField.getText()) ?
                              "Presenças eliminadas com sucesso!" : "Presenças não eliminadas!");
        });
        inserirPresencas.setOnAction(e -> {
            resultado.setText(progClienteManager.eliminaInsere_Eventos(Message_types.INSERE_PRES,
                              ListarEventosUI.eventoSelecionado, emailsTextField.getText()) ?
                              "Presenças inseridas com sucesso!" : "Presenças não inseridas!");
        });

        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
        opcaoEdicao.addListener(observable -> update2());
        progClienteManager.addAtualizacaoListener(observable -> Platform.runLater(this::extrairListaPresencas));
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("EDITOR_EVENTOS"));
        if(this.isVisible()) eventoUI.setInfoAntiga(ListarEventosUI.eventoSelecionado);

    }

    private void update2() {
        eliminarEvento.setDisable(!opcaoEdicao.get().equals("LISTA"));
        gerarCodigoPresencas.setDisable(!opcaoEdicao.get().equals("LISTA"));
        nomeFicheiro.setDisable(!opcaoEdicao.get().equals("LISTA"));
        obterPresencasCSV.setDisable(!opcaoEdicao.get().equals("LISTA"));

        editarEvento.setDisable(!opcaoEdicao.get().equals("EDITAR"));
        eventoUI.setVisible(opcaoEdicao.get().equals("EDITAR"));

        eliminarPresencas.setDisable(!opcaoEdicao.get().equals("ELI_PRESENCA"));
        inserirPresencas.setDisable(!opcaoEdicao.get().equals("INS_PRESENCA"));
        eliminarInserirPresencas.setVisible(opcaoEdicao.get().equals("ELI_PRESENCA") || opcaoEdicao.get().equals("INS_PRESENCA"));
    }

    private void extrairListaPresencas() {
        listaPresencas.getItems().clear();
        for (String evento : progClienteManager.consultaPresencasEvento(ListarEventosUI.eventoSelecionado)) {
            listaPresencas.getItems().add(evento);
        }
    }
}
