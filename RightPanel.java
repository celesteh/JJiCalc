import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.*;
import java.io.*;

/**
known bugs:
1)
if any cell is freezed then 1 sort might not
produce the correct sorted result, test it
with the random scale button (right click on cell to freeze)

suspect source is the freezed cell is used as the
target cell in quick sort algorithm
*/

class RightPanel extends JPanel implements TonicListener, TonicBroadcaster {

	//private final int DEFAUT11 = 440;	//default 1/1 is A440

	private double tonicHz = /*TuningTable.*/DEFAUT11;
	private TuningTable tuningTable;
	//private KeyLabels keys;
	private JFrame currentFrame;
	private JPanel pnlUFreq = new JPanel();
	private Box pnlUInnerFreq = new Box(BoxLayout.Y_AXIS);
	
	private JPanel pnlBFreq = new JPanel(new GridLayout(1, 1));
	private Box pnlBInnerFreq = new Box(BoxLayout.Y_AXIS);
	
	private Box pnlFreq = new Box(BoxLayout.Y_AXIS);
        
        private transient Vector tonicListeners;
        
	
		
	//JButton btnRotateLeft = new JButton("<-");
	//JButton btnRotateRight = new JButton("->");
	//JButton btnKeys = new JButton("Keys");
	
	/*pnlFreq.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder()
			)
		);*/
	JButton btnCalculateName = new JButton("Calculate Key Name");
	JButton btnSort = new JButton("Sort");
	JButton btnRandom = new JButton("Random Tuning");
	JButton btnClear = new JButton("Clear");
	JButton btnLattice = new JButton("Lattice");
	JButton btnConvert = new JButton("Update Base", new ImageIcon("images/upArrow.gif"));
	JButton btnComments = new JButton("Comments");
	JButton btnDuplicate = new JButton("Clear Duplicates");
	JTextField txtTonicHz;
        JRatioField rtoRatio = new JRatioField();
	JLabel lblTonicHz = new JLabel("1/1 freq");
	JLabel lblBlank = new JLabel("");
        
        public RightPanel(TuningTable ttable, JFrame cd, TonicListener tl) {
            this(ttable, cd);
            this.addTonicListener(tl);
        }

