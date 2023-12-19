import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.*;
import java.util.*;

class TableCell extends JPanel implements Comparable, JRatioField.JRatioFieldActionListener, Serializable, SoundPlayListener {

	//upper[0] handles the integer part,
	//upper[1] handles the fractional part, it needs be
	//similar in the "lower" case
	private String cellName = "";
	private JRatioField upper;
	private JTextField[] lower;
	private JPanel upperField = new JPanel();
	private JPanel lowerField = new JPanel();
	private TableCellNameBox nameBox = new TableCellNameBox(" ", 2);
	private JPopupMenu popup = new JPopupMenu();
	private JMenuItem popDisable = new JMenuItem();
	private JMenuItem popClear = new JMenuItem("Clear this cell");
	//private JMenuItem popCalc = new JMenuItem("Calculator");
	private JMenuItem popSound = new JMenuItem();
	private transient Clip clip = null;
	private transient boolean soundingFreezed = false;
	/*this is for playing sound,
	if the cell is originally freezed, then when playing the sound,
	don't freeze the cell again, otherwise, freeze the cell when playing*/
	private transient boolean soundingPEFreezed = false;
        /* this is for freezing when sound is playing on the same
        ratio elsewhere in the table */
	//the state of the lower field
	public static final int FRACTION = 1;
	public static final int REAL = 2;
	public static final int INACTIVE = 3;	//this refers to the whole cell
	public static final int TYPE_NUMBER = 4;
	public static final int TYPE_TEXT = 5;
	
	private int upperState = INACTIVE;
	private int lowerState = INACTIVE;
	private int lowerType = INACTIVE;
	private double lowerValue = 0;
	private Ratio lowerRatio;
	private transient boolean freezed;	//true if the user select to freeze this cell,
								//used as a placeholder or whatever reason, a
								//function originated from JICalc for mac
        
        private transient TonicBroadcaster tonicBroadcaster;
        private transient Vector soundPlayListeners;
        
        public TableCell(TonicBroadcaster tb) {
            this();
            tonicBroadcaster = tb;
        }

        /*
        public TableCell(String str, TonicBroadcaster tb) {
            this(str);
            tonicBroadcaster = tb;
        }
        */


