package pt.isec.pd.trabalhoPratico.vista.funcionalidadesUI.Utilizador;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class ContaUtilizadorUI extends BorderPane {
    static protected SimpleStringProperty opcaoUti = new SimpleStringProperty("NADA");
    private Button listaPresencas, marcarPresenca, editarRegisto, logout, voltar;
    private HBox funcionalidades;
    private final ProgClienteManager progClienteManager;
    public ContaUtilizadorUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update1();
        update2();
    }

    private void createViews() {
        voltar = new Button("Voltar");
        voltar.getStyleClass().add("movButton");
        listaPresencas = new Button("Ver Lista de Presenças");
        marcarPresenca = new Button("Registar Presença");
        editarRegisto = new Button( "Editar Registo");
        logout = new Button("Logout");
        logout.getStyleClass().add("btnLogout");

        VBox vBox0 = new VBox(listaPresencas, marcarPresenca, editarRegisto);
        vBox0.setSpacing(10);
        vBox0.setMaxWidth(300);

        VBox vBox = new VBox(vBox0, logout);
        vBox.setMaxSize(400,100);
        vBox.getStyleClass().add("menuBotoes");

        funcionalidades =  new HBox(voltar, new StackPane(new ListarPresencasUI(progClienteManager), new MarcarPresencaUI(progClienteManager), new EditarRegistoUI(progClienteManager)));
        funcionalidades.setFocusTraversable(true);
        funcionalidades.getStyleClass().add("funcionalidades");

        StackPane stackPane = new StackPane(vBox, funcionalidades);

        Text tipoConta = new Text("Conta Utilizador");
        tipoConta.getStyleClass().add("titulo");
        VBox centralNode = new VBox(tipoConta, stackPane);
        centralNode.setPadding(new Insets(20));

        this.getStyleClass().add("contaUtilizador");
        this.setCenter(centralNode);
        this.setFocusTraversable(true);
    }

    private void registerHandlers() {
        voltar.setOnAction(e -> opcaoUti.set("NADA"));

        listaPresencas.setOnAction(e -> opcaoUti.set("LISTAR_PRESENCAS"));

        marcarPresenca.setOnAction(e -> opcaoUti.set("MARCAR_PRES"));

        editarRegisto.setOnAction(e -> opcaoUti.set("EDITAR_REGISTO"));

        logout.setOnAction(e -> {
            progClienteManager.logout("UTI");
            MainCliente.menuSBP.set("MENU");
        });

        opcaoUti.addListener(observable -> update2());
        progClienteManager.addLogadoListener(observable -> update1());
    }

    //-----------------------------------------------------------------
    private void update1() {
        this.setVisible(progClienteManager.getLogado().equals("UTILIZADOR"));
    }
    private void update2() {
        funcionalidades.setVisible(!opcaoUti.get().equals("NADA"));
    }
}
