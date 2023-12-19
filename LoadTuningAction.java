import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;

class LoadTuningAction extends AbstractAction {

	TuningTable currentTT;
	RightPanel rp;
	JFrame mainFrame;
	String tuning;
	File file;
        
	public LoadTuningAction(TuningTable currentTT, RightPanel rp, JFrame mainFrame, String tuning, File file) {
		
		putValue(Action.NAME, tuning);
		
		this.currentTT = currentTT;
		this.rp = rp;
		this.mainFrame = mainFrame;
		this.file = file;
                //need frame here so that all dialog boxed will appear as if they belogns to the main Jframe
		//which looks better
		//"current" means it belongs to the tuning table (there can be many in future implementation)
		//there will be only 1 right panel regardless of now many tuning tables we've loaded
	}

	public void loadJic(File jicFile) {
		try {
                    /*
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(jicFile));
			JIState jis = (JIState) in.readObject();
			in.close();

			//at this point a valid file is read into jis
			currentTT.restoreTT(jis.data, jis.cellName);
			currentTT.setTitle(jis.title);
			currentTT.restoreTitle(jis.title);
			currentTT.setComment(jis.comment);
			rp.setTonic(jis.baseHz);
                        */
                    //BufferedReader in = new BufferedReader(new FileReader(jicFile));
                    //currentTT.fromReader(in);
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
			);*/
		}
	}

	public void actionPerformed(ActionEvent ae) {
			loadJic(this.file);
	}

}