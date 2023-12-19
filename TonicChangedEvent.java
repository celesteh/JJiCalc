//
//  TonicChangedEvent.java
//  JJiCalc
//
//  Created by Celeste Hutchins on Wed Jun 18 2003.
//  Copyright (c) 2003 Berkeley Noise. All rights reserved.
//

import java.util.*;

public class TonicChangedEvent extends EventObject {
    protected double tonic;
        
    public TonicChangedEvent(Object source, double newTonic) {
        super(source);
        tonic = newTonic;
    }
    
    public double getTonic() {
        return tonic;
    }
    

    
}

