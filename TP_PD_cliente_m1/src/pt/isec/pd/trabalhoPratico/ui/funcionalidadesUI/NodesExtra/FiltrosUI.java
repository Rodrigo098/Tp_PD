package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.NodesExtra;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.Objects;

public class FiltrosUI extends HBox {
    private HBox filtrosData;
    public Button procurar, verFiltros;
    private TextField nomeEvento, local;
    private DatePicker limData1, limData2;
    private Spinner<Integer> horaInicio, horaFim;
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
        local = new TextField();
        local.setPromptText("local");
        limData1 = new DatePicker();
        limData2 = new DatePicker();
        horaInicio = new Spinner<>(0, 24, 9);
        horaFim = new Spinner<>(0, 24, 10);

        VBox vBox1 = new VBox(new Text("Evento&Local:"), nomeEvento, local);
        VBox vBox2 = new VBox(new Text("Data:"), limData1, limData2);
        VBox vBox3 = new VBox(new Text("Horas:"), horaInicio, horaFim);

        filtrosData = new HBox(vBox1, vBox2, vBox3, procurar);
        filtrosData.setPadding(new Insets(0, 0, 0, 10));
        filtrosData.setMaxWidth(370);
        filtrosData.setManaged(false);
        filtrosData.setVisible(false);

        this.setPadding(new Insets(10, 0, 5, 0));
        this.getChildren().addAll(verFiltros, filtrosData);
        this.setMaxWidth(400);
    }

    public void registerHandlers() {
        verFiltros.setOnAction(e -> {
            filtrosData.setManaged(!filtrosData.isManaged());
            filtrosData.setVisible(!filtrosData.isVisible());
            verFiltros.setText(Objects.equals(verFiltros.getText(), "-") ? "+" : "-");
        });
    }

    private void update(){
    }

    public LocalDate getLimData1() {
        return limData1.getValue();
    }
    public LocalDate getLimData2() {
        return limData2.getValue();
    }
    public String getNomeEvento() {
        return nomeEvento.getText();
    }
    public String getLocal() {
        return local.getText();
    }
    public int getHoraInicio() {
        return horaInicio.getValue();
    }
    public int getHoraFim() {
        return horaFim.getValue();
    }
}
