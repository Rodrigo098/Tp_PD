package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

import java.io.IOException;

public class RegistoUtilizadorUI extends BorderPane {
    private Button registar, voltar;
    private TextField nomeUtilizador, email, password, confirmar_password, numIdentificacao;
    private ProgClienteManager progClienteManager;
    public RegistoUtilizadorUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        nomeUtilizador = new TextField();
        email = new TextField();
        password = new TextField();
        confirmar_password = new TextField();
        numIdentificacao = new TextField();

        nomeUtilizador.setPromptText("nomeUtilizador");
        email.setPromptText("Email");
        password.setPromptText("Password");
        confirmar_password.setPromptText("Confirmar Password");
        numIdentificacao.setPromptText("Numero de Identificação");

        registar = new Button("Criar registo");
        voltar = new Button("Voltar");

        HBox hBox = new HBox(voltar, registar);
        HBox hBox1 = new HBox(nomeUtilizador, email);
        HBox hBox2 = new HBox(password, confirmar_password);
        VBox vBox = new VBox(hBox1, numIdentificacao, hBox2, hBox);

        this.setStyle("-fx-background-color: #78909C; -fx-padding: 30;");
        this.setCenter(vBox);
        this.setFocusTraversable(true);
    }

    private void registerHandlers() {
        voltar.setOnAction(e -> {
            MainCliente.menuSBP.set("MENU");
        });
        registar.setOnAction(e -> {
            progClienteManager.registar(nomeUtilizador.getText(), email.getText(), numIdentificacao.getText(), password.getText(), confirmar_password.getText());
            nomeUtilizador.setText(null);
            email.setText(null);
            password.setText(null);
            confirmar_password.setText(null);
            numIdentificacao.setText(null);
        });
        MainCliente.menuSBP.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(MainCliente.menuSBP.get().equals("REGISTO"));
    }
}
