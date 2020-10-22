package edu.ohiou.mfgresearch.moiga;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.UniformCrossover;
import org.moeaframework.core.operator.permutation.PMX;
import org.moeaframework.core.operator.permutation.Swap;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.OperatorProvider;
import org.moeaframework.problem.misc.Binh2;
import org.uncommons.watchmaker.framework.selection.RankSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import edu.ohiou.mfgresearch.fuzzy.FuzzyMembershipT;
import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;
import edu.ohiou.mfgresearch.operators.PMXCrossover;
import edu.ohiou.mfgresearch.operators.SwapMutation;
import edu.ohiou.mfgresearch.schedule.Job;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.schedule.ScheduleProblem;
import edu.ohiou.mfgresearch.solver.GASolver;
import edu.ohiou.mfgresearch.solver.Selector;


public class SingleMachProblemTest {

	@Test
	public void testInitialScheduleCandidate() throws Exception{
		List<Job> jobs = new LinkedList<Job>();
		jobs.add(new Job(1, 2.0, 1.0));
		jobs.add(new Job(2, 3.0, 3.0));
		jobs.add(new Job(3, 4.0, 4.0));
		jobs.add(new Job(4, 1.0, 8.0));
		jobs.add(new Job(5, 3.0, 2.0));
		Problem prob = new ScheduleProblem(jobs, new LinkedList<PerformanceMeasures>());
		RandomInitialization init = new RandomInitialization(prob, 10);
		Solution[] sols =  init.initialize();
		for(Solution s:sols){
			System.out.println("Solution ->");
			for(int i=0; i<s.getNumberOfVariables(); i++){
				System.out.print(s.getVariable(i));
			}
			
		}
	}
	
