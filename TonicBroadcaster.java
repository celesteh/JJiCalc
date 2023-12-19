//
//  TonicBroadcaster.java
//  JJiCalc
//
//  Created by Celeste Hutchins on Thu Jun 19 2003.
//  Copyright (c) 2003 Berkley Noise. All rights reserved.
//

public interface TonicBroadcaster {

    public final int DEFAUT11 = 440;	//default 1/1 is A440
    public void addTonicListener(TonicListener tl);
    public void removeTonicListener(TonicListener tl);
    public double getTonic();
}
