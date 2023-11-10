package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class EditarRegistoUI extends BorderPane {
    private TextField nome, numID, password, confPassword;
    private Button confirmar, cancelar;
    private Text resultado;
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
        numID.setPromptText("novo número");
        password = new TextField();
        password.setPromptText("nova password");
        confPassword = new TextField();
        confPassword.setPromptText("confirme a password");
        resultado = new Text("");

        confirmar = new Button("Confirmar");
        confirmar.getStyleClass().add("confirmar");
        cancelar = new Button("Cancelar");
        cancelar.getStyleClass().add("cancelar");

        VBox vBox = new VBox(new Text("Nome:"), nome,
                             new Text("Número de Identificação:"), numID,
                             new VBox(new Text("Palavra passe:"), new HBox(password, confPassword)));
        vBox.setSpacing(10);
        Label label = new Label("Editar Registo");
        label.getStyleClass().add("titulo");

        setMargin(vBox, new javafx.geometry.Insets(10, 0, 0, 0));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(new VBox(vBox, resultado));
        this.setBottom(new HBox(confirmar, cancelar));
    }

    private void registerHandlers() {
        confirmar.setOnAction( e -> {
            resultado.setText(progClienteManager.editarRegisto(nome.getText(), numID.getText(), password.getText(), confPassword.getText()));
            limparCampos();
        });

        cancelar.setOnAction(e -> {
            ContaUtilizadorUI.opcaoUti.set("NADA");
            nome.setText(null);numID.setText(null);password.setText(null);confPassword.setText(null);
        });

        ContaUtilizadorUI.opcaoUti.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(ContaUtilizadorUI.opcaoUti.get().equals("EDITAR_REGISTO"));
    }

    private void limparCampos() {
        nome.setText(null);numID.setText(null);password.setText(null);confPassword.setText(null);
    }

}
