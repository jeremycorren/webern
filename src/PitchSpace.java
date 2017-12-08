import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum PitchSpace {
    C(Utils.buildPitchSpace(0)),
    Db(Utils.buildPitchSpace(1)),
    D(Utils.buildPitchSpace(2)),
    Eb(Utils.buildPitchSpace(3)),
    E(Utils.buildPitchSpace(4)),
    F(Utils.buildPitchSpace(5)),
    Gb(Utils.buildPitchSpace(6)),
    G(Utils.buildPitchSpace(7)),
    Ab(Utils.buildPitchSpace(8)),
    A(Utils.buildPitchSpace(9)),
    Bb(Utils.buildPitchSpace(10)),
    B(Utils.buildPitchSpace(11))
    ;

    static Map<String, PitchSpace> pitchSpaceNames = new LinkedHashMap<>();
    static {
        for (PitchSpace pitchSpace : PitchSpace.values()) {
            pitchSpaceNames.put(pitchSpace.toString(), pitchSpace);
        }
    }

    List<Integer> pitchSpace;

    PitchSpace(List<Integer> pitchSpace) {
        this.pitchSpace = pitchSpace;
    }

    List<Integer> asList() {
        return pitchSpace;
    }

    static Set<String> getPitchSpaceNames() {
        return pitchSpaceNames.keySet();
    }

    static PitchSpace getPitchSpace(String name) {
        return pitchSpaceNames.get(name);
    }
}
