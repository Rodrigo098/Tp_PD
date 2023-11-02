package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import pt.isec.pd.trabalhoPratico.MainCliente;
import pt.isec.pd.trabalhoPratico.model.ProgClienteManager;

public class ContaAdministradorUI extends BorderPane {
    static protected SimpleStringProperty opcaoAdmin = new SimpleStringProperty("NADA");
    //Eventos
    private Button criarEvento, listarEventos, eventosUti, logout;
    private final ProgClienteManager progClienteManager;

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
        eventosUti = new Button("Eventos Utilizador");

        logout = new Button("Logout");
        logout.getStyleClass().add("btnLogout");

        HBox funcionalidades = new HBox(criarEvento, listarEventos, eventosUti);
        StackPane stackPane = new StackPane(new ListarEventosUI(progClienteManager), new CriarEventoUI(progClienteManager), new EditorEventos(progClienteManager), new ConsultaEventosUtiUI(progClienteManager));

        stackPane.setMaxSize(500, 250);
        this.setStyle("-fx-background-color: #E8EFF6; -fx-padding: 20 20 0 20;");
        this.setTop(funcionalidades);
        this.setCenter(stackPane);
        this.setBottom(logout);
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
        eventosUti.setOnAction(e -> {
            opcaoAdmin.set("EVENTOS_PRESENCA_UTI");
        });
        logout.setOnAction(e -> {
            progClienteManager.logout();
            MainCliente.menuSBP.set("MENU");
            MainCliente.clienteSBP.set("INDEFINIDO");
        });

        MainCliente.clienteSBP.addListener(observable -> update());
    }

    //-----------------------------------------------------------------
    private void update() {
        this.setVisible(MainCliente.clienteSBP.get().equals("ADMINISTRADOR"));
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
