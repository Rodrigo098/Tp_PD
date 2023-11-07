package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.UniversalPanes;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.MainCliente;

public class MessageBox extends VBox {
    private Button erro;
    public MessageBox(){
        createViews();
        registerHandlers();
        update();
    }

    private void createViews(){
        Label label = new Label("Ocorreu um erro :(");
        erro = new Button();
        erro.getStyleClass().add("erroButton");
        erro.setMinSize(90, 90);

        this.setMaxSize(300, 200);
        this.getStyleClass().add("erroBox");
        this.getChildren().addAll(label, erro);
    }

    private void registerHandlers() {
        MainCliente.menuSBP.addListener(e -> update());
        erro.setOnAction(e -> this.setVisible(false));
    }
    private void update(){
        this.setVisible(MainCliente.menuSBP.get() == "ERRO");
    }
}
