//
//  SCLFile.java
//  JJiCalc
//
//  Created by Celeste Hutchins on Fri Jun 27 2003.
//  Copyright (c) 2003 Berkeley Noise. All rights reserved.
//
import java.io.*;
import java.util.*;

public class SCLFile  extends Vector implements TuningFile {
    
    public static final String extension = "scl";
    public static final int READFILE = 0;
    public static final int WRITEFILE = 1;


    private int kind;
    private String title;
    //private double tonic;
    private String comment;
	private String fileName;
    boolean EOF;
    TonicBroadcaster tb;
    
    private transient BufferedReader in;
    //DataOutputStream out;
	private transient FileOutputStream out;

	
	private String tableOutput = "";
	private String lineTerminator = "\n";
	private String commentDelim = "!";
	private int cellCount;
	private boolean readyToWrite;
    
    public SCLFile (File sclFile, int kind, TonicBroadcaster tb) throws IOException {
        super();
		readyToWrite = false;
        this.kind = kind;
        if (kind == READFILE) {
            in = new BufferedReader(new FileReader(sclFile));
            EOF = false;
            this.tb = tb;
            readTable();
            in.close();
        } else if (kind == WRITEFILE){
            //out = new DataOutputStream (new FileOutputStream(sclFile));
			out = new FileOutputStream(sclFile);
			this.fileName = sclFile.getName();

        } else {
            IOException e = new IOException();
            throw (e);
        }
        //System.out.println("leaving JICFile constructor");
    }


    public SCLFile (File sclFile) throws IOException {
        super();
        kind = WRITEFILE;
        //out = new DataOutputStream (new FileOutputStream(sclFile));
		out = new FileOutputStream(sclFile);
		this.fileName = sclFile.getName();

    }

    private String nextLine() throws IOException {
        String line = null;
        try {
            line = in.readLine();
        } catch (EOFException e) {
            EOF = true;
        }
        if (line == null) {
            EOF = true;
        }
        return line;
    }

    private void readTable () throws IOException {
        // ok, all tunings implicitly start with 1/1
        this.addElement(new TableCell (1, 1));

        String line = nextLine();
        comment = "";
        
        try {
            boolean foundTitle = false;
            while (line != null && !EOF && !foundTitle) {
            
                if (line != "") {
                    if (line.startsWith("!")) { // comment!
                        // chop off the !
                        line = line.substring(1).trim();
                        comment += line + "\n";
                    } else {
                        // the title!
                        title = line;
                        foundTitle = true;
                    }
                }
                line = nextLine();
            }
            
            // next line is the number of tunings, which we don't care about
            boolean foundNumTunings = false;
            while (line != null && !EOF && !foundNumTunings) {
            
                if (line != "") {
                    if (line.startsWith("!")) { // comment!
                        // chop off the !
                        line = line.substring(1).trim();
                        comment += line + "\n";
                    } else {
                        // number of tunings
                        foundNumTunings = true;
                    }
                }
                line = nextLine();
            }
            
            
            // and now reading in tunings                
            while (line != null && !EOF) {
            
                if (line.startsWith("!")) { // comment!
                    // chop off the !
                    line = line.substring(1).trim();
                    comment += line + "\n";
                } else {
                    line = line.trim();
                    // is it cents?  all ines with dots are cents
                    //if (line.indexOf('.') < line.length()) {
                    if (isCent(line)) {
                        boolean gotCents = false;
                        String token = "";
                        double cents = 0;
                        while (!gotCents && token != null) {
                            StringTokenizer toke = new StringTokenizer (line);
                            try {
                                token = getToken(toke, " \t");
                                cents = Double.valueOf(token).doubleValue();
                                gotCents = true;
                            } catch (NumberFormatException nfe) {
                                gotCents = false;
                            }
                        }
                        Ratio r = new Ratio( Ratio.LOG_CENT, cents, 0.0001);
                        this.addElement(new TableCell(r));

                    } else { 
                    // must be a ratio
                        StringTokenizer toke = new StringTokenizer (line);
                        String ratioString = getToken(toke, " \t");
                        //if (ratioString.indexOf("/") < line.length()) {
                        if( hasSlash( line)) {
                            // not all ratios need contain /
                            Ratio r = new Ratio (ratioString);
                            this.addElement(new TableCell(r));
                        } else {
                            Ratio r = new Ratio (ratioString + "/1");
                            this.addElement(new TableCell(r));
                        }
                    }
                }
                line = nextLine();
            }
        } catch (IOException ioe) {
            throw (ioe);
        } catch (Exception e) {
            IOException e2 = new IOException();
            throw (e2);
        }
    } // end readTable

