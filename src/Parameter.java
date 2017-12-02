import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum Parameter {
    PITCH(55, 75),
    VELOCITY(20, 35),
    TIMBRE_PIANO(0,2),
    TIMBRE_PERC(8, 13),
    TIMBRE_STRINGS(40, 43),
    TIMBRE_CHOIR(52, 54)
    ;

    static Map<String, Parameter> timbreNames = new HashMap<>();
    static {
        timbreNames.put("Piano", TIMBRE_PIANO);
        timbreNames.put("Percussion", TIMBRE_PERC);
        timbreNames.put("Strings", TIMBRE_STRINGS);
        timbreNames.put("Choir", TIMBRE_CHOIR);
    }

    private int origin;
    private int bound;

    Parameter(int origin, int bound) {
        this.origin = origin;
        this.bound = bound;
    }

    int getOrigin() {
        return origin;
    }

    int getBound() {
        return bound;
    }

    static Set<String> getTimbreNames() {
        return timbreNames.keySet();
    }

    static Parameter getTimbre(String name) {
        return timbreNames.get(name);
    }
}
