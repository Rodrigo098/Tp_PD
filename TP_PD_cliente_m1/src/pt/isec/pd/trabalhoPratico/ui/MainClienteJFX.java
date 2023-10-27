package pt.isec.pd.trabalhoPratico.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class MainClienteJFX extends Application {
    ProgClienteManager clienteManager;
    private String title = "App Presenças";

    @Override
    public void init() throws Exception {
        super.init();
        clienteManager = new ProgClienteManager();
    }

    @Override
    public void start(Stage stage) throws Exception {
        RootPane root = new RootPane(clienteManager);
        Scene scene = new Scene(root,500,200);
        String css = this.getClass().getResource("css/estilos.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setMinWidth(700);
        stage.setMinHeight(400);
        stage.show();
    }
}
/*
        Stage stage2 = new Stage();
        ListarEventosUI listPane = new ListarEventosUI(clienteManager);
        Scene scene2 = new Scene(listPane, 300, 400);
        stage2.setScene(scene2);
        stage2.setTitle("Lista Eventos");
        stage2.setX(stage.getX() + stage.getWidth());
        stage2.setY(stage.getY());
        stage2.show();
 */