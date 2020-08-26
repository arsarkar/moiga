package edu.ohiou.mfgresearch.moiga;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.UniformCrossover;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.problem.ZDT.ZDT1;
import org.moeaframework.problem.misc.Binh2;
import org.moeaframework.problem.misc.Fonseca2;
import org.moeaframework.problem.misc.Schaffer;
import org.uncommons.watchmaker.framework.selection.RankSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import edu.ohiou.mfgresearch.fuzzy.FuzzyAsympMembership;
import edu.ohiou.mfgresearch.fuzzy.FuzzyMembershipT;
import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;
import edu.ohiou.mfgresearch.solver.GASolver;
import edu.ohiou.mfgresearch.solver.Selector;

public class FuzzySolver {

	@Before
	public void setUp() throws Exception {
	}

	
	@Test
	public void testFuzzy() {
		FuzzyMembershipT fuzzy = new FuzzyMembershipT(25, 5, 20, 10);
		IntStream.range(5, 26)	
				 .map(n->{
					 System.out.print(n+"->");
					 return n;
				 })
				 .mapToDouble(i->fuzzy.findSatisfaction(i*1.0))
				 .forEach(System.out::println);
	}
	
	@Test
	public void testFuzzyAsymp() {
		FuzzyAsympMembership fuzzy = new FuzzyAsympMembership(3.0, 2.0);
		IntStream.range(-6, 10)	
		 .map(n->{
			 System.out.print(n+"->");
			 return n;
		 })
		 .mapToDouble(i->fuzzy.findSatisfaction(i*1.0))
		 .forEach(System.out::println);
	}
	
// 	@Test
// 	public void schaffer_fuzzy(){
// 		List<Double> list=new ArrayList<Double>();
// 		Function<List<Double>,Double> func=new BiFtoFWrapper(ComponentFactory.evaluatorMap.get("Schaffer-N2"));
// //		FuzzyFunction fuzzySchaffer = new FuzzyFunction(func, new FuzzyMembershipT(0, 1, uf, lf));
// 		FunctionSolver solver1 = new FunctionSolver(
				
// 												ComponentFactory.evaluatorMap.get("Schaffer-N2"), 
// 												  ()->false, 
// 												  2, 
// 												  ComponentFactory.factoryMap.get("SchafferFactory"), 
// 												  0.8, 
// 												  ComponentFactory.crossoverMap.get("ArithmeticCrossover"),
// 												  ComponentFactory.mutationMap.get("GaussianMutation"), 
// 												  data->{
// 													  System.out.printf("Generation %d: %s %s\n",
// 								                              data.getGenerationNumber(),data.getBestCandidateFitness(),func.apply(data.getBestCandidate()));
// 								                              list.add(data.getBestCandidateFitness());								                              		
// 												  },
// 												  new RankSelection(),
// 												  ComponentFactory.repairMap.get("NoRepair"),null);
// 		List<Double> can=solver1.buildEngine().evolve(100,10,new GenerationCount(200));
// 		System.out.println(can.toString());
// 		System.out.println(func.apply(can));
// 		LineChart chart = new LineChart("Value Evolve", "Value Evolve", list);
// 		chart.pack( );
// 		RefineryUtilities.centerFrameOnScreen( chart );
// 		chart.setVisible( true );
// 	}
	
