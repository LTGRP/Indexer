package com.billooms.indexercontrol;

/******************************************************************
* Beginning Java 5 Game Programming
* by Jonathan S. Harbour
* modified by Bill Ooms
* SoundClip class
******************************************************************/

import javax.sound.sampled.*;
import java.io.*;

public class SoundClip {
    //the source for audio data
    private AudioInputStream sample;

    //sound clip property is read-only here
    private Clip clip;

    public Clip getClip() {
		return clip;
	}

    //looping property for continuous playback
    private boolean looping = false;

    public void setLooping(boolean flag) {
		looping = flag;
	}

    public boolean getLooping() {
		return looping;
	}

    //repeat property used to play sound multiple times
    private int repeat = 0;

    public void setRepeat(int num) {
		repeat = num;
	}

    public int getRepeat() {
		return repeat;
	}

    //filename property
    private String filename = "";

    public void setFilename(String str) {
		filename = str;
	}

    public String getFilename() {
		return filename;
	}

    //property to verify when sample is ready
    public boolean isLoaded() {
        return (boolean)(sample != null);
    }

    //constructor
    public SoundClip() {
        try {
            clip = AudioSystem.getClip();			// create a sound buffer
        } catch (LineUnavailableException e) { }
    }

    //overloaded constructor accepts a filename
    public SoundClip(String filename) {
        this();									// call the default constructor first
        load(filename);							//load the sound file
    }

    //load sound file
    public boolean load(String audiofile) {
        try {
            setFilename(audiofile);					// prepare the input stream for an audio file
            sample = AudioSystem.getAudioInputStream(getClass().getResource(filename));	// set the audio stream source
            clip.open(sample);						// load the audio file
            return true;
        } catch (IOException e) {
            return false;
        } catch (UnsupportedAudioFileException e) {
            return false;
        } catch (LineUnavailableException e) {
            return false;
        }
    }

    public void play() {
        if (!isLoaded())
			return;						// exit if the sample hasn't been loaded
        clip.setFramePosition(0);		// reset the sound clip
        if (looping)
            clip.loop(Clip.LOOP_CONTINUOUSLY);		// play sample with optional looping
        else
            clip.loop(repeat);
    }

    public void stop() {
        clip.stop();
    }

}
