package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

import java.io.IOException;

public class ContaAdministradorUI extends BorderPane {
    static protected SimpleStringProperty opcaoAdmin = new SimpleStringProperty("NADA");
    //Eventos
    private Button criarEvento, listarEventos, editorEventos, logout;
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
        criarEvento = new Button("Criar Evento");
        listarEventos = new Button("Lista Eventos");

        logout = new Button("Logout");
        logout.getStyleClass().add("btnLogout");

        VBox funcionalidades = new VBox(criarEvento, listarEventos);
        StackPane ladoDireito = new StackPane(new ListarEventosUI(progClienteManager), new CriarEventoUI(progClienteManager), new EditorEventos(progClienteManager));
        ladoDireito.setPrefWidth(400);
        HBox hBox = new HBox(funcionalidades, ladoDireito);

        this.setStyle("-fx-background-color: #E8EFF6; -fx-padding: 20 20 0 20;");
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
