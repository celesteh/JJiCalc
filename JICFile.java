//
//  JICFile.java
//  JJiCalc
//
//  Created by Celeste Hutchins on Fri Jun 27 2003.
//  Copyright (c) 2003 Berkeley Noise. All rights reserved.
//

import java.io.*;
import java.util.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;


public class JICFile extends Vector implements TuningFile {

    public static int READFILE = 1;
    public static int WRITEFILE = 0;
    public static int RATIOSONLY = 2;
    private transient BufferedReader in;
    private transient FileOutputStream out;
    private int kind;
    private boolean isRatiosOnly;
    private String name;
    private String comment;
    private double tonic;
    boolean EOF;
    int index;
    String tcSeperator = "\t";
    String tableSeperator = "\n";
    String tableOutput = "";
    public static final String extension = "jic";
    //static JICFilter filter = new JICFilter();
  
    public JICFile (File jicFile, int kind) throws IOException {
        super();
        this.kind = kind;
        if (kind == RATIOSONLY) {
            isRatiosOnly = true;
            this.kind = WRITEFILE;
        } else {
            isRatiosOnly = false;
        }
        if (this.kind == READFILE) {
            in = new BufferedReader(new FileReader(jicFile));
            EOF = false;
            index = 0;
            readTable();
            in.close();
        } else if (this.kind == WRITEFILE){
            out = new FileOutputStream(jicFile);
        } else {
            IOException e = new IOException();
            throw (e);
        }
        //System.out.println("leaving JICFile constructor");
    }

    private void readTable() throws IOException {
        //System.out.println("in readTable()");
        try {
            name = in.readLine();
            tonic = Double.parseDouble (in.readLine());
            for (int i=0; i< TuningTable.MAX_CELL && !EOF ; i++) {
                try{ 
                    readCell(in.readLine());
                } catch (EOFException e) {
                    EOF = true;
                }
                //System.out.println("done with reading cells");	
            }
            
            comment = new String();
            String commentLine;
            
            while (!EOF) {
                try {
                    commentLine = in.readLine();
                    if (commentLine == null) {
                        EOF = true;
                    } else {
                        comment += commentLine;
                    }
                } catch (EOFException e) {
                    EOF = true;
                }
            }

        } catch (IOException ioe) {
            //System.out.println("IOException in readTable()");
            throw (ioe);
        } catch (Exception e) {
            //System.out.println("Exception in readTable()");
            IOException e2 = new IOException();
            throw (e2);
        }
    }
    
    private synchronized void readCell (String tablecell) throws IOException {
        if (tablecell == null ) {
            EOFException eofe = new EOFException();
            throw (eofe);
        }
        
        //System.out.println("in readCell(String)");
        TableCell tc = new TableCell ();
        try {
            int state = 0;
            int type = 0;
            String token = new String();
            StringTokenizer st = new StringTokenizer(tablecell, tcSeperator, true);
            token = getToken(st, tcSeperator);
            if (token != null) {
                tc.setNameBox(token);
            }
            token = getToken(st, tcSeperator);
            if (token != null) {
                state = Integer.parseInt( token);
                //upperState = Integer.parseInt( token);
            }
            token = getToken(st, tcSeperator);
            if (token != null && state == tc.FRACTION) {
                    tc.setUpper( new Ratio (token));
                    token = getToken(st, tcSeperator);
            }
            if (token != null) {
                state = Integer.parseInt( token);
                //lowerState = state;
                token = getToken(st, tcSeperator);
                if (token != null) {
                    if (state == tc.FRACTION) {
                        tc.setLower( new Ratio (token));
                    } else if (state == tc.REAL) {
                        type = Integer.parseInt( token);
                        //lowerType = type;
                        token = getToken(st, tcSeperator);
                        if (token != null) {
                            double lowerValue = Double.parseDouble( token);
                            token = getToken(st, tcSeperator);
                            if (token != null && type == tc.TYPE_TEXT) {
                                tc.setLower(lowerValue, token);
                            } else {
                                tc.setLower(lowerValue);
                            }
                        }
                    } else if (state == tc.TYPE_TEXT ) {
                            //lower[1].setText(token);
                    }
                }
            }
        /*} catch (IOException ioe) {
            throw (ioe);*/
        } catch (Exception e1) {
            //System.out.println("exception in readCell()");
            IOException e2 = new IOException ();
            throw (e2);
        } finally {
            this.insertElementAt(tc, index);
            incrementIndex();
        }
    } // end ReadCell


    private synchronized void incrementIndex() {
        index++;
    }
    
