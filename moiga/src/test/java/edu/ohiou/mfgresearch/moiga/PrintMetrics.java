package edu.ohiou.mfgresearch.moiga;

import java.util.List;

import org.moeaframework.core.Indicator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

 public class PrintMetrics
 {
	 Problem prob;
	 Indicator ind[];
	 public PrintMetrics(Problem prob,Indicator ...ind)
	{
		this.prob=prob;
		this.ind=ind;
	}
	public void printMetrics(List<List<Double>> X)
	{
		for(Indicator i:ind)
			System.out.println(i.toString().replaceAll("org.moeaframework.core.indicator.","")
					.replaceAll(i.toString().substring(i.toString().indexOf('@')),"")+" : "
					+i.evaluate(this.apply(X))
					);
	}
	public static NondominatedPopulation apply(List<List<Double>> X)
	{
		NondominatedPopulation ob=new NondominatedPopulation();
		for(List<Double> x:X)
		{
			double []p=new double[x.size()];
			for(int i=0;i<p.length;i++)
				p[i]=x.get(i);
			Solution ob11=new Solution(p);
			ob.add(ob11);
		}
		return ob;
	}

}
