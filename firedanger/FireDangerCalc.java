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
	
	double herb; //herb stage, has 3 states. 0%, 5%, 10%

	double PRECIP; //amount of precipitation in INCHES

	double FFM; //fine Fuel moisture rating

	double BUI; //Buildup Index

	double BUO; //yesterday's BUI

	double WIND; // wind speed MPH

	double ADFM; //adjusted fuel moisture rating based on 50 day lag

	double grass; //Fine fuel spread 

	double timber; //Timber spread index

	double FLOAD; //Fire load index
	
	double DF; //drying factor

	double a; //regression coefficient

	double b; //regression coefficient 
	
	double dryTemp;
	
	double wetTemp;
	
	double drywetRange;
	
	public boolean convertYesNo(char ch){
		if(ch == 'y'|| ch == 'Y'){
			return true;
		}
		else
			return false;
	}

	public double calcDryWetRange(){
		drywetRange = dryTemp - wetTemp;
		return drywetRange;
	}
	
	public void calcDryingFactor(){
		double[] dryingFactors = {16, 10, 7, 5, 4, 3};
		int i = 0;
		while (FFM - dryingFactors[i] <= 0){
			DF = i;
			i++;
			if(i == 6){
				DF = 7;
				break;
			}//drying factor can be 0-7
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
	
	public void calcAB(){
		//all temperatures are measured in fahrenheit
		if(drywetRange < 4.5){
			a = 30;
			b = -0.1859;
		}
		else if(drywetRange < 12.5){
			a = 19;
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

	public double calcFineFuelMoisture(){
		FFM = a*Math.exp(b* drywetRange);
		return FFM;
	}
	
	public double calcAdjustedFuelMoist(){
		ADFM = 0.9*FFM + 9.5*Math.exp(-BUI/50);
		return ADFM;
	}

	public double calcBuildupIndex(){
		BUI = -50*(Math.log(1-(Math.E*(-BUO/50))*Math.exp(-1.175*(PRECIP - 0.1))));
		
		//catch negative BUI
		if(BUI < 0){ 
			BUI = 0;
		}
		return BUI;
	}
	
	public double calcFineFuelSpread(){
		double A, B;
		if(WIND < 14){
			A = 0.01312;
			B = 6;
		}
		else{
			A = 0.009184;
			B = 14.4;
		} //double check this...
		grass = A*(WIND + B) * Math.pow(Math.abs(33 - FFM),1.65) - 3;
		return grass;
	}
	
	public double calcTimberSpreadIndex(){
		double A, B; //Special wind regression coefficients 
		if(WIND < 14){
			A = 0.01312;
			B = 6;
		}
		else{
			A = 0.009184;
			B = 14.4;
		}
		timber = A*(WIND + B) * Math.pow((Math.abs(33-ADFM)),1.65) - 3;
		//absolute value for (33-ADFM) ok because not raised by even constant
		return timber;
	}

	public double calcFireLoadIndex(){
		FLOAD = Math.pow(10, 1.75*Math.log10(timber) + 0.32*Math.log10(BUI)-1.64);
		
		if(FLOAD < 0){
			FLOAD = 0;
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
		boolean skip = true; //skip for FFM & ADFM 30% check
		
		FireDangerCalc n = new FireDangerCalc(); //n is for new
		
		Scanner in = new Scanner(System.in);
		
		//Get initial data
		System.out.print("Enter Dry bulb temperature: ");
		n.dryTemp = in.nextDouble();
		
		System.out.print("Enter Wet bulb temperature: ");
		n.wetTemp = in.nextDouble();
		
		System.out.print("Enter Wind Speed: ");
		n.WIND = in.nextDouble();
		
		System.out.print("Enter Pecipitation: ");
		n.PRECIP = in.nextDouble();
		
		System.out.print("Is there snow: ");
		n.snow = n.convertYesNo(in.next().charAt(0));
		
		System.out.print("Enter Herb Stage: (Cured, Transistion, Green): ");
		n.getHerbStage(in.next().charAt(0));
		
		System.out.print("Enter Yesterday's BUI: ");
		n.BUO = in.nextDouble();
		
		//Generate initial data
		n.calcDryWetRange();
		n.calcAB(); //a & b are now initialized
		n.FFM = 99;
		n.ADFM = 99;
		n.DF = 0;
		n.FLOAD = 0;
		
		if(n.snow){
			//if snow on ground, all spread indexes must be 0 
			n.grass = 0;
			n.timber = 0;
			n.BUI = 0;
			n.FLOAD = 0;
			if(n.PRECIP > 0.1){
				//adjust BUI for rain
				n.calcBuildupIndex();

				n.printAllResults();
				System.exit(0); //line 2-->4
			}
			n.printAllResults();
			System.exit(0); //line 1-->4
		}
		
		//if no snow
		n.calcFineFuelMoisture(); //returns FFM line 7
		
		n.calcDryingFactor(); //returns DF line 8
		
		if(n.FFM - 1 <= 0){ //line 10
			n.FFM = 1;
		}
		
		//adjust for herb stage 
		n.FFM = n.FFM + n.herb; //line 12
		
		if(n.PRECIP > 0.1){  
			//adjust BUI for rain
			n.calcBuildupIndex();
		}
		//if no rain, yesterday's BUI used as initial BUI
		else{ 
			n.BUI = n.BUO; 
		}
		
		//increase BUI by drying factor
		n.BUI = n.BUI + n.DF;
		
		n.calcAdjustedFuelMoist(); //returns ADFM line 15
		
		//check if Fuel moistures are greater than 30%
		if(n.ADFM > 30){ //line 16
			skip = false; 
		}
		if(n.FFM > 30 && !skip){ //33% in documentation
			//all spread indexes to 1
			n.grass = 1;
			n.timber = 1;
			n.calcFireLoadIndex();
			
			n.printAllResults();
			System.exit(0);
		}
		
		//calc if wind greater than 14mph-- already done in function
		n.calcTimberSpreadIndex(); //line
		n.calcFineFuelSpread(); //grass
		
		if(n.timber <= 0 && n.WIND <= 14){ //line 22
			n.timber = 1;
		}
		if(n.grass <= 0 && n.WIND <= 14){ //line 23
			n.grass = 1;
		}
		if(n.timber > 99 && n.WIND > 14){ //line 27
			n.timber = 99;
		}
		if(n.grass > 99 && n.WIND > 14){ //line 26
			n.grass = 99;
		}
		if(n.timber <= 0){
			n.printAllResults();
			System.exit(0);
		}
		if(n.BUI <= 0){ //line 29
			n.FLOAD = 0;
			
			n.printAllResults(); 
			System.exit(0); //line 30
		}
		n.calcFireLoadIndex();

		n.printAllResults();

	}

}
