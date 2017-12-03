import javafx.scene.control.Alert;
import javafx.scene.control.Slider;

import java.util.concurrent.ThreadLocalRandom;

final class Utils {
    static Alert buildAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert;
    }

    static Slider buildSlider(int min, int max, int value, int tickUnit) {
        Slider slider = new Slider();
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMinorTickCount(3);
        slider.setMajorTickUnit(tickUnit);
        return slider;
    }

    static int generate(Timbre timbre) {
        return ThreadLocalRandom.current().nextInt(timbre.getOrigin(), timbre.getBound());
    }

    static int generate(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    static int validate(String input) {
        Integer validInput = null;
        try {
            validInput = Integer.parseInt(input);
            if (validInput < 1) {
                return -1;
            }
        } catch (NumberFormatException e) {
            return -2;
        }
        return validInput;
    }
}