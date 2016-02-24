/*
 * James Rehak							COSC603
 * Re-engineering of Fire Danger Rating Calculator
 * Original Source Code @:
 * http://www.nrs.fs.fed.us/pubs/rn/rn_nc079.pdf
 */

package firedanger;
import java.util.Scanner;
// TODO: Auto-generated Javadoc

public class FireDangerCalc {
	
	boolean snow;

	boolean rain;
	
	char herbstage; //get input
	
	double herb; // Herb stage, has 3 states. 0%, 5%, 10%

	double PRECIP; // Amount of precipitation in INCHES

	double FFM; // Fine Fuel moisture rating

	double BUI; // Buildup Index

	double BUO; // Yesterday's BUI

	double wind; // Wind speed MPH

	double ADFM; // Adjusted fuel moisture rating based on 50 day lag

	double grass; // Fine fuel spread 

	double timber; // Timber spread index

	double FLOAD; // Fire load index
	
	double DF; // Drying factor

	double a; // Regression coefficient

	double b; // Regression coefficient 
	
	double dryTemp; // dry bulb temperature
	
	double wetTemp; // wet bulb temperature
	
	double drywetRange; // 
	
	
	public boolean convertYesNo(char ch){
		if(ch == 'y'|| ch == 'Y'){
			return true;
		}
		else
			return false;
	}
	
	

	public double computeDryWetRange(){
		drywetRange = dryTemp - wetTemp;
		return drywetRange;
	}
	
	
	
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
	
	

	public double computeFineFuelMoisture(){
		FFM = a * Math.exp(b * drywetRange);
		return FFM;
	}
	
	
	
	public double computeAdjustedFuelMoist(){
		ADFM = 0.9 * FFM + 9.5 * Math.exp(-BUI/50);
		return ADFM;
	}
	
	

	public double computeBuildupIndex(){
		BUI = -50.0 * (Math.log(1-(Math.E * (-BUO/50) )*Math.exp(-1.175 * (PRECIP - 0.1) ) ) );
		
		if(BUI < 0){ 
			BUI = 0;
		}
		return BUI;
	}
	
	
	
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
	
	

	public double computeFireLoadIndex(){
		FLOAD = Math.pow(10, 1.75 * Math.log10(timber) + 0.32 * Math.log10(BUI) - 1.64);
		
		if(FLOAD < 0.0){
			FLOAD = 0.0;
		}
		return FLOAD;
	}
	
	
	
	public void printAllResults(){
		System.out.println("\n-------Results--------");
		System.out.println("BUI: "+ BUI);
		System.out.println("FFM: "+ FFM);
		System.out.println("ADFM: "+ ADFM);
		System.out.println("Timber: "+ timber);
		System.out.println("grass: "+ grass);
		System.out.println("FLOAD: "+ FLOAD);
	}

	
	
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
