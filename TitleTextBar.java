import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class TitleTextBar extends JTextField{
	
	public TitleTextBar(String s, int n){
		super(s, n);
	}
		
	public float setAlignmentX(){
		return Component.CENTER_ALIGNMENT;
	}	
	
}