package edu.ohiou.mfgresearch.operators;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import edu.ohiou.mfgresearch.schedule.Job;

public class SwapMutation implements Variation
{
	private final double probability;

	public SwapMutation(double probability) {
		this.probability = probability;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result = parents[0].copy();
		int ind1=2;
		try{ind1 = PRNG.nextInt(result.getNumberOfVariables());}
		catch(NotStrictlyPositiveException e)
		{
			System.out.println(result.getVariable(0));
		}
		int ind2 = PRNG.nextInt(result.getNumberOfVariables() - 1);
		if (ind1 == ind2)
		{
			ind2 = (ind2+1) % result.getNumberOfVariables() ;
		}
		Job t=(Job) result.getVariable(ind1);
		result.setVariable(ind1, result.getVariable(ind2));
		result.setVariable(ind2, t);
		return new Solution[] { result };
	}


	@Override
	public int getArity() {
		return 1;
	}

}

