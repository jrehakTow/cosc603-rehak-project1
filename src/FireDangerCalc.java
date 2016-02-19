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
		
	}
	
	public void getHerbStage(char ch){
		switch(ch){
			case 'c': herb = 1; //don't use
				break;
			case 't': herb = 0.5;
				break;
			case 'g': herb = 0.1;
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
		FFM = a*Math.exp(b) * drywetRange;
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
	
	public double calcFineFuelSpread(double a, double b, double WIND){
		grass = a*(WIND + b) * Math.pow(33 - FFM,1.65) - 3;
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
		timber = A*(WIND + B) * Math.pow(33 - ADFM,1.65) - 3;
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
		
		/*
		//might be too early to do these
		n.calcFineFuelMoisture(n.a, n.b);
		n.calcBuildupIndex(n.BUO, n.PRECIP);
		n.calcFineFuelSpread(n.a, n.b, n.WIND); //grass
		n.calcAdjustedFuelMoist(n.FFM, n.BUI);
		n.calcTimberSpreadIndex(n.WIND, n.ADFM); //timber
		n.calcFireLoadIndex();
		*/
		
		//pre-logic
		System.out.println("initialize");
		n.printAllResults();
		System.out.println("------This is just a test please ignore-------\n\n");
		
		if(n.snow){
			n.grass = 0;
			n.timber = 0;
			n.BUI = 0;
			n.FLOAD = 0;//fload 
			if(n.PRECIP > 0){
				n.calcFineFuelMoisture(n.a, n.b); 
				
				/*
				 * not in actual code
				 * //drying factor
					n.BUI = n.BUI + 1;// not sure of drying factor yet
				 */
				
				n.printAllResults();
				System.exit(0); //line 2-->4
			}
			n.printAllResults();
			System.exit(0); //line 1-->4
		}
		//no snow
		n.calcFineFuelMoisture(n.a, n.b);
		//calc drying factor
		n.FFM = n.FFM + 2; //adjust for herb stage... find herb stage
		if(n.PRECIP>0){
			//adjust bui
		}
		//increase bui by drying factor
		if(n.FFM > 33){
			//all spread indexes to 1
			n.grass = 1;
			n.timber = 1;
			n.calcFireLoadIndex();
			n.printAllResults();
			System.exit(0);
		}
		//calc if wind greater than 14mph-- already done in function
		n.calcTimberSpreadIndex(n.WIND, n.ADFM);
		if(n.timber == 0 && n.grass == 0){
			n.printAllResults();
			System.exit(0);
		}
		n.calcFireLoadIndex();
		n.printAllResults();

	}

}
