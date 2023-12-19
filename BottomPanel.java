import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

class BottomPanel extends JPanel {
	private final int LOG_CENT = 1200;
	private final int LOG_1024 = 1024;
	private final int LOG_64th = 768;

	private TuningTable tuningTable;
	private RightPanel rightPanel;
	private JFrame currentFrame;
	private final double DEFAULT_STRING_LENGTH = 1;
	private Ratio temp1;
	private Ratio temp2;

	private JButton btnCalSuccessive = new JButton("successive");
	private JButton btnCalModulation = new JButton("modulation  /");
	private JButton btnCalSucc_rel = new JButton("succ > rel");
	private JButton btnCalOctave = new JButton("octave");
	private JButton btnCalBottom_top = new JButton("bot <> top");
	private JButton btnCalRatios_cts = new JButton("ratios > cts");
	private JButton btnCalTransposition = new JButton("transposition  X");

	private JButton btnViwET_cents = new JButton("ET+-cents");
	private JButton btnViwET_1024 = new JButton("ET+-1024");
	private JButton btnViwET_64ths = new JButton("ET+-64ths");
	private JButton btnViwCents = new JButton("cents");
	private JButton btnViwHertz = new JButton("Hertz");
	private JButton btnViwFret = new JButton("fret pos");
	private JButton btnViwStringLen = new JButton("string len");

	private JButton btnSoundEnable = new JButton("Sound Off");

	private JTextField txtError = new Utilities.JTextFieldStdNoFocus("1");
	private Utilities.JTextFieldStd txtCentValue = new Utilities.JTextFieldStd();
	private Utilities.JTextFieldStd txt1024Value = new Utilities.JTextFieldStd();
	private Utilities.JTextFieldStd txt64thValue = new Utilities.JTextFieldStd();
	private JTextField txtStringLen = new Utilities.JTextFieldStd();
	private JRatioField rtoCalcRatio = new JRatioField();
	private JRatioField rtoCalPadRatio = new JRatioField();

	private JRatioField rtoCalcRatio1 = new JRatioField();
	private JRatioField rtoCalcRatio2 = new JRatioField();
	private JRatioField rtoCalcRatio3 = new JRatioField();
	private JButton 	btnEqual  = new JButton(" = ");
	private JButton 	btnAC  = new JButton(" Clear ");
	
	private JPanel pnlCalculate	 = new JPanel(new GridLayout(1, 1));
	private Box innerPnlCalculate = new Box(BoxLayout.Y_AXIS);
	private JPanel upperPnlCalculate = new JPanel(new GridLayout(3, 2));
	private JPanel lowerPnlCalculate = new JPanel(new GridLayout(2, 2));
	
	private JPanel pnlViewAs = new JPanel(new GridLayout(1, 1));
	private Box innerPnlViewAs = new Box(BoxLayout.Y_AXIS);
	private JPanel upperPnlViewAs = new JPanel(new GridLayout(3, 2));
	private JPanel lowerPnlViewAs = new JPanel(new GridLayout(1, 2));
	
	private JPanel pnlCalPad = new JPanel(new GridLayout(2, 0));
	private JPanel pnlCalcLeft = new JPanel(new GridLayout(5, 2));
	private JPanel pnlCal = new JPanel(new GridLayout(1,2));

	//private JPanel pnlSoundEnable = new JPanel(new BoxLayout(this,BoxLayout.Y_AXIS)); // this line was commented

	private Box pnlViewStrLen = new Box(BoxLayout.Y_AXIS);
	private Box btnEmpty = new Box(BoxLayout.X_AXIS);
	private Box pnlCalPane1 = new Box(BoxLayout.X_AXIS);
	private Box pnlCalPane2 = new Box(BoxLayout.X_AXIS);
	private Box pnlCalPane3 = new Box(BoxLayout.X_AXIS);
	
	private Box pnlCalbox = new Box(BoxLayout.Y_AXIS);
	private Box pnlCalcRight = new Box(BoxLayout.Y_AXIS);
	private JPanel pnlCalc = new JPanel(new GridLayout(2,1));

	private JPanel pnlBottom = new JPanel(new GridLayout(3, 1));
	private JFrameC frmCalc = new JFrameC();

