import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

//create a static 2D picture of the lattice representing
//a group of table cell

//lattics3D is planned after lattice finishes

class Lattice2D extends JPanel implements ComponentListener, MouseMotionListener,
                                MouseListener,  SoundPlayListener {
    /*plan:
    1 get the whole array of table cells
    2 extract the data and draw it on the panel
    3 if user click on the notes switch sound
    4 build a legend
    5 if user click on the legend switch sound
    */

    JFrame latticeFrame;
    LatticeRatio[] lr;
    int maxX = 0, minX = 0, maxY = 0, minY = 0;
    boolean coordsCalced;
    Point oneOverOnePoint;
    LatticeRatio oneOverOneLR;
    private transient boolean alive = true;
    
    public Lattice2D(TableCell[] tc, Dimension size) {
        this (tc);
        //latticeFrame.setSize(size);
        //calcCoords();
    }

    public Lattice2D(TableCell[] tc) {
        
        latticeFrame = new JFrame();
        oneOverOneLR = null;
        oneOverOnePoint = null;
        Vector vlr;
            
        //input and scan ratios (only get cells with cent value [0, 1200))
        //decompose ratios into prime base sequences
        //calcaulate the coordinate of each ratio
        //transform model coordinate to screen coordinate
        //draw related lines
                /*
		int validCount = 0;
		for (int i = 0; i < tc.length; i++) {
			if (isValidTableCell(tc[i])) {
				validCount++;
			}
		}

		lr = new LatticeRatio[validCount];
                */
                
        vlr = new Vector (tc.length);
                 
        for (int i = 0, j = 0; i < tc.length; i++) {
            // look for 1/1
            Ratio r = tc[i].getUpperRatio();
            if (r != null ) {
                if (r.getNum() == 1 && r.getDen() == 1) {
                    oneOverOneLR = new LatticeRatio(tc[i]);
                }
            }
            if (!isValidTableCell(tc[i])) continue;
            LatticeRatio lattRat = new LatticeRatio (tc[i]);
            boolean dupe = false;
            for (int k = 0; k < j; k++) {
                        
                LatticeRatio temp = (LatticeRatio)vlr.elementAt(k);
                boolean thisDupe = lattRat.isDuplicate(temp);
                if (thisDupe ) {
                    temp.addSoundPlayListener(tc[i]);
                    tc[i].addSoundPlayListener(temp);
                    lattRat.addSoundPlayListener(tc[i]);
                    tc[i].addSoundPlayListener(lattRat);
                    dupe = true;
                }
            }
            if (!dupe) {
                if (lattRat.modelX > (double)maxX) maxX = (int)lattRat.modelX;
                if (lattRat.modelX < (double)minX) minX = (int)lattRat.modelX;
                if (lattRat.modelY > (double)maxY) maxY = (int)lattRat.modelY;
                if (lattRat.modelY < (double)minY) minY = (int)lattRat.modelY;

			//create margin spaces
			//maxX++; maxY++; minX--; minY--;

                lattRat.addSoundPlayListener(this);
                tc[i].addSoundPlayListener(this);
                vlr.insertElementAt(lattRat, j);
                j++;
            } else {
                lattRat.destroy();
            }
        }
        int arraySize = vlr.size();
        lr = new LatticeRatio [ arraySize ] ;
        for (int i = 0; i < arraySize; i++ ) {
            lr[i] = (LatticeRatio) vlr.elementAt(i);
        }
        
        coordsCalced = false;
                //oneOverOne = null;
        this.addComponentListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        latticeFrame.getContentPane().add(this);
        latticeFrame.setSize(400,300);
        latticeFrame.setTitle("Lattice");
        latticeFrame.setVisible(true);
    } // end constructor
        
    public void mouseClicked (MouseEvent me) {
        if (isShowing() && isVisible()) {
            Point p = me.getPoint();
            if (oneOverOneLR != null) {
                oneOverOneLR.clickSound(p);
            }
            for (int i = 0; i < lr.length ; i++) {
                lr[i].clickSound(p);
            }
        }
    }
    public void mouseEntered (MouseEvent me) {}
    public void mouseExited (MouseEvent me) {}
    public void mousePressed (MouseEvent me) {}
    public void mouseReleased (MouseEvent me) {}
        
    public void mouseMoved( MouseEvent me) {
        if (isShowing() && isVisible()) {
            Point p = me.getPoint();
            if (oneOverOneLR != null) {
                oneOverOneLR.checkOverlap(p);
            }
            for (int i = 0; i < lr.length ; i++) {
                lr[i].checkOverlap(p);
            }
            Graphics g = this.getGraphics();
            this.paintComponent(g);
            g.dispose();
        }
    }
    public void mouseDragged( MouseEvent me) {
        if (isShowing() && isVisible()) {
            Point p = me.getPoint();
            if (oneOverOneLR != null) {
                oneOverOneLR.checkOverlap(p);
            }
            for (int i = 0; i < lr.length ; i++) {
                lr[i].checkOverlap(p);
            }
            Graphics g = this.getGraphics();
            this.paintComponent(g);
            g.dispose();
        }
    }

    public synchronized void soundPlay (SoundPlayEvent e) {
        Graphics g = this.getGraphics();
        this.paintComponent(g);
        g.dispose();
    }
        
    public synchronized void soundStopped (SoundPlayEvent e) {
        Graphics g = this.getGraphics();
        this.paintComponent(g);
        g.dispose();
    }
    
    public synchronized boolean isAlive() {
        return alive;
    }

                           
    public synchronized void componentResized(ComponentEvent e) {
                        //calcOne();
                        //calcCoords();
        coordsCalced = false;
    }
                                        
    public synchronized void componentShown(ComponentEvent e) {
                        //calcOne();
                        //calcCoords();
        coordsCalced = false;
        
    }
        
    public synchronized void componentHidden(ComponentEvent e) {
            this.destroy();
    }
        
    public synchronized void componentMoved(ComponentEvent e) {}


    private boolean isValidTableCell(TableCell tc) {
        if (tc.isActive()) {
            if (tc.getUpperRatio().getValue() > 1 && 
                tc.getUpperRatio().getValue() < 2) {
                
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
            //return (tc.isActive() && tc.getUpperRatio().getValue() > 1 && tc.getUpperRatio().getValue() < 2);
    }

    private void calcOne() {
        Dimension winSize = getSize();
        calcOne(winSize);
    }
    
    private void calcOne(Dimension winSize) {
        int x = (int)((double)winSize.getWidth() / 2);
        int y = (int)((double)winSize.getHeight() / 2);
        oneOverOnePoint = new Point(x,y);
    }
        

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //there is no valid cells
        //also is needed to avoid division by zero

        Dimension winSize = getSize();
        int realHeight = Math.max(maxY, minY * -1) * 2;
        int realWidth = Math.max(maxX, minX * -1) * 2;

        if (realWidth == 0 || realHeight == 0) return;

        double heightRatio = (double) winSize.getHeight() * .7 / (double) realHeight;
        double widthRatio = (double) winSize.getWidth() * .7 / (double) realWidth;
                
        if (coordsCalced == false) {
            calcCoords(widthRatio, heightRatio, winSize);
        }


            
        for (int i = 0; i < lr.length; i++) {
            Point pointI = lr[i].getCoords();
            if (pointI == null) {
                lr[i].calculateCoordinates(widthRatio, heightRatio, winSize);
                pointI = lr[i].getCoords();
            }
                    
                    // how about drawing lines before numbers
			//draw lines

            for (int j = i+1; j < lr.length; j++) {

                if (isRelated(lr[i], lr[j])) {
                    Point pointJ = lr[j].getCoords();
                    if (pointJ == null) {
                        lr[j].calculateCoordinates(widthRatio, heightRatio, winSize);
                        pointJ = lr[j].getCoords();
                    }
                    g.drawLine(pointI.x, pointI.y, pointJ.x, pointJ.y);
                }
            }


			//draw lines from 1/1
            if (lr[i].isSingleTerm()) {
                g.drawLine(pointI.x, pointI.y, oneOverOnePoint.x, oneOverOnePoint.y);
            }
                    
                    
            lr[i].draw(g);
                    
        }
                
        if (oneOverOnePoint == null && oneOverOneLR == null) {
            calcOne();
            g.drawString("1/1", oneOverOnePoint.x, oneOverOnePoint.y);

        } else if (oneOverOneLR != null) {
            oneOverOneLR.calculateCoordinates(0, 0, winSize);
            oneOverOneLR.draw(g);
        } else if (oneOverOnePoint != null) {
            g.drawString("1/1", oneOverOnePoint.x, oneOverOnePoint.y);
        }

    } // end drawComponent
        

        
    private synchronized void calcCoords() {
        Dimension winSize = getSize();
        int realHeight = Math.max(maxY, minY * -1) * 2;
        int realWidth = Math.max(maxX, minX * -1) * 2;
        double heightRatio = (double) winSize.getHeight() * .7 / (double) realHeight;
        double widthRatio = (double) winSize.getWidth() * .7 / (double) realWidth;
        calcCoords(widthRatio, heightRatio, winSize);
    }
        
    private synchronized void calcCoords(double widthRatio, double heightRatio, 
                                            Dimension winSize) {
        calcOne(winSize);
        for (int i=0; i <lr.length; i++) {
            lr[i].calculateCoordinates(widthRatio, heightRatio, winSize);
        }
        coordsCalced = true; // this must be changed if the window resizes
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
        latticeFrame.setVisible(b);
    }

    private boolean isRelated(LatticeRatio lr1, LatticeRatio lr2) {

		//must know which one is larger
        if (lr1.primeBase.size() > lr2.primeBase.size()) {
            LatticeRatio temp = lr1;
            lr1 = lr2;
            lr2 = temp;
        }
        int size1 = lr1.primeBase.size();
        int size2 = lr2.primeBase.size();


        boolean matched;
        int powerDiff = 0;
		//case where the two power and primeBase exactly match
        if (size1 == size2) {
            for (int i = 0; i < size1; i++) {
                matched = false;
                for (int j = 0; j < size2; j++) {
                    if (((Integer) lr1.primeBase.get(i)).intValue() == ((Integer)lr2.primeBase.get(j)).intValue()) {
                        powerDiff += Math.abs(((Integer)lr1.power.get(i)).intValue() - ((Integer)lr2.power.get(j)).intValue());
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    return false;
                }
            }
            if (powerDiff == 1) {
                return true;
            } else {
                return false;
            }
		//case where the power and base 1 has 1 more term than power and base 2,
		//and the total difference in raised power is 1
        } else if (size2 - size1 == 1) {
            boolean found[] = new boolean[size2];
            for (int i = 0; i < size2; i++) found[i] = false;

            for (int i = 0; i < size2; i++) {
                for (int j = 0; j < size1; j++) {
                    if (((Integer)lr1.primeBase.get(j)).intValue() == ((Integer)lr2.primeBase.get(i)).intValue() &&
                        ((Integer)lr1.power.get(j)).intValue() == ((Integer)lr2.power.get(i)).intValue()) {
                            found[i] = true;
                            break;
                        }
                }
            }

            int unfound = -1;
            int unfoundCount = 0;
            for (int i = 0; i < found.length; i++) {
                if (!found[i]) {
                    unfound = i;
                    unfoundCount++;
                }
            }

            if (unfound == -1) {
				/*
				//the below codes are for debug purpose only
				System.out.println("error: unfound = -1");
				System.out.println("bigger:");
				for (int i = 0; i < size2; i++) {
					System.out.print(((Integer)lr2.primeBase.get(i)).intValue() + "^" + ((Integer)lr2.power.get(i)).intValue() + ", ");
				}
				System.out.println("\nSmaller:");
				for (int i = 0; i < size1; i++) {
					System.out.print(((Integer)lr1.primeBase.get(i)).intValue() + "^" + ((Integer)lr1.power.get(i)).intValue() + ", ");
				}
				System.out.println("\nunfound: ");
				for (int i = 0; i < found.length; i++) {
					System.out.print(found[i] + ", ");
				}
				System.out.println("\nend error meg");
				*/
                return false;
            }

            if (unfoundCount > 1) {
                return false;
            }

            if (Math.abs(((Integer)lr2.power.get(unfound)).intValue()) == 1) {
				//System.out.println("passed: case 2");
                return true;
            } else {
				//System.out.println("reject: case 2a");
                return false;
            }
        } else {
			//System.out.println("reject: case 2b");
            return false;
        }
    } //end isRelated
    
    public synchronized void destroy () {
    
        alive = false;
        for (int i = 0; i < lr.length; i++) {
            lr[i].removeSoundPlayListener(this);
            lr[i].destroy();
        }
        if (oneOverOneLR != null) {
            oneOverOneLR.removeSoundPlayListener(this);
        }
    
    }


    class LatticeRatio implements SoundPlayListener {
        TableCell tableCell;	//which table cell this info belongs to
        Ratio ratio;  // don't need the cell, just the ratio
        Vector primeBase;
        Vector power;
        double modelX = 0;	//model level x coordinate
        double modelY = 0;	//model level y coordinate
        private Point coords; // actual x & Y coordinates
        private Rectangle activeArea;
        private boolean knowActiveArea = false;
        boolean isActive = false;
        private Vector listeners;
        protected Color rectColor;
        protected Color playColor = Color.gray;
        protected Color stopColor = Color.white;
        private transient boolean alive = true;
                
		//get a table cell and try to make
		//will not check the vaildity of the cell, this must be done
		//before the creation of this object
        public LatticeRatio(TableCell tc) {
            tableCell = tc;
            ratio = tableCell.getUpperRatio();
            try {
                primeBase = new Vector(2, 5);
                power = new Vector(2, 5);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            }

            decompose();
            genModelCoordinate();
            rectColor = stopColor;
            tableCell.addSoundPlayListener(this);
        } // end constructor

                
        public boolean isDuplicate(LatticeRatio lr) {
            Ratio r = lr.getRatio();
            if (r != null && ratio != null) {
                if (r.getDen() == ratio.getDen() &&
                    r.getNum() == ratio.getNum()) {
                    return true;
                }
            }
            return false;
        }
                
        protected Ratio getRatio() {
            return ratio;
        }
        
        public synchronized void addSoundPlayListener (SoundPlayListener spl) {
            if (listeners == null) {
                listeners = new Vector();
            }
            listeners.addElement(spl);
        }
        
        public synchronized void removeSoundPlayListener (SoundPlayListener spl) {
            if (listeners != null) {
                listeners.removeElement(spl);
            }
        }
        
        private void alertSoundStopped() {
                    if (listeners != null) {
                SoundPlayEvent e = new SoundPlayEvent(this, SoundPlayEvent.STOPPED);
                Enumeration enum = listeners.elements();
                while (enum.hasMoreElements()) {
                    SoundPlayListener spl = (SoundPlayListener) enum.nextElement();
                    if (spl != null && spl.isAlive()) {
                        spl.soundStopped(e);
                    } else {
                        // um can we remove null elements?
                        removeSoundPlayListener(spl);
                    }
                }
            }
        }
        
        private void alertSoundPlay() {
            if (listeners != null) {
                SoundPlayEvent e = new SoundPlayEvent(this, SoundPlayEvent.PLAY);
                Enumeration enum = listeners.elements();
                while (enum.hasMoreElements()) {
                    SoundPlayListener spl = (SoundPlayListener) enum.nextElement();
                    if (spl != null && spl.isAlive()) {
                        spl.soundPlay(e);
                    } else {
                        // um can we remove null elements?
                        removeSoundPlayListener(spl);
                    }
                }
            }
        }
        
        
        public synchronized void soundPlay (SoundPlayEvent e) {
            rectColor = playColor;
        }
        
        public synchronized void soundStopped (SoundPlayEvent e) {
            rectColor = stopColor;
            alertSoundStopped(); // if a user starts a sound in the lattice and stops it in
                                // the tuning table, we need to let everyone know it stopped.
        }
        
        public synchronized boolean isAlive() {
            return alive;
        }

        public synchronized void destroy() {
            alive = false;
            tableCell.removeSoundPlayListener(this);
            listeners = null;
        }
                
        private void decompose() {
			//goal: to find out all the prime factors and their degree
			//Ratio ratio = tableCell.getUpperRatio();
			//int num = r.getNum();
			//int den = r.getDen();

            genPrimeList(ratio.getNum(), 0);
            genPrimeList(ratio.getDen(), 1);

        }
                
        public synchronized void calculateCoordinates (double widthRatio, 
                                double heightRatio, Dimension winSize) {
                
            int x = (int)(modelX * widthRatio)  + 
                    (int)((double)winSize.getWidth() / 2);
            int y = (int)(modelY * heightRatio) + 
                    (int)((double)winSize.getHeight() / 2);
            coords = new Point (x, y);
            knowActiveArea = false;
        }
                
        public int getXCoord() {
            return coords.x;
        }
                
        public int getYCoord() {
            return coords.y;
        }

        public Point getCoords() {
            return coords;
        }
                
        private synchronized void computeActiveArea(Graphics g) {

            FontMetrics fm = g.getFontMetrics();
            int strWidth = fm.stringWidth(ratio.toString());
            int strHeight = fm.getHeight();
            activeArea = new Rectangle(coords.x /*- strWidth / 2*/, 
                                    coords.y - strHeight, 
                                    strWidth, strHeight);
            knowActiveArea = true;
        }

                
        public void draw(Graphics g, double widthRatio, 
                                double heightRatio, Dimension winSize) {
                
            int x = (int)(modelX * widthRatio)  + 
                    (int)((double)winSize.getWidth() / 2);
            int y = (int)(modelY * heightRatio) + 
                    (int)((double)winSize.getHeight() / 2);
            coords = new Point(x,y);
            computeActiveArea(g);
            draw(g);
        }

        public void draw(Graphics g) {
            if (knowActiveArea == false || activeArea == null) {
                computeActiveArea(g);
            }
            Color c = g.getColor();
            g.setColor(rectColor);
            g.fillRect(activeArea.x, activeArea.y, activeArea.width,
                                activeArea.height);
            g.setColor(c);
            g.drawString(ratio.toString(), coords.x, coords.y);
            if (isActive) {
                        //Color c = g.getColor();
                g.setColor(Color.red);
                g.drawRect(activeArea.x, activeArea.y, activeArea.width,
                                activeArea.height);
                g.setColor(c);
            }
        }
                    
        private boolean isSingleTerm() {
            return (primeBase.size() == 1);
        }


		//generate the list of prime base and corresponding power
		//type 0 = positive power
		//type 1 = negative power
        void genPrimeList(int number, int type) {
            int current = 1;	//primelist at position 1 is the number 3
            int nextPrime;
            int degree = 0;
            boolean foundBase;

			//we do not consider prime factors of 2
            while (number % 2 == 0) {
                number /= 2;
            }

            while (number != 1) {
                foundBase = false;

            if (Utilities.largestPrime() < number) {
                Utilities.registerPrime(Utilities.largestPrime() + 100);
            }

            nextPrime = ((Integer)Utilities.primeList.get(current++)).intValue();
				//num will be within reach of prime number list,
				//because all ratios are simplified before entering
				//here, at that time the numbers are already registered
				//bu Utilities.registerPrime();
            while (number % nextPrime == 0) {
                number /= nextPrime;
                degree++;
                foundBase = true;
            }

            if (foundBase) {
                primeBase.add(new Integer(nextPrime));
                if (type == 1) degree *= -1;	//type 1 means negative degees
                power.add(new Integer(degree));
                degree = 0;
            }
        }
    }
                
    public boolean checkOverlap (Point p) {

        if (! knowActiveArea || activeArea == null) {
            activeArea = null;
            isActive = false;

        } else if (activeArea.contains(p)) {
            isActive = true;

        } else {
            isActive = false;
        }
        return isActive;
    }
                
                public synchronized void clickSound ( Point p) {
                    if (checkOverlap(p)) {
                        tableCell.switchSound();
                        if (tableCell.isPlayingSound()) {
                            rectColor = playColor;
                            alertSoundPlay();
                        } else {
                            rectColor = stopColor;
                            alertSoundStopped();
                        }
                    }
                }
                

		//generate the model level coordinate of the ratio
		void genModelCoordinate() {
			double theta; //angle from 12 o'clock in radians
			double xDisplacement;
			double yDisplacement;
			int hypotenuse;
			int degree;

			//for each number in the prime base list
			for (int i = 0; i < primeBase.size(); i++) {

				int baseNum = ((Integer)primeBase.get(i)).intValue();
				hypotenuse = baseNum;
				int baseDen = 1;
				while ((double)baseNum / (double)baseDen >= 2) {
					baseDen *= 2;
				}
                                Ratio r = new Ratio(baseNum, baseDen);
				theta = r.getLogValue()
						/ 1200 * 2 * Math.PI;
				theta = (theta * -1); //the angle starts form 6 o'clock going anticlockwise

				xDisplacement = hypotenuse * Math.sin(theta);
				yDisplacement = hypotenuse * Math.cos(theta);

				degree = ((Integer)power.get(i)).intValue();
				//go opposite if the degree is negative
				if (degree < 0) {
					xDisplacement *= -1;
					yDisplacement *= -1;
				}

				for (int j = 0; j < Math.abs(degree); j++) {
					modelX += xDisplacement;
					modelY += yDisplacement;
				}
			}
		}

	}

	public static void main(String[] args) {
		TuningTable.main(null);
	}
}