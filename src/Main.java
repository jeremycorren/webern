import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;
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
    private Button selectInstrumentsButton, playButton;
    private Slider volumeSlider, durationSlider, registerSlider;
    private List<ComboBox<String>> timbreDropdowns;

    private int nThreads, nEvents;
    private ObservableList<String> timbreNames;

    private static int volumeValue, durationValue, registerValue;

    public Main() {
        layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));

        nThreadsInput = new TextField();
        nEventsInput = new TextField();

        selectInstrumentsButton = new Button("Select instrumental timbres");
        playButton = new Button("Play");

        volumeSlider = Utils.buildSlider(0, 120, 60, 20);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            volumeValue = (int) volumeSlider.getValue();
        });
        volumeValue = (int) volumeSlider.getValue();

        durationSlider = Utils.buildSlider(0, 4000, 2000, 1000);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            durationValue = (int) durationSlider.getValue();
        });
        durationValue = (int) durationSlider.getValue();

        registerSlider = Utils.buildSlider(40, 80, 60, 20);
        registerSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            registerValue = (int) registerSlider.getValue();
        });
        registerValue = (int) registerSlider.getValue();

        timbreDropdowns = new ArrayList<>();
        timbreNames = FXCollections.observableArrayList(Timbre.getTimbreNames());
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("webern v1.0");

        layout.getChildren().addAll(
                new Label("Enter a number of instruments:"),
                nThreadsInput,
                new Label("Enter a number of events:"),
                nEventsInput,
                selectInstrumentsButton
        );

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
            layout.getChildren().addAll(
                    new Label("Volume"),
                    volumeSlider,
                    new Label("Duration"),
                    durationSlider,
                    new Label("Register"),
                    registerSlider,
                    playButton
            );
        });

        playButton.setOnAction((ActionEvent event) -> {
            playButton.setDisable(true);

            ExecutorService executor = Executors.newFixedThreadPool(nThreads);
            for (ComboBox<String> dropdown : timbreDropdowns) {
                Timbre timbre = Timbre.getTimbre(dropdown.getSelectionModel().getSelectedItem());
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

        primaryStage.setScene(new Scene(layout, 450, 600));
        primaryStage.show();
    }

    static int getVolumeValue() {
        return volumeValue;
    }

    static int getDurationValue() {
        return durationValue;
    }

    static int getRegisterValue() {
        return registerValue;
    }
}
