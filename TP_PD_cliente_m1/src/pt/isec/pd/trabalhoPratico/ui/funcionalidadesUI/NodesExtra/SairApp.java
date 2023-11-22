package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.NodesExtra;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Timer;
import java.util.TimerTask;
public class SairApp extends VBox {
    // TEMPO
    private static final int TEMPO_MAXIMO = 10; // 10 segundos
    private final Timer temporizador = new Timer();
    private int contagem = 0;
    private Button sair;


    public SairApp(String msg, String classe) {
        createViews(msg, classe);
        registerHandlers();
        update();
    }

    private void createViews(String msg, String classe) {
        sair = new Button("SAIR");
        VBox vBox = new VBox();
        vBox.setMinSize(100, 100);
        vBox.getStyleClass().addAll("imagens", classe);
        Label label = new Label(msg);

        setMargin(vBox, new javafx.geometry.Insets(20, 0, 20, 0));
        getStyleClass().add("erroBox");
        getChildren().addAll(label, new Text("(10 segundos para feche automático da app)"), vBox, sair);
    }

    private void registerHandlers() {
        sair.setOnAction(e -> {
            temporizador.cancel();
            Platform.exit();
        });
    }

    private void update() {
        temporizador.schedule(new TimerTask() {
            @Override
            public void run() {
                contagem++;
                if (contagem == TEMPO_MAXIMO) {
                    temporizador.cancel();
                    Platform.exit();
                }
            }
        }, 0, 1000);
    }
}
