import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

final class Utils {
    private final static Integer OCTAVE;
    private final static List<Integer> PITCH_CLASSES;
    static {
        OCTAVE = 12;
        PITCH_CLASSES = Arrays.asList(0, 2, 4, 5, 7, 9, 11);
    }

    /*
     * UI element methods
     */
    static Alert buildAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert;
    }

    static Button buildButton(String message, int width) {
        Button button = new Button(message);
        button.setMinWidth(width);
        button.setPrefWidth(button.getMinWidth());
        return button;
    }

    static ComboBox<String> buildDropdown(ObservableList<String> options, int width) {
        ComboBox<String> dropdown = new ComboBox<>(options);
        dropdown.setMinWidth(width);
        dropdown.setPrefWidth(dropdown.getMinWidth());
        return dropdown;
    }

    static Slider buildSlider(int min, int max, int value, int tickUnit) {
        Slider slider = new Slider();
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(value);
        slider.setShowTickMarks(true);
        slider.setMinorTickCount(3);
        slider.setMajorTickUnit(tickUnit);
        return slider;
    }

    /*
     * MIDI methods
     */
    static List<Integer> buildPitchSpace(int offset) {
        List<Integer> pitchSpace = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int pitchClass : PITCH_CLASSES) {
                pitchSpace.add((pitchClass + offset) + (i * OCTAVE));
            }
        }
        return pitchSpace;
    }

    static int generate(Timbre timbre) {
        return ThreadLocalRandom.current().nextInt(timbre.getOrigin(), timbre.getBound());
    }

    static int generate(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    static int generate(int min, int max, PitchSpace pitchSpace) {
        if (pitchSpace != null) {
            int pitch = ThreadLocalRandom.current().nextInt(min, max);
            if (pitchSpace.asList().contains(pitch)) {
                return pitch;
            } else {
                return generate(min, max, pitchSpace);
            }
        }
        return generate(min, max);
    }
}