import javax.swing.JFrame;
import java.awt.event.*;
import java.awt.*;

public class JFrameC extends JFrame{
	public boolean smallScreen = false;
	public JFrameC() {
		
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					//Prompt for save to each tab of Tuning Table
					System.exit(0);
				}
			}
		);
	}

	public void goCenter() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenD = tk.getScreenSize();
		Dimension windowD = getSize();
		int x = (screenD.width - windowD.width)/2;
		int y = (screenD.height - windowD.height)/2;
		if ( x <0 || y< 0 || x>screenD.width || y>screenD.height){
			setLocation(0,0);
			smallScreen = true;
		}
		else{
			setLocation(x,y);
		}
	}

	public void view(int x, int y) {
		setSize(x,y);
		goCenter();
		setVisible(true);
	}
}