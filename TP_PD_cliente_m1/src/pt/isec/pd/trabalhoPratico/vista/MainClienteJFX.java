package pt.isec.pd.trabalhoPratico.vista;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.model.classesPrograma.ParResposta;
import pt.isec.pd.trabalhoPratico.vista.funcionalidadesUI.NodesExtra.SairApp;

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
    public void start(Stage stage) {
        Parameters params = getParameters();
        List<String> list = params.getRaw();

        //-----------------------------------
        System.out.println(list.size());
        for(String each : list){
            System.out.println(each);
        }
        //-----------------------------------

        String css = this.getClass().getResource("css/estilos.css").toExternalForm();
        Scene scene;

        //-----------------------------------
        ParResposta conexao = clienteManager.criaSocket(list);
        if(conexao.resultado()) {
            RootPane root = new RootPane(clienteManager);
            scene = new Scene(root, 700, 500);
            stage.setMaxHeight(600);
        }
        else{
            SairApp root = new SairApp(conexao.mensagem(), "conexao");
            scene = new Scene( root,400, 400);
        }
        stage.setOnCloseRequest(e -> {
            clienteManager.logout("WND");
        });
        //-----------------------------------

        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
}
