import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
    private ComboBox<Integer> numThreadsDropdown;
    private TextField numEventsInput;
    private Button selectInstrumentsButton, playButton;
    private HBox playButtonContainer;
    private Slider volumeSlider, durationSlider, registerSlider;
    private List<ComboBox<String>> timbreDropdowns;

    private int numThreads, numEvents;
    private ObservableList<String> timbreNames;

    private static int volumeValue, durationValue, registerValue;

    public Main() {
        layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));

        numThreadsDropdown = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4));
        numThreadsDropdown.setValue(1);
        numEventsInput = new TextField();

        selectInstrumentsButton = new Button("Select instrumental timbres");
        playButton = new Button("Play");

        playButtonContainer = new HBox();
        playButtonContainer.getChildren().add(playButton);
        playButtonContainer.setAlignment(Pos.CENTER);

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
                new Label("Select a number of instruments:"),
                numThreadsDropdown,
                new Label("Enter a number of events:"),
                numEventsInput,
                selectInstrumentsButton
        );

        selectInstrumentsButton.setOnAction((ActionEvent event) -> {
            numThreads = numThreadsDropdown.getValue();
            numEvents = Utils.validate(numEventsInput.getText());

            String invalidInput = "Enter a positive integer greater than or equal to 1.";
            if (numEvents == -1) {
                Alert alert = Utils.buildAlert(invalidInput);
                alert.showAndWait();
            } else if (numEvents == -2) {
                Alert alert = Utils.buildAlert(invalidInput);
                alert.showAndWait();
            } else {
                selectInstrumentsButton.setDisable(true);
                for (int i : IntStream.range(0, numThreads).toArray()) {
                    ComboBox<String> option = new ComboBox<>(timbreNames);
                    option.setPromptText("Instrument " + (i + 1));
                    timbreDropdowns.add(option);
                    layout.getChildren().add(option);
                }

                layout.getChildren().addAll(
                        playButtonContainer,
                        new Label("Volume"),
                        volumeSlider,
                        new Label("Duration"),
                        durationSlider,
                        new Label("Register"),
                        registerSlider
                );
            }
        });

        playButton.setOnAction((ActionEvent event) -> {
            playButton.setDisable(true);

            boolean timbreNotSelected = false;
            for (ComboBox<String> dropdown : timbreDropdowns) {
                timbreNotSelected = Timbre.getTimbre(dropdown.getSelectionModel().getSelectedItem()) == null;
            }

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            if (timbreNotSelected) {
                Alert alert = Utils.buildAlert("A timbre was left unspecified.");
                alert.showAndWait();
            } else {
                for (ComboBox<String> dropdown : timbreDropdowns) {
                    Timbre timbre = Timbre.getTimbre(dropdown.getSelectionModel().getSelectedItem());
                    Runnable thread = new Player(numEvents, timbre);
                    executor.execute(thread);
                }

                executor.shutdown();
                try {
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