	public TableCell() {
		lower = new JTextField[2];
                upper = new JRatioField();
                upper.addJRatioFieldActionListener(this);
		
		//upperNameField.setLayout(new BoxLayout(upperField, BoxLayout.Y_AXIS));
		upperField.setLayout(new BoxLayout(upperField, BoxLayout.Y_AXIS));
		lowerField.setLayout(new BoxLayout(lowerField, BoxLayout.Y_AXIS));
		upperField.setBorder(BorderFactory.createLineBorder(Color.black));
		lowerField.setBorder(BorderFactory.createLineBorder(Color.black));

		for (int i = 0; i < lower.length; i++) {
			lower[i] = new Utilities.JTextFieldStdNoFocus();
			lower[i].setEditable(false);
			lowerField.add(lower[i]);
			lower[i].addMouseListener(new PopupListener());
		}

		upperField.add(nameBox);
		
		upperField.add(upper);
		upper.txtNum.addMouseListener(new PopupListener());
		upper.txtDen.addMouseListener(new PopupListener());

		freeze(false);

		popSound.setText("Enable Sound");
		enablePopSound();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(upperField);
		add(lowerField);

		setSize(Parameters.TEXT_FIELD_DIMENSION.width, Parameters.TEXT_FIELD_DIMENSION.height*4);
		
		popup.add(popSound);
		popSound.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switchSound();
				}
			}
		);		

		popup.add(popDisable);
		popDisable.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (freezed) {
						freeze(false);
					} else {
						freeze(true);
					}
				}
			}
		);

		popup.add(popClear);
		popClear.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					clear();
				}
			}
		);
		

		/*popup.add(popCalc);
		popCalc.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JCalPane calPane = new JCalPane();

				}
			}
		);*/
		//potential bug warning!!!
		//the same component has another focuslistener!
		//this procedure depends on it, it is in
		//JRatioField
		//Daniel added
		//solved with defining an event at the level of object upper
		upper.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent fe) {
					if (upper.getValue() != null) {
						upperState = FRACTION;
                                                enablePopSound();
						clearLower();
					} else {
						upperState = INACTIVE;
						popSound.setEnabled(false);
					}
				}
			}
		);
		nameBox.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent fe) {
					cellName = nameBox.getText();
				}
			}
		);
                //was commented
		upper.addMouseListener(
			new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (Parameters.soundEnabled == true) {
						//System.out.println("mouse click detected");
						switchSound();
					}
				}
			}
		);

                popSound.setEnabled(false);
           // end old comment
	}
        
        public TableCell (Ratio r) {
        
            this();
            
            setUpper(r);
        }
        
        public TableCell (int numerator, int denominator) {
        
            this( new Ratio(numerator, denominator));
        }
        
        public TableCell(Ratio r, TonicBroadcaster tb) {
            this(r);
            tonicBroadcaster = tb;
        }

        
        public void writeFile(TuningFile out) throws IOException {
            if (upperState == FRACTION) {
                out.writeTableCell(this.getName(), upperState, getUpperRatio(), lowerState,
                        getLowerType(), getLowerRatio(), getLowerReal(), getLowerText());
            } else {
                out.writeTableCell(null);
            }
        }
        
        public void setTonicBroadcaster (TonicBroadcaster tb) {
            tonicBroadcaster = tb;
        }
        
	public void switchSound() {
            if (clip == null) {
                playSound();
            } else {
                stopSound();
            }
	}
        
        public synchronized void playSound() {
        
            if (!isActive() || !Parameters.soundEnabled) return;

		//if clip == null create clip and start play
		if (clip == null) {
                    soundingFreezed = isFreezed();

                    if (!soundingFreezed) {
                        freeze(true);
                    }

                    popSound.setText("Disable Sound");
                    enablePopSound();

                    clip = Audio.makeClip(getFreq(), Parameters.currentWaveForm);
                    if (clip != null) {
                        clip.loop(Clip.LOOP_CONTINUOUSLY);
                        //System.out.println("clip made and looping");
                    }
                    alertSoundPlay();
                }
        }
        
        public synchronized void stopSound() {

            if (clip != null) {
                //if clip != null close and delete clip
                clip.close();
                clip = null;

                popSound.setText("Enable Sound");

                if (isFreezed() && !soundingFreezed) {
                    freeze(false);
                }
                //System.out.println("clip nulled");
                alertSoundStopped ();
            }
        }


	public boolean isPlayingSound() {
        
		if (!isActive() || clip == null) { //System.out.println(getName() + ": no clip"); 
                                                return false;}
		if (clip.isRunning()) {
                        //System.out.println("clip is running");
			return true;
		} else {
                        //System.out.println("clip is not running");
			return false;
		}
	}

	public double getFreq() {
            if (tonicBroadcaster == null) {
		return getUpperRatio().getHz(Parameters.currentBaseFreq);
            } else {
                return getUpperRatio().getHz(tonicBroadcaster.getTonic());
            }
	}

	
	// why would parameters get to disable sounds?
	public void enablePopSound() {
		popSound.setEnabled(Parameters.soundEnabled);
	}
	

	public void freeze(boolean s) {
		if (s) {
			upper.txtNum.setEditable(false);
			upper.txtDen.setEditable(false);
			popDisable.setText("Defrost this cell");
			popClear.setEnabled(false);
                        popSound.setEnabled(false);
		} else {
			upper.txtNum.setEditable(true);
			upper.txtDen.setEditable(true);
			popDisable.setText("Freeze this cell");
			popClear.setEnabled(true);
                        enablePopSound();
		}

		freezed = s;
		//originallyFreezed = s;
		//System.out.println("originallyFreezed is changed to " + s);
	}

	public synchronized boolean isFreezed() {
		return freezed;
	}

	//if the cell is freezed, it will not be cleared
	public synchronized void clear() {
		if (freezed) return;

		for (int i = 0; i < lower.length; i++) {
			lower[i].setText("");
		}
		
		nameBox.clear();
                cellName = "";
		upper.clear();
		upperState= INACTIVE;
		/*
                lowerValue = 0;
		lowerRatio = null;
		lowerState= INACTIVE;
                */
                clearLower();
                popSound.setEnabled(false);
	}

	public synchronized void clearLower() {
		if (freezed) return;

		for (int i = 0; i < lower.length; i++) {
			lower[i].setText("");
		}
		lowerValue = 0;
		lowerRatio = null;
		lowerState= INACTIVE;
	}

	public boolean isActive() {
            if (upperState == FRACTION) {
                    // alas, we can't rely on this
                    if (upper == null) {
                        upper = new JRatioField();
                        upperState = INACTIVE;
                        return false;
                    } else if (upper.getValue() == null) {
                        upperState = INACTIVE;
                        return false;
                    } else {
                        return true;
                    }
            } else {
                return false;
            }
	}
        
        public boolean actionOk() {
        
            return isActive() && ! isFreezed();
        }
        
        public void upperToLower() {
        
            if (actionOk()){
                this.setLower(getUpperRatio());
            }
        }
        
        public double getUpperCents() {
            Ratio r = getUpperRatio();
            if (r == null) return 0;
            return r.getCents();
        }
        
        public double getLowerCents() {
            if (lowerRatio == null) {
                return 0;
            }
            return lowerRatio.getCents();
        }

	/**
	return the constant specifying the state of the upper cell
	*/
	public int getUpperState() {
		return upperState;
	}

	/**
	return the constant specifying the state of the lower cell
	*/
	public int getLowerState() {
		return lowerState;
	}

	public Ratio getUpperRatio() {
		if (upperState != FRACTION) return null;
		return upper.getValue();
	}

	public Ratio getLowerRatio() {
		if (lowerState != FRACTION) return null;
		return (Ratio)lowerRatio.clone();
	}

	/**
	get the number in the lower part of the cell
	if it is of double type, otherwise return 0
	*/
	public double getLowerReal() {
		if (lowerState != REAL) return 0.0;
		return lowerValue;
	}

	/**
	return the type of the lower field in terms
	of that if it is a Hertz value or a Cent value
	*/
	public int getLowerType() {
		if (lowerState != REAL) return 0;
		return lowerType;
	}

	public String getLowerText() {
		if (lowerType != TYPE_TEXT) return null;
		return lower[1].getText();
	}

	public boolean setUpper(Ratio r){
		if (r == null) {
			clear();
			return true;
		}

		if (Parameters.autoNormalize) r.normalize();

		if ((upper.getValue() != null) &&
				(r.getNum() == upper.getValue().getNum() &&
					r.getDen() == upper.getValue().getDen()))
			return true;

		upper.setValue(r);
		upperState = FRACTION;
		clearLower();
                enablePopSound();

		return true;
	}
	
        public void JRatioFieldAction (JRatioField.JRatioFieldEvent e) {
        
            //JRatioField jrf = e.getJRatioField();
            if (upper.updated() && upper.getValue() != null) {
                upperState = FRACTION;
                clearLower();
                enablePopSound();
            }
        }
        
        public synchronized void soundPlay(SoundPlayEvent e) {

            if (!(isPlayingSound()) ) {
                soundingPEFreezed = isFreezed();

                //if ( !soundingPEFreezed) {
                    freeze(true);
                //}
            }
        }
        
        public synchronized void soundStopped (SoundPlayEvent e) {
            if (isPlayingSound()) {
                // send out word that we're playing
                alertSoundPlay();
            } else if (isFreezed() && !soundingPEFreezed) {
                freeze(false);
            }

        }
        
        
        public boolean isAlive() {
            return true;
        }
        

        public synchronized void addSoundPlayListener( SoundPlayListener spl) {
        
            if (soundPlayListeners == null) {
                soundPlayListeners = new Vector();
            }
            
            soundPlayListeners.addElement(spl);
        
        }
        
        public synchronized void removeSoundPlayListener (SoundPlayListener spl) {
            if (soundPlayListeners != null) {
                soundPlayListeners.removeElement(spl);
            }
        }
        
        private synchronized void alertSoundPlay () {
            if (soundPlayListeners != null) {
                SoundPlayEvent e = new SoundPlayEvent(this, SoundPlayEvent.PLAY);
                Enumeration enumer = soundPlayListeners.elements();
                while (enumer.hasMoreElements()) {
                    SoundPlayListener spl = (SoundPlayListener) enumer.nextElement();
                    if (spl != null) {
                        if (spl.isAlive()) {
                            spl.soundPlay(e);
                            
                        } else {
                            removeSoundPlayListener(spl);
                        }
                    }
                }
            }
        }
        
        private synchronized void alertSoundStopped () {
            if (soundPlayListeners != null) {
                SoundPlayEvent e = new SoundPlayEvent(this, SoundPlayEvent.STOPPED);
                Enumeration enumer = soundPlayListeners.elements();
                while (enumer.hasMoreElements()) {
                    SoundPlayListener spl = (SoundPlayListener) enumer.nextElement();
                    if (spl != null) {
                        if (spl.isAlive()) {
                            spl.soundStopped(e);
                        } else {
                            removeSoundPlayListener(spl);
                        }
                    }
                }
            }
        }
        
	public boolean setNameBox(String r){
		if (r == null) {
			clear();
			return true;
		}
		cellName = r;
		nameBox.setName(r);
		nameBox.moveCaretPosition(0);
		return true;
	}
	public String getName() {return cellName;}
        
        public void calculateName(){

            if (this.isActive() && !this.isFreezed()){
                        
                double cents = this.getUpperRatio().getCents();
                Utilities.NoteDiff note = new Utilities.NoteDiff(cents, 1200);
                this.setNameBox(note.getNote()+' '+note.getDiff());//, note.getNote());
            }
        }


	public void setLower(Ratio r){
		if (r == null) return;
		if (r.getDen() == 0) return;

		lowerRatio = r;

		lower[0].setText(Integer.toString(Math.abs(r.getNum())));
		lower[1].setText(Integer.toString(Math.abs(r.getDen())));

		lower[0].setHorizontalAlignment(JTextField.CENTER);
		lower[1].setHorizontalAlignment(JTextField.CENTER);

		lower[0].setCaretPosition(0);
		lower[1].setCaretPosition(0);

		lowerState = FRACTION;

		return;
	}

	public void setLower(double v){

		//lowerValue = Math.abs(v);
		lowerValue = v;

		String valueString = Double.toString(lowerValue);

		lower[0].setHorizontalAlignment(JTextField.LEFT);
		lower[1].setHorizontalAlignment(JTextField.LEFT);

		int index = valueString.indexOf('.');
		lower[0].setText(valueString.substring(0, index+1)); //including the '.'
		lower[1].setText(valueString.substring(index+1, valueString.length()));

		lower[0].setCaretPosition(0);
		lower[1].setCaretPosition(0);

		lowerState = REAL;
	}

	public void setLower(double v, String unit){

		//lowerValue = Math.abs(v);
		lowerValue = v;

		lower[0].setHorizontalAlignment(JTextField.LEFT);
		lower[1].setHorizontalAlignment(JTextField.RIGHT);

		lower[0].setText(Double.toString(v));
		lower[1].setText(unit);

		lower[0].setCaretPosition(0);
		lower[1].setCaretPosition(0);

		lowerState = REAL;
		lowerType = TYPE_TEXT;
	}

	/**
	swap the value of the upper cell to the lower cell
	*/
	public boolean swap(){
		//if the bottom is in the fraction form, swap
		if (lowerState != FRACTION) {
			return false;
		}
		
		Ratio temp = upper.getValue();
		upper.setValue(lowerRatio);
		setLower(temp);
		
		int tempState = lowerState;
		lowerState = upperState;
		upperState = tempState;

		return true;
	}

	public void normalizeLower() {
		if (lowerState != FRACTION) return;
		Ratio r = getLowerRatio();
		r.normalize();
		setLower(r);
	}

	public boolean calRatioToCent() {
		if (upperState != FRACTION) {
			return false;
		}

		setLower(upper.getValue().getLogValue());
		return true;
	}

        
	public static void swap(TableCell a, TableCell b) {
		//omit to improve performance
		//if (a == null || b == null) return;

		if (a.isFreezed() || b.isFreezed()) return;
		String tempNameA = a.getName();
		String tempNameB = b.getName();
		//System.out.println(tempNameA);
		//System.out.println(tempNameB);
		Ratio tempA = a.getUpperRatio();
		Ratio tempB = b.getUpperRatio();
		a.clear();
		b.clear();
		a.setNameBox(tempNameB);
		b.setNameBox(tempNameA);
		a.setUpper(tempB);
		b.setUpper(tempA);
		
	}
        
        public void swap(TableCell cellToSwitch) {
		//omit to improve performance
		if (cellToSwitch== null) return;

		if (this.isFreezed() || cellToSwitch.isFreezed()) return;
		String tempNameA = this.getName();
		String tempNameB = cellToSwitch.getName();
		//System.out.println(tempNameA);
		//System.out.println(tempNameB);
		Ratio tempA = this.getUpperRatio();
		Ratio tempB = cellToSwitch.getUpperRatio();
		this.clear();
		cellToSwitch.clear();
		this.setNameBox(tempNameB);
		cellToSwitch.setNameBox(tempNameA);
		this.setUpper(tempB);
		cellToSwitch.setUpper(tempA);
        }
        


        public void overWrite (TableCell newCell) {

            if (this.isFreezed() || newCell.isFreezed()) return;
            this.clear();
            this.setNameBox(newCell.getName());
            this.setUpper(newCell.getUpperRatio());
            int newLowerState = newCell.getLowerState();
            if (newLowerState == FRACTION) {
                this.setLower(newCell.getLowerRatio());
            } else if (newLowerState == REAL) {
                if (newCell.getLowerType() == TYPE_TEXT) {
                    this.setLower(newCell.getLowerReal(), getLowerText());
                } else {
                    this.setLower(newCell.getLowerReal());
                }
            }

        }
        
        
        
        

	/**
	if this is bigger then the input, return n < 0
	if this is same as the input, return n == 0
	if this is smaller then the input, return n > 0
	input must be of form Ratio, return 0 otherwise
	if the cell is null assume the cell is Short.MAX_VALUE
	*/
	public int compareTo(Object b) {

		double d1, d2;

		if (getUpperRatio() == null) {
			d1 = Double.MAX_VALUE;
		} else {
			d1 = getUpperRatio().getValue();
		}

		if (((TableCell)b).getUpperRatio() == null) {
			d2 = Double.MAX_VALUE;
		} else {
			d2 = ((TableCell)b).getUpperRatio().getValue();
		}

		double comp = d1 - d2;

		if (comp > 0) return 1;
		else if (comp == 0) return 0;
		else return -1;
	}

	/**
	similar to compareTo but works on the lower half
	if the cell is null assume the cell is 0;
	*/
	public int compareLowerTo(Object b) {

		double d1, d2;

		if (getLowerRatio() == null) {
			d1 = Double.MAX_VALUE;
		} else {
			d1 = getLowerRatio().getValue();
		}

		if (((TableCell)b).getLowerRatio() == null) {
			d2 = 0;
		} else {
			d2 = ((TableCell)b).getLowerRatio().getValue();
		}

		double comp = d1 - d2;

		if (comp > 0) return 1;
		else if (comp == 0) return 0;
		else return -1;
	}
        
        private void readObject(ObjectInputStream in) 
                    throws IOException, ClassNotFoundException {
            
            in.defaultReadObject();
            freeze(false);
            clip = null;
            enablePopSound();
                
        }
        
        private void writeObject(ObjectOutputStream out) 
                throws IOException {
                
            out.defaultWriteObject();
            
        }
        
        public double calRatioCents (double tonic, double inCents, double baseCents) {
            
            if (!( isFreezed() || getLowerState() == INACTIVE)) {
                if (getLowerState() == FRACTION) {
                    inCents = getLowerCents();
                    setLower(inCents - baseCents);
                } else if (getLowerState() == REAL) {
                    if (getLowerType() == TYPE_TEXT) {
                        inCents = Ratio.hzToLogValue(getLowerReal(),
                                tonic, 1200);
                    } else {
                        inCents = getLowerReal();
                    }
                } else {
                    setLower(inCents - baseCents);
                }
            }
            return inCents;
        }
            

	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	public static void main(String args[]) {
		JFrameC frm = new JFrameC();
		Container contentPane = frm.getContentPane();
		final TableCell testCell = new TableCell();
		contentPane.add(testCell, BorderLayout.CENTER);

		Box boxButtons = new Box(BoxLayout.Y_AXIS);
		JButton btnTestSwap = new JButton("Swap");
		btnTestSwap.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					testCell.setLower(new Ratio(1,1));
					testCell.swap();
				}
			}
		);
		boxButtons.add(btnTestSwap);

		JButton btnTestClear = new JButton("Clear");
		btnTestClear.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					testCell.clear();
				}
			}
		);
		boxButtons.add(btnTestClear);

		JButton btnTestRtc = new JButton("Ratio -> Cent");
		btnTestRtc.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					testCell.calRatioToCent();
				}
			}
		);
		boxButtons.add(btnTestRtc);

		contentPane.add(boxButtons, BorderLayout.SOUTH);
		frm.view(400,400);
	}
}