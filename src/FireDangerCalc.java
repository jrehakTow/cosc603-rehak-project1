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
	
	double dryTemp;
	
	double wetTemp;
	
	public boolean convertYesNo(char ch){
		if(ch == 'y'|| ch == 'Y'){
			return true;
		}
		else
			return false;
	}

	public double calcDryWetRange(double dryTemp, double wetTemp){
		return dryTemp - wetTemp;
	}
	
	public void calcDryingFactor(){
		
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
		System.out.println("FLOAD: "+ FLOAD);
	}

	public static void main(String[] args) {
		// The logical flow will go here
		FireDangerCalc n = new FireDangerCalc(); //n is for new
		//in fortran77 the drying factor is initialized as 0. Referred to as DF
		Scanner in = new Scanner(System.in);
		
		//Get initial data
		System.out.println("Enter Dry bulb temperature: ");
		n.dryTemp = in.nextDouble();
		
		System.out.println("Enter Wet bulb temperature: ");
		n.wetTemp = in.nextDouble();
		
		System.out.println("Enter Wind Speed: ");
		n.WIND = in.nextDouble();
		
		System.out.println("Enter Pecipitation: ");
		n.PRECIP = in.nextDouble();
		
		System.out.println("Is there snow: ");
		n.snow = n.convertYesNo(in.next().charAt(0));
		
		System.out.println(n.snow);
		
		System.out.println("Enter Yesterday's BUI: ");
		n.BUO = in.nextDouble();
		
		
		//Generate initial data
		n.calcAB(n.calcDryWetRange(n.dryTemp, n.wetTemp)); //a & b are now initialized
		System.out.println("Wet range: "+ n.calcDryWetRange(n.dryTemp, n.wetTemp)+", A: "+n.a+", B: "+n.b);
		
		//might be too early to do these
		n.calcFineFuelMoisture(n.a, n.b);
		n.calcBuildupIndex(n.BUO, n.PRECIP);
		n.calcFineFuelSpread(n.a, n.b, n.WIND); //grass
		n.calcAdjustedFuelMoist(n.FFM, n.BUI);
		n.calcTimberSpreadIndex(n.WIND, n.ADFM); //timber
		n.calcFireLoadIndex();
		
		if(n.snow){
			n.grass = 0;
			n.timber = 0;
			n.calcFireLoadIndex();
			if(n.PRECIP > 0){
				n.calcFineFuelMoisture(n.a, n.b); //Superfluous? 
				
				//drying factor
				n.BUI = n.BUI + 1;// not sure of drying factor yet
				
				n.printAllResults();
				System.exit(0);
			}
			n.printAllResults();
			System.exit(0);
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