	public RightPanel(TuningTable ttable,JFrame cf) {
		tuningTable = ttable;
		currentFrame = cf;
                
                txtTonicHz = new Utilities.JTextFieldStd(Double.toString(tuningTable.getTonic()));

		rtoRatio.setMaximumSize(new Dimension(Parameters.TEXT_FIELD_DIMENSION.width*2, Short.MAX_VALUE));
		txtTonicHz.setMaximumSize(new Dimension(Parameters.TEXT_FIELD_DIMENSION.width*2,
			Parameters.TEXT_FIELD_DIMENSION.height));

		initializeButtions();

		//JPanel panKeys = new JPanel();
		//panKeys.setLayout(new BoxLayout(panKeys, BoxLayout.X_AXIS));

		//panKeys.add(btnRotateLeft);
		//panKeys.add(btnRotateRight);
		//panKeys.add(btnKeys);
		
		pnlBInnerFreq.add(lblTonicHz);
		pnlBInnerFreq.add(txtTonicHz);
		pnlBInnerFreq.add(pnlUFreq);
		pnlBInnerFreq.add(btnConvert);
		pnlBInnerFreq.add(rtoRatio);
		pnlBFreq.add(pnlBInnerFreq);
		pnlBFreq.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"base frequency:", TitledBorder.CENTER, TitledBorder.TOP
			)
		);
		
		//pnlFreq.add(pnlBFreq);
		
		JPanel topKeys = new JPanel();
		topKeys.setLayout(new GridLayout(7,1));
		topKeys.add(btnCalculateName);
		topKeys.add(btnSort);
		topKeys.add(btnClear);
		topKeys.add(btnRandom);
		topKeys.add(btnLattice);
		topKeys.add(btnDuplicate);
		topKeys.add(btnComments);
		
		/*JPanel midKeys = new JPanel();
		midKeys.setLayout(new GridLayout(1,1));
		midKeys.add(btnConvert);*/

		JPanel lowKeys = new JPanel();
		lowKeys.setLayout(new GridLayout(1,1));
		lowKeys.add(lblBlank);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		//add(panKeys);
		add(topKeys);
		add(pnlBFreq);
		add(lowKeys);

	}

	/** get the current base ferquency */
	public double getTonic() {
		//return tonicHz;
            return tuningTable.getTonic();
	}

	/** set the base frequency to the absolute value of the argument */
	public void setTonic(double t) {
		t = Math.abs(t);
		tonicHz = t;
		txtTonicHz.setText(Double.toString(tonicHz));
	}

	private void lattice(){
            /*
            //System.out.println("spawning lattice");
		JFrame latticeFrame = new JFrame();
		Lattice2D latticePanel = new Lattice2D(tuningTable.cells);
            //System.out.println("lattive newed");
		latticeFrame.getContentPane().add(latticePanel);
		latticeFrame.setSize(400,300);
		latticeFrame.setTitle("Lattice");
		latticeFrame.setVisible(true);
            */
            tuningTable.showLattice();
	}
	private void comments(){
		JCommentFrame txtComment = new JCommentFrame(tuningTable, this);
		txtComment.view(300,400);
		btnComments.setEnabled(false);
	}

	void finishedComments() {
		btnComments.setEnabled(true);
	}


        /*
	public void sort(){
            // this should not be in RightPanel, this should be in tuningTable
                
		for (int i = 0; i < tuningTable.cells.length; i++) {
			tuningTable.cells[i].clearLower();
		}
		quicksort(tuningTable.cells, 0, tuningTable.cells.length-1);
                
            
	}
        */
        /*
	private void quicksort(TableCell[] a, int l, int r) {
		try {
			int i;
			if (r <= l) return;
			i = partition(a, l, r);
			quicksort(a, l, i-1);
			quicksort(a, i+1, r);
		} catch (Exception e) {
			System.out.println("RightPanel.quicksort: " + e);
		}
	}
	private int partition(TableCell[] a, int l, int r) {
		int i = l-1;
		int j = r;
		TableCell v = a[r];
		while(true) {
			while (a[++i].compareTo(v) < 0);
			while (v.compareTo(a[--j]) < 0) if (j==l) break;
			if (i >= j) break;
			TableCell.swap(a[i], a[j]);
		}

		TableCell.swap(a[i], a[r]);
		return i;
	}
        */
        /*
	public void randomTuning(){
		for (int i = 0; i < tuningTable.cells.length; i++) {
			if (tuningTable.cells[i].isFreezed()) continue;
			tuningTable.cells[i].setUpper(new Ratio((int)(Math.random()*100), (int)(Math.random()*100)));
		}
	}
        */

        /*
	public void DEBUGrandomTuning(){
		int q = 120;
		for (int i = 0; i < 60; i++){
			tuningTable.cells[i].setUpper(new Ratio(q--,q--));
		}
	}
        */

        /*
	public void eliminateDuplicate() {
		for (int i = 0; i < tuningTable.cells.length-1; i++) {
			for (int j = i+1; j < tuningTable.cells.length; j++) {
				if (tuningTable.cells[i].compareTo(tuningTable.cells[j]) == 0) {
					tuningTable.cells[j].clear();
				}
			}
		}
	}
        */

	private void toClear() {
		if (JOptionPane.showConfirmDialog(
				this,
				"Are you sure you want to delete all data on this tuning table?",
				"Confirm Delete",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE
			) == JOptionPane.OK_OPTION) {
			/*
			tuningTable.restoreTitle("Untitled");
			
			for (int i = 0; i < tuningTable.cells.length; i++) {
				tuningTable.cells[i].clear();
				tuningTable.cells[i].setNameBox("");
			}
                        */
                        tuningTable.clearAll();
		}
	}
	
	private void initializeButtions() {
            txtTonicHz.addFocusListener(
                new FocusAdapter() {
                    public void focusLost(FocusEvent fe) {
                        currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                        try {
                            tonicHz = Double.parseDouble(txtTonicHz.getText());

                        } catch (NumberFormatException nfe) {
                            //tonicHz = /*TuningTable.*/DEFAUT11;
                            //txtTonicHz.setText(tonicHz);
                            //setTonic(tonicHz);
                            setTonic(DEFAUT11);
                            alertTonicChanged(getTonic());
                            //System.out.println("Number format exception: " + nfe);
                        }
                        //tuningTable.setTonic(tonicHz);
                        alertTonicChanged(tonicHz);
                        currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            );

		/*btnRotateLeft.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					keys.rotateLeft();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnRotateRight.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					keys.rotateRight();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnKeys.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					keys.writeStandard();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);*/
		btnLattice.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					lattice();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);
		
		btnCalculateName.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					tuningTable.calculateName();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);
		
		
		btnSort.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					tuningTable.sort();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnRandom.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					tuningTable.randomTuning();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnDuplicate.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					tuningTable.eliminateDuplicate();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnClear.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					toClear();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnComments.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					comments();
					currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		);

		btnConvert.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					currentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					try {

						Ratio r = rtoRatio.getValue();
						if (r == null) return;

						r.absolute();

						double baseHz = Double.parseDouble(txtTonicHz.getText());
						//tonicHz = baseHz * r.getNum() / r.getDen();
						//txtTonicHz.setText(Double.toString(tonicHz));
                                                //setTonic(tonicHz);
                                                //double ratioHz = r.getHz(baseHz);
                                                tonicHz = r.getHz(baseHz);
                                                setTonic(tonicHz);
                                                alertTonicChanged(tonicHz);

					} catch (NumberFormatException nfe) {
						rtoRatio.clear();
						//txtTonicHz.setText(Double.toString(TuningTable.DEFAUT11));
						//tonicHz = TuningTable.DEFAUT11;
                                                //setTonic(/*TuningTable.*/DEFAUT11);
                                                //alertTonicChanged(tonicHz);

					} finally {
                                                //tuningTable.setTonic(tonicHz);
                                                //alertTonicChanged(tonicHz);
						currentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}
		);

		//align all components to the center
		//btnRotateLeft.setAlignmentX(Component.CENTER_ALIGNMENT);
		//btnRotateRight.setAlignmentX(Component.CENTER_ALIGNMENT);
		//btnKeys.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnSort.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnRandom.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnClear.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnLattice.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnConvert.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnComments.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnDuplicate.setAlignmentX(Component.CENTER_ALIGNMENT);
		txtTonicHz.setAlignmentX(Component.CENTER_ALIGNMENT);
		rtoRatio.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTonicHz.setAlignmentX(Component.CENTER_ALIGNMENT);
	}
        
        public void addTonicListener(TonicListener tl) {
            if (tl != this) {
                if (tonicListeners == null) {
                    tonicListeners = new Vector();
                }
                //don't add yourself!!!
                tonicListeners.addElement(tl);
            }
        }
        
        public void removeTonicListener(TonicListener tl) {
            if (tonicListeners != null) {
                tonicListeners.removeElement(tl);
            }
        }
        
        protected void alertTonicChanged( double newTonic) {
            if (tonicListeners != null) {
                TonicChangedEvent e = new TonicChangedEvent(this, newTonic);
                Enumeration enum = tonicListeners.elements();
                while (enum.hasMoreElements()) {
                    ((TonicListener)enum.nextElement()).tonicChanged(e);
                }
            }
            //System.out.println("Right panel tonic is " + newTonic);
        }
        
        public void tonicChanged(TonicChangedEvent e) {
            this.setTonic(e.getTonic());
        }



	class JTextFieldFixedSize extends JTextField {
		public JTextFieldFixedSize() {
			super();
			setMaximumSize(new Dimension(Parameters.TEXT_FIELD_DIMENSION.width*2,
				Parameters.TEXT_FIELD_DIMENSION.height));
		}

		public JTextFieldFixedSize(String s) {
			super(s);
			setMaximumSize(new Dimension(Parameters.TEXT_FIELD_DIMENSION.width*2,
				Parameters.TEXT_FIELD_DIMENSION.height));
		}
	}

	public static void main(String[] args) {
			TuningTable.main(null);
	}
}
