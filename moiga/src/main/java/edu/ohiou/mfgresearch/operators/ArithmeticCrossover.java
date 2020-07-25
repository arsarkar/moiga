package edu.ohiou.mfgresearch.operators;

import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.RealVariable;

public class ArithmeticCrossover implements Variation {


	private final double alpha;

	public ArithmeticCrossover(double alpha) {
		this.alpha = alpha;
		try {
			if(alpha<0 || alpha>1)
				throw new Exception("Alpha should be within 0.0 and 1.0");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result1 = parents[0].copy();
		Solution result2 = parents[1].copy();
			for (int i = 0; i < result1.getNumberOfVariables(); i++) {
					Variable temp1= result1.getVariable(i);
					Variable temp2=result2.getVariable(i);
					result1.setVariable(i,
							new RealVariable(((RealVariable)temp1).getValue()*alpha+((RealVariable)temp2).getValue()*(1-alpha),((RealVariable)temp1).getLowerBound(),((RealVariable)temp1).getUpperBound()));
					result2.setVariable(i,
							new RealVariable(((RealVariable)temp2).getValue()*alpha+((RealVariable)temp1).getValue()*(1-alpha),((RealVariable)temp1).getLowerBound(),((RealVariable)temp1).getUpperBound()));
			}

		return new Solution[] { result1, result2 };
	}

	@Override
	public int getArity() {
		return 2;
	}

}

