import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main extends Application {
    private VBox layout;
    private TextField nThreadsInput, nEventsInput;
    private Slider durationSlider;
    private Button selectInstrumentsButton, playButton;
    private List<ComboBox<String>> timbreDropdowns;

    private int nThreads, nEvents;
    private ObservableList<String> timbreNames;

    private static int durationValue;

    public Main() {
        durationSlider = MidiUtil.buildSlider();
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            durationValue = (int) durationSlider.getValue();
        });
        durationValue = (int) durationSlider.getValue();

        timbreDropdowns = new ArrayList<>();
        timbreNames = FXCollections.observableArrayList(
                Parameter.getTimbreNames()
        );
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("webern v1.0");

        layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));

        nThreadsInput = new TextField();
        nThreadsInput.setPromptText("Instruments");

        nEventsInput = new TextField();
        nEventsInput.setPromptText("Events");

        layout.getChildren().addAll(
                new Label("Number of instruments:"),
                nThreadsInput,
                new Label("Number of events:"),
                nEventsInput
        );

        selectInstrumentsButton = new Button("Select instrumental timbres");
        selectInstrumentsButton.setOnAction((ActionEvent event) -> {
            selectInstrumentsButton.setDisable(true);

            nThreads = Integer.parseInt(nThreadsInput.getText());
            nEvents = Integer.parseInt(nEventsInput.getText());

            for (int i : IntStream.range(0, nThreads).toArray()) {
                ComboBox<String> option = new ComboBox<>(timbreNames);
                option.setPromptText("Instrument " + i);
                timbreDropdowns.add(option);
                layout.getChildren().add(option);
            }
            layout.getChildren().addAll(new Label("Duration"), durationSlider, playButton);
        });
        layout.getChildren().add(selectInstrumentsButton);

        playButton = new Button("Play");
        playButton.setOnAction((ActionEvent event) -> {
            playButton.setDisable(true);

            ExecutorService executor = Executors.newFixedThreadPool(nThreads);
            for (ComboBox<String> dropdown : timbreDropdowns) {
                Parameter timbre = Parameter.getTimbre(dropdown.getSelectionModel().getSelectedItem());
                Runnable thread = new Player(nEvents, timbre);
                executor.execute(thread);
            }

            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            playButton.setDisable(false);
        });

        primaryStage.setScene(new Scene(layout, 450, 480));
        primaryStage.show();
    }

    static int getDurationValue() {
        return durationValue;
    }
}
