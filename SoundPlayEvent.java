//
//  SoundPlayEvent.java
//  JJiCalc
//
//  Created by Celeste Hutchins on Thu Jun 26 2003.
//  Copyright (c) 2003 Berkeley Noise. All rights reserved.
//

import java.util.*;

public class SoundPlayEvent extends EventObject {

  public static final int STOPPED = 0;
  public static final int PLAY = 1;
  public static final int ERR = -1;
  protected int eventType;
  
    public SoundPlayEvent( Object source, int type) {
  
        super (source);
        if (type == STOPPED || type == PLAY) {
            eventType = type;
        } else {
            eventType = ERR;
        }
    }


    public int getEventType() {
        return eventType;
    }

}
