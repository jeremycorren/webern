import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

class Midi {
    private Synthesizer synth;
    private MidiChannel channel;

    Midi() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            channel = synth.getChannels()[0];
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    void play(int noteNumber, int timbre, int duration, int velocity) {
        channel.programChange(timbre);
        channel.noteOn(noteNumber, velocity);
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.noteOff(noteNumber);
    }

    void breakdown() {
        synth.close();
    }
}
