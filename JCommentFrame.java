import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class JCommentFrame extends JFrame{

	private JTextArea txtFComment = new JTextArea(5,20);
	private JButton oK = new JButton("OK");
	private JButton canCel = new JButton("Cancel");
	private TuningTable tuningT;
	private RightPanel rp;
		
	JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	
	public JCommentFrame(TuningTable tuningT, RightPanel rp) {
		
		pnlButton.add(oK); 
		pnlButton.add(canCel);
		this.tuningT = tuningT;
		this.rp = rp;
		
		Container containPane = getContentPane();
		JScrollPane sp = new JScrollPane(txtFComment,
						ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		containPane.add(sp, BorderLayout.CENTER);
		containPane.add(pnlButton, BorderLayout.SOUTH);
		
		initializeBtn();
	}
	
	public int getDefaultCloseOperation(){
		return WindowConstants.DISPOSE_ON_CLOSE;
		}

	private void initializeBtn(){
		txtFComment.setWrapStyleWord(true);
		txtFComment.setText(tuningT.getComment());
		
		oK.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					tuningT.setComment(txtFComment.getText());
					setVisible(false);
					rp.finishedComments();
				}	
			}	
		);
	
		canCel.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					setVisible(false);
					rp.finishedComments();
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