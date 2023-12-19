//
//  SCLFilter.java
//  JJiCalc
//
//  Created by Celeste Hutchins on Sat Jun 28 2003.
//  Copyright (c) 2003 Berkeley Noise. All rights reserved.
//

import java.io.*;

public class SCLFilter extends javax.swing.filechooser.FileFilter {
    
    // Accept all directories and all gif, jpg, or tiff files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String fileExtension = Utils.getExtension(f);
        if (fileExtension != null) {
            //if (extension.equals(Utils.jic)) {
            if (fileExtension.equals(SCLFile.extension)){
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }
    
    // The description of this filter
    public String getDescription() {
        return "Scala Save File (*.scl)";
    }
}