    private synchronized void decrementIndex() {
        index--;
    }

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
                    String foo = st.nextToken();
                }                
                return token;
            }
        }
        return null;
    }


    
    public String getTuningTableName() throws IOException {
        //System.out.println("getting name");
        if (kind != READFILE) {
            IOException e = new IOException();
            throw (e);
        }
        return name;
    }
    
    public String getTuningTableComment() throws IOException {
        //System.out.println("getting comment");
        if (kind != READFILE) {
            IOException e = new IOException();
            throw (e);
        }
        return comment;
    }

    public double getTonic() throws IOException {
        //System.out.println("getting tonic");
        if (kind != READFILE) {
            IOException e = new IOException();
            throw (e);
        }
        return tonic;
    }
    
    public synchronized boolean hasMoreTableCells() {
        return (size() > 0);
    }
    
    public synchronized TableCell getNextTableCell() throws IOException {
        //System.out.println("getting a tablecell");
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
        //System.out.println("getting a tablecell array");
        this.trimToSize();
        try {
            return (TableCell []) elementData;
        } catch (Exception e) {
            IOException ioe = new IOException ();
            throw (ioe);
        }
    }


    public void writeTuningTable(String title, double tonic, String comment) throws IOException {
        if (kind != WRITEFILE) {
            IOException e = new IOException();
            throw (e);
        }
        this.name = title;
        this.tonic = tonic;
        this.comment = comment;
        
    }
        
    public void writeTuningTable(String title, double tonic, String comment, TableCell[] cells)
                                                                throws IOException {
        if (kind != WRITEFILE) {
            IOException e = new IOException();
            throw (e);
        }
        writeTuningTable(title, tonic, comment);
        for (int i = 0; i< cells.length; i++) {
            writeTableCell(cells[i].getUpperRatio());
        }
    }

    public void writeTableCell(String name, int upperState, Ratio upperRatio, 
                                int lowerState, int lowerType, Ratio lowerRatio, 
                                double lowerReal, String lowerText) 
                                                        throws IOException {
                                                        
        if (kind != WRITEFILE) {
            IOException e = new IOException();
            throw (e);
        }
        
        if (isRatiosOnly == true) {
            writeTableCell(upperRatio);
        } else {
            tableOutput += name + tcSeperator;
            tableOutput += upperState + tcSeperator;
            if (upperState != TableCell.INACTIVE && upperRatio!= null) {
                tableOutput += upperRatio.toString();
            }
            tableOutput +=  tcSeperator;
            tableOutput += lowerState + tcSeperator;
            if (lowerState == TableCell.FRACTION) {
                if (lowerRatio != null) {
                    tableOutput += lowerRatio.toString();
                }
                tableOutput += tcSeperator;
            } else if (lowerState == TableCell.REAL) {
                tableOutput += lowerType + tcSeperator;
                //if (type == TYPE_NUMBER) {
                tableOutput += lowerReal + tcSeperator;
                if (lowerType == TableCell.TYPE_TEXT) {
                    tableOutput += lowerText + tcSeperator;
                }
            } else if (lowerState == TableCell.TYPE_TEXT) {
                tableOutput += lowerText + tcSeperator;
            }
            tableOutput += tableSeperator;
        } // end else
    }

    
    public void writeTableCell(Ratio upperRatio) throws IOException {
        if (kind != WRITEFILE) {
            IOException e = new IOException();
            throw (e);
        }
        
        // no name
        tableOutput += tcSeperator;
        if (upperRatio != null) {
            tableOutput += TableCell.FRACTION + tcSeperator;
            tableOutput += upperRatio.toString() + tcSeperator;
            // no lower ratio
            tableOutput += TableCell.INACTIVE + tcSeperator;
        } else {
            tableOutput += tcSeperator;
            tableOutput += TableCell.INACTIVE + tcSeperator;
        }
        tableOutput += tableSeperator;
    }


    public void close() throws IOException {
        if (kind == WRITEFILE) {
        
            String totalOut;
            totalOut = name + tableSeperator;
            totalOut += tonic + tableSeperator;

            // this has tableSeperators appended on it
            totalOut += tableOutput;
            
            if (comment != null) {
                totalOut += comment;
            }
            
            totalOut += tableSeperator;
            
            PrintWriter pw = new PrintWriter(out);
            pw.println(totalOut);
            pw.flush();
            pw.close();
        }
    }

    public void flush () throws IOException {}
    
    public static String getExtension() {
        return extension;
    }
    
    /*
    public static javax.swing.filechooser.FileFilter getFileFilter() {
        //JICFilter f = new JICFilter();
        //f.addExtension(extension);
        
        return filter;
    }
    */
    
    
    


}
