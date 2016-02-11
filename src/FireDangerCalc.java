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
		FFM = a*Math.exp(b);
		return FFM;
	}
	
	public double calcAdjustedFuelMoist(double FFM, double BUI){
		ADFM = 0.9*FFM + 9.5*Math.E*(-BUI/50);
		return ADFM;
	}
	
	public double calcBuildupIndex(double BUO, double PRECIP){
		BUI = -50*(Math.log(1-(-Math.E*(BUO/50))*Math.exp(1.175*(PRECIP - 0.1))));
		return BUI;
	}
	
	public double calcFineFuelSpread(double a, double b, double WIND){
		grass = a*(WIND + b) * Math.pow(33 - FFM,1.65) - 3;
		return grass;
	}
	
	public double calcTimberSpreadIndex(double WIND, double ADFM){
		double A, B;
		if(WIND < 14){
			A = 0.01312;
			B = 6;
		}
		else{
			A = 0.009184;
			B = 14.4;
		}
		timber = A*(WIND + B) * Math.pow(33 - ADFM,1.65) - 3;
		return timber;
	}
	
	public double calcFireLoadIndex(){
		FLOAD = Math.pow(10, 1.75*Math.log10(timber) + 0.32*Math.log10(BUI)-1.64);
		return FLOAD;
	}

	public static void main(String[] args) {
		// The logical flow will go here

	}

}
