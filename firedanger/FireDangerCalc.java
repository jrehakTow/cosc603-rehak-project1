
package firedanger;

import java.util.Scanner;

/**
 * <h1>Fire Danger Calculator</h1>
 * FireDangerCalc is a Re-engineering of Fire Danger Rating Calculator. 
 * This program is used to calculate National Fire Danger Rating indexes based on
 * Fuel moisture, buildup index, and drying factor. 
 * <p>
 * Original source code can be found at
 * <a href="http://www.nrs.fs.fed.us/pubs/rn/rn_nc079.pdf"> Computer Calculation of Fire Danger </a>
 * @author James Rehak
 */
public class FireDangerCalc {
	
	/** The snow. */
	boolean snow;

	/** The rain. */
	boolean rain;
	
	/** The herb stage. */
	char herbstage; //get input
	
	/** The herb stage percentage. */
	double herb; // Herb stage, has 3 states. 0%, 5%, 10%

	/** The precipitation. */
	double PRECIP; // Amount of precipitation in INCHES

	/** The Fine Fuel Moisture Rating. */
	double FFM; 

	/** The Buildup Index. */
	double BUI; 

	/** Yesterday's Buildup Index. */
	double BUO; 

	/** The wind. */
	double wind; // Wind speed measured in MPH

	/** The Adjusted Fuel Moisture Rating. */
	double ADFM; // Adjusted fuel moisture rating based on 50 day lag

	/** The Fine Fuel Spread. Shown as Grass */
	double grass; 

	/** The Timber Spread index. */
	double timber; 

	/** The Fire Load Index. */
	double FLOAD; 
	
	/** The Drying factor. */
	double DF; 

	/** The a regression coefficient. */
	double a; 

	/** The b regression coefficient. */
	double b; 
	
	/** The dry bulb temperature. */
	double dryTemp; 
	
	/** The wet bulb temperature. */
	double wetTemp; 
	
	/** The dry wet temperature range. */
	double drywetRange; // 
	
	
	/**
	 * This method converts the yes or no
	 * input character into a boolean 
	 *
	 * @param ch the character
	 * @return true, if yes
	 */
	public boolean convertYesNo(char ch){
		if(ch == 'y'|| ch == 'Y'){
			return true;
		}
		else
			return false;
	}
	
	

	/**
	 * Compute the dry wet temperature range. 
	 * <p>
	 * Used to calculate fine fuel moisture.
	 *
	 * @return the dry wet temperature range.
	 */
	public double computeDryWetRange(){
		drywetRange = dryTemp - wetTemp;
		return drywetRange;
	}
	
	
	
	/**
	 * Compute the drying factor.
	 * <p>
	 * The drying factor is added to the Build Up Index.
	 */
	public void computeDryingFactor(){
		double[] dryingFactors = {16.0, 10.0, 7.0, 5.0, 4.0, 3.0};
		int i = 0;
		while (FFM - dryingFactors[i] <= 0){
			DF = i;
			i++;
			if(i == 6){
				DF = 7.0;
				break;
			}
		}
	}
	
	
	
	/**
	 * Gets the herb stage.
	 * <p> 
	 * Herb stage is used to adjust the calculated fine fuel 
	 * moisture by adding 5% for transition stage or 10% for green fuels
	 *
	 * @param ch the character used to determine the herb stage.
	 * @return the herb stage percentage.
	 */
	public void getHerbStage(char ch){
		switch(ch){
			case 'c': herb = 0; 
				break;
			case 't': herb = 5;
				break;
			case 'g': herb = 10;
				break;
		}
	}
	
	
	
	/**
	 * Compute a b regression coefficients. 
	 * <p>
	 * Used to find the fine fuel moisture index.
	 */
	public void computeAB(){
		// All temperatures are measured in fahrenheit
		if(drywetRange < 4.5){
			a = 30.0;
			b = -0.1859;
		}
		else if(drywetRange < 12.5){
			a = 19.0;
			b = -0.0859;
		}
		else if(drywetRange < 27.5){
			a = 13.8;
			b = -0.0579;
		}
		else{
			a = 22.5;
			b = -0.0774;
		}
	}
	
	