	public BottomPanel(RightPanel rp, TuningTable t, JFrame jf) {
		tuningTable = t;
		rightPanel = rp;
		currentFrame = jf;

		initializeBtns();

		upperPnlCalculate.add(btnCalSuccessive);
		upperPnlCalculate.add(btnCalSucc_rel);
		upperPnlCalculate.add(btnCalOctave);
		upperPnlCalculate.add(btnCalBottom_top);
		upperPnlCalculate.add(btnCalRatios_cts);
		upperPnlCalculate.add(btnSoundEnable);
		upperPnlCalculate.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder()
			)
		);
		
		lowerPnlCalculate.add(btnCalTransposition);
		lowerPnlCalculate.add(new JLabel("Ratio"));
		lowerPnlCalculate.add(btnCalModulation);
		lowerPnlCalculate.add(rtoCalcRatio);
		lowerPnlCalculate.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder()
			)
		);
		
		innerPnlCalculate.add(upperPnlCalculate);
		innerPnlCalculate.add(lowerPnlCalculate);
		
		pnlCalculate.add(innerPnlCalculate);
		pnlCalculate.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"calculate:", TitledBorder.CENTER, TitledBorder.TOP
			)
		);

		pnlViewStrLen.add(new JLabel("Str. Len."));
		pnlViewStrLen.add(txtStringLen);

		upperPnlViewAs.add(btnViwET_cents);
		upperPnlViewAs.add(btnViwHertz);
		upperPnlViewAs.add(btnViwET_1024);
		upperPnlViewAs.add(btnViwFret);
		upperPnlViewAs.add(btnViwET_64ths);
		upperPnlViewAs.add(btnViwCents);
		
		upperPnlViewAs.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder()
			)
		);
		
		lowerPnlViewAs.add(btnViwStringLen);
		lowerPnlViewAs.add(pnlViewStrLen);
		
		lowerPnlViewAs.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder()
			)
		);
		
		innerPnlViewAs.add(upperPnlViewAs);
		innerPnlViewAs.add(lowerPnlViewAs);
		
		pnlViewAs.add(innerPnlViewAs);
		pnlViewAs.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"view as:", TitledBorder.CENTER, TitledBorder.TOP
			)
		);

		pnlCalcLeft.add(new JLabel(""));
		pnlCalcLeft.add(new JLabel("value"));
		pnlCalcLeft.add(new JLabel("cents"));
		pnlCalcLeft.add(txtCentValue);
		pnlCalcLeft.add(new JLabel("1024/oct"));
		pnlCalcLeft.add(txt1024Value);
		pnlCalcLeft.add(new JLabel("64ths"));
		pnlCalcLeft.add(txt64thValue);

		pnlCalcRight.add(new JLabel("resolution"));
		pnlCalcRight.add(txtError);
		pnlCalcRight.add(new JLabel("ratio"));
		pnlCalcRight.add(rtoCalPadRatio);

		pnlCalPane1.add(rtoCalcRatio1);
		pnlCalPane1.add(new JLabel("  X  "));
		pnlCalPane1.add(rtoCalcRatio2);
		
		//pnlCalPane.add(new JLabel(" = "));
		pnlCalPane2.add(btnEqual);
		pnlCalPane2.add(rtoCalcRatio3);
		pnlCalPane2.add(btnAC);
		
		pnlCalPad.add(pnlCalPane1);
		pnlCalPad.add(pnlCalPane2);
		//pnlCalPad.add(pnlCalPane3);
		pnlCalPad.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"Ratio Calculation:", TitledBorder.CENTER, TitledBorder.TOP
			)
		);


		pnlCal.add(pnlCalcLeft);
		pnlCal.add(pnlCalcRight);
		pnlCalbox.add(pnlCalPad);
		pnlCalc.add(pnlCal);
		pnlCalc.add(pnlCalbox);
		pnlCalc.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"Sketch Pad:", TitledBorder.CENTER, TitledBorder.TOP
			)
		);

		pnlBottom.add(pnlCalculate);
		pnlBottom.add(pnlViewAs);
		pnlBottom.add(pnlCalc);

		// was comment
                /*
                pnlSoundEnable.add(btnSoundEnable);
		pnlSoundEnable.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"Audio Realization:", TitledBorder.CENTER, TitledBorder.TOP
			)
		); 
                */ // end was comment

		((FlowLayout)getLayout()).setAlignment(FlowLayout.LEFT);
		add(pnlCalculate);
		add(pnlViewAs);
		add(pnlCalc);
		//add(pnlSoundEnable); // this line was commented

		txtCentValue.noSelect();
		txt1024Value.noSelect();
		txt64thValue.noSelect();
	}

	private void initializeBtns() {
		
		btnEqual.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					temp1 = rtoCalcRatio1.getValue();
					temp2 = rtoCalcRatio2.getValue();
					if ((temp1 != null)||(temp2 != null)){
						rtoCalcRatio3.setValue(temp1.multiply(temp2));}
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);
		
		btnAC.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					rtoCalcRatio1.clear();
					rtoCalcRatio2.clear();
					rtoCalcRatio3.clear();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);
		
		btnCalSuccessive.setToolTipText("Calculates the ratios between successive notes of the scale in the TOP HALF of each row, and displays the results in the BOTTOM HALF");
		btnCalSuccessive.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

					//if (tuningTable.cells[0].isActive() && !tuningTable.cells[0].isFreezed())
						//tuningTable.cells[0].setLower(tuningTable.cells[0].getUpperRatio());
                                        tuningTable.cells[0].upperToLower();

					for (int i = 1; i < tuningTable.cells.length; i++) {
						if (tuningTable.cells[i].isFreezed()) continue;
						Ratio upperRatio = tuningTable.cells[i].getUpperRatio();
						if (upperRatio == null) continue;
						//scan last ratio that is active and not freezed
						int j;
						for (j = i-1; j >=0; j--) {
							if (tuningTable.cells[j].isActive() &&
								!tuningTable.cells[j].isFreezed()) {
								break;
							}
						}
						if (j < 0) j = 0;

						Ratio lastUpperRatio;
						if (tuningTable.cells[j].isActive() && !tuningTable.cells[j].isFreezed()) {
							lastUpperRatio = tuningTable.cells[j].getUpperRatio();
							tuningTable.cells[i].setLower(upperRatio.divide(lastUpperRatio));
						} else {
							tuningTable.cells[i].setLower(upperRatio);
						}
					}
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		/**
		taking the first appearing upper value of a cell as the base
		this button calculate the difference between the base each lower value
		and present it in cents, it will avoid freezed and blank cells
		*/
		btnCalRatios_cts.setToolTipText("Calculate the difference between the base each lower value and present it in cents");
		btnCalRatios_cts.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
                                    /*
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

					//find max that is not freezed
					int base = tuningTable.getFundamental(true);
					if (base == -1) {
						finished();
						return; 	//there is no active cell in the table
					}

					Ratio baseRatio = tuningTable.cells[base].getUpperRatio();
					double baseCents = baseRatio.getCents();

					double inCents = 0;
					for (int i = 0; i < tuningTable.cells.length; i++) {
						if (tuningTable.cells[i].isFreezed() ||
							tuningTable.cells[i].getLowerState() == TableCell.INACTIVE) {
							continue;
						} else if (tuningTable.cells[i].getLowerState() == TableCell.FRACTION) {
							inCents = tuningTable.cells[i].getLowerRatio().getCents();
						} else if (tuningTable.cells[i].getLowerState() == TableCell.REAL) {
							if (tuningTable.cells[i].getLowerType() == TableCell.TYPE_TEXT) {
								inCents = Ratio.hzToLogValue(tuningTable.cells[i].getLowerReal(),
									rightPanel.getTonic(), 1200);
							} else {
								inCents = tuningTable.cells[i].getLowerReal();
							}
							continue;
						}
						tuningTable.cells[i].setLower(inCents - baseCents);
					}

					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                    */
                                    tuningTable.calRatioCents();
				}

				private void finished() {
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnCalBottom_top.setToolTipText("Swaps the lower half of each scale field with the upper half");
		btnCalBottom_top.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					for (int i = 0; i < tuningTable.MAX_CELL; i++) {
						if (tuningTable.cells[i].isActive() &&
							!tuningTable.cells[i].isFreezed())
							tuningTable.cells[i].swap();
					}
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnCalModulation.setToolTipText("Calculates the ratios in a fixed scale as they appear relative to a selected interval");
		btnCalModulation.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					Ratio r = rtoCalcRatio.getValue();

					if (r != null) {
						Ratio currentR;
						for (int i = 0; i < tuningTable.cells.length; i++) {
							if (!tuningTable.cells[i].isActive() ||
								tuningTable.cells[i].isFreezed()) {
									continue;
								}
							currentR = tuningTable.cells[i].getUpperRatio();
							if (currentR == null) continue;
							tuningTable.cells[i].setLower(currentR.divide(r));
						}
					}

					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnCalTransposition.setToolTipText("Transposition the Tuning by a entered Ratio at the ratio field");
		btnCalTransposition.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					Ratio r = rtoCalcRatio.getValue();

					if (r != null) {
						Ratio currentR;
						for (int i = 0; i < tuningTable.cells.length; i++) {
							if (!tuningTable.cells[i].isActive() ||
								tuningTable.cells[i].isFreezed()) {
									continue;
								}
							currentR = tuningTable.cells[i].getUpperRatio();
							if (currentR == null) continue;
							tuningTable.cells[i].setLower(currentR.multiply(r));
						}
					}

					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnCalSucc_rel.setToolTipText("calculate from successive ratios a set of ratios showing all intervals relative to the first");
		btnCalSucc_rel.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

					if (tuningTable.cells[0].isActive() && !tuningTable.cells[0].isFreezed())
						tuningTable.cells[0].setLower(tuningTable.cells[0].getUpperRatio());

					for (int i = 1; i < tuningTable.cells.length; i++) {
						if (!tuningTable.cells[i].isActive() ||
							tuningTable.cells[i].isFreezed()) continue;

						//find last cell that is active and not frozen
						int j;
						for (j = i-1; j >= 0; j--)
							if (tuningTable.cells[j].isActive() &&
							!tuningTable.cells[j].isFreezed())
								break;
						Ratio current;
						if (j == -1) {	//this is the first cell
							current = tuningTable.cells[i].getUpperRatio();
						} else {
							current = tuningTable.cells[j].getLowerRatio().multiply(
								tuningTable.cells[i].getUpperRatio()
							);
						}

						if (current.getNum() < 0 || current.getDen() < 0) {	//if overflow happens
							current = new Ratio(0,1);
						}

						current.simplify();
						tuningTable.cells[i].setLower(current);
					}

					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnViwHertz.setToolTipText("Shows frequencies in Hertz for the entire scale");
		btnViwHertz.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					double hz = 0;
					Ratio r;
					for (int i = 0; i < tuningTable.cells.length; i++) {
						if (tuningTable.cells[i].isActive() &&
							!tuningTable.cells[i].isFreezed()) {
							r = tuningTable.cells[i].getUpperRatio();
							if (r == null) continue;
							hz = r.getHz(rightPanel.getTonic());
							tuningTable.cells[i].setLower(hz, "Hz");
						}
					}
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnViwCents.setToolTipText("Shows the interval, in cents, between any scale tone and the 1/1");
		btnViwCents.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					for (int i = 0; i < tuningTable.MAX_CELL; i++) {
						if (tuningTable.cells[i].isActive() &&
							!tuningTable.cells[i].isFreezed())
							tuningTable.cells[i].calRatioToCent();
					}
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnCalOctave.setToolTipText("Reducing the ratios in the BOTTOM HALF into one octave, from 1/1 to 2/1");
		btnCalOctave.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

					for (int i = 0; i < tuningTable.MAX_CELL; i++) {
						if (tuningTable.cells[i].isActive() &&
							!tuningTable.cells[i].isFreezed()) {

							Ratio current = tuningTable.cells[i].getUpperRatio();
							current.normalize();
							tuningTable.cells[i].setLower(current);
						}
					}

					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnViwET_cents.setToolTipText("Show closest ET note and the difference between the scale note and the closest ET note in cents");
		btnViwET_cents.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

					for (int i = 0; i < tuningTable.MAX_CELL; i++) {
						if (tuningTable.cells[i].isActive() &&
							!tuningTable.cells[i].isFreezed()) {

							double cents = tuningTable.cells[i].getUpperRatio().getCents();

							Utilities.NoteDiff note = new Utilities.NoteDiff(cents, 1200);
							tuningTable.cells[i].setLower(note.getDiff(), note.getNote());
						}
					}

					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnViwET_1024.setToolTipText("Show closest ET note and the difference between the note and that ET note in 1024ths");
		btnViwET_1024.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

					for (int i = 0; i < tuningTable.MAX_CELL; i++) {
						if (tuningTable.cells[i].isActive() &&
							!tuningTable.cells[i].isFreezed()) {

							double cents = tuningTable.cells[i].getUpperRatio().getLogValue(1024);

							Utilities.NoteDiff note = new Utilities.NoteDiff(cents, 1024);
							tuningTable.cells[i].setLower(note.getDiff(), note.getNote());
						}
					}

					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnViwET_64ths.setToolTipText("Show closest ET note and the difference between the scale note and the closest ET note in 64ths");
		btnViwET_64ths.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

					for (int i = 0; i < tuningTable.MAX_CELL; i++) {
						if (tuningTable.cells[i].isActive() &&
							!tuningTable.cells[i].isFreezed()) {

							double cents = tuningTable.cells[i].getUpperRatio().getLogValue(768);

							Utilities.NoteDiff note = new Utilities.NoteDiff(cents, 768);
							tuningTable.cells[i].setLower(note.getDiff(), note.getNote());
						}
					}

					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		/**
		calculate the fret position for this tuning
		formula provided by Carl
		fret pos = for fraction x/y, (1-(y/x))* string len
		default string length is set at the top of this file
		note that the ratio is normalized (multiplied to > 1)
		in order to make sense out of this formula

		*/
		btnViwFret.setToolTipText("Calculate the fret position");
		btnViwFret.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

					for (int i = 0; i < tuningTable.MAX_CELL; i++) {
						if (tuningTable.cells[i].isActive() &&
							!tuningTable.cells[i].isFreezed()) {

							Ratio r = tuningTable.cells[i].getUpperRatio();
							if (r == null) continue;
							if (Parameters.autoNormalize) r.normalize();
							r.inverse();
							double length = DEFAULT_STRING_LENGTH;
							try {
								length = Double.parseDouble(txtStringLen.getText());
								if (length <= 0) throw new NumberFormatException();
							} catch (NumberFormatException nfe) {
								length = DEFAULT_STRING_LENGTH;
							} finally {
								txtStringLen.setText(new Double(length).toString());
							}

							// this is the right calculation
							double pos = (double)(r.getDen() - r.getNum()) / (double)r.getDen() * length;
							tuningTable.cells[i].setLower(pos);
						}
					}

					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		/**
		calculate the length of string for producing the tones for the ratio
		string len = for fraction x/y, y/x * string len
		*/
		btnViwStringLen.setToolTipText("Calculates string lengths for all ratios");
		btnViwStringLen.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

					for (int i = 0; i < tuningTable.MAX_CELL; i++) {
						if (tuningTable.cells[i].isActive() &&
							!tuningTable.cells[i].isFreezed()) {

							Ratio r = tuningTable.cells[i].getUpperRatio();
							if (r == null) continue;
							if (Parameters.autoNormalize) r.normalize();
							r.inverse();

							double length = DEFAULT_STRING_LENGTH;
							try {
								length = Double.parseDouble(txtStringLen.getText());
								if (length <= 0) throw new NumberFormatException();
							} catch (NumberFormatException nfe) {
								length = DEFAULT_STRING_LENGTH;
							} finally {
								txtStringLen.setText(new Double(length).toString());
							}

							double pos = (double)r.getNum() / (double)r.getDen() * length;
							tuningTable.cells[i].setLower(pos);

						}
					}

					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnSoundEnable.setToolTipText("Enable Sound");
		btnSoundEnable.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					System.out.println("Sound Off");
					for (int i = 0; i < 60; i++) {
						if(tuningTable.cells[i].isPlayingSound()) {
							tuningTable.cells[i].switchSound();
						}
					}
					/*
                                        // was commented
					Utilities.enableSound(!Parameters.soundEnabled);
					for (int i = 0; i < 60; i++) {
						tuningTable.cells[i].enablePopSound();
					} // end was comment
					// recommented because it's not a useful feature
					*/
				}
			}
		);

		txtCentValue.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent fe) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					Object[] answers = calLogs(
						txtCentValue.getText(),
						LOG_CENT,
						LOG_1024,
						LOG_64th,
						txtError.getText(),
						txtError
					);

					rtoCalPadRatio.setValue((Ratio)answers[0]);
					txt1024Value.setText((String)answers[1]);
					txt64thValue.setText((String)answers[2]);

					//txtError.requestFocus();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		txt1024Value.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent fe) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					Object[] answers = calLogs(
						txt1024Value.getText(),
						LOG_1024,
						LOG_CENT,
						LOG_64th,
						txtError.getText(),
						txtError
					);

					rtoCalPadRatio.setValue((Ratio)answers[0]);
					txtCentValue.setText((String)answers[1]);
					txt64thValue.setText((String)answers[2]);

					//txtError.requestFocus();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		txt64thValue.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent fe) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					Object[] answers = calLogs(
						txt64thValue.getText(),
						LOG_64th,
						LOG_CENT,
						LOG_1024,
						txtError.getText(),
						txtError
					);

					rtoCalcRatio.setValue((Ratio)answers[0]);
					txtCentValue.setText((String)answers[1]);
					txt1024Value.setText((String)answers[2]);

					//txtError.requestFocus();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		rtoCalPadRatio.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent fe) {
				}
			}
		);

		rtoCalPadRatio.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent fe) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

					txtCentValue.setText(Double.toString(rtoCalcRatio.getValue().getCents()));
					txt1024Value.setText(Double.toString(rtoCalcRatio.getValue().getLogValue(LOG_1024)));
					txt64thValue.setText(Double.toString(rtoCalcRatio.getValue().getLogValue(LOG_64th)));

					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		/*rtoCalcRatio1.txtNum.addKeyListener(
			new KeyAdaptor() {
				public void keyTyped(KeyEvent e) {
					if(e.getKeyCode() != KeyEvent.VK_ENTER){
					return;}
					rtoCalcRatio1.txtDum.requestFocus();
				}

		rtoCalcRatio1.txtDen.addKeyListener(
			new KeyAdaptor() {
				public void keyTyped(KeyEvent e) {
					if(e.getKeyCode() != KeyEvent.VK_ENTER){
					return;}
					rtoCalcRatio2.txtNum.requestFocus();
				}*/

		/*rtoCalcRatio1.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent fe) {
					temp1 = rtoCalcRatio1.getValue();

				}
			}
		);

		rtoCalcRatio2.txtNum.addKeyListener(
			new KeyAdaptor() {
				public void keyTyped(KeyEvent e) {
					if(e.getKeyCode() != KeyEvent.VK_ENTER){
					return;}
					rtoCalcRatio2.txtDen.requestFocus();
				}

		rtoCalcRatio2.txtDen.addKeyListener(
			new KeyAdaptor() {
				public void keyTyped(KeyEvent e) {
					if(e.getKeyCode() != KeyEvent.VK_ENTER){
					return;}
					rtoCalcRatio2.txtDen.requestFocus();
				}

		rtoCalcRatio2.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent fe) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					temp2 = rtoCalcRatio1.getValue();
					if ((temp1 != null)||(temp2 != null)){
						rtoCalcRatio3.setValue(temp1.multiply(temp2));}
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);*/
		//rtoCalcRatio3.setEditable(false);
	}

	/**
	return an array of 3 Objects,
	object1 = the calculated ratio within error
	object2 = the String of the value calculated based on the first log base
	object3 = the String of the value calculated based on the second log base

	if the input strings are not in numeric format, the returned values will
	be null and empty strings
	*/
	private Object[] calLogs(String _toCal, int toCalLog, int log1, int log2, String _error, JTextComponent txt_error) {
		Object[] go = new Object[3];
		double toCal;
		double error;

		try {
			toCal = Double.parseDouble(_toCal);
			error = Double.parseDouble(_error);
		} catch(NumberFormatException nfe) {
			clearInputs();
			go[0] = null;
			go[1] = "";
			go[2] = "";
			return go;
		}

		//error cannot be 0, otherwise the result will exceed max value of
		//integer and also will take too long to calculate to the maximum
		//double precision
		if (error == 0) {
			error = 1;
			txt_error.setText("1");
			JOptionPane.showConfirmDialog(
				this,
				"You are not sure you want the error to be 0, let me change it",
				"Information",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE
			);
		}

		Ratio r = new Ratio(toCalLog, toCal, error);
		Double ans1 = new Double(r.getLogValue(log1));
		Double ans2 = new Double(r.getLogValue(log2));

		go[0] = r;
		go[1] = ans1.toString();
		go[2] = ans2.toString();
		return go;
	}

	private void clearInputs() {
		txtCentValue.setText("");
		txt1024Value.setText("");
		txt64thValue.setText("");
		rtoCalcRatio.clear();
	}

	public static void main(String[] args) {
		TuningTable.main(null);
	}
}