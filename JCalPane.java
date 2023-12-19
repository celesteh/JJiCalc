import javax.swing.JFrame;
import java.awt.event.*;
import java.awt.*;

public class JCalPane extends JFrame{
	public JCalPane() {	
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
	}
	
	public void goCenter() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenD = tk.getScreenSize();
		Dimension windowD = getSize();

		setLocation((screenD.width - windowD.width)/2, (screenD.height - windowD.height)/2);
	}
	
	public void view(int x, int y) {
		setSize(x,y);
		goCenter();
		setVisible(true);
	}
}