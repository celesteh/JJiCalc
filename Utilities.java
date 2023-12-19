import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class Utilities {

	static final int INITIAL_PRIME = 100;
	static Vector primeList = new Vector(INITIAL_PRIME);
	//static Vector primeList = new Vector(INITIAL_PRIME, INITIAL_PRIME);

	static {
		primeList.add(new Integer(2));
		primeList.add(new Integer(3));
	}

	//test the current number that if it is included in the prime number list
	//if not generate the next lot of primes number until number reached
	//(size of step = size of initial prime number list)
	//Daniel change started 9/3/2000
	static public void registerPrime(int test) {
		//while the primelist's biggest element is larger than the tested value

		while (test > largestPrime()) {
			int primeCount = 0;
			int current = largestPrime() + 2;

			boolean prime = true;
			while (primeCount < INITIAL_PRIME) {
			//insert the next lot of prime numbers to the prime list

				int sqrt = ((int)Math.sqrt(current)) + 1;
				for (int i = 0; i < primeList.size() && getPrime(i) <= sqrt; i++) {
					if (current % getPrime(i) == 0) {
						prime = false;
						break;
					}
				}
				if (prime) {
					primeList.add(new Integer(current));
					primeCount++;
				}
				prime = true;
				current += 2;
			}
		}
	}
	//Daniel change ended 9/3/2000

	static public int getPrime(int x) {
		return ((Integer)(primeList.get(x))).intValue();
	}

	static public int largestPrime() {
		return ((Integer)primeList.get(primeList.size()-1)).intValue();
	}

	static class JTextFieldStd extends JTextField {
		private FocusListener fl;

		public JTextFieldStd() {
			super();
			setPreferredSize(Parameters.TEXT_FIELD_DIMENSION);
			setMaximumSize(new Dimension(Short.MAX_VALUE,Parameters.TEXT_FIELD_DIMENSION.height));
			setHorizontalAlignment(JTextField.CENTER);
			fl = makeFL();
			addFocusListener(fl);
		}

		public JTextFieldStd(String s) {
			super(s);
			setPreferredSize(Parameters.TEXT_FIELD_DIMENSION);
			setMaximumSize(new Dimension(Short.MAX_VALUE,Parameters.TEXT_FIELD_DIMENSION.height));
			setHorizontalAlignment(JTextField.CENTER);
			fl = makeFL();
			addFocusListener(fl);
		}

		class FA extends FocusAdapter implements Serializable {
			public void focusGained(FocusEvent e) {
				setSelectionStart(0);
				setSelectionEnd(getText().length());
			}
		}

		private FocusListener makeFL() {
			return new FA();
		}

		public void noSelect() {
			removeFocusListener(fl);
		}

		public void setText(String s) {
			super.setText(s);
			setCaretPosition(0);
		}
	}

	static class JTextFieldStdNoFocus extends JTextFieldStd {
		public JTextFieldStdNoFocus() {
			super();
		}

		public JTextFieldStdNoFocus(String s) {
			super(s);
		}

		//so that this textfield cannot receive focus
		public boolean isFocusTraversable() {
			return false;
		}
	}

	/**
	decide the cloest ET note to the given cent base on the log
	value given
	plan:
	in the for loop, if value is
	0<=v<50 -> this note
	50<=v<100 -> next note
	the cent value of the octave is always in 100 increments
	*/
	static class NoteDiff {
		double value;
		int logBase;
		double diff;
		int increment;

		public NoteDiff(double value, int logBase) {
			this.value = value;
			this.logBase = logBase;

			double noteValue = logBase / 12;
			increment = (int)(value / noteValue);
			if (value - (increment * noteValue ) < noteValue / 2) {
				diff = value - (increment * noteValue);
			} else {
				diff = ((++increment * noteValue) - value) * -1;
			}
		}

		public String getNote() {
			int octave = increment / 12;
			int note = increment % 12;
			String name;
			switch (note) {
				case 0: name = "C"; break;
				case 1: name = "C#"; break;
				case 2: name = "D"; break;
				case 3: name = "D#"; break;
				case 4: name = "E"; break;
				case 5: name = "F"; break;
				case 6: name = "F#"; break;
				case 7: name = "G"; break;
				case 8: name = "G#"; break;
				case 9: name = "A"; break;
				case 10: name = "A#"; break;
				case 11: name = "B"; break;
				default: name = "fault";
			}

			name += octave;

			return name;
		}

		public double getValue() {
			return value;
		}

		public int getBase() {
			return logBase;
		}

		public double getDiff() {
			return diff;
		}
	}
	static public void autoNormalize(boolean state) {
		Parameters.autoNormalize = state;
	}

	// was commented
	static public void enableSound(boolean state) {
		Parameters.soundEnabled = state;
	}// end was comment

	public static void main(String[] args) {
		//this procedure tests the prime number generation
		System.out.println("printing");
		registerPrime(46341);
		for(int i = 0; i < primeList.size(); i++) {
			System.out.print((Integer)primeList.get(i) + " ");
		}
		System.out.println("\nfinished printing");
		System.out.println("count = " + primeList.size());
		System.out.println("Largest Prime: " + largestPrime());
	}
	public static void switchWave(int wave){
		Parameters.currentWaveForm = wave;
		System.out.println(wave);
		}
}