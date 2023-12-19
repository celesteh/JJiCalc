//
//  JJiCalc.java
//  JJiCalc
//
//  Created by Celeste Hutchins on Thu May 29 2003.
//  Copyright (c) 2003 BerkeleyNoise. All rights reserved.
//
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.lang.*;

public class JJiCalc extends JPanel {

    JFrameC frm;
    TuningTable tab;
    RightPanel rp;
    BottomPanel bp;
    private transient Piano keyBoard;
    JFileChooser fc;


    private String title;
    private String comment;

    //Piano keyBoard;
    //JFrame keybdFrame;
    

    public  JJiCalc () {
        
        frm = new JFrameC();
        JFrame.setDefaultLookAndFeelDecorated(true);

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenD = tk.getScreenSize();
        Dimension windowD = frm.getSize();
		
        tab = new TuningTable();

        //keyBoard = new Piano("Keyboard", tab);

        Container contentPane = frm.getContentPane();
        //KeyLabels keys = new KeyLabels();
        rp = new RightPanel(tab,frm);
        
        tab.addTonicListener(rp);
        rp.addTonicListener(tab);

        Box boxCenter = new Box(BoxLayout.X_AXIS);
        Box boxBottom = new Box(BoxLayout.X_AXIS);
        Box boxLeft  = new Box(BoxLayout.Y_AXIS);

        //Create JScrollPane View
        JScrollPane spBottomPanel = new JScrollPane(boxBottom,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollPane spRightPanel = new JScrollPane(rp,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        frm.setTitle("JI Calc for Java 1.7 beta");
        Menus menuBar = new Menus(frm, rp, tab, this, fc);
        frm.setJMenuBar(menuBar);

        bp = new BottomPanel(rp, tab, frm);
        boxBottom.add(bp);

        contentPane.add(spRightPanel, BorderLayout.EAST);
        contentPane.add(tab, BorderLayout.CENTER);
        contentPane.add(spBottomPanel, BorderLayout.SOUTH);
        if(frm.smallScreen){
            frm.setSize(screenD.width, screenD.height);
        }
        else{
            frm.setSize(750, 730);
        }
        frm.goCenter();
        frm.setVisible(true);
        
        keyBoard = new Piano("Keyboard", tab, false);
        fc = new JFileChooser();
        fc.setCurrentDirectory(new File("./"));


    } // end constructor


    public String getTitle() {return title;}

    public void setTitle(String t) {
        title = t;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String t) {
        comment = t;
    }
    
    //public void restoreTitle(String t){
    //    txtTitle.setText(t);
    //}
    
    public void showKeyBoard() {
        keyBoard.showPiano();
    }
    
    public void hideKeyBoard() {
        keyBoard.hidePiano();
    }

    public void toggleKeyBoard() {
        keyBoard.toggleVisible();
    }


    public class Menus extends JMenuBar{

        JJiCalc myCalc;
        TuningTable tt;
        JFileChooser fc;


        public Menus(JFrameC frm, RightPanel rp, TuningTable tab, JJiCalc calc, JFileChooser fc) {

            myCalc = calc;
            tt = tab;
            this.fc = fc;
    
            /*<<<<<<<<<MENU BAR>>>>>>>>>>>>>>>>>*/
            //Create the menu bar.
            //JMenuBar menuBar;
            JMenu menu = new JMenu();
            JMenu submenu = new JMenu();
            JMenu soundconfig = new JMenu("Wave Form Setting");
            JMenuItem menuItem = new JMenuItem();
            final JMenuItem popSound = new JMenuItem();
            final JMenuItem switchSine = new JMenuItem();
            final JMenuItem switchSquare = new JMenuItem();
            final JMenuItem switchSawtooth = new JMenuItem();
            JMenu importMenu = new JMenu("Import");
            JMenu exportMenu = new JMenu("Export");
		
            //menuBar = new JMenuBar();
            //frm.setJMenuBar(menuBar);

            //Build the File menu.
            menu = new JMenu("File");
            menu.setMnemonic(KeyEvent.VK_A);
            menu.getAccessibleContext().setAccessibleDescription(
                "File Menu->New/Save/Load/Exit");
            //menuBar.add(menu);
            this.add(menu);

	        /*menuItem = new JMenuItem("New",
	                                 KeyEvent.VK_N);
	        //menuItem.setMnemonic(KeyEvent.VK_N); //used constructor instead
	        menuItem.setAccelerator(KeyStroke.getKeyStroke(
	                KeyEvent.VK_N, ActionEvent.ALT_MASK));
	        menuItem.getAccessibleContext().setAccessibleDescription(
	                "call function toClear()");

	        menu.add(menuItem);*/

			//save:- (Daniel modified 19/2/00)
            menuItem = new JMenuItem();
            menuItem.setMnemonic(KeyEvent.VK_S);
            SaveAction sa = new SaveAction(tab, rp, frm, fc);
            menuItem.setText((String) sa.getValue(Action.NAME));
            Icon icon = (Icon) sa.getValue(Action.SMALL_ICON);
            if (icon != null) menuItem.setIcon(icon);
            menuItem.addActionListener(sa);
            menu.add(menuItem);
            //(finished:- Daniel modified 19/2/00)

            menuItem = new JMenuItem();
            menuItem.setMnemonic(KeyEvent.VK_O);
            LoadAction lo = new LoadAction(tab, rp, frm, fc);
            menuItem.setText((String) lo.getValue(Action.NAME));
            Icon iconLoad = (Icon) lo.getValue(Action.SMALL_ICON);
            if (icon != null) menuItem.setIcon(iconLoad);
            menuItem.addActionListener(lo);
            menu.add(menuItem);
            
            menuItem = new JMenuItem();
            menuItem.setMnemonic(KeyEvent.VK_I);
            ImportAction io = new ImportAction(tab, rp, frm, fc);
            menuItem.setText((String) io.getValue(Action.NAME));
            //Icon iconLoad = (Icon) io.getValue(Action.SMALL_ICON);
            //if (icon != null) menuItem.setIcon(iconLoad);
            menuItem.addActionListener(io);
            //menu.add(menuItem);
            importMenu.add(menuItem);
            menu.add(importMenu);
            
            menuItem = new JMenuItem();
            menuItem.setMnemonic(KeyEvent.VK_E);
            SaveAction sa2 = new SaveAction(tab, rp, frm, fc, true);
            menuItem.setText("Ratios Only");
            //Icon iconLoad = (Icon) io.getValue(Action.SMALL_ICON);
            //if (icon != null) menuItem.setIcon(iconLoad);
            menuItem.addActionListener(sa2);
            //menu.add(menuItem);
            exportMenu.add(menuItem);
			
			menuItem = new JMenuItem();
			ExportAction ea2 = new ExportAction(tab, rp, frm, fc);
			menuItem.setText("Scala File");
			menuItem.addActionListener(ea2);
			exportMenu.add(menuItem);
            menu.add(exportMenu);
            


            menuItem = new JMenuItem("Exit");
            menuItem.setMnemonic(KeyEvent.VK_Q);
            menuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                }
            );
            menu.add(menuItem);

	        /*Build Appearance menu
        	menu = new JMenu("Appearance");
        	menu.setMnemonic(KeyEvent.VK_S);
        	menu.getAccessibleContext().setAccessibleDescription(
                "Appearance->Color Select");
                menuBar.add(menu);

                menuItem = new JMenuItem("Color Manager");
	        menuItem.setMnemonic(KeyEvent.VK_C);
	        menuItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent o) {
					JFrame win = new JFrame();
					JColorChooser colorChooser = new JColorChooser();
					JButton btnConfirm = new JButton("Confirm");
					btnConfirm.addActionListener(
						new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								//Pass color value to color setting class
							}
						}
					);
					Container cp = win.getContentPane();
					Box bc = new Box(BoxLayout.Y_AXIS);
					bc.add(colorChooser);
					bc.add(btnConfirm);
					cp.add(bc, BorderLayout.CENTER);
					win.setSize(550, 400);
					win.setLocation(50, 60);
					win.setVisible(true);
				}
			}
		);
	        menu.add(menuItem);
	        	        
	        menuItem = new JMenuItem("Cell Name");
	        menuItem.setMnemonic(KeyEvent.VK_C);
	        menuItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent o) {
					tab.calculateName();
				}
			}
		);
	        menu.add(menuItem);*/

            //Build the Configuration menu.
		
            menu = new JMenu("Configuration");
            menu.setMnemonic(KeyEvent.VK_A);
            menu.getAccessibleContext().setAccessibleDescription(
                "Configuration->AutoNormalize");
            //menuBar.add(menu);
            this.add(menu);
		
            popSound.setText("Enable Autonormalize");
	        
            //menuItem.setMnemonic(KeyEvent.VK_N); //used constructor instead
            popSound.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));

