//
//  SoundPlayListener.java
//  JJiCalc
//
//  Created by Celeste Hutchins on Thu Jun 26 2003.
//  Copyright (c) 2003 Berkeley Noise. All rights reserved.
//

import java.util.*;

public interface SoundPlayListener extends EventListener{

  public void soundPlay(SoundPlayEvent e);
  public void soundStopped (SoundPlayEvent e);
  public boolean isAlive();

}
