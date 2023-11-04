package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class ListarPresencasUI extends BorderPane {
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

        Label label = new Label("Lista de Presenças");
        label.getStyleClass().add("titulo");

        setMargin(lista, new Insets(20, 0, 10, 0));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(lista);
        this.setBottom(gerarCSV);
    }

    private void registerHandlers() {
        gerarCSV.setOnAction( e -> {
            progClienteManager.obterCSV_Uti();
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
