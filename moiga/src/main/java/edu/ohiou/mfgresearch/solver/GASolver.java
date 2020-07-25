package edu.ohiou.mfgresearch.solver;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;

import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;

public class GASolver extends GenerationalEvolutionEngine<Solution> {

	private Problem problem;
    /**
     * Creates a new evolution engine by specifying the various components required by
     * a generational evolutionary algorithm.
     * @param candidateFactory Factory used to create the initial population that is
     * iteratively evolved.
     * @param evolutionScheme The combination of evolutionary operators used to evolve
     * the population at each generation.
     * @param fitnessEvaluator A function for assigning fitness scores to candidate
     * solutions.
     * @param selectionStrategy A strategy for selecting which candidates survive to
     * be evolved.
     * @param rng The source of randomness used by all stochastic processes (including
     * evolutionary operators and selection strategies).
     */
//    protected GASolver(CandidateFactory<Solution> candidateFactory,
//            EvolutionaryOperator<Solution> evolutionScheme,
//            FitnessEvaluator<Solution> fitnessEvaluator,
//            SelectionStrategy<Solution> selectionStrategy,
//            Random rng)
//		{
//		super(candidateFactory, fitnessEvaluator, rng);
//		this.evolutionScheme = evolutionScheme;
//		this.fitnessEvaluator = fitnessEvaluator;
//		this.selectionStrategy = selectionStrategy;
//		}
    
    /**
     * Receives Problem in MOEA structure, which contains objectives and variables
     * also receives operators from MOEA
     * @param prob
     * @param aggregator
     * @param operators
     * @param selection
     * @param isNatural
     * @param rng
     */
	public GASolver(Problem prob, 
					List<edu.ohiou.mfgresearch.fuzzy.Fuzzyficator> fuzzyficators,
					BinaryOperator<Double> aggregator, 
					List<Variation> operators, 
					Double[] probabilities,
					Selector selection, 
					boolean isNatural, 
					Random rng, 
					Consumer<Solution> solutionObserver,
					Consumer<PopulationData<? extends Solution>> dataObserver){
		super(createCandidateFactory(prob), 
			 createOperatorPipe(operators, probabilities, rng), 
			 createFitnessEvaluator(prob, fuzzyficators, aggregator, isNatural), 
			 selection, 
			 rng);
		
		addEvolutionObserver(new EvolutionObserver<Solution>() {

			@Override
			public void populationUpdate(PopulationData<? extends Solution> data) {
//				System.out.print("Isle:Gen#"+data.getGenerationNumber());
				Solution best = data.getBestCandidate();				
//				System.out.print(" Vars->");
//				IntStream.range(0, best.getNumberOfVariables()).forEach(v->System.out.print(" "+best.getVariable(v)));
//				System.out.print(" Obj->");
//				IntStream.range(0, best.getNumberOfObjectives()).forEach(o->System.out.print(" f"+o+": "+best.getObjective(o)));
//				System.out.print(" Opt->"+data.getBestCandidateFitness());
//				System.out.print("\n");
//				best.getAttributes().forEach((k,v)->{
//					System.out.println(" Att:"+k.toString()+","+v.toString());
//				});
				solutionObserver.accept(best);
				dataObserver.accept(data);
			}
		});
		this.problem = prob;
	}

	private static EvolutionaryOperator<Solution> createOperatorPipe(List<Variation> operators, Double[] probability, Random rng) {
//		List<EvolutionaryOperator<Solution>> watchOps = new LinkedList<EvolutionaryOperator<Solution>>();
		//add the crosover operator
		//watchOps.add(new CrossoverOperator(1, 0.5, operators.get(0)));
		//add the mutation operator
//		watchOps.add(new EvolutionaryOperator<Solution>() {
//					@Override
//					public List<Solution> apply(List<Solution> selectedCandidates, Random rng) {
//						Solution[] parent = selectedCandidates.stream().toArray(Solution[]::new);
//						return Arrays.asList(operators.get(1).evolve(parent));
//					}
//				});
		EvolutionaryOperator<Solution> pipeline = new EvolutionaryOperator<Solution>() {

			@Override
			public List<Solution> apply(List<Solution> selectedCandidates, Random rng) {
				EvolutionaryVariation variation = new EvolutionaryVariation();
				variation.setOperator(operators.get(0), probability[0]);
				variation.setOperator(operators.get(1), probability[1]);
				return Arrays.asList(variation.evolve(selectedCandidates.toArray(new Solution[selectedCandidates.size()])));
			}
		};
		return pipeline;
	}

	/**
	 * Create a new Fitness Evaluator from the Problem
	 * @param prob
	 * @return
	 */
	public static CandidateFactory<Solution> createCandidateFactory(Problem prob){
		CandidateFactory<Solution> candidateFactory = new CandidateFactory<Solution>() {

			@Override
			public List<Solution> generateInitialPopulation(int populationSize, Random rng) {
				return IntStream.range(0, populationSize)
						.boxed()
						.map(i->generateRandomCandidate(rng)) //generate random initial solution
						.collect(Collectors.toList());				
			}

			@Override
			public List<Solution> generateInitialPopulation(int populationSize, Collection<Solution> seedCandidates,
					Random rng) {
				//pop size is pop size - seed count (seed count cannot be more than pop size)
				populationSize = Math.max(0, populationSize - seedCandidates.size());
				//fill the rest of the pop with random solution
				List<Solution> pop =
				IntStream.range(0, populationSize)
						.boxed()
						.map(i->generateRandomCandidate(rng))
						.collect(Collectors.toList()); //generate random initial solution
				pop.addAll(seedCandidates); // seed candidates need to be added
				return pop;
			}

			@Override
			public Solution generateRandomCandidate(Random rng) {
				RandomInitialization init = new RandomInitialization(prob, 1);
				Solution[] sols =  init.initialize();
				return sols[0];
			}
		};
		return candidateFactory;
	}
	
	public static FitnessEvaluator<Solution> createFitnessEvaluator(Problem prob, List<Fuzzyficator> fuzzyficators, BinaryOperator<Double> aggregator, boolean isNatural){
		FitnessEvaluator<Solution> fitnessEvaluator = new FitnessEvaluator<Solution>() {

			@Override
			public double getFitness(Solution candidate, List<? extends Solution> population) {
				prob.evaluate(candidate);
				return
				IntStream.range(0, candidate.getNumberOfObjectives())
						.boxed()
						.map(i->fuzzyficators.get(i).apply(candidate.getObjective(i))) //fuzzyfy the solution
						.reduce(aggregator) //add all fuzzy satisfaction
						.get();
			}

			@Override
			public boolean isNatural() {
				return isNatural;
			}
		};
		return fitnessEvaluator;
	}

}