	/**
	 * Compute the fine fuel moisture index.
	 * <p>
	 * Used to fined the fine fuel spread index. 
	 *
	 * @return the fine fuel moisture.
	 */
	public double computeFineFuelMoisture(){
		FFM = a * Math.exp(b * drywetRange);
		return FFM;
	}
	
	
	
	/**
	 * Compute adjusted fuel moisture index. The adjusted 
	 * fine fuel moisture is also equal to equivalent fuel 
	 * moisture
	 * <p>
	 * Used to find the timber spread index.
	 *
	 * @return the Adjusted Fine Fuel Moisture
	 */
	public double computeAdjustedFuelMoist(){
		ADFM = 0.9 * FFM + 9.5 * Math.exp(-BUI/50);
		return ADFM;
	}
	
	

	/**
	 * Compute buildup index. This method is called whenever there is rain. 
	 *
	 * @return the Buildup Index
	 */
	public double computeBuildupIndex(){
		BUI = -50.0 * (Math.log(1-(Math.E * (-BUO/50) )*Math.exp(-1.175 * (PRECIP - 0.1) ) ) );
		
		if(BUI < 0){ 
			BUI = 0;
		}
		return BUI;
	}
	
	
	
	/**
	 * Compute the fine fuel spread index. The A and B coefficients
	 * are adjusted based on wind speed. The wind and fine fuel moisture
	 * are used as parameters.
	 *
	 * @return the Fine Fuel Spread referred to as grass
	 */
	public double computeFineFuelSpread(){
		double A, B;
		if(wind < 14){
			A = 0.01312;
			B = 6;
		}
		else{
			A = 0.009184;
			B = 14.4;
		} 
		
		grass = A * (wind + B) * Math.pow(Math.abs(33 - FFM), 1.65) - 3.0;
		return grass;
	}
	
	
	
	/**
	 * Compute the timber spread index.
	 * The A and B coefficients are adjusted based on wind speed. 
	 * The wind and adjusted fine fuel moisture are used as parameters.
	 * <p>
	 * Used to calculate fire load index
	 * 
	 * @return the Timber Spread Index
	 */
	public double computeTimberSpreadIndex(){
		double A, B; // Special wind regression coefficients 
		if(wind < 14){
			A = 0.01312;
			B = 6.0;
		}
		else{
			A = 0.009184;
			B = 14.4;
		}
		timber = A * (wind + B) * Math.pow(Math.abs(33 - ADFM), 1.65) - 3.0;
		//Absolute value for (33-ADFM) ok because not raised by even constant
		
		return timber;
	}
	
	

	/**
	 * Compute the fire load index.
	 * The fire load index uses the Timber spread and Build Up Index as parameters.
	 * <p>
	 * Fire load index was an experimental calculation back in 1968. It is now apart of 
	 * the National Fire Danger Rating System. 
	 *
	 * @return the Fire Load Index
	 */
	public double computeFireLoadIndex(){
		FLOAD = Math.pow(10, 1.75 * Math.log10(timber) + 0.32 * Math.log10(BUI) - 1.64);
		
		if(FLOAD < 0.0){
			FLOAD = 0.0;
		}
		return FLOAD;
	}
	
	
	
	/**
	 * Prints all results.
	 */
	public void printAllResults(){
		System.out.println("\n-------Results--------");
		System.out.println("BUI: "+ BUI);
		System.out.println("FFM: "+ FFM);
		System.out.println("ADFM: "+ ADFM);
		System.out.println("Timber: "+ timber);
		System.out.println("grass: "+ grass);
		System.out.println("FLOAD: "+ FLOAD);
	}

	
	
