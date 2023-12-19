import javax.swing.*;
import java.util.*;

//create a static 2D picture of the lattice representing
//a group of table cell

//lattics3D is planned after lattice finishes

class Lattice2D extends JPanel {
	/*plan:
	1 get the whole array of table cells
	2 extract the data and draw it on the panel
	3 if user click on the notes switch sound
	4 build a legend
	5 if user click on the legend switch sound
	*/

	public void Lattice2D(TableCell[] tc) {
		//rule out all the cells that is above and below the octave
		//find all prime base
		//find the angle, thickness and length of each prime base

		//draw the lattice:
			//analyse all ratios
				//define data structure for ratios
			//create phantom ratios
			//relate  ratios
	}

	//note: cannot accept table cell == null and cell not active
	//otherwise it will generate error: hint for debug!!
	class LatticeRatio {
		TableCell tableCell;	//which table cell this info belongs to
		Vector primeBase;
		Vector power;

		public void LatticeRatio(TableCell tc) {
			tableCell = tc;
			try {
				primeBase = new Vector(2, 5);
				power = new Vector(2, 5);
			} catch (IllegalArgumentException iae) {
				iae.printStackTrace();
			}

			analyze();
		}

		void analyze() {
			//goal find out all the prime factors and their degree
			Ratio r = tc.getUpperRatio();
			int num = r.getNum();
			int den = r.getDen();
			r = null;

			int temp = num;
			int i = 0;
			while (temp > 1 && i < Ratio.MAX_PRIME) {
				int p = 0;
				while (temp % Ratio.primeList[i] == 0 && temp != 1) {
					temp /= Ratio.primeList[i];
					p++;
				}
				primeBase.add(new Integer(Ratio.primeList[i]));
				power.add(new Integer(p));
				i++;
			}

			if (i >= 100) {

			}
		}
	}
}