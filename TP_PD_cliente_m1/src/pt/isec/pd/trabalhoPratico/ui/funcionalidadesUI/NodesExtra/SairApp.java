package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.NodesExtra;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SairApp extends VBox {
    private Button sair;
    public SairApp() {
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        sair = new Button("SAIR");
        VBox v = new VBox();
        v.setMinSize(100, 100);
        v.getStyleClass().addAll("imagens", "ligacaoExpirou");

        setMargin(v, new javafx.geometry.Insets(20, 0, 20, 0));
        setVisible(false);
        getStyleClass().add("erroBox");
        getChildren().addAll(new Label("A sua ligação ao servidor expirou!"), new Text("(10 segundos para feche automático da app)"), v, sair);
    }

    private void registerHandlers() {
        sair.setOnAction(e -> {
            Platform.exit();
        });
    }

    private void update() {}
}
