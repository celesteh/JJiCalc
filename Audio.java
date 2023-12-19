import javax.sound.sampled.*;
import java.io.*;

class Audio {
	static final int SINE = 1;
	static final int SQUARE = 2;
	static final int SAWTOOTH = 3;

	/**
	return an opened clip of the specified frequency and waveform,
	at maximum amplitude, sampling rate at 48KHz (um or 44.1?)

	if there is an error a null is returned.
	freq = frequency needed. range is the nyquest frequency = 1-12000
	waveform, please see the fields
	*/
	static Clip makeClip(double freq, int waveform) {
                // celeste has the simply brilliant idea of checking the freq
                if(freq < 1 || freq > 12000) return null;
                
		byte[] soundData;
		//daniel change started 5/3/2000
		int amplitude = 4000; //maximum 32767
		//daniel change ended 5/3/2000
		int samplingRate = 44100;
		double periodInFrames = samplingRate / freq;
		//buffer length = 1 second round to the next period to avoid click
		//the unit is in frames
		int bufferLength = (int) Math.round(periodInFrames * ((int)freq + 1));
		//16 bit sound = 2 bytes per sample, 1 sample per frame = mono
		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)samplingRate, 16, 1, 2, samplingRate, false);

		soundData = new byte[bufferLength * format.getFrameSize()];

		int gradient = (int) (4 * (double)amplitude / periodInFrames);

		int value = 0;
		int polarity = 1;
    
                // celeste asks:  wtf?
                /*
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File("res.txt"));
		} catch (IOException ioe) {
			System.out.println("cannot write to file");
		}
                */

		for (int frame = 0; frame < bufferLength; frame++) {
			switch (waveform) {
			case SINE:
				value = (int) (amplitude * Math.sin((double)frame / (double)samplingRate * freq * 2 * Math.PI));
				break;
			case SQUARE:
				if ((int)((double)frame / (periodInFrames / 2)) % 2 != 0) {
					polarity = 1;
				} else {
					polarity = -1;
				}
				value = (int) (amplitude * polarity);
				break;
			case SAWTOOTH:
				int dir = (int)((double)frame / (periodInFrames / 4)) % 4;
				if (dir == 0 || dir == 3) {
					value += gradient;
				} else {
					value -= gradient;
				}
				break;
			}
			soundData[frame * 2] = (byte) (value & 0xFF);
			soundData[frame * 2 + 1] = (byte) ((value >>> 8) & 0xFF);

                        /* why why why???
			try {
				fw.write(frame + ": " + value + "\n");
			} catch (IOException ioe) {
				System.out.print("cannot write to file");
			}
                        */
		}

                /* for the love of god, why?
		try {
			fw.flush();
		} catch (IOException ioe) {
			System.out.println("Cannot flush");
		}
                */

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(soundData);
			AudioInputStream ais = new AudioInputStream(bais, format, soundData.length / format.getFrameSize());
			DataLine.Info clipLineInfo = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(clipLineInfo);
			//Clip clip = (Clip) mixer.getLine(clipLineInfo);
			clip.open(ais);
			return clip;
		} catch (LineUnavailableException lue) {
			System.out.println("Unable to allocate resource to play sound");
		} catch (IOException ioe) {
			System.out.println("There is a problem with the sound i/o");
		}

		//when an exception occured
		return null;
	}

	//this is an example of how to use this function
	public static void main(String args[]) {
		//USAGE:

		//Alvin: this is how to use it
		//1:- create the clip
		Clip clip1 = makeClip(440, Audio.SINE);
		Clip clip2 = makeClip(440.5, Audio.SINE);

		//2:- start the clip
		clip1.loop(3);	//this means loop 3 times
		clip2.loop(Clip.LOOP_CONTINUOUSLY); //this means loop endlessly

		//3:- if you want to set the volume (actually its setting the gain, there is subtle difference)
		FloatControl clip1Gain = (FloatControl) clip1.getControl(FloatControl.Type.MASTER_GAIN);
		FloatControl clip2Gain = (FloatControl) clip2.getControl(FloatControl.Type.MASTER_GAIN);
		clip1Gain.setValue(-10);
		clip2Gain.setValue(+0.6f);	//note the trailing f, it is used to specify that its a "float" numeric
		/*note:
		the range of gain is around -83 to +10
		it can be acquired by float getMaximum() and float getMinumum() respectively
		please implement a volume control somewhere in the program, it is necessary
		otherwise multiple channels of audio will accumulate on volume and overflow
		the sound system, causing an ugly sound
		*/

		/*4:- (to delete the clips from memory)
		clip1.close();
		clip2.close();
		clip1 = null;
		clip2 = null;
		only close when you need to dispose of the clip, because the start() or
		loop() function will go on to the next line in the code even if it has not
		finished playing the sound, in effect the above code will turn off the sound
		immediately after it is played.

		each clip uses 86.1KB RAM
		*/


		/*final suggestion:
		As I was thinking about how to implement it in the tuning table,
		I think a neat approach is to:

		add the Clip obejct as a data field of the TableCell
		once the sound is enabled by the user...
			implement a function swapState {
				if(clip is playing) {
					clip.stop();
					if (cell is freezed && !originally_freezed) {
						cell.defrost();
					}
				}else{
					clip.start()
					if (cell is freezed)
						originally_freezed = true;
					else
						originally_freezed = false;
					}
				}
			}
			call swapState() when user click on the gray area of a cell
			enable setting volume somewhere in the UI
		After user disabled sound...
			clean up all the clips in the table cell

		Alvin what do you think about this?

		Finally, please do not include this discussion in the final version
		of the code :)

		wish you happy coding, well... at least not having a hard time la
		*/

	}
}