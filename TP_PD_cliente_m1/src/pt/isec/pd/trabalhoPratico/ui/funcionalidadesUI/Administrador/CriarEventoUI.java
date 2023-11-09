package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class CriarEventoUI extends BorderPane {
    private Button confirmar, cancelar;
    private EventoUI eventoUI;
    private Text resultado;
    private final ProgClienteManager progClienteManager;


    public CriarEventoUI(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        eventoUI = new EventoUI();
        confirmar = new Button("Confirmar");
        confirmar.getStyleClass().add("confirmar");
        cancelar = new Button("Cancelar");
        cancelar.getStyleClass().add("cancelar");
        resultado = new Text();

        Label label = new Label("Criar Evento");
        label.getStyleClass().add("titulo");

        setMargin(eventoUI, new Insets(10, 10, 10, 10));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(new VBox(eventoUI, resultado));
        this.setBottom(new HBox(confirmar, cancelar));
    }
    private void registerHandlers() {
        confirmar.setOnAction( e -> {
            resultado.setText(progClienteManager.criar_Evento(eventoUI.getNomeEvento(), eventoUI.getLocal(), eventoUI.getData(), eventoUI.getHoraInicio(), eventoUI.getHoraFim()));
        });
        cancelar.setOnAction(e -> {
            ContaAdministradorUI.opcaoAdmin.set("NADA");
        });
        ContaAdministradorUI.opcaoAdmin.addListener(observable -> update());
    }

    private void update() {
        eventoUI.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("CRIAR_EVENTO"));
        this.setVisible(ContaAdministradorUI.opcaoAdmin.get().equals("CRIAR_EVENTO"));
        resultado.setText("");
    }
}
