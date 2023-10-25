package pt.isec.pd.trabalhoPratico.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.isec.pd.trabalhoPratico.model.ClienteManager;

public class MainClienteJFX extends Application {
    //ClienteManager clienteManager;
    private String title = "Registo Presenças";

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        RootPane root = new RootPane();
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
