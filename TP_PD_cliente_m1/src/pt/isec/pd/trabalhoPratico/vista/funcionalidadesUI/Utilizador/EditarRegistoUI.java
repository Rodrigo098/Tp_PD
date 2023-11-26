package pt.isec.pd.trabalhoPratico.vista.funcionalidadesUI.Utilizador;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.model.classesPrograma.ParResposta;

public class EditarRegistoUI extends BorderPane {
    private TextField nome, numID, password, confPassword;
    private Button confirmar, cancelar;
    private Text resultado;
    private Label label;
    private final ProgClienteManager progClienteManager;

    public EditarRegistoUI(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        nome = new TextField();
        numID = new TextField();
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
        label = new Label();
        label.getStyleClass().add("titulo");

        setMargin(vBox, new javafx.geometry.Insets(10, 0, 0, 0));
        setAlignment(label, javafx.geometry.Pos.CENTER);
        this.setTop(label);
        this.setCenter(new VBox(vBox, resultado));
        this.setBottom(new HBox(confirmar, cancelar));
    }

    private void registerHandlers() {
        confirmar.setOnAction( e -> {
            ParResposta res = progClienteManager.editarRegisto(nome.getText(), numID.getText(), password.getText(), confPassword.getText());
            if (res.resultado()) {
            } else {
                nome.getStyleClass().add("camposInvalidos");
                numID.getStyleClass().add("camposInvalidos");
                password.getStyleClass().add("camposInvalidos");
                confPassword.getStyleClass().add("camposInvalidos");
            }
            resultado.setText(res.mensagem());
        });

        cancelar.setOnAction(e -> {
            ContaUtilizadorUI.opcaoUti.set("NADA");
            resultado.setText("");
            limpaStyle();
        });

        ContaUtilizadorUI.opcaoUti.addListener(observable -> update());
    }

    private void update() {
        if(ContaUtilizadorUI.opcaoUti.get().equals("EDITAR_REGISTO")) {
            resultado.setText("");
            limpaStyle();
            label.setText("Editar Registo - " + progClienteManager.getEmailCliente());
            nome.setText(progClienteManager.getNomeCliente());
            numID.setText(progClienteManager.getNumeroCliente());
            this.setVisible(true);
        }
        else {
            this.setVisible(false);
        }
    }
    private void limpaStyle() {
        nome.getStyleClass().remove("camposInvalidos");
        numID.getStyleClass().remove("camposInvalidos");
        password.getStyleClass().remove("camposInvalidos");
        confPassword.getStyleClass().remove("camposInvalidos");
        password.clear();
        confPassword.clear();
    }
}
