package pt.isec.pd.trabalhoPratico.vista.funcionalidadesUI.NodesExtra;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MensagemBox extends VBox {
    private Button erro;
    public MensagemBox(String mensagem, String classe){
        createViews(mensagem, classe);
        registerHandlers();
        update();
    }

    private void createViews(String mensagem, String classe){
        Label label = new Label(mensagem);
        erro = new Button();
        erro.getStyleClass().addAll("imagens", classe);
        erro.setMinSize(90, 90);

        this.setMaxSize(500, 200);
        this.getStyleClass().add("erroBox");
        this.getChildren().addAll(label, erro);
    }

    private void registerHandlers() {
        erro.setOnAction(e -> update());
    }
    public void update(){
        this.setVisible(!this.isVisible());
    }
}