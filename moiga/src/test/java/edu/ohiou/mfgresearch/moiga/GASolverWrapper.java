package edu.ohiou.mfgresearch.moiga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.selection.RankSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import edu.ohiou.mfgresearch.fuzzy.FuzzyMembershipT;
import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;
import edu.ohiou.mfgresearch.schedule.Job;
import edu.ohiou.mfgresearch.solver.GASolver;
import edu.ohiou.mfgresearch.solver.Selector;
import edu.ohiou.mfgresearch.schedule.ScheduleProblem;

public class GASolverWrapper
{
	Problem prob; 
	BinaryOperator<Double> aggregator; 
	List<Variation> operators; 
	Double[] probabilities;
	Selector selection; 
	boolean isNatural; 
	Random rng;
	Consumer<Solution> solutionObserver;
	Consumer<PopulationData<? extends Solution>> dataObserver;
	List<List<Double>> fuzzyProfiles;
	int epoch;
	int steps[];
	Consumer<Job> jobModifier;
	long[] IDs;
	int dim;
	TerminationCondition[] term;
	/**
	 * <p>Will only work for 2/3 objectives</p>  
	 * <p>Please add fuzzys.add(Arrays.asList(1.0,0.0,1.0,0.0)); as the third fuzzy profile 
	 * when dim = 2</p>
	 * @param steps No of steps for each fuzzy bound
	 * @param jobModifier Job modification Strategy
	 * @param IDs The jobs to be modified
	 * @param term list of termination conditions
	 * <p>For rest of the parameters</p>
	 * @see GASolver 
	 * */
	public GASolverWrapper(Problem prob,  
			BinaryOperator<Double> aggregator, 
			List<Variation> operators, 
			Double[] probabilities,
			Selector selection, 
			boolean isNatural, 
			Random rng, 
			Consumer<Solution> solutionObserver,
			Consumer<PopulationData<? extends Solution>> dataObserver,
			List<List<Double>> fuzzyProfiles,
			//int epoch,
			int[] steps,
			long[] IDs,
			Consumer<Job> jobModifier,
			TerminationCondition[] term,
			int dim)
	{
		this.prob=prob;
		this.aggregator=aggregator;
		this.operators=operators;
		this.probabilities=probabilities;
		this.selection=selection;
		this.isNatural=isNatural;
		this.rng=rng;
		this.solutionObserver=solutionObserver;
		this.dataObserver=dataObserver;
		this.fuzzyProfiles=fuzzyProfiles;
		//this.epoch=epoch;
		this.steps=steps;
		this.jobModifier=jobModifier;
		this.IDs=IDs;
		this.dim=dim;
		this.term=term;
	}
	
	public List<Solution> getTotalPop(boolean genRandom)throws Exception
	{
		double[] prob1=new double[this.fuzzyProfiles.size()];//stores (final-initial)/no_of_steps of each profile
		List<Fuzzyficator> fuzzys = new LinkedList<Fuzzyficator>();
		List<Solution> totalPop=new ArrayList<Solution>();
		int c=0;
		System.out.println("Generation 0 ------------");
		System.out.println(prob1.length);
		for(List<Double> i:fuzzyProfiles)
		{
			
			prob1[c++]=(i.get(2)-i.get(3))/steps[c-1];
		}

		List<EvaluatedCandidate<Solution>> ob=new ArrayList<EvaluatedCandidate<Solution>>();
		List<Solution> pop=new ArrayList<Solution>();
		GASolver solver;
		c=0;
		
		for(double i=fuzzyProfiles.get(0).get(3);Math.round(i)<fuzzyProfiles.get(0).get(2);i+=prob1[0])
			for(double j=fuzzyProfiles.get(1).get(3);Math.round(j)<fuzzyProfiles.get(1).get(2);j+=prob1[1])
				for(double k=fuzzyProfiles.get(2).get(3);Math.round(k)<fuzzyProfiles.get(2).get(2);k+=prob1[2])
		{	
			if(c++<IDs.length)
				((ScheduleProblem)prob).modifyJob(IDs[c-1],this.jobModifier);
			totalPop.addAll(pop);
			fuzzys = new LinkedList<Fuzzyficator>();
			System.out.println("Epoch "+c+" ------------");
			
			System.out.println("Fuzzy bound 0 : "+fuzzyProfiles.get(0).get(0)+","+fuzzyProfiles.get(0).get(1)+","+(i+prob1[0])+","+i);
				fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(fuzzyProfiles.get(0).get(0),
																fuzzyProfiles.get(0).get(1),
																i+prob1[0],
																i)));
				