	@Test
	public void testScheduleEvaluation(){
		List<Job> jobs = new LinkedList<Job>();

		jobs.add(0,new Job(1,4.0,4.0,0.0));
		jobs.add(1,new Job(4,7.0,18.0,11.0));
		jobs.add(2,new Job(6,5.0,26.0,21.0));
		jobs.add(3,new Job(9,5.0,47.0,42.0));
		jobs.add(4,new Job(11,7.0,55.0,48.0));
		jobs.add(5,new Job(12,6.0,61.0,55.0));
		jobs.add(6,new Job(13,4.0,65.0,61.0));
		jobs.add(7,new Job(14,1.0,66.0,65.0));
		jobs.add(8,new Job(15,9.0,75.0,66.0));
		jobs.add(9,new Job(16,9.0,84.0,75.0));
		jobs.add(10,new Job(17,9.0,93.0,84.0));
		jobs.add(11,new Job(18,8.0,101.0,93.0));
		jobs.add(12,new Job(19,7.0,108.0,101.0));
		jobs.add(13,new Job(20,6.0,114.0,108.0));
		jobs.add(14,new Job(3,2.0,11.0,9.0));
		jobs.add(15,new Job(2,5.0,9.0,4.0));
		jobs.add(16,new Job(10,1.0,48.0,47.0));
		jobs.add(17,new Job(5,3.0,21.0,18.0));
		jobs.add(18,new Job(8,7.0,42.0,35.0));
		jobs.add(19,new Job(7,9.0,35.0,26.0));
		Solution sol=new Solution(20, 12);

		sol.setVariable(0,new Job(1,4.0,4.0,0.0));
		sol.setVariable(1,new Job(4,7.0,18.0,11.0));
		sol.setVariable(2,new Job(6,5.0,26.0,21.0));
		sol.setVariable(3,new Job(9,5.0,47.0,42.0));
		sol.setVariable(4,new Job(11,7.0,55.0,48.0));
		sol.setVariable(5,new Job(12,6.0,61.0,55.0));
		sol.setVariable(6,new Job(13,4.0,65.0,61.0));
		sol.setVariable(7,new Job(14,1.0,66.0,65.0));
		sol.setVariable(8,new Job(15,9.0,75.0,66.0));
		sol.setVariable(9,new Job(16,9.0,84.0,75.0));
		sol.setVariable(10,new Job(17,9.0,93.0,84.0));
		sol.setVariable(11,new Job(18,8.0,101.0,93.0));
		sol.setVariable(12,new Job(19,7.0,108.0,101.0));
		sol.setVariable(13,new Job(20,6.0,114.0,108.0));
		sol.setVariable(14,new Job(3,2.0,11.0,9.0));
		sol.setVariable(15,new Job(2,5.0,9.0,4.0));
		sol.setVariable(16,new Job(10,1.0,48.0,47.0));
		sol.setVariable(17,new Job(5,3.0,21.0,18.0));
		sol.setVariable(18,new Job(8,7.0,42.0,35.0));
		sol.setVariable(19,new Job(7,9.0,35.0,26.0));
		
		
		List<PerformanceMeasures> measures = new LinkedList<PerformanceMeasures>();
		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
		measures.add(PerformanceMeasures.AVERAGE_COMPLETION_TIME);
		measures.add(PerformanceMeasures.AVERAGE_WAITING_TIME);
		measures.add(PerformanceMeasures.MAKESPAN);
		measures.add(PerformanceMeasures.MAXIMUM_EARLINESS);
		measures.add(PerformanceMeasures.MAXIMUM_LATENESS);
		measures.add(PerformanceMeasures.NUM_TARDY_JOB);
		measures.add(PerformanceMeasures.NUM_EARLY_JOB);
		measures.add(PerformanceMeasures.MAXIMUM_TARDINESS);
		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
		measures.add(PerformanceMeasures.AVERAGE_FLOW_TIME);
		measures.add(PerformanceMeasures.TOTAL_EARLINESS);
		Problem prob=null;
		try {
			prob = new ScheduleProblem(jobs, measures);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			System.out.println("Evaluation->");
			prob.evaluate(sol);
			for(int i=0; i<measures.size(); i++){
				System.out.print(measures.get(i).name() + " = "+sol.getObjective(i)+"\n");
			}
			for(int i=0; i<jobs.size(); i++)
				System.out.println(sol.getVariable(i));
		}
	//}
	
	@Test
	public void testScheduleCrossover(){
		List<Job> jobs = new LinkedList<Job>();
		jobs.add(new Job(1, 2.0, 1.0));
		jobs.add(new Job(2, 3.0, 3.0));
		jobs.add(new Job(3, 4.0, 4.0));
		jobs.add(new Job(4, 5.0, 8.0));
		jobs.add(new Job(5, 3.0, 2.0));
		jobs.add(new Job(6, 2.0, 10.0));
		jobs.add(new Job(7, 1.0, 14.0));
		jobs.add(new Job(8, 4.0, 12.0));
		jobs.add(new Job(9, 3.0, 20.0));
		jobs.add(new Job(10, 2.0, 18.0));
		List<PerformanceMeasures> measures = new LinkedList<PerformanceMeasures>();
		measures.add(PerformanceMeasures.AVERAGE_COMPLETION_TIME);
		measures.add(PerformanceMeasures.NUM_TARDY_JOB);
		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
		Problem prob=null;
		try {
			prob = new ScheduleProblem(jobs, measures);
		} catch (Exception e) {
			e.printStackTrace();
		}
		RandomInitialization init = new RandomInitialization(prob, 2);
		Solution[] sols =  init.initialize();
		for(Solution s:sols){
			System.out.println("Solution ->");
			for(int i=0; i<s.getNumberOfVariables(); i++){
				//System.out.print(s.getVariable(i));
				System.out.print(((Job)s.getVariable(i)).jobID+" ");
			}
			System.out.println();
		}
		
		Variation variation = new PMXCrossover();
		sols = variation.evolve(sols);
		for(Solution s:sols){
			System.out.println("Crossed Solution ->");
			for(int i=0; i<s.getNumberOfVariables(); i++){
				//System.out.print(s.getVariable(i));
				System.out.print(((Job)s.getVariable(i)).jobID+" ");
			}
			System.out.println();
		}
	}

	@Test
	public void testScheduleCover()
	{
		Solution[] sols =new Solution[2];
		sols[0]=new Solution(10, 2);
		sols[1]=new Solution(10, 2);
		sols[0].setVariable(0,new Job(8,1,1));
		sols[0].setVariable(1,new Job(3,1,1));
		sols[0].setVariable(2,new Job(2,1,1));
		sols[0].setVariable(3,new Job(10,1,1));
		sols[0].setVariable(4,new Job(4,1,1));
		sols[0].setVariable(5,new Job(7,1,1));
		sols[0].setVariable(6,new Job(9,1,1));
		sols[0].setVariable(7,new Job(1,1,1));
		sols[0].setVariable(8,new Job(5,1,1));
		sols[0].setVariable(9,new Job(6,1,1));
		
		sols[1].setVariable(0,new Job(8,1,1));
		sols[1].setVariable(1,new Job(7,1,1));
		sols[1].setVariable(2,new Job(5,1,1));
		sols[1].setVariable(3,new Job(4,1,1));
		sols[1].setVariable(4,new Job(1,1,1));
		sols[1].setVariable(5,new Job(9,1,1));
		sols[1].setVariable(6,new Job(3,1,1));
		sols[1].setVariable(7,new Job(2,1,1));
		sols[1].setVariable(8,new Job(6,1,1));
		sols[1].setVariable(9,new Job(10,1,1));
		for(Solution s:sols){
			System.out.println("Solution ->");
			for(int i=0; i<s.getNumberOfVariables(); i++){
				System.out.print(((Job)(s.getVariable(i))).jobID+" ");
			}System.out.println();
	}
		sols=new PMXCrossover().evolve(sols);
		for(Solution s:sols){
			System.out.println("Crossover Solution ->");
			for(int i=0; i<s.getNumberOfVariables(); i++){
				System.out.print(((Job)(s.getVariable(i))).jobID+" ");
			}System.out.println();
	}

	}
	
	@Test
	public void MOEA_Schedule1() throws Exception{
		List<Job> jobs = new LinkedList<Job>();
		jobs.add(new Job(1, 4.0, 4.0,0.0));
		jobs.add(new Job(2, 5.0, 9.0,4.0));
		jobs.add(new Job(3, 2.0, 11.0,9.0));
		jobs.add(new Job(4, 7.0, 18.0,11.0));
		jobs.add(new Job(5, 3.0, 21.0,18.0));
		jobs.add(new Job(6, 5.0, 26.0,21.0));
		jobs.add(new Job(7, 9.0, 35.0,26.0));
		jobs.add(new Job(8, 7.0, 42.0,35.0)	);
		jobs.add(new Job(9, 5.0, 47.0,42.0));
		jobs.add(new Job(10, 1.0, 48.0,47.0));
		jobs.add(new Job(11, 7.0, 55.0,48.0));
		jobs.add(new Job(12, 6.0, 61.0,55.0));
		jobs.add(new Job(13, 4.0, 65.0,61.0));
		jobs.add(new Job(14, 1.0, 66.0,65.0));
		jobs.add(new Job(15, 9.0, 75.0,66.0));
		jobs.add(new Job(16, 9.0, 84.0,75.0));
		jobs.add(new Job(17, 9.0, 93.0,84.0));
		jobs.add(new Job(18, 8.0, 101.0,93.0));
		jobs.add(new Job(19, 7.0, 108.0,101.0));
		jobs.add(new Job(20, 6.0, 114.0,108.0));
		List<PerformanceMeasures> measures = new LinkedList<PerformanceMeasures>();
		
//		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
//		measures.add(PerformanceMeasures.AVERAGE_COMPLETION_TIME);
//		measures.add(PerformanceMeasures.AVERAGE_WAITING_TIME);
//		measures.add(PerformanceMeasures.MAKESPAN);
//		measures.add(PerformanceMeasures.MAXIMUM_EARLINESS);
//		measures.add(PerformanceMeasures.MAXIMUM_LATENESS);

		measures.add(PerformanceMeasures.NUM_TARDY_JOB);
		measures.add(PerformanceMeasures.NUM_EARLY_JOB);
//		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
//		measures.add(PerformanceMeasures.MAXIMUM_TARDINESS);
//		measures.add(PerformanceMeasures.AVERAGE_FLOW_TIME);
//		measures.add(PerformanceMeasures.TOTAL_EARLINESS);
		
		NondominatedPopulation result = new Executor()
			    .withProblem(new ScheduleProblem(jobs,measures))
				.withAlgorithm("eMOEA")
//				.withProperty("epsilon", 1.0)
				.withProperty("operator", "PMX")
				.withProperty("operator", "Swap")
				.withProperty("populationSize", 500)
				.withProperty("sbx.distributionIndex", 0.6)
				.withMaxEvaluations(500)
				.run();	
		
		System.out.println("f1,f2");
		result.forEach(res->System.out.printf("%f,%f\n", res.getObjective(0), res.getObjective(1)));
//		System.out.println("f1,f2,f3");
//		result.forEach(res->System.out.printf("%f,%f,%f\n", res.getObjective(0), res.getObjective(1), res.getObjective(2)));
		result.forEach(res->System.out.printf("%f,%f\n", res.getObjective(0), res.getObjective(1)));
		//result.forEach(res->{for(int i=0;i<res.getNumberOfVariables();i++)System.out.print(res.getVariable(i));System.out.println("---");});
	}

	@Test
	public void testScheduleProb() throws Exception{
		List<Job> jobs = new LinkedList<Job>();

		jobs.add(new Job(1,3,4,2.0));
		jobs.add(new Job(2,2,8,5.0));
		jobs.add(new Job(3,4,12,7.0));
		jobs.add(new Job(4,3,4,1.0));
		jobs.add(new Job(5,4,4,1.0));

		List<PerformanceMeasures> measures = new LinkedList<PerformanceMeasures>();
		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
		measures.add(PerformanceMeasures.AVERAGE_COMPLETION_TIME);
		measures.add(PerformanceMeasures.AVERAGE_WAITING_TIME);
		measures.add(PerformanceMeasures.MAXIMUM_EARLINESS);
		measures.add(PerformanceMeasures.MAXIMUM_LATENESS);
		measures.add(PerformanceMeasures.NUM_TARDY_JOB);
		measures.add(PerformanceMeasures.NUM_EARLY_JOB);
		measures.add(PerformanceMeasures.MAXIMUM_TARDINESS);
		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
		measures.add(PerformanceMeasures.AVERAGE_FLOW_TIME);
		measures.add(PerformanceMeasures.TOTAL_EARLINESS);

		measures.add(PerformanceMeasures.MAKESPAN);
		
		Solution sol=new Solution(5,measures.size());

		sol.setVariable(3,new Job(4,3,4,1.0));
		sol.setVariable(0,new Job(3,4,12,7.0));
		sol.setVariable(1,new Job(2,2,8,5.0));
		sol.setVariable(2,new Job(1,3,4,2.0));
		sol.setVariable(4,new Job(5,4,4,1.0));
		
		ScheduleProblem ob=new ScheduleProblem(jobs,measures);
		ob.evaluate(sol);
		for(int i=0;i<measures.size();i++)
			System.out.println(sol.getObjective(i));
	}
}
