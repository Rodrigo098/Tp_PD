package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

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

public class ContaAdministradorUI extends BorderPane {
    static protected SimpleStringProperty opcaoAdmin = new SimpleStringProperty("NADA");
    private Button criarEvento, listarEventos, eventosUti, logout, voltar;
    private HBox funcionalidades;
    private final ProgClienteManager progClienteManager;

    //-----------------------------------------------------------------
    public ContaAdministradorUI(ProgClienteManager progClienteManager)  {
        this.progClienteManager = progClienteManager;
        createViews();
        registerHandlers();
        update1();
        update2();
    }

    //-----------------------------------------------------------------
    private void createViews() {
        voltar = new Button("Voltar");
        criarEvento = new Button("Criar Evento");
        listarEventos = new Button("Lista Eventos");
        eventosUti = new Button("Eventos Utilizador");

        logout = new Button("Logout");
        logout.getStyleClass().add("btnLogout");

        VBox vBox0 = new VBox(criarEvento,listarEventos, eventosUti);
        vBox0.setSpacing(10);
        vBox0.setMaxWidth(300);

        VBox vBox = new VBox(vBox0, logout);
        vBox.setMaxSize(400,100);
        vBox.getStyleClass().add("menuBotoes");

        funcionalidades =  new HBox(voltar, new StackPane(new ListarEventosUI(progClienteManager),
                                                          new CriarEventoUI(progClienteManager),
                                                          new ConsultaEventosUtiUI(progClienteManager),
                                                          new EditorEventosUI(progClienteManager)));
        funcionalidades.setFocusTraversable(true);
        funcionalidades.getStyleClass().add("funcionalidades");

        StackPane stackPane = new StackPane(vBox, funcionalidades);

        Text tipoConta = new Text("Conta Administrador");
        tipoConta.getStyleClass().add("titulo");
        VBox centralNode = new VBox(tipoConta, stackPane);
        centralNode.setPadding(new Insets(20));

        this.getStyleClass().add("contaAdministrador");
        this.setCenter(centralNode);
        this.setFocusTraversable(true);
    }

    //-----------------------------------------------------------------
    private void registerHandlers() {
        voltar.setOnAction(e -> {
            opcaoAdmin.set("NADA");
        });
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
        MainCliente.clienteSBP.addListener(observable -> update1());
        opcaoAdmin.addListener(observable -> update2());
    }

    //-----------------------------------------------------------------
    private void update1() {
        this.setVisible(MainCliente.clienteSBP.get().equals("ADMINISTRADOR"));
    }
    private void update2() {
        funcionalidades.setVisible(!opcaoAdmin.get().equals("NADA"));
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
