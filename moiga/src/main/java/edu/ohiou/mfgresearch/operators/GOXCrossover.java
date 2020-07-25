package edu.ohiou.mfgresearch.operators;

import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import edu.ohiou.mfgresearch.schedule.JobT;

public class GOXCrossover implements Variation
{
	int jobCount;
	public GOXCrossover(int jobCnt)
	{
		this.jobCount=jobCnt;
	}
	@Override
	public int getArity() {
		return 2;
	}
	//To Be Fixed
 public Solution[] evolve(Solution[] parents)
	{ 
	
		Solution o1=this.getOffspring(parents[0],parents[1]);
		Solution o2=this.getOffspring(parents[1],parents[0]);
	    return new Solution[] {o1,o2};
	}
 	Solution getOffspring(Solution p1,Solution p2)
 	{
 	 	int size=p1.getNumberOfVariables();
  		JobT []parent1=new JobT[size];
  		JobT []parent2=new JobT[size];
  		int ind1[]=new int[size];
  		int ind2[]=new int[size];
  		int cnt1[]=new int[this.jobCount];
  		int cnt2[]=new int[this.jobCount];
  		
		for(int i=0;i<size;i++)
		{
			parent1[i]=(JobT)p1.getVariable(i);
			parent2[i]=(JobT)p2.getVariable(i);
			ind1[i]=cnt1[(int) parent1[i].jobID]++;
			ind2[i]=cnt2[(int) parent2[i].jobID]++;
		}
		
		//1/3*size <= cut length <= 1/2*size
		int cutLen=(int)(0.33*size +Math.random()*0.17*size);
		int ind=(int)(Math.random()*size);
//		System.out.println(ind+"----"+cutLen);
		Solution offspring=new Solution(p1.getNumberOfVariables(),p1.getNumberOfObjectives());
		int indf=0;
		for(int k=ind;k<ind+cutLen;k++)
		{
			int ktemp=(k%size==0)?size-1:k%size;
			for(int j=0;j<size;j++)
				if(ind2[j]==ind1[k%size] && parent2[j].jobID==parent1[k%size].jobID)
				{
					ind2[j]=-1;
					if(ind==k)
						indf=j+1;
					break;
				}
		}
		for(int k=0;k<cutLen;k++)
		{
			int ktemp=(k+indf)%size;
			offspring.setVariable(ktemp,parent1[(k+ind)%size]);
		}
//		for(int i=0;i<offspring.getNumberOfVariables();i++)
//			System.out.println(offspring.getVariable(i));
		int k=0;
		for(int i=0;i<offspring.getNumberOfVariables();i++)
		{
			while(ind2[k%size]==-1)k++;
			if(offspring.getVariable(i)==null)
				offspring.setVariable(i,parent2[k++%size]);
		}
		return offspring;
 	}
}