            popSound.getAccessibleContext().setAccessibleDescription(
                "Set AutoNormalize(True/False)");
            popSound.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent o) {
                        Utilities.autoNormalize(!Parameters.autoNormalize);
                        if(Parameters.autoNormalize){
                            popSound.setText("Disable Autonormalize");
                            popSound.setIcon(new ImageIcon("images/tick.gif"));
                        }
                        else{
                            popSound.setText("Enable Autonormalize");
                            popSound.setIcon(new ImageIcon(""));
                        }
                    }
                }
            );
            menu.add(popSound);
		
            switchSine.setText("Sine Wave");
            switchSquare.setText("Square Wave");
            switchSawtooth.setText("Saw Tooth Wave");
		
            if(Parameters.currentWaveForm == Audio.SINE){
                switchSine.setIcon(new ImageIcon("images/tick.gif"));
                switchSquare.setIcon(new ImageIcon(""));
                switchSawtooth.setIcon(new ImageIcon(""));
            }
            else if(Parameters.currentWaveForm == Audio.SQUARE){
                switchSine.setIcon(new ImageIcon(""));
                switchSquare.setIcon(new ImageIcon("images/tick.gif"));
                switchSawtooth.setIcon(new ImageIcon(""));
            }
            else if(Parameters.currentWaveForm == Audio.SAWTOOTH){
                switchSine.setIcon(new ImageIcon(""));
                switchSquare.setIcon(new ImageIcon(""));
                switchSawtooth.setIcon(new ImageIcon("images/tick.gif"));
            }
            menu.add(soundconfig);
            switchSine.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent o) {
                        Utilities.switchWave(Audio.SINE);
                        switchSine.setIcon(new ImageIcon("images/tick.gif"));
                        switchSquare.setIcon(new ImageIcon(""));
                        switchSawtooth.setIcon(new ImageIcon(""));
                    }
                }
            );
            soundconfig.add(switchSine);
            switchSquare.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent o) {
                        Utilities.switchWave(Audio.SQUARE);
                        switchSine.setIcon(new ImageIcon(""));
                        switchSquare.setIcon(new ImageIcon("images/tick.gif"));
                        switchSawtooth.setIcon(new ImageIcon(""));
					
                    }
                }
            );
            soundconfig.add(switchSquare);
            switchSawtooth.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent o) {
                        Utilities.switchWave(Audio.SAWTOOTH);
                        switchSine.setIcon(new ImageIcon(""));
                        switchSquare.setIcon(new ImageIcon(""));
                        switchSawtooth.setIcon(new ImageIcon("images/tick.gif"));
					
                    }
                }
            );
            soundconfig.add(switchSawtooth);
		
            
            //Build the Standard Tuning Menu
            menu = new JMenu("Tuning Table");
            menu.setMnemonic(KeyEvent.VK_S);
            menu.getAccessibleContext().setAccessibleDescription(
                "Standard Tuning -> tunings");
            //menuBar.add(menu);
            this.add(menu);
			
            
            menu = new JMenu("Windows");
            menu.setMnemonic(KeyEvent.VK_W);
            menu.getAccessibleContext().setAccessibleDescription(
                "Windows->Show/Hide Windows");
            this.add(menu);
            
            menuItem = new JMenuItem("KeyBoard");
            menuItem.setMnemonic(KeyEvent.VK_W);
            menuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        myCalc.toggleKeyBoard();
                    }
                }
            );
            menu.add(menuItem);
            
            menuItem = new JMenuItem("Lattice");
            menuItem.setMnemonic(KeyEvent.VK_L);
            menuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        tt.showLattice();
                    }
                }
            );
            menu.add(menuItem);

		
            //Build the Help menu.
            menu = new JMenu("Help");
            menu.setMnemonic(KeyEvent.VK_A);
            menu.getAccessibleContext().setAccessibleDescription(
                "Help->About/Help/Tips");
            //menuBar.add(menu);
            this.add(menu);

                /*menuItem = new JMenuItem("Help");
	        menuItem.setMnemonic(KeyEvent.VK_H);
	        menu.add(menuItem);

	        menuItem = new JMenuItem("Tips");
	        menuItem.setMnemonic(KeyEvent.VK_T);
	        menu.add(menuItem);*/

            menuItem = new JMenuItem("About");
            menuItem.setMnemonic(KeyEvent.VK_A);
            menuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        JOptionPane.showMessageDialog	(null,
                            "JI Calc for Java 1.7 beta 7 January 2004\nJICalc Original Version Programmed by Carter Scholz\nJICalc Java Version Programmed by Daniel Suek and Alvin Cheung, and\nSupervised by Dr. Lydia Ayers\nUpdated by Celeste Hutchins\nSupport: celesteh@users.sourceforge.net",
                            "About JICalc", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            );
            //menu.add(menuItem);
            menu.add(menuItem);
        
        } // end constructor


    }


    class SaveAction extends AbstractAction {

        TuningTable currentTT;
	RightPanel rp;
	//KeyLabels currentKeys;
	JFrame mainFrame;
        JFileChooser fc;
        boolean ratiosOnly;

	public SaveAction(TuningTable currentTT, RightPanel rp, JFrame mainFrame, JFileChooser fc, boolean ratiosOnly) {

            this(currentTT, rp, mainFrame, fc);
            this.ratiosOnly = ratiosOnly;
        }
        

	public SaveAction(TuningTable currentTT, RightPanel rp, JFrame mainFrame, JFileChooser fc) {
          
            putValue(Action.NAME, "Save");
            putValue(Action.SMALL_ICON, new ImageIcon("images/save.gif"));

            this.currentTT = currentTT;
            //this.currentKeys = currentKeys;
            this.rp = rp;
            this.mainFrame = mainFrame;
            this.fc = fc;
            this.ratiosOnly = false;

            //need frame here so that all dialog boxed will appear as if they belogns to the main Jframe
            //which looks better
            //"current" means it belongs to the tuning table (there can be many in future implementation)
            //there will be only 1 right panel regardless of now many tuning tables we've loaded
	}

	public void saveJic(File jicFile) {
        
            try {
                //FileOutputStream out = new FileOutputStream(jicFile);
                //tab.writeStream(out);
                JICFile out;
                if (ratiosOnly) {
                    out = new JICFile (jicFile, JICFile.RATIOSONLY);
                } else {
                    out = new JICFile (jicFile, JICFile.WRITEFILE);
                }
                tab.writeFile(out);
                //PrintWriter pw = new PrintWriter(out, true);
                //pw.println(tab);
                //pw.flush();
                //pw.close();
                out.flush();
                out.close();
                //ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(jicFile));
                //out.writeObject(tab);
                //tab.writeExternal(out);
                //out.close();

            } catch (IOException ioe) {
                JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Error writing to file, please make sure the file and path name of "
                    + jicFile
                    + " is correct.\n\nError code: "
                    + ioe,
                    "Save",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
                    );
                System.out.println("caught exception: " + ioe);
            }
                
	}

	public void actionPerformed(ActionEvent ae) {
            if (fc == null) {
                //FileFilter f = JICFile.getFileFilter();
                fc = new JFileChooser();
                //fc.addChoosableFileFilter(new JICFilter());
                //fc.addChoosableFileFilter(f);
                fc.setCurrentDirectory(new File("."));
            }
            fc.addChoosableFileFilter(new JICFilter());
            int returnVal = fc.showSaveDialog(currentTT);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                //if the file name entered by user is not end with .jic
                //then, concatenate the extension to the file name
                String tmp = file.getPath();
                //System.out.println("1:" + tmp);
                if(!tmp.endsWith(".jic")) {
                    tmp = tmp.concat(".jic");
                    //System.out.println("2:" + tmp);
                    file = null;
                    file = new File(tmp);
                    //System.out.println("3:" + file.getPath());
                }

                if (file.exists() == true ){
                    if (JOptionPane.showConfirmDialog(
                        mainFrame,
                        "File exist already,\nare you sure to over-write the current tuning file?",
                        "Confirm Delete",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    ) == JOptionPane.OK_OPTION) {
                        file.delete();
                        saveJic(file);
                    }
                    } else {
                        saveJic(file);
                    }
		}
	}


    }

    class ExportAction extends AbstractAction {

        TuningTable currentTT;
	RightPanel rp;
	//KeyLabels currentKeys;
	JFrame mainFrame;
        JFileChooser fc;
        boolean ratiosOnly;

	public ExportAction(TuningTable currentTT, RightPanel rp, JFrame mainFrame, JFileChooser fc, boolean ratiosOnly) {

            this(currentTT, rp, mainFrame, fc);
            this.ratiosOnly = ratiosOnly;
        }
        

	public ExportAction(TuningTable currentTT, RightPanel rp, JFrame mainFrame, JFileChooser fc) {
          
            putValue(Action.NAME, "SCL File");
            //putValue(Action.SMALL_ICON, new ImageIcon("images/save.gif"));

            this.currentTT = currentTT;
            //this.currentKeys = currentKeys;
            this.rp = rp;
            this.mainFrame = mainFrame;
            this.fc = fc;
            this.ratiosOnly = false;

            //need frame here so that all dialog boxed will appear as if they belogns to the main Jframe
            //which looks better
            //"current" means it belongs to the tuning table (there can be many in future implementation)
            //there will be only 1 right panel regardless of now many tuning tables we've loaded
	}

	public void saveScl(File sclFile) {
        
            try {
                //FileOutputStream out = new FileOutputStream(jicFile);
                //tab.writeStream(out);
                SCLFile out;
				out = new SCLFile(sclFile);
                tab.writeFile(out);
                out.flush();
                out.close();

            } catch (IOException ioe) {
                JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Error writing to file, please make sure the file and path name of "
                    + sclFile
                    + " is correct.\n\nError code: "
                    + ioe,
                    "Save",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
                    );
                System.out.println("caught exception: " + ioe);
            }
                
	}

	public void actionPerformed(ActionEvent ae) {
            if (fc == null) {
                fc = new JFileChooser();
                fc.setCurrentDirectory(new File("."));
            }
            fc.addChoosableFileFilter(new SCLFilter());
            int returnVal = fc.showSaveDialog(currentTT);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                //if the file name entered by user is not end with .jic
                //then, concatenate the extension to the file name
                String tmp = file.getPath();
                //System.out.println("1:" + tmp);
                if(!tmp.endsWith(".scl")) {
                    tmp = tmp.concat(".scl");
                    //System.out.println("2:" + tmp);
                    file = null;
                    file = new File(tmp);
                    //System.out.println("3:" + file.getPath());
                }

                if (file.exists() == true ){
                    if (JOptionPane.showConfirmDialog(
                        mainFrame,
                        "File exist already,\nare you sure to over-write the current tuning file?",
                        "Confirm Delete",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    ) == JOptionPane.OK_OPTION) {
                        file.delete();
                        saveScl(file);
                    }
                    } else {
                        saveScl(file);
                    }
		}
	}


    }

    
    class ImportAction extends AbstractAction {

	TuningTable currentTT;
	RightPanel rp;
	JFrame mainFrame;
        JFileChooser fc;


	public ImportAction(TuningTable currentTT, RightPanel rp, JFrame mainFrame, JFileChooser fc) {
            putValue(Action.NAME, "Import Scala File");
            putValue(Action.SMALL_ICON, new ImageIcon("images/open.gif"));

            this.currentTT = currentTT;
            this.rp = rp;
            this.mainFrame = mainFrame;
            this.fc = fc;

            //need frame here so that all dialog boxed will appear as if they belogns to the main Jframe
            //which looks better
            //"current" means it belongs to the tuning table (there can be many in future implementation)
            //there will be only 1 right panel regardless of now many tuning tables we've loaded
	}

	public void loadScl(File sclFile) {

            try {
                //ObjectInputStream in = new ObjectInputStream(new FileInputStream(jicFile));
                //currentTT.readExternal(in);
                /*BufferedReader in = new BufferedReader(new FileReader(jicFile));
                currentTT.fromReader(in);*/
                //boolean eof = false;
                //String data = new String();
                //while (!eof) {
                    //try	{
                        //data += in.readLine() + "\n";
                    //} catch (EOFException eofe) {
                        //eof = true;
                    //}
                //}
                //JICFile in = new JICFile(jicFile, JICFile.READFILE);
                SCLFile in = new SCLFile(sclFile, SCLFile.READFILE, currentTT);
                currentTT.readFile(in);
                in.close();
            } catch (IOException ioe) {
                JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Error reading file, please make sure the file and path name of "
                    + sclFile
                    + " is correct.\n\nError code: "
                    + ioe,
                    "Load",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
                );
            /*} catch (ClassNotFoundException cnfe) {
                JOptionPane.showConfirmDialog(
                    mainFrame,
                    "The file is corrupted, I am sorry.\n\n"
                    + jicFile + "\n\n" + cnfe,
                    "Load",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (NumberFormatException nfe) {
                JOptionPane.showConfirmDialog(
                    mainFrame,
                    "The file is corrupted, I am sorry.\n\n"
                    + jicFile + "\n\n" + nfe,
                    "Load",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
                );*/
            }

            
                
	}

	public void actionPerformed(ActionEvent ae) {
            if (fc == null) {
                //FileFilter f = JICFile.getFileFilter();
                fc = new JFileChooser();
                //fc.addChoosableFileFilter(new JICFile.JICFilter());
                //fc.addChoosableFileFilter(f);
                fc.setCurrentDirectory(new File("."));
            }
            //fc.addChoosableFileFilter(new JICFile.JICFilter());
            //fc.addChoosableFileFilter(new SCLFile.getFileFilter());
            fc.addChoosableFileFilter(new SCLFilter());
            int returnVal = fc.showOpenDialog(currentTT);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                currentTT.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                File file = fc.getSelectedFile();
                loadScl(file);
            }
            currentTT.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

    }
    

    class LoadAction extends AbstractAction {

	TuningTable currentTT;
	RightPanel rp;
	JFrame mainFrame;
        JFileChooser fc;


	public LoadAction(TuningTable currentTT, RightPanel rp, JFrame mainFrame, JFileChooser fc) {
            putValue(Action.NAME, "Open");
            putValue(Action.SMALL_ICON, new ImageIcon("images/open.gif"));

            this.currentTT = currentTT;
            this.rp = rp;
            this.mainFrame = mainFrame;
            this.fc = fc;

            //need frame here so that all dialog boxed will appear as if they belogns to the main Jframe
            //which looks better
            //"current" means it belongs to the tuning table (there can be many in future implementation)
            //there will be only 1 right panel regardless of now many tuning tables we've loaded
	}

	public void loadJic(File jicFile) {

            try {
                //ObjectInputStream in = new ObjectInputStream(new FileInputStream(jicFile));
                //currentTT.readExternal(in);
                /*BufferedReader in = new BufferedReader(new FileReader(jicFile));
                currentTT.fromReader(in);*/
                //boolean eof = false;
                //String data = new String();
                //while (!eof) {
                    //try	{
                        //data += in.readLine() + "\n";
                    //} catch (EOFException eofe) {
                        //eof = true;
                    //}
                //}
                JICFile in = new JICFile(jicFile, JICFile.READFILE);
                currentTT.readFile(in);
                in.close();
            } catch (IOException ioe) {
                JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Error reading file, please make sure the file and path name of "
                    + jicFile
                    + " is correct.\n\nError code: "
                    + ioe,
                    "Load",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
                );
            /*} catch (ClassNotFoundException cnfe) {
                JOptionPane.showConfirmDialog(
                    mainFrame,
                    "The file is corrupted, I am sorry.\n\n"
                    + jicFile + "\n\n" + cnfe,
                    "Load",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (NumberFormatException nfe) {
                JOptionPane.showConfirmDialog(
                    mainFrame,
                    "The file is corrupted, I am sorry.\n\n"
                    + jicFile + "\n\n" + nfe,
                    "Load",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
                );*/
            }

            
                
	}

	public void actionPerformed(ActionEvent ae) {
            if (fc == null) {
                //FileFilter f = JICFile.getFileFilter();
                fc = new JFileChooser();
                //fc.addChoosableFileFilter(new JICFile.getFileFilter());
                //fc.addChoosableFileFilter(f);
                fc.setCurrentDirectory(new File("."));
            }
            //fc.addChoosableFileFilter(new JICFile.getFileFilter());
            fc.addChoosableFileFilter(new JICFilter());
            int returnVal = fc.showOpenDialog(currentTT);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                currentTT.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                File file = fc.getSelectedFile();
                loadJic(file);
            }
            currentTT.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

    }

    public static void main (String args[]) {
        // insert code here...
        //System.out.println("Hello World!");
        final JJiCalc calc = new JJiCalc();
    }
}



