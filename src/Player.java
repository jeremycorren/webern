import java.util.stream.IntStream;

public class Player implements Runnable {
    private final int EVENTS;
    private final Parameter TIMBRE;

    private Integer MIN_DURATION;
    private Integer MAX_DURATION;

    Player(int events, Parameter timbre) {
        EVENTS = events;
        TIMBRE = timbre;
        MIN_DURATION = calculateMinDuration();
        MAX_DURATION = calculateMaxDuration();
    }

    @Override
    public void run() {
        Midi midi = new Midi();
        for (int ignore : IntStream.range(0, EVENTS).toArray()) {
            MIN_DURATION = calculateMinDuration();
            MAX_DURATION = calculateMaxDuration();

            midi.play(
                    MidiUtil.generate(Parameter.PITCH),
                    MidiUtil.generate(TIMBRE),
                    MidiUtil.generate(MIN_DURATION, MAX_DURATION),
                    MidiUtil.generate(Parameter.VELOCITY)
            );
        }
        midi.breakdown();
    }

    private int calculateMinDuration() {
        return Main.getDurationValue() / 2;
    }

    private int calculateMaxDuration() {
        return Double.valueOf(Main.getDurationValue() * 1.5).intValue();
    }
}