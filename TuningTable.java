import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
//import JJiCalc.*;

class TuningTable extends JPanel implements /*Externalizable,*/ TonicListener, TonicBroadcaster {

	private JTextField txtTitle;
       	private transient JPanel pnlTitle;
	private transient Box pnlTable;
        
	//public final int DEFAUT11 = 440;	//default 1/1 is A440

	private double tonicHz = DEFAUT11;
        
        //JFrame keybdFrame;

	static final int MAX_CELL = 60;
	TableCell[] cells; //60 cells
	private String title;
	private String comment;
	private transient Box l1;
	private transient Box l2;
	private transient Box l3;
	private transient Box l4;
	private transient Box l5;
	private transient Box mainBox;
        private transient Lattice2D lattice;

	private transient JScrollPane sp;
        
        private transient Vector tonicListeners;
        
        public TuningTable (TonicListener tl) {
            this();
            addTonicListener(tl);
        }
        
	public TuningTable() {
        
            title = "Untitled";
            
        
            cells = new TableCell[MAX_CELL];
            for (int i = 0; i < cells.length; i++) {
                cells[i] = new TableCell(this);
            }

            initLayout();

	} // end constructor
        
        
        
        private void initLayout() {
        
        
            txtTitle = new JTextField(title,40);
            pnlTitle = new JPanel();
            pnlTable = new Box(BoxLayout.Y_AXIS);

            l1 = new Box(BoxLayout.X_AXIS);
            l2 = new Box(BoxLayout.X_AXIS);
            l3 = new Box(BoxLayout.X_AXIS);
            l4 = new Box(BoxLayout.X_AXIS);
            l5 = new Box(BoxLayout.X_AXIS);
            mainBox = new Box(BoxLayout.Y_AXIS);
  
        
            sp = new JScrollPane(mainBox,
		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            txtTitle.addFocusListener(
                new FocusAdapter() {
                    public void focusLost(FocusEvent fe) {
                        setTitle(txtTitle.getText());
                    }
                }
            );

            txtTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            txtTitle.setHorizontalAlignment(JTextField.CENTER);
            pnlTitle.add(txtTitle);


            for (int i = 0; i < 12; i++) {
                l1.add(cells[i]);
                l2.add(cells[i+12]);
                l3.add(cells[i+24]);
                l4.add(cells[i+36]);
                l5.add(cells[i+48]);
            }

            mainBox.add(l1);
            mainBox.add(l2);
            mainBox.add(l3);
            mainBox.add(l4);
            mainBox.add(l5);

            //mainBox.setAlignmentX(Component.CENTER_ALIGNMENT);

            pnlTable.add(pnlTitle);
            pnlTable.add(sp);
            setLayout(new BorderLayout());
            add(pnlTable, BorderLayout.CENTER);
                
            JFrame.setDefaultLookAndFeelDecorated(true);


        }
            
        
        
        public void readFile(TuningFile in) {
        
            try {
            
                setTitle(in.getTuningTableName());
                try { 
                    setTonic (in.getTonic());
                } catch (Exception e) {
                  setTonic (DEFAUT11);
                }
                
                setComment(in.getTuningTableComment());
                
                TableCell tc;
                int cellState;
                
                for (int i=0; i < MAX_CELL ; i++) {
                    if (in.hasMoreTableCells()) {
                        tc = in.getNextTableCell();
                        cells[i].overWrite(tc);
                    } else {
                        cells[i].clear();
                    }
                }

                //initLayout();
                Graphics g = this.getGraphics();
                this.paint(g);
                g.dispose();
            } catch (Exception e) {
            }
        
        }
        
        public void writeFile(TuningFile out) {
            try {
                out.writeTuningTable(this.getTitle(), this.getTonic(), this.getComment());
                for (int i=0; i < MAX_CELL ; i++) {
                    cells[i].writeFile(out);
                }
            } catch (IOException e) {
            }
        
        }

	/** return the index of the fundamental cell (supposedly 1/1) */
	public int getFundamental() {
		int fundamental = -1;
                boolean isFound = false;
		for (int i = 0; i < cells.length && !isFound; i++) {
                    if (cells[i].isActive() && !cells[i].isFreezed()) {
                        fundamental = i;
                        isFound = true;
                    }
		}
		return fundamental;
	}

	public String getTitle() {return title;}

	public void setTitle(String t) {
		title = t;
                restoreTitle(t);
	}

	public String getComment() {
		return comment;
	}

        
	public void calculateName(){
            pnlTitle.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
            for (int i = 0; i < cells.length; i++){
                    cells[i].calculateName();    
            }

            pnlTitle.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
        
	
	public void setComment(String t) {
		comment = t;
	}

	public void restoreTitle(String t){
		txtTitle.setText(t);
		}

	/** return the index of the the first lower cell that is a fraction*/
	public int getFundamentalLowerFraction() {
		int fundamental = -1;
                boolean isFound = false;
		for (int i = 0; i < cells.length && !isFound; i++) {
                    if (cells[i].isActive() && !cells[i].isFreezed() &&
                        cells[i].getLowerState() == TableCell.FRACTION) {
				fundamental = i;
				isFound = true;
			}
		}
		return fundamental;
	}
        
        private synchronized void finished() {
            pnlTitle.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        
        public void calRatioCents() {
        
            pnlTitle.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            //find max that is not freezed
            int base = this.getFundamentalLowerFraction();
            if (base == -1) {
                finished();
                System.out.println("no active cell");
                return; 	//there is no active cell in the table
            }

            Ratio baseRatio = cells[base].getUpperRatio();
            double baseCents = baseRatio.getCents();

            double inCents = 0;
            for (int i = 0; i < cells.length; i++) {
                inCents = cells[i].calRatioCents(getTonic(), inCents, baseCents);
            }

            pnlTitle.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        

        public double getTonic () {
            return tonicHz;
        }
        
        public void setTonic (double t) {
            t = Math.abs(t);
            tonicHz = t;
            //System.out.println("new tonic is " + t);
        }
        
        public void tonicChanged( TonicChangedEvent e) {
            this.setTonic(e.getTonic());
        }
        
        public int getTableSize() {
            return MAX_CELL;
        }
        
        public void playSoundAt(int index) {
            cells[index].playSound();
        }
        
        public void stopSoundAt(int index) {
            cells[index].stopSound();
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
            if (tonicListeners != null ) {
                TonicChangedEvent e = new TonicChangedEvent(this, newTonic);
                Enumeration enumer = tonicListeners.elements();
                while (enumer.hasMoreElements()) {
                    ((TonicListener)enumer.nextElement()).tonicChanged(e);
                }
            }
        }
        
        public void sort(){
            // moved from RightPanel
                
            for (int i = 0; i < cells.length; i++) {
                cells[i].clearLower();
            }
            quicksort(cells, 0, cells.length-1);
                
	}

	private void quicksort(TableCell[] a, int l, int r) {
            try {
                int i;
                if (r <= l) return;
                i = partition(a, l, r);
                quicksort(a, l, i-1);
                quicksort(a, i+1, r);
            } catch (Exception e) {
                System.out.println("TuningTable.quicksort: " + e);
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

        public void randomTuning(){
            for (int i = 0; i < cells.length; i++) {
                if (!(cells[i].isFreezed())) {
                    cells[i].setUpper(new Ratio((int)(Math.random()*100), (int)(Math.random()*100)));
		}
            }
	}

        public void DEBUGrandomTuning(){
            int q = 120;
            for (int i = 0; i < cells.length; i++){
                cells[i].setUpper(new Ratio(q--,q--));
            }
	}

        public void eliminateDuplicate() {
            for (int i = 0; i < cells.length-1; i++) {
                for (int j = i+1; j < cells.length; j++) {
                    if (cells[i].compareTo(cells[j]) == 0) {
                        cells[j].clear();
                    }
                }
            }
	}


        public void clearAll() {
            restoreTitle("Untitled");
			
            for (int i = 0; i < cells.length; i++) {
                cells[i].clear();
                //cells[i].setNameBox("");
            }
        }
        
        public void showLattice() {
        
            if (lattice != null ) {
                lattice.setVisible(false);
                lattice.destroy();
            }
            lattice = new Lattice2D(cells, this.getSize());
        }


	public static void main(String args[]) {
		
	}
}
