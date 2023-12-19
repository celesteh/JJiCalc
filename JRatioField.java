import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

class JRatioField extends JPanel {
	protected JTextField txtNum = new Utilities.JTextFieldStd();
	protected JTextField txtDen = new Utilities.JTextFieldStd();
	private Ratio value;
	private boolean updated = false;
        private boolean enabled;
        JRatioFieldActionListener listener;

	public JRatioField() {
        
            
		/*
		txtNum.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent fe) {
					txtNum.transferFocus();
				}
			}
		);
		*/
                enabled = false;
		
		txtNum.addKeyListener(
			new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() != KeyEvent.VK_ENTER){
					return;
					}
					txtDen.requestFocus();
				}
			}
		);
				
		txtDen.addKeyListener(
			new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() != KeyEvent.VK_ENTER){
					return;
					}
					txtDen.transferFocus();
				}
			}
		);	
				
		
		final FocusEvent feThis = new FocusEvent(this, FocusEvent.FOCUS_LOST);
                final JRatioField JRFthis = this;

		txtDen.addFocusListener(
                    new FocusAdapter() {
                        public void focusLost(FocusEvent fe){
                            verifyData(true);
                            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(feThis);
                            if (listener != null) {
                                listener.JRatioFieldAction(new JRatioField.JRatioFieldEvent(JRFthis));
                            }
                        }
                    }
		);
		
		/* if txtNum looses focus AND
			txtDen doesn't gain the focus  (this may be optional) AND
			there is a number in txtDen THEN
			we should do the same thing as txtDen.addFocusListener above
		*/
		
		txtNum.addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent fe){
					if (verifyData(false)) { // we don't want to clear the top if the bottom is empty
						Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(feThis);
						if (listener != null) {
							listener.JRatioFieldAction(new JRatioField.JRatioFieldEvent(JRFthis));
						}
					}
				}
			}
		);


		makeAutoSelect(txtNum);
		makeAutoSelect(txtDen);
		
		txtNum.setToolTipText("Numerator of tuning ratio");
		txtDen.setToolTipText("Denominator of tuning ratio");

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(txtNum);
		add(txtDen);
	}
        
        public void addJRatioFieldActionListener(JRatioFieldActionListener jfcl) {
        
            listener = jfcl;
            
        }

	private boolean verifyData(boolean shouldClear) {
		try {
			int num = Integer.parseInt(txtNum.getText());
			int den = Integer.parseInt(txtDen.getText());

			//if (den == 0) throw new NumberFormatException("attempt to divide by 0, change aborted");
			//if (num == 0) den = 1;	//change the base to 1 if the value is 0

			num = Math.abs(num);
			den = Math.abs(den);

			Ratio r = new Ratio(num, den);
			if (Parameters.autoNormalize) {
				r.normalize();
			}
			setValue(r);
			updated = true;
			setEnabled(true);
		} catch (NumberFormatException nfe) {
			if (shouldClear) {
				clear();
			}
		}
		
		return updated;
	}

	/**
	a test to make sure the data is updated,
	if it is updated reset the flag.
	need this because there are 2 focus adapter for
	the lower field, one defined in thie class and
	one in TableCell
	*/
	public boolean updated() {
		if (updated) {
			updated = false;
			return true;
		} else {
			return false;
		}
	}
        
        // we need to do some sort of event thing where if this is enabled
        // or updated or whatever, it calls an action listener appropriately
        
        public void setEnabled(boolean en) {
        
            enabled = en;
        }
        
        public boolean getEnabled() {
            return enabled;
        }
            

	public void clear() {
		txtNum.setText("");
		txtDen.setText("");
		value = null;
	}

	/**
	will change input to absolute value
	*/
	public void setValue(Ratio r){
		if (r == null) return;

		r.absolute();
		value = r;
		txtNum.setText(Integer.toString(r.getNum()));
		txtDen.setText(Integer.toString(r.getDen()));

		txtNum.setCaretPosition(0);
		txtDen.setCaretPosition(0);
	}

	public Ratio getValue(){
		if (value == null) return null;
		return (Ratio)value.clone();
	}

	private void makeAutoSelect(JTextField t) {
		final JTextField jtf = t;
		jtf.addFocusListener(
			new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					jtf.setSelectionStart(0);
					jtf.setSelectionEnd(jtf.getText().length());
				}
			}
		);
	}
        
        public interface JRatioFieldActionListener {
        
          public void JRatioFieldAction(JRatioFieldEvent jrfe);
        }
        
        public class JRatioFieldEvent extends java.util.EventObject {
        
            private JRatioField source;

            public JRatioFieldEvent (JRatioField src) {
                super (src);
                source = src;
            }
            
            public JRatioField getJRatioField() {
            
                return source;
            }
            
        }

	public static void main(String args[]) {
		JFrameC frm = new JFrameC();
		Container cp = frm.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
		cp.add(new JRatioField());
		cp.add(new JRatioField());
		frm.view(200, 100);
	}
}