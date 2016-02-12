/*
 * James Rehak							COSC603
 * Re-engineering of Fire Danger Rating Calculator
 * Original Source Code @:
 * http://www.nrs.fs.fed.us/pubs/rn/rn_nc079.pdf
 */


package src;

// TODO: Auto-generated Javadoc
/**
 * The Class FireDangerCalc.
 */
public class FireDangerCalc {
	
	/** The snow. */
	boolean snow;
	
	/** The rain. */
	boolean rain;
	
	/** The precip. */
	double PRECIP; //amount of precipitation in INCHES
	
	/** The ffm. */
	double FFM; //fine Fuel moisture rating
	
	/** The bui. */
	double BUI; //Buildup Index
	
	/** The buo. */
	double BUO; //yesterday's BUI
	
	/** The wind. */
	double WIND; // wind speed MPH
	
	/** The adfm. */
	double ADFM; //adjusted fuel moisture rating based on 50 day lag
	
	/** The grass. */
	double grass; //Fine fuel spread 
	
	/** The timber. */
	double timber; //Timber spread index
	
	/** The fload. */
	double FLOAD; //Fire load index
	
	/** The a. */
	double a; //regression coefficient
	
	/** The b. */
	double b; //regression coefficient 
	
	/**
	 * Calc ab.
	 *
	 * @param tempRange the temp range
	 */
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
	
	/**
	 * Calc fine fuel moisture.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double
	 */
	public double calcFineFuelMoisture(double a, double b){
		FFM = a*Math.exp(b);
		return FFM;
	}
	
	/**
	 * Calc adjusted fuel moist.
	 *
	 * @param FFM the ffm
	 * @param BUI the bui
	 * @return the double
	 */
	public double calcAdjustedFuelMoist(double FFM, double BUI){
		ADFM = 0.9*FFM + 9.5*Math.E*(-BUI/50);
		return ADFM;
	}
	
	/**
	 * Calc buildup index.
	 *
	 * @param BUO the buo
	 * @param PRECIP the precip
	 * @return the double
	 */
	public double calcBuildupIndex(double BUO, double PRECIP){
		BUI = -50*(Math.log(1-(-Math.E*(BUO/50))*Math.exp(1.175*(PRECIP - 0.1))));
		return BUI;
	}
	
	/**
	 * Calc fine fuel spread.
	 *
	 * @param a the a
	 * @param b the b
	 * @param WIND the wind
	 * @return the double
	 */
	public double calcFineFuelSpread(double a, double b, double WIND){
		grass = a*(WIND + b) * Math.pow(33 - FFM,1.65) - 3;
		return grass;
	}
	
	/**
	 * Calc timber spread index.
	 *
	 * @param WIND the wind
	 * @param ADFM the adfm
	 * @return the double
	 */
	public double calcTimberSpreadIndex(double WIND, double ADFM){
		double A, B; //Special wind regression coefficients 
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
	
	/**
	 * Calc fire load index.
	 *
	 * @return the double
	 */
	public double calcFireLoadIndex(){
		FLOAD = Math.pow(10, 1.75*Math.log10(timber) + 0.32*Math.log10(BUI)-1.64);
		return FLOAD;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		// The logical flow will go here

	}

}
