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
	double BUO; //yesterday's BUI
	double WIND; // wind speed MPH
	double ADFM; //adjusted fuel moisture rating based on 50 day lag
	double grass; //Fine fuel spread 
	double timber; //Timber spread index
	double FLOAD; //Fire load index
	double a; //regression coefficient
	double b; //regression coefficient 
	
	public void calcAB(double tempRange){
		//all temperatures are measured in fahrenheit
		if(tempRange < 4.5){
			a = 30;
			b = -0.1859;
		}
		else if(tempRange < 12.5){
			a = 19;
			b = -0.0859;
		}
		else if(tempRange < 27.5){
			a = 13.8;
			b = -0.0579;
		}
		else{
			a = 22.5;
			b = -0.0774;
		}
	}
	
	public double calcFineFuelMoisture(double a, double b){
		double FFM = 0;
		return FFM;
	}
	
	public double calcAdjustedFuelMoist(double FFM, double BUI){
		double ADFM = 0;
		return ADFM;
	}
	
	public double calcBuildupIndex(double BUO, double PRECIP){
		double BUI = 0;
		return BUI;
	}
	
	public double calcFineFuelSpread(double a, double b, double WIND){
		double grass = 0;
		return grass;
	}
	
	public double calcTimberSpreadIndex(double a, double b, double WIND, double ADFM){
		double timber = 0;
		return timber;
	}
	
	public double calcFireLoadIndex(){
		double FLOAD = 0;
		return FLOAD;
	}

	public static void main(String[] args) {
		// The logical flow will go here

	}

}
