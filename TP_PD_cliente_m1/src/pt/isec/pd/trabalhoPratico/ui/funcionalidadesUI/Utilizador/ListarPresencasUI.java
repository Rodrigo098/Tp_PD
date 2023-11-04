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

public class ListarPresencasUI extends BorderPane {
    private Text resultadoCSV;
    private TextField nomeFicheiro;
    private Button gerarCSV;
    private final ProgClienteManager progClienteManager;
    private ListView<String> lista;

    public ListarPresencasUI(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        lista = new ListView<>();
        extrairListaEventos();
        gerarCSV = new Button("gerar CSV");
        resultadoCSV = new Text("");
        nomeFicheiro = new TextField();
        nomeFicheiro.setPromptText("Nome do ficheiro");

        Label label = new Label("Lista de Presenças");
        label.getStyleClass().add("titulo");

        setMargin(lista, new Insets(20, 0, 10, 0));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(lista);
        this.setBottom(new HBox(new VBox(nomeFicheiro, resultadoCSV), gerarCSV));
    }

    private void registerHandlers() {
        gerarCSV.setOnAction( e -> {
            resultadoCSV.setText(progClienteManager.obterCSV_Presencas(nomeFicheiro.getText()) ? "CSV gerado com sucesso" : "Erro ao gerar CSV");
        });
        ContaUtilizadorUI.opcaoUti.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaUtilizadorUI.opcaoUti.get().equals("LISTAR_PRESENCAS"));
        if(this.isVisible()) extrairListaEventos();
    }

    private void extrairListaEventos() {
        lista.getItems().clear();
        for (String evento : progClienteManager.consultarPresencasUti()) {
            lista.getItems().add(evento);
        }
    }
}
