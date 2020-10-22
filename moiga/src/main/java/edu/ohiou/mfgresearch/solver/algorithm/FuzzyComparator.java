package edu.ohiou.mfgresearch.solver.algorithm;

import java.io.Serializable;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;

import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;

public class FuzzyComparator implements DominanceComparator, Serializable {

    private static final long serialVersionUID = 1L;
    private List<Fuzzyficator> fuzzyficators;
    private BinaryOperator<Double> aggregator;   

    public FuzzyComparator(List<Fuzzyficator> fuzzyficators) {
        this.fuzzyficators = fuzzyficators;
    } 

    public FuzzyComparator(List<Fuzzyficator> fuzzyficators, BinaryOperator<Double> aggregator) {
        this.fuzzyficators = fuzzyficators;
        this.aggregator = aggregator;
    }

    @Override
    public int compare(Solution solution1, Solution solution2) {
        Double value1, value2;
        if(aggregator == null){
            value1 = fuzzyficators.get(0).apply(solution1.getObjective(0));
            value2 = fuzzyficators.get(0).apply(solution2.getObjective(0));
        }
        else{
            value1 = IntStream.range(0, solution1.getNumberOfObjectives())
                                        .boxed()
                                        .map(i->fuzzyficators.get(i).apply(solution1.getObjective(i))) //fuzzyfy the solution
                                        .reduce(aggregator) //add all fuzzy satisfaction
                                        .get();
            value2 = IntStream.range(0, solution2.getNumberOfObjectives())
                                        .boxed()
                                        .map(i->fuzzyficators.get(i).apply(solution2.getObjective(i))) //fuzzyfy the solution
                                        .reduce(aggregator) //add all fuzzy satisfaction
                                        .get();
        }

        if (value1 == value2) {
			return 0;
		} else if (value1 < value2) {
			return 1;
		} else {
			return -1;
		}
    }
    
}
