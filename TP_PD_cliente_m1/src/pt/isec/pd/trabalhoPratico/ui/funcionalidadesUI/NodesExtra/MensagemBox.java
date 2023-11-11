package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.NodesExtra;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MensagemBox extends VBox {
    private Button erro;
    public MensagemBox(String mensagem){
        createViews(mensagem);
        registerHandlers();
        update();
    }

    private void createViews(String mensagem){
        Label label = new Label(mensagem);
        erro = new Button();
        erro.getStyleClass().addAll("imagens", "erroButton");
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
