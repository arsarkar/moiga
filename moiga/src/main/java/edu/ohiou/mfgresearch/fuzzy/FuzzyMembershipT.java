package edu.ohiou.mfgresearch.fuzzy;

import java.util.ArrayList;
import java.util.List;

public class FuzzyMembershipT extends FuzzyMembership 
{ 
	

	double lf,uf;
    /**
     * @param ub The global upper bound  
     * @param lb The global lower bound 
     * @param uf The upper bound of the sub range  
     * @param lf The lower bound of the sub range 
     * */
    public FuzzyMembershipT(double ub,double lb,double uf,double lf)
    {
    	lowerBound=lb;
    	upperBound=ub;
    	this.lf=lf;
    	this.uf=uf;
    }
	public double findSatisfaction(double val) 
	{
		
		double satisfaction=0.0;
		if(val>=lf && val<=uf)
			satisfaction= 1.0;//when val lies in [Lf,Uf]
		else if(val>=lowerBound && val <lf)
			satisfaction= (lowerBound-val)/(double)(lowerBound - lf);//when val lies in [L,Lf)
		else if(val>uf && val<=upperBound)
			satisfaction= (val-upperBound)/(double)(uf-upperBound);//when val lies in (Uf,U]
		return satisfaction;//when val lies in (-inf,L) U (U,inf)
	}
	public List<Double> getRange()
	{
		List<Double> ob=new ArrayList<Double>();
		ob.add(upperBound);
		ob.add(lowerBound);
		ob.add(uf);
		ob.add(lf);
		return ob;
	}
	
}