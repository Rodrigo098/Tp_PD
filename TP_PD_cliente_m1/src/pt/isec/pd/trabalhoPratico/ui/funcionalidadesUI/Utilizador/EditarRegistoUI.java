package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class EditarRegistoUI extends BorderPane {
    private TextField nome, numID, password, confPassword;
    private Button confirmar, cancelar;
    private final ProgClienteManager progClienteManager;

    public EditarRegistoUI(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        nome = new TextField();
        nome.setPromptText("novo nome");
        numID = new TextField();
        numID.setPromptText("novo número de identificação");
        password = new TextField();
        password.setPromptText("nova palavra passe");
        confPassword = new TextField();
        confPassword.setPromptText("confirme a nova palavra passe");

        confirmar = new Button("Confirmar");
        cancelar = new Button("Cancelar");

        VBox vBox = new VBox(new Label("Nome:"), nome,
                             new Label("Número de Identificação:"), numID,
                             new Label("Palavra passe:"), password, confPassword);

        this.setCenter(vBox);
        this.setBottom(new HBox(confirmar, cancelar));
    }

    private void registerHandlers() {
        confirmar.setOnAction( e -> {
            progClienteManager.editarRegisto(nome.getText(), numID.getText(), password.getText(), confPassword.getText());
        });
        cancelar.setOnAction(e -> {
            ContaUtilizadorUI.opcaoUti.set("NADA");
        });
        ContaUtilizadorUI.opcaoUti.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaUtilizadorUI.opcaoUti.get().equals("EDITAR_REGISTO"));
    }

}
