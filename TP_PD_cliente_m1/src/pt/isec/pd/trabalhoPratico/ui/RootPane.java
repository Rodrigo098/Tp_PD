package pt.isec.pd.trabalhoPratico.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador.ContaAdministradorUI;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.PersonalNodes.MensagemBox;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador.ContaUtilizadorUI;
import pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Utilizador.RegistoUtilizadorUI;

import java.util.Timer;
import java.util.TimerTask;

public class RootPane extends BorderPane {
    private Button login, sair;
    private Text registar;
    private TextField username, password;
    private MensagemBox msgBox;
    private VBox tempoExcedidoNode;

    ProgClienteManager progClienteManager;

    public RootPane(ProgClienteManager progClienteManager) {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        //update();
    }

    private void createViews() {
        username = new TextField();
        password = new TextField();
        username.setPromptText("Username");
        password.setPromptText("Password");

        login = new Button("Login");
        registar = new Text("Registar");
        registar.getStyleClass().add("links");

        Label label = new Label("Entrar na aplicação");
        label.getStyleClass().add("titulo");

        VBox vBox = new VBox(label, new HBox(new Text("Nome de utilizador: "), username), new HBox(new Text("Password: "), password), login, registar);
        vBox.getStyleClass().add("sombreamentoBox");
        VBox.setMargin(label, new Insets(0, 10, 30, 10));

        msgBox = new MensagemBox("Erro da comunicação com o Servidor :(");

        sair = new Button("SAIR");
        tempoExcedidoNode = new VBox(new Text("Tempo de sessão excedido!"), new Text("10 segundos para feche automático da app"), sair);
        tempoExcedidoNode.setVisible(false);
        tempoExcedidoNode.getStyleClass().add("erroBox");
        tempoExcedidoNode.setPrefSize(100, 100);

        StackPane stackPane = new StackPane(
                new BorderPane(vBox),
                new RegistoUtilizadorUI(progClienteManager),
                new ContaUtilizadorUI(progClienteManager),
                new ContaAdministradorUI(progClienteManager),
                tempoExcedidoNode,
                msgBox
        );
        this.getStyleClass().add("entradaPane");
        this.setCenter(stackPane);
    }

    private void registerHandlers() {
        login.setOnAction(e -> {
            progClienteManager.login(username.getText(), password.getText());
            username.setText(null);
            password.setText(null);
        });

        registar.setOnMouseClicked( e -> MainCliente.menuSBP.set("REGISTO"));

        sair.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        progClienteManager.addErroListener(observable -> Platform.runLater(msgBox::update));
        progClienteManager.addLogadoListener(observable -> Platform.runLater(this::update));
    }

    private void update() {
        if(progClienteManager.getLogado().equals("EXCEDEU_TEMPO")) {
            tempoExcedidoNode.setVisible(true);
            PauseTransition espera = new PauseTransition(Duration.seconds(10));
            espera.setOnFinished(e -> {
                Platform.exit();
                System.exit(0);
            });
            espera.play();
        }
    }
}
