import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    private Button selectInstrumentsButton;
    private Button homogenizeTimbresButton, homogenizePitchButton;
    private Button playButton, stopButton, restartButton;
    private HBox homogenizeButtonContainer, playButtonContainer;
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

        selectInstrumentsButton = new Button("Create ensemble");

        homogenizeTimbresButton = Utils.buildButton("Homogenize", 140);
        homogenizePitchButton = Utils.buildButton("Homogenize", 140);

        homogenizeButtonContainer = new HBox(10);
        homogenizeButtonContainer.getChildren().addAll(homogenizeTimbresButton, homogenizePitchButton);

        playButton = new Button("Play");
        stopButton = new Button("Stop");
        restartButton = new Button("Restart");

        playButtonContainer = new HBox(10);
        playButtonContainer.getChildren().addAll(playButton, stopButton, restartButton);
        playButtonContainer.setAlignment(Pos.CENTER);

        volumeSlider = Utils.buildSlider(0, 120, 60, 20);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> volumeValue = (int) volumeSlider.getValue());
        volumeValue = (int) volumeSlider.getValue();

        durationSlider = Utils.buildSlider(100, 4100, 2100, 1000);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> durationValue = (int) durationSlider.getValue());
        durationValue = (int) durationSlider.getValue();

        registerSlider = Utils.buildSlider(40, 80, 60, 20);
        registerSlider.valueProperty().addListener((observable, oldValue, newValue) -> registerValue = (int) registerSlider.getValue());
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

        selectInstrumentsButton.setOnAction((ActionEvent event) -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            numThreads = numThreadsDropdown.getValue();
            switch (numThreads) {
                case 1:
                    stage.setHeight(460);
                    break;
                case 2:
                    stage.setHeight(520);
                    break;
                case 3:
                    stage.setHeight(560);
                case 4:
                    stage.setHeight(600);
            }

            if (numThreads > 1) {
                layout.getChildren().add(homogenizeButtonContainer);
            }

            selectInstrumentsButton.setDisable(true);
            pitchSpaceCheckbox.setDisable(true);

            for (int i : IntStream.range(0, numThreads).toArray()) {
                ComboBox<String> timbreOption = Utils.buildDropdown(timbreNames, 140);
                timbreOption.setPromptText("Instrument " + (i + 1));
                timbreDropdowns.add(timbreOption);

                ComboBox<String> pitchSpaceOption = null;
                if (pitchSpaceCheckbox.isSelected()) {
                    pitchSpaceOption = Utils.buildDropdown(pitchSpaceNames, 140);
                    pitchSpaceOption.setPromptText("Pitch Space " + (i + 1));
                    pitchSpaceDropdowns.add(pitchSpaceOption);
                }
                if (pitchSpaceOption != null) {
                    layout.getChildren().add(new HBox(10, timbreOption, pitchSpaceOption));
                } else {
                    layout.getChildren().add(timbreOption);
                }
            }

            stopButton.setDisable(true);
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

        homogenizeTimbresButton.setOnAction((ActionEvent event) -> {
            ComboBox<String> timbreDropdown = timbreDropdowns.get(0);
            if (timbreDropdown != null) {
                Timbre timbre = Timbre.getTimbre(timbreDropdown.getSelectionModel().getSelectedItem());

                if (timbre != null) {
                    for (ComboBox<String> dropdown : timbreDropdowns) {
                        dropdown.getSelectionModel().select(timbre.toString());
                    }
                } else {
                    Alert alert = Utils.buildAlert("Set field Instrument 1.");
                    alert.showAndWait();
                }
            }
        });

        homogenizePitchButton.setOnAction((ActionEvent event) -> {
            if (!pitchSpaceDropdowns.isEmpty()) {
                ComboBox<String> pitchSpaceDropdown = pitchSpaceDropdowns.get(0);
                if (pitchSpaceDropdown != null) {
                    PitchSpace pitchSpace = PitchSpace.getPitchSpace(pitchSpaceDropdown.getSelectionModel().getSelectedItem());

                    if (pitchSpace != null) {
                        for (ComboBox<String> dropdown : pitchSpaceDropdowns) {
                            dropdown.getSelectionModel().select(pitchSpace.toString());
                        }
                    } else {
                        Alert alert = Utils.buildAlert("Set field Pitch Space 1.");
                        alert.showAndWait();
                    }

                }
            }
        });

        playButton.setOnAction((ActionEvent event) -> {
            playButton.setDisable(true);
            stopButton.setDisable(false);
            homogenizePitchButton.setDisable(true);
            homogenizeTimbresButton.setDisable(true);

            boolean timbreNotSelected = false;
            for (ComboBox<String> timbreDropdown : timbreDropdowns) {
                if (timbreNotSelected = Timbre.getTimbre(timbreDropdown.getSelectionModel().getSelectedItem()) == null) {
                    break;
                }
            }

            Boolean pitchSpaceNotSelected = null;
            if (!pitchSpaceDropdowns.isEmpty()) {
                for (ComboBox<String> pitchSpaceDropdown : pitchSpaceDropdowns) {
                    if (pitchSpaceNotSelected = PitchSpace.getPitchSpace(pitchSpaceDropdown.getSelectionModel().getSelectedItem()) == null) {
                        break;
                    }
                }
            }

            if (timbreNotSelected || (pitchSpaceNotSelected !=null && pitchSpaceNotSelected)) {
                Alert alert = Utils.buildAlert("Set all ensemble fields.");
                alert.showAndWait();

                playButton.setDisable(false);
                stopButton.setDisable(true);
                homogenizePitchButton.setDisable(false);
                homogenizeTimbresButton.setDisable(false);
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
        });

        stopButton.setOnAction((ActionEvent event) -> {
            playerShutdown();
            stopButton.setDisable(true);
            playButton.setDisable(false);
            homogenizePitchButton.setDisable(false);
            homogenizeTimbresButton.setDisable(false);
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
        primaryStage.setOnHiding((WindowEvent event) -> playerShutdown());

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(screenBounds.getMinX() + 400);
        primaryStage.setScene(new Scene(layout, 450, 170));
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
            playButton.setDisable(false);
        }
    }
}