    private String getToken (StringTokenizer st, String seperator) {
        String token;
        if (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.equalsIgnoreCase(seperator)) {
                // there was no token
                return null;
            } else {
                if (st.hasMoreTokens()) {
                    // strip next seperator
                    st.nextToken();
                }
                return token;
            }
        }
        return null;
    }

    private boolean isCent(String candidate) {
        for (int i = 0; i < candidate.length(); i++) {
            if (candidate.charAt(i) == '.') {
                return true;
            }
        }
        return false;
    }

    private boolean hasSlash(String candidate) {
        for (int i = 0; i < candidate.length(); i++) {
            if (candidate.charAt(i) == '/') {
                return true;
            }
        }
        return false;
    }
    

    public void writeTuningTable(String title, double tonic, String comment) throws IOException {
        if (kind != WRITEFILE) {
            IOException e = new IOException();
            throw (e);
        }
		
		
		
		readyToWrite = true;  //any time we've got new things to write, we're ready
		
		// reset cell count
		cellCount = 0;


        this.title = title + lineTerminator + "!" + lineTerminator;
        //out.writeUTF(title);
        
        // since comments can be multi-line, add bangs to all the lines
		this.comment = "";
		
		if (!( comment == null ||  comment.equals(""))) {
			
			System.out.println(comment);
			
			/*String token ="";
			StringTokenizer st = new StringTokenizer (comment, "\n", true);
			//token = getToken(st, "\n");
			while (st.hasMoreTokens()) {
				//token = getToken(st, "\n");
				token = getToken(st, "\n");
				if (token.equalsIgnoreCase("\n")) {
					this.comment += lineTerminator;
				} else {
					this.comment = "! " + token;
				}
			*/	
				/*
				if (token != null) {
					this.comment += "! " + token + lineTerminator;
				} else {
					this.comment += "!" + lineTerminator;
				}
				*/
			/*}
			
			if (! this.comment.endsWith(lineTerminator)) {
				this.comment += lineTerminator;
			}*/
			
			String [] commentLines = comment.split("\\n");
			for (int i = 0; i < commentLines.length; i++) {
				this.comment += "! " + commentLines[i] + lineTerminator;
			}
			
			System.out.println("this.comment: " + this.comment);
        }
        
        //out.writeUTF(comment);
    
    }
    
    public synchronized void writeTuningTable(String title, double tonic, String comment, TableCell[] cells)
                                                                throws IOException {
                            
        if (kind != WRITEFILE) {
            IOException e = new IOException();
            throw (e);
        }
		
		//System.out.println ("writeTuningTable");
		
		tableOutput = "";
		
        writeTuningTable(title, tonic, comment);
		
        for (int i=0 ; i < cells.length; i++) {
            if (cells[i] != null) {
                if (cells[i].getUpperState() == cells[i].FRACTION) {
					writeTableCell(cells[i].getUpperRatio());
                }
            }
        }
		readyToWrite = true;
		writeOutput();
    }

    public void writeTableCell(String name, int upperState, Ratio upperRatio, 
                                int lowerState, int lowerType, Ratio lowerRatio, double lowerReal,
                                String lowerText) throws IOException {
                                
        if (kind != WRITEFILE) {
            IOException e = new IOException();
            throw (e);
        }
		if (upperState == TableCell.FRACTION && upperRatio != null) {
			writeTableCell(upperRatio);
		}
    }
    
    public void writeTableCell(Ratio upperRatio) throws IOException {
        if (kind != WRITEFILE) {
            IOException e = new IOException();
            throw (e);
        }
		
		// we could be getting all kinds of junk
		
		if (upperRatio != null) {
			String ratio = upperRatio.toString();
			if (ratio != null) {
				if (ratio.length() >= 3 && !(ratio.equals("1/1"))) {
					//out.writeUTF(upperRatio.toString() + "\n");
					tableOutput += ratio + lineTerminator;
					cellCount ++;
					readyToWrite = true;  //any time we've got new things to write, we're ready
				}
			}
		}
    }
    
    public String getTuningTableName() throws IOException {
        if (kind != READFILE) {
            IOException e = new IOException();
            throw (e);
        }

        return title;
    }
    
    public String getTuningTableComment() throws IOException {
        if (kind != READFILE) {
            IOException e = new IOException();
            throw (e);
        }
        return comment;
    }
    
    public double getTonic() throws IOException {
        IOException ioe = new IOException();
        throw (ioe);
    }
    
    public boolean hasMoreTableCells() {
    
        return (size() > 0);
    }
    
    public TableCell getNextTableCell() throws IOException {
            TableCell tc = null;
        
        if (kind != READFILE) {
            IOException e = new IOException();
            throw (e);
        }
        try {
            tc = (TableCell) this.elementAt(0);
            removeElementAt(0);
        } catch (Exception e1) {
            IOException e2 = new IOException();
            throw (e2);
        }
        return tc;
    }

    
    public boolean hasMoreElements() {
        return hasMoreTableCells();
    }
    
    public Object nextElement() {
        try {
            return getNextTableCell();
        } catch (IOException e) {
            return null;
        }
    }
    
    public TableCell[] getTableCellArray() throws IOException {
        this.trimToSize();
        try {
            return (TableCell []) elementData;
        } catch (Exception e) {
            IOException ioe = new IOException ();
            throw (ioe);
        }
    }
	
	private synchronized void writeOutput() {
	
		if (kind == WRITEFILE && readyToWrite) {
		
			//System.out.println("writing file");
		
			String totalOut = "";
			
			if (fileName != null) {
				totalOut = "! " + fileName + lineTerminator;
			}
			
            totalOut += title;
            //totalOut += tonic + tableSeperator;

            if (comment != null) {
                totalOut += comment;
            }
            
            totalOut += "!" + lineTerminator;
			totalOut += cellCount + lineTerminator + "!" + lineTerminator;
			
			totalOut += tableOutput;
            
            PrintWriter pw = new PrintWriter(out);
            pw.println(totalOut);
            pw.flush();
            pw.close();

			readyToWrite = false;
		}
	}
		
    
    public void flush() throws IOException {
        if (kind == WRITEFILE) {
		
			if (readyToWrite) {
				writeOutput();
			}
            out.flush();
        }
    }
    
    
    public void close() throws IOException {
        if (kind == WRITEFILE) {
            out.close();			
        }
    }


    /*
    public static javax.swing.filechooser.FileFilter getFileFilter() {
        SCLFilter f = new SCLFilter();
        //f.addExtension(extension);
        
        return f;
    }
    */





}
