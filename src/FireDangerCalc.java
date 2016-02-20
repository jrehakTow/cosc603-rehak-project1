/*
 * James Rehak							COSC603
 * Re-engineering of Fire Danger Rating Calculator
 * Original Source Code @:
 * http://www.nrs.fs.fed.us/pubs/rn/rn_nc079.pdf
 */

package src;
import java.util.*;
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

	public double calcDryWetRange(double dryTemp, double wetTemp){
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
			case 'c': herb = 0; //don't use
				break;
			case 't': herb = 5;
				break;
			case 'g': herb = 10;
				break;
		}
		
	}
	
	public void calcAB(double DryWetRange){
		//all temperatures are measured in fahrenheit
		if(DryWetRange < 4.5){
			a = 30;
			b = -0.1859;
		}
		else if(DryWetRange < 12.5){
			a = 19;
			b = -0.0859;
		}
		else if(DryWetRange < 27.5){
			a = 13.8;
			b = -0.0579;
		}
		else{
			a = 22.5;
			b = -0.0774;
		}
	}

	public double calcFineFuelMoisture(double a, double b){
		FFM = a*Math.exp(b* drywetRange);
		return FFM;
	}
	
	public double calcAdjustedFuelMoist(double FFM, double BUI){
		ADFM = 0.9*FFM + 9.5*Math.E*(-BUI/50);
		return ADFM;
	}

	public double calcBuildupIndex(double BUO, double PRECIP){
		BUI = -50*(Math.log(1-(Math.E*(-BUO/50))*Math.exp(-1.175*(PRECIP - 0.1))));
		return BUI;
	}
	
	public double calcFineFuelSpread(double WIND){
		double A, B;
		if(WIND < 14){
			A = 0.01312;
			B = 6;
		}
		else{
			A = 0.009184;
			B = 14.4;
		}
		grass = a*(WIND + b) * Math.pow(Math.abs(33 - FFM),1.65) - 3;
		return grass;
	}
	
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
		timber = A*(WIND + B) * Math.pow((Math.abs(33-ADFM)),1.65) - 3;
		return timber;
	}

	public double calcFireLoadIndex(){
		FLOAD = Math.pow(10, 1.75*Math.log10(timber) + 0.32*Math.log10(BUI)-1.64);
		return FLOAD;
	}
	
	public void printAllResults(){
		System.out.println("BUI: "+ BUI);
		System.out.println("FFM: "+ FFM);
		System.out.println("ADFM: "+ ADFM);
		System.out.println("Timber: "+ timber);
		System.out.println("grass: "+ grass);
		System.out.println("FLOAD: "+ FLOAD);
	}

	public static void main(String[] args) {
		boolean skip = false;
		// The logical flow will go here
		FireDangerCalc n = new FireDangerCalc(); //n is for new
		//in fortran77 the drying factor is initialized as 0. Referred to as DF
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
		n.calcAB(n.calcDryWetRange(n.dryTemp, n.wetTemp)); //a & b are now initialized
		System.out.println("Wet range: "+ n.calcDryWetRange(n.dryTemp, n.wetTemp)+", A: "+n.a+", B: "+n.b);
		
		n.FFM = 99;
		n.ADFM = 99;
		n.DF = 0;
		n.FLOAD = 0;
		
		if(n.snow){
			n.grass = 0;
			n.timber = 0;
			n.BUI = 0;
			n.FLOAD = 0;
			if(n.PRECIP > 0){
				n.calcFineFuelMoisture(n.a, n.b); 
				n.printAllResults();
				System.exit(0); //line 2-->4
			}
			n.printAllResults();
			System.exit(0); //line 1-->4
		}
		
		//no snow
		n.calcFineFuelMoisture(n.a, n.b); //line 7
		n.calcDryingFactor(); //line 8
		
		if(n.FFM - 1 <= 0){ //line 10
			n.FFM = 1;
		}
		
		n.FFM = n.FFM + n.herb; //adjust for herb stage line 12
		
		if(n.PRECIP>0.1){ 
			//adjust bui
			n.calcBuildupIndex(n.BUO, n.PRECIP);
			if(n.BUI < 0){ //line 14
				n.BUI = 0;
			}
		}
		else{
			n.BUI = n.BUO; //if no rain
		}
		
		n.BUI = n.BUI + n.DF;//increase BUI by drying factor
		n.calcAdjustedFuelMoist(n.FFM, n.BUI); //line 15
	
		if(n.ADFM > 30){ //line 16
			skip = true; 
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
		n.calcTimberSpreadIndex(n.WIND, n.ADFM); //line
		n.calcFineFuelSpread(n.WIND); //grass
		
		if(n.timber <= 0 && n.WIND < 14){ //line 22
			n.timber = 1;
		}
		if(n.grass <= 0 && n.WIND < 14){ //line 23
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
		if(n.BUI <= 0){
			n.BUI = 0;
			n.FLOAD = 0;
			n.printAllResults();
			System.exit(0);
		}
		n.calcFireLoadIndex();
		
		if(n.FLOAD > 0){
			n.FLOAD = Math.pow(10, n.FLOAD);
		}
		else{
			n.FLOAD = 0;
		}
		n.printAllResults();
		
		/*
		if(n.timber == 0 && n.grass == 0){ //only in documentation
			n.printAllResults();
			System.exit(0);
		}
		n.calcFireLoadIndex();
		n.printAllResults();
		*/
	}

}
