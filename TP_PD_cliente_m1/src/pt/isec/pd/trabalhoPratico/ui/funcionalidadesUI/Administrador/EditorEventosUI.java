package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

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
import pt.isec.pd.trabalhoPratico.model.recordDados.Utilizador;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.NodesExtra.EventoUI;

public class EditorEventosUI extends BorderPane {
    protected static SimpleStringProperty opcaoEdicao = new SimpleStringProperty("Lista Presenças");
    private int indice = 0;
    private final String[] opcoes = {"Lista Presenças", "Editar evento", "Eliminar presenças", "Inserir Presenças"};
    private Label mais, labelPane;
    private TextField emailsTextField, nomeFicheiro, caminhoCSV, tempoValido;
    private Text resultado;
    private Button editarEvento, eliminarEvento, gerarCodigoPresencas, obterPresencasCSV, eliminarPresencas, inserirPresencas;
    private ListView<Utilizador> listaPresencas;
    private EventoUI eventoUI;
    private VBox eliminarInserirPresencas;
    private HBox listaOpcoes;
    private final ProgClienteManager progClienteManager;

    public EditorEventosUI(ProgClienteManager progClienteManager){
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
        update2();
    }

    private void createViews() {
        //CODIGO
        gerarCodigoPresencas = new Button("Gerar Código");
        gerarCodigoPresencas.getStyleClass().add("eventoButton");
        tempoValido = new TextField();
        tempoValido.setPromptText("Mins validade");
        tempoValido.setMaxWidth(85);
        tempoValido.setStyle("-fx-font-size: 12px;");

        //EDICAO
        editarEvento = new Button("Editar");
        editarEvento.getStyleClass().add("eventoButton");

        //ELIMINAR
        eliminarEvento = new Button("Eliminar");
        eliminarEvento.getStyleClass().add("eventoButton");

        //CSV
        nomeFicheiro = new TextField();
        nomeFicheiro.setPromptText("Nome csv");
        nomeFicheiro.setStyle("-fx-font-size: 12px;");
        nomeFicheiro.setMaxWidth(85);
        caminhoCSV = new TextField();
        caminhoCSV.setPromptText("Caminho csv");
        caminhoCSV.setStyle("-fx-font-size: 12px;");
        caminhoCSV.setMaxWidth(85);
        obterPresencasCSV = new Button("CSV");
        obterPresencasCSV.getStyleClass().add("eventoButton");

        //PRESENCAS
        eliminarPresencas = new Button("Eliminar Presenças");
        eliminarPresencas.getStyleClass().add("eventoButton");
        inserirPresencas = new Button("Inserir Presenças");
        inserirPresencas.getStyleClass().add("eventoButton");

        ////////////////////////////
        HBox hbox =  new HBox(caminhoCSV, nomeFicheiro, obterPresencasCSV);
        hbox.getStyleClass().add("textfieldsEditorEvento");
        HBox hbox2 = new HBox(tempoValido, gerarCodigoPresencas);
        hbox2.getStyleClass().add("textfieldsEditorEvento");

        listaOpcoes = new HBox(eliminarEvento, hbox2, hbox);

        FlowPane flowPane = new FlowPane(editarEvento, eliminarPresencas, inserirPresencas, listaOpcoes);

        flowPane.setVgap(10);
        flowPane.setHgap(20);

        mais = new Label("+");
        mais.getStyleClass().add("eventoButton");

        listaPresencas = new ListView<>();
        listaPresencas.setPlaceholder(new Text("Sem presenças registadas"));

        resultado = new Text("");

        eventoUI = new EventoUI();

        emailsTextField = new TextField();
        emailsTextField.setPromptText("Isepare os emails por \" \"");
        eliminarInserirPresencas = new VBox(new Text("Insira os emails pretendidos:"), emailsTextField);
        eliminarInserirPresencas.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        HBox centralNode = new HBox(new VBox(new StackPane(listaPresencas, eventoUI, eliminarInserirPresencas), resultado), mais);

        centralNode.setSpacing(0);

        labelPane = new Label();
        labelPane.getStyleClass().add("titulo");

        setAlignment(labelPane, javafx.geometry.Pos.CENTER);
        setMargin(centralNode, new javafx.geometry.Insets(20, 0, 5, 0));
        this.setTop(labelPane);
        this.setCenter(centralNode);
        this.setBottom(flowPane);
    }

    private void registerHandlers() {
        mais.setOnMouseClicked(e -> {
            resultado.setText("");
            opcaoEdicao.set(opcoes[indice++ % opcoes.length]);
        });
        gerarCodigoPresencas.setOnAction(e -> resultado.setText("Novo código: " + progClienteManager.gerarCodPresenca(ListarEventosUI.eventoSelecionado.nomeEvento(), tempoValido.getText())));

        editarEvento.setOnAction(e -> resultado.setText(progClienteManager.editar_Evento(ListarEventosUI.eventoSelecionado.nomeEvento() ,eventoUI.getNomeEvento(), eventoUI.getLocal(),
                          eventoUI.getData(), eventoUI.getHoraInicio(), eventoUI.getHoraFim())));

        eliminarEvento.setOnAction(e -> resultado.setText(progClienteManager.eliminarEvento(ListarEventosUI.eventoSelecionado.nomeEvento())));

        obterPresencasCSV.setOnAction(e -> progClienteManager.obterCSV_ListaEventos(caminhoCSV.getText(), nomeFicheiro.getText(), Message_types.CSV_PRESENCAS_DO_EVENTO));

        eliminarPresencas.setOnAction(e -> resultado.setText(progClienteManager.eliminaInsere_Eventos(Message_types.ELIMINA_PRES,
                          ListarEventosUI.eventoSelecionado.nomeEvento(), emailsTextField.getText())));

        inserirPresencas.setOnAction(e -> resultado.setText(progClienteManager.eliminaInsere_Eventos(Message_types.INSERE_PRES,
                          ListarEventosUI.eventoSelecionado.nomeEvento(), emailsTextField.getText())));

        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());

        opcaoEdicao.addListener(observable -> update2());

        progClienteManager.addAtualizacaoListener(observable -> Platform.runLater(this::extrairListaPresencas));
    }

    private void update() {
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("EDITOR_EVENTOS"));
        if(this.isVisible()) {
            extrairListaPresencas();
            eventoUI.setInfoAntiga(ListarEventosUI.eventoSelecionado);
            resultado.setText("");
        }
    }

    private void update2() {
        labelPane.setText(opcaoEdicao.get());

        listaOpcoes.setDisable(!opcaoEdicao.get().equals("Lista Presenças"));

        editarEvento.setDisable(!opcaoEdicao.get().equals("Editar evento"));
        eventoUI.setVisible(opcaoEdicao.get().equals("Editar evento"));

        eliminarPresencas.setDisable(!opcaoEdicao.get().equals("Eliminar presenças"));
        inserirPresencas.setDisable(!opcaoEdicao.get().equals("Inserir Presenças"));
        eliminarInserirPresencas.setVisible(opcaoEdicao.get().equals("Eliminar presenças") || opcaoEdicao.get().equals("Inserir Presenças"));
    }

    private void extrairListaPresencas() {
        listaPresencas.getItems().clear();

        for (Utilizador utilizador : progClienteManager.consultaPresencasEvento(ListarEventosUI.eventoSelecionado.nomeEvento())) {
            listaPresencas.getItems().add(utilizador);
        }
    }
}
