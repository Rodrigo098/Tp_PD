package pt.isec.pd.trabalhoPratico.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

import javax.swing.*;
import java.util.List;

public class MainClienteJFX extends Application {
    ProgClienteManager clienteManager;
    private String title = "App Presenças";

    @Override
    public void init() throws Exception {
        super.init();
        clienteManager = MainCliente.progClienteManager;
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
        Pair<Boolean, String> conexao = clienteManager.criaSocket(list);
        //if(conexao.getKey()) {
            RootPane root = new RootPane(clienteManager);
            Scene scene = new Scene(root, 700, 500);
            String css = this.getClass().getResource("css/estilos.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setMaxHeight(600);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        //}
        /*else{
            Scene scene = new Scene(new VBox(new Label(conexao.getValue()), new Label("(Vou pôr aqui um smily triste ou uma cruz fofinha)")), 300, 300);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        }*/
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