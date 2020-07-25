package edu.ohiou.mfgresearch.fuzzy;

/**
 * Fuzzy asymptotic profile based on function a+ b/x-c
 * We consider a = 0, b = slope = upper bound - intercept
 * 			   c  = intercept = (up-low)/2	
 * @author sarkara1
 *
 */
public class FuzzyAsympMembership extends FuzzyMembership {

	public FuzzyAsympMembership(double up, double low) {
		super(up, low);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void findLine() {
		yIntercept = lowerBound + (upperBound - lowerBound ) /2;
		slope = upperBound - yIntercept;
//		System.out.println("intercept ="+yIntercept + " Slope = "+ slope );
	}

	@Override
	public double findSatisfaction(double val) {
		findLine();
        double satisfaction = 0.0;
        if(val > upperBound){
        	satisfaction = slope / (val - yIntercept);
        }
        else if(val < lowerBound){
        	satisfaction = -1 * slope / ( val - yIntercept);
        }
        else {
        	satisfaction = 1;
        }
        return satisfaction;
	}
}
