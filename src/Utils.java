import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import java.util.concurrent.ThreadLocalRandom;

final class Utils {
    static int generate(Timbre timbre) {
        return ThreadLocalRandom.current().nextInt(timbre.getOrigin(), timbre.getBound());
    }

    static int generate(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
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
}