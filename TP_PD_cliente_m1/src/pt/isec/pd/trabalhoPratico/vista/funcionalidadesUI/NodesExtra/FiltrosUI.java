package pt.isec.pd.trabalhoPratico.vista.funcionalidadesUI.NodesExtra;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.Objects;

public class FiltrosUI extends HBox {
    private HBox filtrosData;
    public Button procurar, verFiltros;
    private TextField nomeEvento, local, horaInicio, horaFim, limData1, limData2;
    public FiltrosUI() {
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        verFiltros = new Button("+");
        verFiltros.getStyleClass().addAll("movButton");
        procurar = new Button();
        procurar.getStyleClass().addAll("imagens", "procurar");
        procurar.setMinSize(30, 30);
        nomeEvento = new TextField();
        nomeEvento.setPromptText("evento");
        nomeEvento.setStyle("-fx-text-size: 10px;");

        local = new TextField();
        local.setPromptText("local");

        limData1 = new TextField();
        limData1.setPromptText("AAAA/MM/DD");

        limData2 = new TextField();
        limData2.setPromptText("AAAA/MM/DD");

        horaInicio = new TextField();
        horaInicio.setPromptText("HH:MM");
        horaFim = new TextField();
        horaFim.setPromptText("HH:MM");

        VBox vBox1 = new VBox(new Text("Evento&Local:"), nomeEvento, local);
        VBox vBox2 = new VBox(new Text("Data (de-até):"), limData1, limData2);
        VBox vBox3 = new VBox(new Text("Horas(de-até):"), horaInicio, horaFim);
        vBox3.setMaxWidth(70);

        filtrosData = new HBox(vBox1, vBox2, vBox3, procurar);
        filtrosData.setPadding(new Insets(0, 0, 0, 10));
        filtrosData.setMaxWidth(370);
        filtrosData.setManaged(false);
        filtrosData.setVisible(false);

        filtrosData.getStyleClass().add("filtros");
        this.setPadding(new Insets(10, 0, 5, 0));
        this.getChildren().addAll(verFiltros, filtrosData);
        this.setMaxWidth(400);
    }

    public void registerHandlers() {
        verFiltros.setOnAction(e -> {
            filtrosData.setManaged(!filtrosData.isManaged());
            filtrosData.setVisible(!filtrosData.isVisible());
            verFiltros.setText(Objects.equals(verFiltros.getText(), "-") ? "+" : "-");
            limData1.setText(null); limData2.setText(null); nomeEvento.setText(null); local.setText(null); horaInicio.setText(null); horaFim.setText(null);
        });
    }

    private void update(){
    }

    public String getLimData1() {
        return limData1.getText();
    }
    public String getLimData2() {
        return limData2.getText();
    }
    public String getNomeEvento() {
        return nomeEvento.getText();
    }
    public String getLocal() {
        return local.getText();
    }
    public String getHoraInicio() {
        return horaInicio.getText();
    }
    public String getHoraFim() {
        return horaFim.getText();
    }
}