	/**
	 * The main method that contains the flow of logic. It calls each method as 
	 * necessary. 
	 *
	 * @param args Unused.
	 */
	public static void main(String[] args) {
		boolean doSkip = true; // Skip for FFM & ADFM 30% check
		
		FireDangerCalc dngr = new FireDangerCalc(); //n is for new
		
		Scanner in = new Scanner(System.in);
		
		// Get initial data
		System.out.print("Enter Dry bulb temperature: ");
		dngr.dryTemp = in.nextDouble();
		
		System.out.print("Enter Wet bulb temperature: ");
		dngr.wetTemp = in.nextDouble();
		
		System.out.print("Enter Wind Speed: ");
		dngr.wind = in.nextDouble();
		
		System.out.print("Enter Pecipitation: ");
		dngr.PRECIP = in.nextDouble();
		
		System.out.print("Is there snow: ");
		dngr.snow = dngr.convertYesNo(in.next().charAt(0));
		
		System.out.print("Enter Herb Stage: (Cured, Transistion, Green): ");
		dngr.getHerbStage(in.next().charAt(0));
		
		System.out.print("Enter Yesterday's BUI: ");
		dngr.BUO = in.nextDouble();
		
		in.close(); // Done with input
		
		//Generate initial data
		dngr.computeDryWetRange();
		dngr.computeAB(); //a & b are now initialized
		dngr.FFM = 99.0;
		dngr.ADFM = 99.0;
		dngr.DF = 0.0;
		dngr.FLOAD = 0.0;
		
		if(dngr.snow){
			dngr.grass = 0.0;
			dngr.timber = 0.0;
			dngr.BUI = 0.0;
			dngr.FLOAD = 0.0;
			
			if(dngr.PRECIP > 0.1){
				// Adjust BUI for rain
				dngr.computeBuildupIndex();

				dngr.printAllResults();
				System.exit(0); 
			}
			dngr.printAllResults();
			System.exit(0); 
		}
		
		// If no snow
		dngr.computeFineFuelMoisture(); 
		
		dngr.computeDryingFactor(); 
		
		if(dngr.FFM - 1 <= 0.0){ 
			dngr.FFM = 1;
		}
		
		// Adjust for herb stage 
		dngr.FFM = dngr.FFM + dngr.herb; 
		
		if(dngr.PRECIP > 0.1){  
			// Adjust BUI for rain
			dngr.computeBuildupIndex();
		}
		else{ // If no rain, yesterday's BUI used as initial BUI
			dngr.BUI = dngr.BUO; 
		}
		
		// Increase BUI by drying factor
		dngr.BUI = dngr.BUI + dngr.DF;
		
		dngr.computeAdjustedFuelMoist(); 
		
		// Check if Fuel moistures are greater than 30%
		if(dngr.ADFM > 30.0){ //line 16
			doSkip = false; 
		}
		if(dngr.FFM > 30 && !doSkip){ 
			// All spread indexes to 1
			dngr.grass = 1;
			dngr.timber = 1;
			dngr.computeFireLoadIndex();
			
			dngr.printAllResults();
			System.exit(0);
		}
		
		// Calculate timber and grass spreads
		dngr.computeTimberSpreadIndex(); //timber
		dngr.computeFineFuelSpread(); //grass
		
		if(dngr.timber <= 0.0 && dngr.wind <= 14){ 
			dngr.timber = 1.0;
		}
		if(dngr.grass <= 0 && dngr.wind <= 14){ 
			dngr.grass = 1.0;
		}
		if(dngr.timber > 99.0 && dngr.wind > 14){ 
			dngr.timber = 99.0;
		}
		if(dngr.grass > 99.0 && dngr.wind > 14){ 
			dngr.grass = 99.0;
		}
		if(dngr.timber <= 0.0){
			dngr.printAllResults();
			System.exit(0);
		}
		
		if(dngr.BUI <= 0.0){ 
			dngr.FLOAD = 0;
			
			dngr.printAllResults(); 
			System.exit(0); 
		}
		
		dngr.computeFireLoadIndex();

		dngr.printAllResults();

	}

}
