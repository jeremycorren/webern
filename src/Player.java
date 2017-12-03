import java.util.stream.IntStream;

public class Player implements Runnable {
    private int EVENTS;
    private Timbre TIMBRE;

    Player(int events, Timbre timbre) {
        EVENTS = events;
        TIMBRE = timbre;
    }

    @Override
    public void run() {
        Midi midi = new Midi();
        for (int ignore : IntStream.range(0, EVENTS).toArray()) {
            int MIN_VOLUME = calculateMinDuration(Parameter.VOLUME);
            int MAX_VOLUME = calculateMaxDuration(Parameter.VOLUME);

            int MIN_DURATION = calculateMinDuration(Parameter.DURATION);
            int MAX_DURATION = calculateMaxDuration(Parameter.DURATION);

            int MIN_PITCH = calculateMinDuration(Parameter.PITCH);
            int MAX_PITCH = calculateMaxDuration(Parameter.PITCH);

            midi.play(
                    Utils.generate(MIN_PITCH, MAX_PITCH),
                    Utils.generate(TIMBRE),
                    Utils.generate(MIN_DURATION, MAX_DURATION),
                    Utils.generate(MIN_VOLUME, MAX_VOLUME)
            );
        }
        midi.breakdown();
    }

    private int calculateMinDuration(Parameter param) {
        switch (param) {
            case VOLUME:
                return Main.getVolumeValue() / 2;
            case DURATION:
                return Main.getDurationValue() / 2;
            case PITCH:
                return Main.getRegisterValue() - 10;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private int calculateMaxDuration(Parameter param) {
        switch (param) {
            case VOLUME:
                return Double.valueOf(Main.getVolumeValue() * 1.5).intValue();
            case DURATION:
                return Double.valueOf(Main.getDurationValue() * 1.5).intValue();
            case PITCH:
                return Main.getRegisterValue() + 10;
            default:
                throw new UnsupportedOperationException();
        }
    }

    enum Parameter {
        VOLUME, DURATION, PITCH
    }
}