	@Test
	public void GASolver1() throws InterruptedException{
		List<Fuzzyficator> fuzzys = new LinkedList<Fuzzyficator>();
		fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(140.0,0.0,120.0,110.0)));
		fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(50.0,0.0,40.0,30.0)));
		List<Variation> operators = Stream.of(new UniformCrossover(0.2), new UM(0.5))
										  .collect(Collectors.toList());
		GASolver solver = 
				new GASolver(new Binh2(), 
							 fuzzys, 
							 (a,b)->a+b,
							 operators,
							 new Double[]{0.2, 0.5},
							 new Selector(new RankSelection()), 
							 true, 
							 new Random(),
							 pop->{},
							 data->{});
		solver.evolve(100, 0, new GenerationCount(500)).copy();
		
	}
	
	//@Test
	//Test For all 70 blocks in B&K Function plot
	public void GASolver2() throws InterruptedException{
	  List<Solution> solutions=new ArrayList<Solution>();
	  for(int i=0;i<=130;i+=10)
		  for(int j=0;j<=40;j+=10)
	  {
		List<Fuzzyficator> fuzzys = new LinkedList<Fuzzyficator>();
		fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(140.0,0.0,i,i+10)));
		fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(50.0,0.0,j,j+10)));
		List<Variation> operators = Stream.of(new UniformCrossover(0.2), new UM(0.5))
										  .collect(Collectors.toList());
		GASolver solver = 
				new GASolver(new Binh2(), 
							 fuzzys, 
							 (a,b)->a+b,
							 operators,
							 new Double[]{0.2, 0.5},
							 new Selector(new RankSelection()), 
							 true, 
							 new Random(),
							 pop->{},
							 data->{});
		solutions.add(solver.evolve(100, 0, new GenerationCount(100)).copy());
	 }
	}
	@Test
	public void GASolver3() throws InterruptedException{
		List<Fuzzyficator> fuzzys = new LinkedList<Fuzzyficator>();
		fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(1.0,0.0,1.0,0.9)));
		fuzzys.add(new Fuzzyficator(new FuzzyMembershipT(1.0,0.0,0.1,0.0)));
		List<Variation> operators = Stream.of(new UniformCrossover(0.2), new UM(0.5))
										  .collect(Collectors.toList());
		GASolver solver = 
				new GASolver(new Fonseca2(), 
							 fuzzys, 
							 (a,b)->a+b,
							 operators,
							 new Double[]{0.2, 0.5},
							 new Selector(new RankSelection()), 
							 true, 
							 new Random(),
							 pop->{},
							 data->{});
		solver.evolve(100, 0, new GenerationCount(500)).copy();
		
	}
	
	@Test
	public void GASolverMultBinh2(){
		double min1=0.0,max1=140,step=10.1,min2=0.0,max2=80;
		  for(double i=min1;i<=max1;i+=step)
			  for(double j=min2;j<=max2;j+=step)
		  {
			List<Fuzzyficator> fuzzys = new LinkedList<Fuzzyficator>();
			fuzzys.add(new Fuzzyficator(new FuzzyAsympMembership(i+step,i)));
			fuzzys.add(new Fuzzyficator(new FuzzyAsympMembership(j+step,j)));
			List<Variation> operators = Stream.of(new UniformCrossover(0.2), new UM(0.5))
											  .collect(Collectors.toList());
			GASolver solver = 
					new GASolver(new Binh2(), 
								 fuzzys, 
								 (a,b)->a+b,
								 operators,
								 new Double[]{0.2, 0.5},
								 new Selector(new RankSelection()), 
								 true, 
								 new Random(),
								 pop->{},
								 data->{});
			Solution sol=solver.evolve(100, 0, new GenerationCount(500)).copy();
			System.out.println(sol.getObjective(0)+","+
							   sol.getObjective(1));
		  }
	}
	
	@Test
	public void GASolverMultFonseca2(){
		double min1=0.0, max1=1.0, step=.1, min2=0.0, max2=1.0;
		  for(double i=min1;i<=max1;i+=step)
			  for(double j=min2;j<=max2;j+=step)
		  {
			List<Fuzzyficator> fuzzys = new LinkedList<Fuzzyficator>();
			fuzzys.add(new Fuzzyficator(new FuzzyAsympMembership(i+step,i)));
			fuzzys.add(new Fuzzyficator(new FuzzyAsympMembership(j+step,j)));
			List<Variation> operators = Stream.of(new UniformCrossover(0.2), new UM(0.5))
											  .collect(Collectors.toList());
			GASolver solver = 
					new GASolver(new Fonseca2(), 
								 fuzzys, 
								 (a,b)->a+b,
								 operators,
								 new Double[]{0.2, 0.5},
								 new Selector(new RankSelection()), 
								 true, 
								 new Random(),
								 pop->{},
								 data->{});
			Solution sol=solver.evolve(100, 0, new GenerationCount(500)).copy();
			System.out.println(sol.getObjective(0)+","+
							   sol.getObjective(1));
		  }
	}
	
	//not successful
	@Test
	public void GASolverMultZDT1(){
		double min1=0.0, max1=1.0, step=.1, min2=0.0, max2=1.0;
		  for(double i=min1;i<=max1;i+=step)
			  for(double j=min2;j<=max2;j+=step)
		  {
			List<Fuzzyficator> fuzzys = new LinkedList<Fuzzyficator>();
			fuzzys.add(new Fuzzyficator(new FuzzyAsympMembership(i+step,i)));
			fuzzys.add(new Fuzzyficator(new FuzzyAsympMembership(j+step,j)));
			List<Variation> operators = Stream.of(new UniformCrossover(0.2), new UM(0.5))
											  .collect(Collectors.toList());
			GASolver solver = 
					new GASolver(new ZDT1(), 
								 fuzzys, 
								 (a,b)->a+b,
								 operators,
								 new Double[]{0.2, 0.5},
								 new Selector(new RankSelection()), 
								 true, 
								 new Random(),
								 pop->{},
								 data->{});
			Solution sol=solver.evolve(100, 0, new GenerationCount(10000)).copy();
			System.out.println(i+","+
							   j);
			System.out.println(sol.getObjective(0)+","+
							   sol.getObjective(1));
		  }
	}
	
	//*successfull.
	@Test
	public void GASolverMultScahffer1(){
		double min1=0.0, max1=4.0, step=.5, min2=0.0, max2=4.5;
		  for(double i=min1;i<=max1;i+=step)
			  for(double j=min2;j<=max2;j+=step)
		  {
			List<Fuzzyficator> fuzzys = new LinkedList<Fuzzyficator>();
			fuzzys.add(new Fuzzyficator(new FuzzyAsympMembership(i+step,i)));
			fuzzys.add(new Fuzzyficator(new FuzzyAsympMembership(j+step,j)));
			List<Variation> operators = Stream.of(new UniformCrossover(0.2), new UM(0.5))
											  .collect(Collectors.toList());
			GASolver solver = new GASolver(new Schaffer(), 
											   fuzzys, 
											   (a,b)->a+b,
											   operators,
											   new Double[]{0.2, 0.5},
											   new Selector(new RankSelection()), 
											   true, 
											   new Random(),
											   pop->{},
											   data->{});
			Solution sol=solver.evolve(100, 0, new GenerationCount(500)).copy();
			System.out.println(sol.getObjective(0)+","+
							   sol.getObjective(1));
		  }
	}
}
