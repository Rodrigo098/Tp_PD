package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class ContaAdministradorUI extends BorderPane {
    static protected SimpleStringProperty opcaoAdmin = new SimpleStringProperty("NADA");
    //Eventos
    private Button criarEvento, listarEventos, logout;
    private ProgClienteManager progClienteManager;

    //-----------------------------------------------------------------
    public ContaAdministradorUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update();
    }

    //-----------------------------------------------------------------
    private void createViews() {
        criarEvento = new Button("+Evento");
        listarEventos = new Button("Listar Eventos");

        logout = new Button("Logout");
        logout.getStyleClass().add("btnLogout");

        VBox funcionalidades = new VBox(criarEvento, listarEventos);

        StackPane ladoDireito = new StackPane(new StackPane(new ListarEventosUI(progClienteManager)), new CriarEventoUI(progClienteManager));
        HBox hBox = new HBox(funcionalidades, ladoDireito);
        hBox.setSpacing(20);
        this.setStyle("-fx-background-color: #E8EFF6; -fx-padding: 30 30 0 30;");
        this.setMargin(logout, new Insets(10));
        this.setBottom(logout);
        this.setCenter(hBox);
        this.setFocusTraversable(true);
    }

    //-----------------------------------------------------------------
    private void registerHandlers() {
        criarEvento.setOnAction(e -> {
            opcaoAdmin.set("CRIAR_EVENTO");
        });
        listarEventos.setOnAction(e -> {
            opcaoAdmin.set("LISTAR_EVENTOS");
        });
        logout.setOnAction(e -> {
            progClienteManager.logout();
            MainCliente.administradorSBP.set("INDEFINIDO");
            MainCliente.menuSBP.set("LOGOUT");
        });

        MainCliente.administradorSBP.addListener(observable -> update());
    }

    //-----------------------------------------------------------------
    private void update() {
        this.setVisible(MainCliente.administradorSBP.get().equals("ADMINISTRADOR"));
    }
    //-----------------------------------------------------------------
/*
    private ScrollPane listaDeEventos(){
        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox();
        String [] listaEventos = progClienteManager.obterListaEventos();
        for(String evento : listaEventos){
            Button eliminar = new Button("Eliminar");
            Button editar = new Button("Editar");
            eliminar.setId(evento);
            editar.setId(evento);
            HBox infoEvento = new HBox(new Label(evento), editar, eliminar);
            vBox.getChildren().add(infoEvento);
        }
        scrollPane.setContent(vBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        //scrollPane.setPrefSize(400, 100);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }*/
}
