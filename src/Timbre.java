import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum Timbre {
    PIANO(0,2),
    PERCUSSION(8, 13),
    STRINGS(40, 43),
    CHOIR(52, 54)
    ;

    static Map<String, Timbre> timbreNames = new HashMap<>();
    static {
        for (Timbre timbre : Timbre.values()) {
            timbreNames.put(timbre.toString(), timbre);
        }
    }

    private int origin;
    private int bound;

    Timbre(int origin, int bound) {
        this.origin = origin;
        this.bound = bound;
    }

    int getOrigin() {
        return origin;
    }

    int getBound() {
        return bound;
    }

    @Override
    public String toString() {
        return capitalize(name());
    }

    static Set<String> getTimbreNames() {
        return timbreNames.keySet();
    }

    static Timbre getTimbre(String name) {
        return timbreNames.get(name);
    }

    private static String capitalize(String s) {
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}
