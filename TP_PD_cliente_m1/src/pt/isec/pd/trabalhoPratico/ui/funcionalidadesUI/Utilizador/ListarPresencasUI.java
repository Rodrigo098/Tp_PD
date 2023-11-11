package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador;

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
import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.PersonalNodes.FiltrosUI;

public class ListarPresencasUI extends BorderPane {
    private FiltrosUI filtros;
    private Text resultadoCSV;
    private TextField nomeFicheiro, caminhoCSV;
    private Button gerarCSV;
    private final ProgClienteManager progClienteManager;
    private ListView<Evento> lista;

    public ListarPresencasUI(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        filtros = new FiltrosUI();
        lista = new ListView<>();
        lista.setPlaceholder(new Text("Não há presenças em eventos registadas"));
        gerarCSV = new Button("gerar CSV");
        resultadoCSV = new Text("");
        caminhoCSV = new TextField();
        caminhoCSV.setPromptText("Caminho ficheiro csv");
        nomeFicheiro = new TextField();
        nomeFicheiro.setPromptText("Nome do ficheiro");

        Label label = new Label("Lista de Presenças");
        label.getStyleClass().add("titulo");

        setMargin(lista, new Insets(20, 0, 10, 0));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(new VBox(filtros, lista, resultadoCSV));
        this.setBottom(new HBox(new VBox(caminhoCSV, nomeFicheiro), gerarCSV));
    }

    private void registerHandlers() {
        filtros.procurar.setOnAction( e -> extrairListaEventos());

        gerarCSV.setOnAction( e -> {
            resultadoCSV.setText(progClienteManager.obterCSV_ListaEventos(caminhoCSV.getText(), nomeFicheiro.getText(), Message_types.CSV_UTILIZADOR));
            nomeFicheiro.clear();
        });

        ContaUtilizadorUI.opcaoUti.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaUtilizadorUI.opcaoUti.get().equals("LISTAR_PRESENCAS"));
        if(this.isVisible()) extrairListaEventos();
    }

    private void extrairListaEventos() {
        lista.getItems().clear();
        for (Evento evento : progClienteManager.obterListaConsulta(Message_types.CONSULTA_PRES_UTILIZADOR,
                filtros.getNomeEvento(), filtros.getLocal(), filtros.getLimData1(), filtros.getLimData2(),
                filtros.getHoraInicio(), filtros.getHoraFim())) {
            lista.getItems().add(evento);
        }
    }
}
