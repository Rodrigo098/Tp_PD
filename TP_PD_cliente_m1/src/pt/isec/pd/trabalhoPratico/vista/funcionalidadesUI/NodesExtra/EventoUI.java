package pt.isec.pd.trabalhoPratico.vista.funcionalidadesUI.NodesExtra;

import javafx.geometry.Insets;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import pt.isec.pd.trabalhoPratico.model.recordDados.Evento;

import java.time.LocalDate;

public class EventoUI extends VBox {
    private TextField nomeEvento, local, data, horaInicio, horaFim;
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
        data = new TextField();
        data.setPromptText("AAAA/MM/DD");

        horaInicio = new TextField();
        horaInicio.setPromptText("HH:MM");
        horaFim = new TextField();
        horaFim.setPromptText("HH:MM");

        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.getChildren().addAll(new Text("Nome:"), nomeEvento, new Text("Local:"), local, new HBox(new Text("Data:"), data, new Text("De:"), horaInicio, new Text("Até:"), horaFim));
        this.setSpacing(10);
        this.setMaxWidth(400);
    }

    private void registerHandlers() {
    }

    private void update(){
    }

    public String getData() {
        return data.getText();
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
    public void setInfoAntiga(Evento eventoSelecionado) {
        nomeEvento.setText(eventoSelecionado.nomeEvento());
        local.setText(eventoSelecionado.local());
        data.setText(eventoSelecionado.data());
        horaInicio.setText(eventoSelecionado.horaInicio());
        horaFim.setText(eventoSelecionado.horaFim());
    }
}
