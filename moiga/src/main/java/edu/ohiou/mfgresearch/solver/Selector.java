package edu.ohiou.mfgresearch.solver;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.moeaframework.core.Solution;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 * A selector function wraps around the Watchmaker SelectionStrategy classes
 * @author sarkara1
 *
 */
public class Selector implements SelectionStrategy<Solution>
{
	Function<List<List<Double>>,List<List<Double>>> SelectionFunction;
	SelectionStrategy<Object> selection;
	
	public Selector(SelectionStrategy<Object> selection)
	{
		this.selection=selection;
	}

	@Override
	public <S extends Solution> List<S> select(List<EvaluatedCandidate<S>> population, boolean naturalFitnessScores,
			int selectionSize, Random rng) {
		return selection.select(population, naturalFitnessScores, selectionSize, rng);
	}
}