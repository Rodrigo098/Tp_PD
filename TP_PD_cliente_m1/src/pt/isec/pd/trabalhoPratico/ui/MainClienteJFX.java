package pt.isec.pd.trabalhoPratico.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

import java.util.List;

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
        Parameters params = getParameters();
        List<String> list = params.getRaw();

        //-----------------------------------
        System.out.println(list.size());
        for(String each : list){
            System.out.println(each);
        }
        //-----------------------------------

        if(clienteManager.handShake(list)) {
            RootPane root = new RootPane(clienteManager);
            Scene scene = new Scene(root, 750, 400);
            String css = this.getClass().getResource("css/estilos.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setMaxWidth(750);
            stage.setMaxHeight(400);
            stage.show();
        }
        else{
            Scene scene = new Scene(new VBox(new Label("Sorry mas o HandShake com o Servidor deu para o torto :("), new Label("(Vou pôr aqui um smily triste ou uma cruz fofinha)")), 300, 300);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        }
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