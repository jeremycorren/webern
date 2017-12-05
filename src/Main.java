import javafx.application.Application;
import javafx.application.Platform;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main extends Application {
    private VBox layout;
    private ComboBox<Integer> numThreadsDropdown;
    private CheckBox pitchSpaceCheckbox;
    private Button selectInstrumentsButton, playButton, stopButton, restartButton;
    private HBox playButtonContainer;
    private Slider volumeSlider, durationSlider, registerSlider;
    private List<ComboBox<String>> timbreDropdowns;
    private List<ComboBox<String>> pitchSpaceDropdowns;

    private int numThreads;
    private ObservableList<String> timbreNames;
    private ObservableList<String> pitchSpaceNames;
    private ExecutorService executor;

    private static int volumeValue, durationValue, registerValue;

    public Main() {
        layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));

        numThreadsDropdown = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4));
        numThreadsDropdown.setValue(1);

        pitchSpaceCheckbox = new CheckBox("Determine pitch spaces");

        selectInstrumentsButton = new Button("Select instrumental timbres");
        playButton = new Button("Play");

        stopButton = new Button("Stop");
        stopButton.setDisable(true);

        restartButton = new Button("Restart");

        playButtonContainer = new HBox(10);
        playButtonContainer.getChildren().addAll(playButton, stopButton, restartButton);
        playButtonContainer.setAlignment(Pos.CENTER);

        volumeSlider = Utils.buildSlider(0, 120, 60, 20);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            volumeValue = (int) volumeSlider.getValue();
        });
        volumeValue = (int) volumeSlider.getValue();

        durationSlider = Utils.buildSlider(100, 4100, 2100, 1000);
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

        pitchSpaceDropdowns = new ArrayList<>();
        pitchSpaceNames = FXCollections.observableArrayList(PitchSpace.getPitchSpaceNames());
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
                pitchSpaceCheckbox,
                selectInstrumentsButton
        );

        stopButton.setOnAction((ActionEvent event) -> {
            playerShutdown();
            stopButton.setDisable(true);
            playButton.setDisable(false);
        });

        restartButton.setOnAction((ActionEvent event) -> {
            playerShutdown();
            primaryStage.close();
            Platform.runLater(() -> {
                try {
                    new Main().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        selectInstrumentsButton.setOnAction((ActionEvent event) -> {
            numThreads = numThreadsDropdown.getValue();

            selectInstrumentsButton.setDisable(true);
            pitchSpaceCheckbox.setDisable(true);

            for (int i : IntStream.range(0, numThreads).toArray()) {
                ComboBox<String> timbreOption = new ComboBox<>(timbreNames);
                timbreOption.setPromptText("Instrument " + (i + 1));
                timbreDropdowns.add(timbreOption);

                ComboBox<String> pitchSpaceOption = null;
                if (pitchSpaceCheckbox.isSelected()) {
                    pitchSpaceOption = new ComboBox<>(pitchSpaceNames);
                    pitchSpaceOption.setPromptText("Pitch Space " + (i + 1));
                    pitchSpaceDropdowns.add(pitchSpaceOption);
                }
                if (pitchSpaceOption != null) {
                    layout.getChildren().add(new HBox(10, timbreOption, pitchSpaceOption));
                } else {
                    layout.getChildren().add(timbreOption);
                }
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
        });

        playButton.setOnAction((ActionEvent event) -> {
            playButton.setDisable(true);
            stopButton.setDisable(false);

            boolean timbreNotSelected = false;
            for (ComboBox<String> timbreDropdown : timbreDropdowns) {
                timbreNotSelected = Timbre.getTimbre(timbreDropdown.getSelectionModel().getSelectedItem()) == null;
            }

            Boolean pitchSpaceNotSelected = null;
            for (ComboBox<String> pitchSpaceDropdown : pitchSpaceDropdowns) {
                pitchSpaceNotSelected = PitchSpace.getPitchSpace(pitchSpaceDropdown.getSelectionModel().getSelectedItem()) == null;
            }

            if (timbreNotSelected && pitchSpaceNotSelected == null) {
                Alert alert = Utils.buildAlert("A timbre was left unspecified.");
                alert.showAndWait();
            } else if (timbreNotSelected && pitchSpaceNotSelected) {
                Alert alert = Utils.buildAlert("Some timbre(s) and pitch space(s) were left unspecified.");
                alert.showAndWait();
            } else {
                executor = Executors.newFixedThreadPool(numThreads);
                if (pitchSpaceCheckbox.isSelected()) {
                    Iterator<ComboBox<String>> timbreIterator = timbreDropdowns.iterator();
                    Iterator<ComboBox<String>> pitchSpaceIterator = pitchSpaceDropdowns.iterator();

                    while (timbreIterator.hasNext() && pitchSpaceIterator.hasNext()) {
                        ComboBox<String> timbreDropdown = timbreIterator.next();
                        ComboBox<String> pitchSpaceDropdown = pitchSpaceIterator.next();

                        Timbre timbre = Timbre.getTimbre(timbreDropdown.getSelectionModel().getSelectedItem());
                        PitchSpace pitchSpace = PitchSpace.getPitchSpace(pitchSpaceDropdown.getSelectionModel().getSelectedItem());
                        Runnable thread = new Player(timbre, pitchSpace);
                        executor.execute(thread);
                    }
                } else {
                    for (ComboBox<String> timbreDropdown : timbreDropdowns) {
                        Timbre timbre = Timbre.getTimbre(timbreDropdown.getSelectionModel().getSelectedItem());
                        Runnable thread = new Player(timbre, null);
                        executor.submit(thread);
                    }
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

    private void playerShutdown() {
        if (executor != null) {
            try {
                executor.shutdownNow();
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("Threads still terminating");
            }
        }
    }
}
