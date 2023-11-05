package pt.isec.pd.trabalhoPratico.ui.funcionalidadesUI.Administrador;

import javafx.geometry.Insets;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.time.LocalDate;

public class EventoUI extends VBox {
    private TextField nomeEvento, local;
    private DatePicker data;
    private Spinner<Integer> horaInicio, horaFim;
    public EventoUI() {
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        nomeEvento = new TextField();
        nomeEvento.setPromptText("nome do evento");
        local = new TextField();
        local.setPromptText("local do evento");
        data = new DatePicker();
        horaInicio = new Spinner<>(0, 24, 9);
        horaFim = new Spinner<>(0, 24, 10);

        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.getChildren().addAll(new Text("Nome:"), nomeEvento, new Text("Local:"), local, new HBox(new Text("Data:"), data, new Text("De:"), horaInicio, new Text("Até:"), horaFim));
        this.setSpacing(10);
        this.setMaxWidth(400);
    }

    private void registerHandlers() {
    }

    private void update(){
    }

    public LocalDate getData() {
        return data.getValue();
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
