/*
 * James Rehak							COSC603
 * Re-engineering of Fire Danger Rating Calculator
 * Original Source Code @:
 * http://www.nrs.fs.fed.us/pubs/rn/rn_nc079.pdf
 */


package src;

public class FireDangerCalc {
	
	boolean snow;
	boolean rain;
	double PRECIP; //amount of precipitation in INCHES
	double FFM; //fine Fuel moisture rating
	double BUI; //Buildup Index
	double WIND; // wind speed MPH
	double ADFM; //adjusted fuel moisture rating based on 50 day lag
	double grass; //Fine fuel spread 
	double timber; //Timber spread index
	double FLOAD; //Fire load index
	double a; //regression coefficient
	double b; //regression coefficient 
	
	public static double FineFuelMoisture(double a, double b){
		double FFM = 0;
		return FFM;
	}
	
	public static double AdjustedFuelMoist(double FFM, double BUI){
		double ADFM = 0;
		return ADFM;
	}
	
	public static double BuildupIndex(double BUO, double PRECIP){
		double BUI = 0;
		return BUI;
	}
	
	public static double FineFuelSpread(double a, double b, double WIND){
		double grass = 0;
		return grass;
	}
	
	public static double TimberSpreadIndex(double a, double b, double WIND, double ADFM){
		double timber = 0;
		return timber;
	}
	
	public static double FireLoadIndex(){
		double FLOAD = 0;
		return FLOAD;
	}

	public static void main(String[] args) {
		// The logical flow will go here

	}

}
