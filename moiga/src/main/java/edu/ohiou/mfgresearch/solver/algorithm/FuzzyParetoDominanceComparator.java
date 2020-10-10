package edu.ohiou.mfgresearch.solver.algorithm;

import java.io.Serializable;
import java.util.List;
import java.util.function.BinaryOperator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;

import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;

public class FuzzyParetoDominanceComparator implements DominanceComparator,
Serializable {

	private static final long serialVersionUID = 1L;
    private List<Fuzzyficator> fuzzyficators;
    private BinaryOperator<Double> aggregator;    

    public FuzzyParetoDominanceComparator(List<Fuzzyficator> fuzzyficators, BinaryOperator<Double> aggregator) {
        this.fuzzyficators = fuzzyficators;
        this.aggregator = aggregator;
    }

	@Override
	public int compare(Solution solution1, Solution solution2) {
		boolean dominate1 = false;
		boolean dominate2 = false;

		for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
			if (fuzzyficators.get(i).apply(solution1.getObjective(i)) > fuzzyficators.get(i).apply(solution2.getObjective(i))) {
				dominate1 = true;

				if (dominate2) {
					return 0;
				}
			} else if (fuzzyficators.get(i).apply(solution1.getObjective(i)) < fuzzyficators.get(i).apply(solution2.getObjective(i))) {
				dominate2 = true;

				if (dominate1) {
					return 0;
				}
			}
		}

		if (dominate1 == dominate2) {
			return 0;
		} else if (dominate1) {
			return -1;
		} else {
			return 1;
		}
	}
}
