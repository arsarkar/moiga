package edu.ohiou.mfgresearch.solver.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.DominanceComparator;

import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;

public class FuzzyNSGA extends AbstractEvolutionaryAlgorithm implements EpsilonBoxEvolutionaryAlgorithm {

	/**
	 * The selection operator. If {@code null}, this algorithm uses binary
	 * tournament selection without replacement, replicating the behavior of the
	 * original NSGA-II implementation.
	 */
	private final Selection selection;

	/**
	 * The variation operator.
	 */
	private final Variation variation;

	private List<Fuzzyficator> fuzzyficators;
	private BinaryOperator<Double> aggregator;

	private static int numGeneration = 0;

	/**
	 * Constructs the NSGA-II algorithm with the specified components.
	 * 
	 * @param problem        the problem being solved
	 * @param population     the population used to store solutions
	 * @param archive        the archive used to store the result; can be
	 *                       {@code null}
	 * @param selection      the selection operator
	 * @param variation      the variation operator
	 * @param initialization the initialization method
	 */
	public FuzzyNSGA(Problem problem, NondominatedSortingPopulation population, EpsilonBoxDominanceArchive archive,
			Selection selection, Variation variation, Initialization initialization, List<Fuzzyficator> fuzzyficators,
			BinaryOperator<Double> aggregator) {
		super(problem, population, archive, initialization);
		this.selection = selection;
		this.variation = variation;
		this.fuzzyficators = fuzzyficators;
		this.aggregator = aggregator;
	}

	@Override
	public void iterate() {
		NondominatedSortingPopulation population = getPopulation();
		EpsilonBoxDominanceArchive archive = getArchive();
		Population offspring = new Population();
		int populationSize = population.size();

		if (selection == null) {
			// recreate the original NSGA-II implementation using binary
			// tournament selection without replacement; this version works by
			// maintaining a pool of candidate parents.
			LinkedList<Solution> pool = new LinkedList<Solution>();

			DominanceComparator comparator = new ChainedComparator(//new FuzzyParetoDominanceComparator(fuzzyficators, aggregator),
					 											   new FuzzyComparator(fuzzyficators, aggregator),
																   new CrowdingComparator()
																   );
			System.out.print("\nGeneration # " + numGeneration++);
			System.out.print(" Best candiates = ");
			getResult().forEach(s -> System.out.print("[(" + s.getObjective(0) + ", " + fuzzyficators.get(0).apply(s.getObjective(0))  + "), "
													 +"(" + s.getObjective(1) + ", " + fuzzyficators.get(1).apply(s.getObjective(1))  + ")]"));
			System.out.print("\n");

			// for (int i = 0; i < population.size(); i++) {
			// 	if(i<4){
			// 		System.out.println(population.get(i).getObjective(0) + " " + population.get(i).getObjective(1));
			// 	}
			// }
			// population.sort(new Comparator<Solution>(){

			// 	@Override
			// 	public int compare(Solution o1, Solution o2) {
			// 		Double value1 = 
			// 		IntStream.range(0, o1.getNumberOfObjectives())
			// 									.boxed()
			// 									.map(i->fuzzyficators.get(i).apply(o1.getObjective(i))) //fuzzyfy the solution
			// 									.reduce(aggregator) //add all fuzzy satisfaction
			// 									.get();
			// 		Double value2 = 
			// 		IntStream.range(0, o2.getNumberOfObjectives())
			// 									.boxed()
			// 									.map(i->fuzzyficators.get(i).apply(o2.getObjective(i))) //fuzzyfy the solution
			// 									.reduce(aggregator) //add all fuzzy satisfaction
			// 									.get();
			
			// 		if (value1 == value2) {
			// 			return 0;
			// 		} else if (value1 < value2) {
			// 			return 1;
			// 		} else {
			// 			return -1;
			// 		}
			// 	}
				
			// });;
			// System.out.println("After sort -->");
			// for (int i = 0; i < population.size(); i++) {
			// 	if(i<4){
			// 		System.out.println(population.get(i).getObjective(0) + " " + population.get(i).getObjective(1));
			// 	}
			// }

			while (offspring.size() < populationSize) {
				// ensure the pool has enough solutions
				while (pool.size() < 2*variation.getArity()) {
					List<Solution> poolAdditions = new ArrayList<Solution>();
					
					for (Solution solution : population) {
						poolAdditions.add(solution);
					}
					
					PRNG.shuffle(poolAdditions);
					pool.addAll(poolAdditions);
				}
				
				// select the parents using a binary tournament
				Solution[] parents = new Solution[variation.getArity()];
				
				for (int i = 0; i < parents.length; i++) {
					Solution s1 = pool.removeFirst();
					Solution s2 = pool.removeFirst();
					parents[i] = TournamentSelection.binaryTournament(
							s1,
							s2,
							comparator);
					// System.out.printf("(s1 = %.2f, %.2f, %.2f, %.2f)", s1.getObjective(0), s1.getObjective(1), 
					// fuzzyficators.get(0).apply(s1.getObjective(0)), fuzzyficators.get(1).apply(s1.getObjective(1)));
					// System.out.printf("(s2 = %.2f, %.2f, %.2f, %.2f)", s2.getObjective(0), s2.getObjective(1), 
					// fuzzyficators.get(0).apply(s2.getObjective(0)), fuzzyficators.get(1).apply(s2.getObjective(1)));	
					// System.out.printf("(selected = %.2f, %.2f, %.2f, %.2f)\n", parents[i].getObjective(0), parents[i].getObjective(1), 
					// fuzzyficators.get(0).apply(parents[i].getObjective(0)), fuzzyficators.get(1).apply(parents[i].getObjective(1)));				
				}
				
				// evolve the children
				offspring.addAll(variation.evolve(parents));
			}
		} else {
			// run NSGA-II using selection with replacement; this version allows
			// using custom selection operators
			while (offspring.size() < populationSize) {
				Solution[] parents = selection.select(variation.getArity(),
						population);

				offspring.addAll(variation.evolve(parents));
			}
		}

		evaluateAll(offspring);

		if (archive != null) {
			archive.addAll(offspring);
		}

		population.addAll(offspring);
		population.truncate(populationSize);
	}

	@Override
	public EpsilonBoxDominanceArchive getArchive() {
		return (EpsilonBoxDominanceArchive)super.getArchive();
	}

	@Override
	public NondominatedSortingPopulation getPopulation() {
		return (NondominatedSortingPopulation)super.getPopulation();
	}
    
}
