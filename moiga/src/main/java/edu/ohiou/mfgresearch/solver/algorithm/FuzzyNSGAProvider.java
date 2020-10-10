package edu.ohiou.mfgresearch.solver.algorithm;

import java.util.List;
import java.util.Properties;
import java.util.function.BinaryOperator;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.OperatorFactory;

import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;

import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

public class FuzzyNSGAProvider extends AlgorithmProvider {

    private List<Fuzzyficator> fuzzyficators;
    private BinaryOperator<Double> aggregator; 

    public FuzzyNSGAProvider(List<Fuzzyficator> fuzzyficators, BinaryOperator<Double> aggregator) {
            this.fuzzyficators = fuzzyficators;
            this.aggregator = aggregator;
    }          

    @Override
    public Algorithm getAlgorithm(String name, Properties properties, Problem problem) {
	if (name.equalsIgnoreCase("FuzzyNSGAII")) {

            int populationSize = Integer.parseInt(properties.getProperty("populationSize", "100"));

            Initialization initialization = new RandomInitialization(problem,
                    populationSize);
    
            NondominatedSortingPopulation population = 
                    new NondominatedSortingPopulation();
    
            TournamentSelection selection = null;
            
            if (Boolean.getBoolean(properties.getProperty("withReplacement", "true"))) {
                selection = new TournamentSelection(2, new ChainedComparator(
                        new ParetoDominanceComparator(),
                        new CrowdingComparator()));
            }
    
            Variation variation = OperatorFactory.getInstance().getVariation(null, 
                    properties, problem);
    
            return new FuzzyNSGA(problem, population, null, selection, variation,
                    initialization, fuzzyficators, aggregator);
		} else {
			// return null if the user requested a different algorithm
			return null;
		}
    }
}
