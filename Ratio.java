import java.io.*;
import java.math.*;
import java.util.*;

/* This class has been modified to handle all the ratio math.
   So Calculator.java is no more
   Celeste 5/29/2003
*/

class Ratio implements Cloneable, Serializable{
	public int getNum() {return numerator;}
	public int getDen() {return denominator;}
	public double getValue() {return (double)numerator / (double)denominator;}
	private int numerator = 1;
	private int denominator = 1;
        
        public static final int LOG_CENT = 1200;
	public static final int LOG_OCT1024 = 1024;
	public static final int LOG_SIXTYFOURTH = 768;

	private static final double DEFAULT_LOG_ERROR = 1.0; //default error for calculating fractions

	//daniel change started 5/3/2000
	//deleted lines
	//daniel change ended 5/3/2000

	public Ratio(int n, int d) {
		numerator = n;
		denominator = d == 0 ? 1 : d;
		simplify();
	}

	/**
	using this constructor will not simplify the inputs
	in some situation this can improve the performance
	since simplify is in time n, can take long time if
	number is big enough.
	*/
	public Ratio(int n, int d, boolean b) {
		numerator = n;
		denominator = d == 0 ? 1 : d;
	}
        
        public Ratio (String ratio) throws NumberFormatException {
            StringTokenizer st = new StringTokenizer (ratio, "/", false);
            if (st.countTokens() != 2) {
                NumberFormatException e = new NumberFormatException (ratio);
                throw(e);
            } else {
                numerator =  Integer.parseInt(st.nextToken());
                denominator = Integer.parseInt(st.nextToken());
                denominator = denominator == 0 ? 1 : denominator;
                simplify();
            }
        }
            
        
        /*
	given a unit (e.g. a cent value) calculate the ratio from the base log value
	return in the form on fractional number, the fractional number is rounded
	to the error given

	e.g. 700 units, 1200 logValue (implies using cent as unit), and error 2 units
	will yield {3, 2} which means 3/2, if the error is 1 cent will yield 295/197,
	if error is 0.001 cent it will yield 2213/1477

	idea:
	starting from 1/1 as the current calculation, if it is smaller than the range
	increase the numerator, if it is larger than the range increase the denominator,
	until it falls into the range, note that cases like 1/2 and 2/4 are both checked,
	leaving some space for improvement to this alrogithm
	*/
        public Ratio(int logValue, double unit, double error){
		if (logValue <= 0) {
                    numerator = 0;
                    denominator = 0;
                } else {
                    error = Math.abs(error);
                    if (error >= unit) error = DEFAULT_LOG_ERROR;
                    double ceiling = logValueToFactor(logValue, unit + error);
                    double floor = logValueToFactor(logValue, unit - error);

                    double num = 1;
                    double den = 1;

                    double ratio = num/den;
                    while (ratio < floor || ratio > ceiling) {	//while out of range
			if (ratio < floor) num++;
			if (ratio > ceiling) den++;
			ratio = num/den;
                    }

                    numerator = (int) num;
                    int d = (int) den;
                    denominator = d == 0 ? 1 : d;
                }
	}


	public Object clone() {
		return new Ratio(numerator, denominator);
	}

	//Daniel change satrted 9/3/2000
	public void simplify() {
		int min = Math.min(numerator, denominator);

		if (numerator == 0) {
			denominator = 1;
			return;
		}

		//this case is needed to be handled if the
		//only need to test the number up to the square of the integer
		if (numerator > denominator) {
			if (numerator % denominator == 0) {
				numerator /= denominator ;
				denominator /= denominator ;
			}
		} else if (numerator < denominator) {
			if (denominator % numerator == 0) {
				denominator /= numerator;
				numerator /= numerator;
			}
		} else {
			numerator = denominator = 1;
		}

		int sqrt = ((int)Math.sqrt(min)) + 1;
		int i = 0;
		while (true) {
			for (; i < Utilities.primeList.size(); i++) {
				int currentPrime = Utilities.getPrime(i);
				if (currentPrime > sqrt) break;
				while (numerator % currentPrime == 0 && denominator % currentPrime== 0) {
					numerator /= currentPrime;
					denominator /= currentPrime;
					//i = 1;		//reset i so that it will start from 2 again
				}
			}
			min = Math.min(numerator, denominator);

			if (Utilities.largestPrime() < sqrt) {
				Utilities.registerPrime(Utilities.largestPrime() + 100);
			} else {
				break;
			}
		}
	}
	//Daniel change ended 9/3/2000

	/**
	modify the ratio so that it inverses,
	will not return a new object but modify the existing object
	*/
	public void inverse() {
		if (numerator == 0) return;
		//do not simplify new ratio
		int temp = denominator;
		denominator = numerator;
		numerator = temp;
	}

	/**
	multiple this ratio with ratio r, does not simplify
	to improve performance in a series of multiplication,
	e.g. in calculating succ > rel
	returns a new ratio
	*/
	public Ratio multiply(Ratio r) {
		if (r == null) return null;
		return new Ratio(numerator * r.getNum(), denominator * r.getDen());
		//return new Ratio(numerator * r.getNum(), denominator * r.getDen(), false);
	}

	public String toString() {
		return numerator + "/" + denominator;
	}
        
        

	//if the ratio is < 1 double it until its >= 1
	public void normalize() {
		double n = numerator;
		double d = denominator;

		if (n == 0) n = d = 1;

		while (n != 0 && n/d < 1) {
			n *= 2;
		}
		while (n != 0 && n/d > 2) {
			d *= 2;
		}

		numerator = (int) n;
		denominator = (int) d;
		simplify();
	}

	public void absolute() {
		numerator = Math.abs(numerator);
		denominator = Math.abs(denominator);
	}

	public Ratio divide(Ratio b) {
		if (b == null) return null;

		int n = numerator * b.denominator;
		int d = denominator * b.numerator;

		return new Ratio(n, d);
	}
        
        
        //note: ratio smaller than 1 will be doubled until its > 1
        public double getLogValue(int logValue) {
            if (Parameters.autoNormalize) this.normalize();
            return logValue * Math.log((double)numerator / denominator) / Math.log(2);
	}
        
        // if no argument, use LOG_CENT
        public double getLogValue(){
          return getLogValue(LOG_CENT);
        }
        
        public double getCents() {
            if (Parameters.autoNormalize) this.normalize();
            return LOG_CENT * Math.log((double)numerator / denominator) / Math.log(2);
        }


	public double getHz(double baseHz) {
            return baseHz * numerator / denominator;
	}

        // static methods:
	
        
        private static double logValueToFactor(int logValue, double unit) {
            return Math.pow(2, unit/logValue);
	}
        
        	/*
	calculate the cent base on the tonic and also the log value
	that an octave is divided into, e.g., 1200 to calculate cents

	The idea of the formula is that Frequency = tonic * 2^(units / parts in an octave)

	note: frequencies < the tonic is doubled until its >= tonic
	*/
	public static double hzToLogValue(double hz, double baseHz, int logValue) {
		if (logValue <= 0) return 0;
		while (hz < baseHz) hz *= 2;
		return logValue * Math.log(hz / baseHz) / Math.log(2);
	}


}