			System.out.println("Fuzzy bound 1 : "+fuzzyProfiles.get(1).get(0)+","+fuzzyProfiles.get(1).get(1)+","+(j+prob1[1])+","+j);
			fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(fuzzyProfiles.get(1).get(0),
																fuzzyProfiles.get(1).get(1),
																j+prob1[1],
																j)));
			if(dim==3)	
			{
				System.out.println("Fuzzy bound 2 : "+fuzzyProfiles.get(2).get(0)+","+fuzzyProfiles.get(2).get(1)+","+(k+prob1[2])+","+k);
				fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(fuzzyProfiles.get(2).get(0),
																fuzzyProfiles.get(2).get(1),
																k+prob1[2],
																k)));
			}
			
			solver = 
					new GASolver(prob, 
								 fuzzys, 
								 (a,b)->a+b,
								 operators,
								 new Double[]{0.2, 0.5},
								 new Selector(new RankSelection()), 
								 true, 
								 new Random(),
								 sol->{},
								 pop_data->{
//									 System.out.println(pop_data.getBestCandidateFitness()+" "+ Arrays.toString(pop_data.getBestCandidate().getObjectives())+"--->");
									 });
			if(genRandom)
				ob=solver.evolvePopulation(100, 0,term);
			else
				ob=solver.evolvePopulation(100, 0,pop,term);
			//Appending the total population obtained to pop
			pop=Arrays.stream(ob.toArray()).map(ii->((EvaluatedCandidate<Solution>)ii).getCandidate()).collect(Collectors.toList());
		}
		return totalPop;
    }
	
	private List<Solution> bestCandidates=new ArrayList<Solution>();
	
	public List<Solution> getBestCandidates(double threshold,boolean stepOrGen,boolean genRandom) throws Exception
	{
		double[] prob1=new double[this.fuzzyProfiles.size()];//stores (final-initial)/no_of_steps of each profile
		//prob1 stores the increment value for each objective
		List<Fuzzyficator> fuzzys = new LinkedList<Fuzzyficator>();
		List<Solution> bestStepCandidate=new ArrayList<Solution>();
		
		int c=0;
		for(List<Double> i:fuzzyProfiles)
		{
			prob1[c++]=(i.get(2)-i.get(3))/steps[c-1];
		}
		
		GASolver solver = null;
		List<EvaluatedCandidate<Solution>> ob=new ArrayList<EvaluatedCandidate<Solution>>();
		List<Solution> pop=new ArrayList<Solution>();
		c=0;
		Solution temp=null;
		for(double i=fuzzyProfiles.get(0).get(3);Math.round(i)<fuzzyProfiles.get(0).get(2);i+=prob1[0])
			for(double j=fuzzyProfiles.get(1).get(3);Math.round(j)<fuzzyProfiles.get(1).get(2);j+=prob1[1])
				for(double k=fuzzyProfiles.get(2).get(3);Math.round(k)<fuzzyProfiles.get(2).get(2);k+=prob1[2])
		{	

			if(c++<IDs.length)
				((ScheduleProblem)prob).modifyJob(IDs[c-1],this.jobModifier);
			//totalPop.addAll(pop);
			fuzzys = new LinkedList<Fuzzyficator>();
			System.out.println("Epoch "+c+" ------------");
			
			//Incrementing bound by corresponding prob1 value

			System.out.println("Fuzzy bound 0 : "+fuzzyProfiles.get(0).get(0)+","+fuzzyProfiles.get(0).get(1)+","+(i+prob1[0])+","+i);
				fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(fuzzyProfiles.get(0).get(0),
																fuzzyProfiles.get(0).get(1),
																i+prob1[0],
																i)));
			System.out.println("Fuzzy bound 1 : "+fuzzyProfiles.get(1).get(0)+","+fuzzyProfiles.get(1).get(1)+","+(j+prob1[1])+","+j);
			fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(fuzzyProfiles.get(1).get(0),
																fuzzyProfiles.get(1).get(1),
																j+prob1[1],
																j)));

			System.out.println("Fuzzy bound 2 : "+fuzzyProfiles.get(2).get(0)+","+fuzzyProfiles.get(2).get(1)+","+(k+prob1[2])+","+k);
			fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(fuzzyProfiles.get(2).get(0),
																fuzzyProfiles.get(2).get(1),
																k+prob1[2],
																k)));
			
			solver = new GASolver(prob, 
								 fuzzys, 
								 (a,b)->a+b,
								 operators,
								 new Double[]{0.2, 0.5},
								 new Selector(new RankSelection()), 
								 true, 
								 new Random(),
								 sol->{},
								 pop_data->{
									 if(pop_data.getBestCandidateFitness()>=threshold)
										 	bestCandidates.add(pop_data.getBestCandidate());
									 	//System.out.println(pop_data.getBestCandidateFitness()+","+
										//Arrays.toString(pop_data.getBestCandidate().getObjectives())+"--->");
										//Arrays.toString(pop_data.getBestCandidate().getObjectives()).replace(']',' ').replace('[',' '));
									 	});
			if(genRandom)
				ob=solver.evolvePopulation(100, 0,term);
			else
				ob=solver.evolvePopulation(100, 0,pop,term);
			//Storing best candidates in pop
			pop=Arrays.stream(ob.toArray()).map(ii->((EvaluatedCandidate<Solution>)ii).getCandidate()).collect(Collectors.toList());
			
			temp=pop.get(0);
			bestStepCandidate.add(temp);
			// System.out.println("Best Candidate : "+
			// Arrays.toString(temp.getObjectives()).replace(']',' ').replace('[',' ')
			// 		+","+new ManualFitnessCalc(fuzzys,aggregator).apply(temp));
			
		}
		if(stepOrGen)
			return bestStepCandidate;
		return bestCandidates;
    }
}
