//
//  Piano.java
//  TuningTable
//
//  Created by Christi Denton on Mon May 26 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//
/*
 * @(#)MidiSynth.java	1.15	99/12/03
 *
 * Copyright (c) 1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.sound.midi.*;
import java.util.Vector;

import java.io.File;
import java.io.IOException;


    /**
     * Piano renders black & white keys and plays the notes for a MIDI 
     * channel.  
     */
    class Piano extends JPanel implements MouseListener {

        Vector blackKeys = new Vector();
        Vector keys = new Vector();
        Vector whiteKeys = new Vector();
        Key prevKey;
        final int kw = 16, kh = 80;
        JCheckBox mouseOverCB = new JCheckBox("mouseOver", true);
        final Color jfcBlue = new Color(204, 204, 255);
        public final static int ON = 0, OFF = 1;
        JFrame myFrame;
        boolean play;
        TuningTable tab;


        public Piano (String title, TuningTable tcs) {
            this(title, tcs, true);
        }

        public Piano(String title, TuningTable tcs, boolean visible) {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(42*kw, kh+1));
            //int transpose = 24;
            //int transpose = 0;  
            int whiteIDs[] = { 0, 2, 4, 5, 7, 9, 11 }; 
            Key key;
            play = true;
            tab = tcs;
            
            keys.setSize(tcs.getTableSize() + 1);
          
            boolean flag = true;
            for (int i = 0, x = 0; i < 6 && flag; i++) {
                for (int j = 0; j < 7 && flag ; j++, x += kw) {
                    int keyNum = i * 12 + whiteIDs[j];
                    if ( keyNum < tcs.getTableSize()) {
                        key = new Key(x, 0, kw, kh, keyNum, tcs);
                        whiteKeys.add(key);
                        try {
                            keys.setElementAt(key, keyNum);
                        } catch (Exception e) {
                            System.err.println(e);
                            keys.addElement(key);
                        }
                    } else {
                        flag = false;
                    }
                }
            }
            setPreferredSize(new Dimension(whiteKeys.size()*kw, kh+1));
            
            //make black keys
            flag = true;
            for (int i = 0, x = 0; i < 6 && flag; i++, x += kw) {
                int keyNum = i * 12;
                int thisKey = keyNum + 1;
                if (thisKey < tcs.getTableSize()) {
                    key = new Key((x += kw)-4, 0, kw/2, kh/2, thisKey, tcs);
                    blackKeys.add(key);
                    try {
                        keys.setElementAt(key, thisKey);
                    } catch (Exception e) {
                        System.err.println(e);
                        keys.addElement(key);
                    }
                } else {
                    flag = false;
                }
                
                thisKey = keyNum+3;
                if (flag && thisKey < tcs.getTableSize()) {
                    key = new Key((x += kw)-4, 0, kw/2, kh/2, thisKey, tcs);
                    blackKeys.add(key);
                    try {
                        keys.setElementAt(key, thisKey);
                    } catch (Exception e) {
                        System.err.println(e);
                        keys.addElement(key);
                    }
                } else {
                    flag = false;
                }
                
                x += kw;
                thisKey = keyNum+6;
                if (flag && thisKey < tcs.getTableSize()) {
                    key = new Key((x += kw)-4, 0, kw/2, kh/2, thisKey, tcs);
                    blackKeys.add(key);
                    try {
                        keys.setElementAt(key, thisKey);
                    } catch (Exception e) {
                        System.err.println(e);
                        keys.addElement(key);
                    }
                } else {
                    flag = false;
                }
                
                thisKey = keyNum+8;
                if (flag && thisKey < tcs.getTableSize()) {
                    key = new Key((x += kw)-4, 0, kw/2, kh/2, thisKey, tcs);
                    blackKeys.add(key);
                    try {
                        keys.setElementAt(key, thisKey);
                    } catch (Exception e) {
                        System.err.println(e);
                        keys.addElement(key);
                    }
                } else {
                    flag = false;
                }

                thisKey = keyNum+10;
                if (flag && thisKey < tcs.getTableSize()) {
                    key = new Key((x += kw)-4, 0, kw/2, kh/2, thisKey, tcs);
                    blackKeys.add(key);
                    try {
                        keys.setElementAt(key, thisKey);
                    } catch (Exception e) {
                        System.err.println(e);
                        keys.addElement(key);
                    }
                } else {
                    flag = false;
                }
                    
            }
            //keys.addAll(blackKeys);
            //keys.addAll(whiteKeys);

            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    if (play && isShowing() && isVisible()) {
                        if (mouseOverCB.isSelected()) {
                            Key key = getKey(e.getPoint());
                            if (prevKey != null && prevKey != key) {
                                prevKey.off();
                            } 
                            if (key != null && prevKey != key) {
                                key.on();
                            }
                            prevKey = key;
                            repaint();
                        }
                    }
                }
            });
            addMouseListener(this);
            
            JFrame.setDefaultLookAndFeelDecorated(true);
            myFrame = new JFrame(title);
            myFrame.getContentPane().add(this, BorderLayout.CENTER);
            
            myFrame.pack();
            //myFrame.setVisible(true);
            if (visible) {
                showPiano();
            } else {
                hidePiano();
            }
            
        } // end constructor

        public void mousePressed(MouseEvent e) { 
            if (play) {
                prevKey = getKey(e.getPoint());
                if (prevKey != null) {
                    prevKey.on();
                    repaint();
                }
            }
        }
        public void mouseReleased(MouseEvent e) { 
            if (prevKey != null) {
                prevKey.off();
                repaint();
            }
        }
        public void mouseExited(MouseEvent e) { 
            if (prevKey != null) {
                prevKey.off();
                repaint();
                prevKey = null;
            }
        }
        public void mouseClicked(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }


        public Key getKey(Point point) {
            for (int i = 0; i < keys.size(); i++) {
                if (((Key) keys.get(i)).contains(point)) {
                    return (Key) keys.get(i);
                }
            }
            return null;
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Dimension d = getSize();

            g2.setBackground(getBackground());
            g2.clearRect(0, 0, d.width, d.height);

            g2.setColor(Color.white);
            g2.fillRect(0, 0, 42*kw, kh);

            for (int i = 0; i < whiteKeys.size(); i++) {
                Key key = (Key) whiteKeys.get(i);
                if (key.isNoteOn()) {
                    g2.setColor(jfcBlue);
                    g2.fill(key);
                }
                g2.setColor(Color.black);
                g2.draw(key);
            }
            for (int i = 0; i < blackKeys.size(); i++) {
                Key key = (Key) blackKeys.get(i);
                if (key.isNoteOn()) {
                    g2.setColor(jfcBlue);
                    g2.fill(key);
                    g2.setColor(Color.black);
                    g2.draw(key);
                } else {
                    g2.setColor(Color.black);
                    g2.fill(key);
                }
            }
        }
        
        public Vector getKeys() {
            return keys;
        }
        
        public void toggleVisible() {
            if (this.isVisible()){
                this.showPiano();
            } else {
                this.hidePiano();
            }
        }
                
                
        public void showPiano() {
            myFrame.setVisible(true);
            play = true;
        }
        
        public void hidePiano() {
            myFrame.setVisible(false);
            play = false;
        }
        
    } // End class Piano



    /**
     * Black and white keys or notes on the piano.
     */
    class Key extends Rectangle {
        int noteState = Piano.OFF;
        int kNum;
        TuningTable tc;
        public Key(int x, int y, int width, int height, int num, TuningTable tt) {
            super(x, y, width, height);
            kNum = num;
            tc = tt;
        }
        public boolean isNoteOn() {
            return noteState == Piano.ON;
        }
        public void on() {
            setNoteState(Piano.ON);
            tc.playSoundAt(kNum);
            //cc.channel.noteOn(kNum, cc.velocity);
            //if (record) {
                //createShortEvent(NOTEON, kNum);
            //}
        }
        public void off() {
            setNoteState(Piano.OFF);
            tc.stopSoundAt(kNum);
            /*
            cc.channel.noteOff(kNum, cc.velocity);
            if (record) {
                createShortEvent(NOTEOFF, kNum);
            }
            */
        }
        public void setNoteState(int state) {
            noteState = state;
        }
        
        /*
        public void setTableCell(TableCell my_cell) {
            tc = my_cell;
        }
        
        public TableCell getTableCell(){
            return tc;
        }
        */
    } // End class Key
