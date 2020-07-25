package edu.ohiou.mfgresearch.operators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import edu.ohiou.mfgresearch.schedule.Job;

public class PMXCrossover implements Variation
{

	@Override
	public int getArity() {
		return 2;
	}
	
 public Solution[] evolve(Solution[] parents)
	{ 
  		int size=parents[0].getNumberOfVariables();
		Map<Long,Job> map=new HashMap<Long,Job>();
		long []parent1=new long[size];
		long []parent2=new long[size];
		
		for(int i=0;i<size;i++)
		{
			map.put(((Job)(parents[0].getVariable(i))).jobID, (Job)(parents[0].getVariable(i)));
			parent1[i]=((Job)(parents[0].getVariable(i))).jobID;
			parent2[i]=((Job)(parents[1].getVariable(i))).jobID;
		}   
		 
		int ind1 = PRNG.nextInt(size); 
		int ind2 = PRNG.nextInt(size);
		
		if (ind1==ind2)
		{ 
			ind2=(ind2++)%size; 
		}
		if (ind1>ind2)
		{ 
		   int t=ind1; 
		   ind1=ind2; 
		   ind2=t; 
		} 
		  
		  long[] offspring1=new long[size]; 
		  long[] offspring2=new long[size]; 
		  long[] rep1=new long[size]; 
		  long[] rep2=new long[size]; 
		 
		  Arrays.fill(rep1, -1); 
		  Arrays.fill(rep2, -1); 
		 
		  for (int i=ind1;i<=ind2;i++)
		  { 
		   offspring1[i]=parent2[i]; 
		   offspring2[i]=parent1[i]; 
		 
		   rep1[(int)parent2[i]-1]=parent1[i]; 
		   rep2[(int)parent1[i]-1]=parent2[i]; 
		  } 
		  
		  for (int i=0;i<size;i++)
		  { 
			if ((i<ind1) || (i>ind2))
		    { 
		     long n1=parent1[i]; 
		     long m1=rep1[(int)n1-1]; 
		  
		     long n2=parent2[i]; 
		     long m2=rep2[(int)n2-1]; 
		 
		     while (m1!=-1)
		     { 
 		       n1=m1; 
		       m1=rep1[(int)m1-1]; 
		     } 
		 
		    while (m2!=-1)
		    { 
		      n2=m2; 
		      m2=rep2[(int)m2-1]; 
		     } 
		 
		     offspring1[i]=n1; 
		     offspring2[i]=n2; 
		   } 
		  } 
		 
		  Solution p1=new Solution(parents[0].getNumberOfVariables(),parents[0].getNumberOfObjectives());
		  Solution p2=new Solution(parents[0].getNumberOfVariables(),parents[0].getNumberOfObjectives());
		  for(int i=0;i<size;i++)
		  {
			  p1.setVariable(i,map.get(offspring1[i]));
			  p2.setVariable(i,map.get(offspring2[i]));
		  }
		  
		  return new Solution[] {p1,p2};
		 } 
		 
}
