import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class TableCellNameBox extends JTextField{
	
	public TableCellNameBox(String s, int n){
		super(s, n);
		setPreferredSize(Parameters.TEXT_FIELD_DIMENSION);
	}
	
	public void setName(String s){
		setText(s);
	}
	
	public void clear(){
		setText("");
		
	}
	
}

