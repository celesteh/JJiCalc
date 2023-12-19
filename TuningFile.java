//
//  TuningFile.java
//  JJiCalc
//
//  Created by Celeste Hutchins on Fri Jun 27 2003.
//  Copyright (c) 2003 Berkeley Noise. All rights reserved.
//

import java.io.*;
import java.util.*;

public interface TuningFile extends Enumeration {

    //public String getExtension();
    

    public void writeTuningTable(String title, double tonic, String comment) throws IOException;
    public void writeTuningTable(String title, double tonic, String comment, TableCell[] cells)
                                                                throws IOException;

    public void writeTableCell(String name, int upperState, Ratio upperRatio, 
                                int lowerState, int lowerType, Ratio lowerRatio, double lowerReal,
                                String lowerText) throws IOException;
    public void writeTableCell(Ratio upperRatio) throws IOException;
    
    public String getTuningTableName() throws IOException;
    public String getTuningTableComment() throws IOException;
    public double getTonic() throws IOException;
    public boolean hasMoreTableCells();
    public TableCell getNextTableCell() throws IOException;
    public TableCell[] getTableCellArray() throws IOException;
    
    public void flush() throws IOException;
    public void close() throws IOException;

}
