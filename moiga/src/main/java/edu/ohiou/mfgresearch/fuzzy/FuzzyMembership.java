package edu.ohiou.mfgresearch.fuzzy;

public class FuzzyMembership {


	protected double slope;
    protected double yIntercept;
    public double upperBound,lowerBound;

    public FuzzyMembership()
    {
        upperBound = 0.0;
        lowerBound = 0.0;
        findLine();
    }

    public FuzzyMembership(double up, double low)
    {
        upperBound = up;
        lowerBound = low;
        if (up > low)
        {
            findLine();
        }
    }
    public void findLine()
    {
        slope = -1 / (upperBound - lowerBound);
        yIntercept = slope * (-1 * upperBound);
    }
    public double findSatisfaction(double val)
    {
    	findLine();
        double satisfaction = 0.0;
        if (lowerBound != -1)
        {

            if (val <= lowerBound)
            {
                satisfaction = 1;
            }
            else if (val >= upperBound)
            {
                satisfaction = 0;
            }
            else
            {
                satisfaction = (slope * val) + yIntercept;
            }
        }
        else
        {
            satisfaction = 1;
        }
        return satisfaction;
    }

	@Override
	public String toString() {
		return "[upperBound=" + upperBound + ", lowerBound="
				+ lowerBound + "]";
	}
    
    
}
