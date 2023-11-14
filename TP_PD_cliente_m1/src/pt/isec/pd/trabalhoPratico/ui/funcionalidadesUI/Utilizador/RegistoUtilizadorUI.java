package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class RegistoUtilizadorUI extends BorderPane {
    private Text resultado;
    private Button registar, voltar;
    private TextField nomeUtilizador, email, password, confirmar_password, numIdentificacao;
    private final ProgClienteManager progClienteManager;

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
        resultado = new Text();

        nomeUtilizador.setPromptText("nomeUtilizador");
        email.setPromptText("Email");
        password.setPromptText("Password");
        confirmar_password.setPromptText("Confirmar Password");
        numIdentificacao.setPromptText("Numero de Identificação");

        registar = new Button("Criar registo");
        registar.getStyleClass().add("confirmar");
        voltar = new Button("Voltar");
        voltar.getStyleClass().add("cancelar");

        Label label = new Label("Registar-se na aplicação");
        label.getStyleClass().add("titulo");

        VBox hBox = new VBox(new HBox(voltar, registar), resultado);
        HBox hBox1 = new HBox(new Text("Nome:"), nomeUtilizador, new Text("Email:"), email);
        HBox hBox2 =  new HBox(new Text("Nº.Identificação"), numIdentificacao);
        HBox hBox3 = new HBox(new Text("Password:"), password, confirmar_password);
        VBox info = new VBox(hBox1, hBox2, hBox3);
        info.setSpacing(10);
        VBox vBox = new VBox(label, info, hBox);
        VBox.setMargin(info, new Insets(30, 0, 30, 0));

        vBox.getStyleClass().add("sombreamentoBox");

        setMargin(vBox, new Insets(60, 50, 60, 50));
        this.getStyleClass().add("entradaPane");
        this.setCenter(vBox);
        this.setFocusTraversable(true);
    }

    private void registerHandlers() {
        voltar.setOnAction(e -> {
            MainCliente.menuSBP.set("MENU");
            limparCampos();
        });

        registar.setOnAction(e -> {
            Pair<String, Boolean> res = progClienteManager.registar(nomeUtilizador.getText(), email.getText(), numIdentificacao.getText(), password.getText(), confirmar_password.getText());
            if(res.getValue()) {
                limparCampos();
                MainCliente.menuSBP.set("MENU");
            }
            else {
                resultado.setText(res.getKey());
            }
        });

        MainCliente.menuSBP.addListener(observable -> update());
    }

    private void update() {
        this.setVisible(MainCliente.menuSBP.get().equals("REGISTO"));
    }

    private void limparCampos() {
        nomeUtilizador.clear();
        email.clear();
        password.clear();
        confirmar_password.clear();
        numIdentificacao.clear();
        resultado.setText("");
    }
}
