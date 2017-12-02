import javafx.scene.control.Slider;

import java.util.concurrent.ThreadLocalRandom;

final class MidiUtil {
    static int generate(Parameter param) {
        return ThreadLocalRandom.current().nextInt(param.getOrigin(), param.getBound());
    }

    static int generate(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    static Slider buildSlider() {
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(3000);
        slider.setValue(1500);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMinorTickCount(3);
        slider.setMajorTickUnit(1000);
        return slider;
    }
}