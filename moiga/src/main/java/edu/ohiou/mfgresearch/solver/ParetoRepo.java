package edu.ohiou.mfgresearch.solver;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.moeaframework.core.Solution;

public class ParetoRepo extends Hashtable<List<List<Double>>, List<Solution>>
{
	private static final long serialVersionUID = 2668010266277577561L;

	// Returns the required points based on Seed selection algo
	List<Solution> getPoints(List<List<Double>> searchSpace,int noOfPts)
	{
		List<Solution> points=this.valueList();
		List<Solution> selected=new ArrayList<Solution>();
		outer:for(Solution i:points)
		{
			for(int j=0;j<searchSpace.size();j++)
			{
				List<Double> temp=searchSpace.get(j);
				if(temp.get(3) >i.getObjective(j) || i.getObjective(j)>temp.get(2))
					continue outer;
			}
			selected.add(i);
			//if(selected.size()>=noOfPts)
				return selected;
		}
//		System.out.println("Selected : "+selected.size()+" "+noOfPts);
		return selected;
	}
	
	//Returns the key in List form
	List<List<List<Double>>> keyList()
	{
		Enumeration<List<List<Double>>> key=super.keys();
		List<List<List<Double>>> list=new ArrayList<List<List<Double>>>(super.size());
		while(key.hasMoreElements())
			list.add(key.nextElement());
		return list;
	}
	
	//Returns the values in List form
	List<Solution> valueList()
	{
		Enumeration<List<List<Double>>> key=super.keys();
		List<Solution> list=new ArrayList<Solution>(super.size());
		while(key.hasMoreElements())
			list.addAll(this.get(key.nextElement()));
//		System.out.println("----------->Value list size : "+list.size());
		return list;
	}
}
