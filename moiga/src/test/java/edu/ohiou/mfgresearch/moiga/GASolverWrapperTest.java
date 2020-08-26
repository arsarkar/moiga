package edu.ohiou.mfgresearch.moiga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.selection.RankSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import edu.ohiou.mfgresearch.operators.PMXCrossover;
import edu.ohiou.mfgresearch.operators.SwapMutation;
import edu.ohiou.mfgresearch.schedule.Job;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.schedule.ScheduleProblem;
import edu.ohiou.mfgresearch.solver.Selector;

public class GASolverWrapperTest {

	@Test
	public void modifyJobtest() throws Exception
	{
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
		
		//measures.add(PerformanceMeasures.TOTAL_TARDINESS);
		//measures.add(PerformanceMeasures.AVERAGE_COMPLETION_TIME);
		//measures.add(PerformanceMeasures.AVERAGE_WAITING_TIME);
//		measures.add(PerformanceMeasures.MAKESPAN);
		//measures.add(PerformanceMeasures.MAXIMUM_EARLINESS);
		//measures.add(PerformanceMeasures.MAXIMUM_LATENESS);
		//measures.add(PerformanceMeasures.NUM_EARLY_JOB);
//		measures.add(PerformanceMeasures.NUM_TARDY_JOB);
		//measures.add(PerformanceMeasures.MAXIMUM_TARDINESS);
		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
		measures.add(PerformanceMeasures.AVERAGE_FLOW_TIME);
		//measures.add(PerformanceMeasures.TOTAL_EARLINESS);
		ScheduleProblem ob=new ScheduleProblem(jobs,measures);
		ob.modifyJob(100,ob1->ob1.setDueDate(10));
	
	}
	@Test
	public void test() throws Exception
	{
		List<List<Double>> fuzzys = new LinkedList<List<Double>>();
		
		List<Variation> operators = Stream.of(new PMXCrossover(), new SwapMutation(0.5))
										  .collect(Collectors.toList());
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
		
		//total tardiness vs no. tardy job	
//		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
//		measures.add(PerformanceMeasures.NUM_TARDY_JOB);
//		fuzzys.add(Arrays.asList(2367.0,0.0,2367.0,0.0));
//		fuzzys.add(Arrays.asList(20.0,0.0,20.0,0.0));
//		fuzzys.add(Arrays.asList(1.0,0.0,1.0,0.0));
		
		//Average Flow time vs no. tardy job	
//		measures.add(PerformanceMeasures.AVERAGE_FLOW_TIME);
//		measures.add(PerformanceMeasures.NUM_TARDY_JOB);
//		fuzzys.add(Arrays.asList(111.0,5.6,111.0,5.6));
//		fuzzys.add(Arrays.asList(20.0,0.0,20.0,0.0));
//		fuzzys.add(Arrays.asList(1.0,0.0,1.0,0.0));
		
		//Maximum Tardiness vs no. tardy job	
//		measures.add(PerformanceMeasures.MAXIMUM_TARDINESS);
//		measures.add(PerformanceMeasures.NUM_TARDY_JOB);
//		fuzzys.add(Arrays.asList(215.0,0.0,215.0,0.0));
//		fuzzys.add(Arrays.asList(20.0,0.0,20.0,0.0));
//		fuzzys.add(Arrays.asList(1.0,0.0,1.0,0.0));
		
		//Total tardiness vs Max Tardiness
//		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
//		measures.add(PerformanceMeasures.MAXIMUM_TARDINESS);
//		fuzzys.add(Arrays.asList(2367.0,0.0,2367.0,0.0));
//		fuzzys.add(Arrays.asList(215.0,0.0,215.0,0.0));
//		fuzzys.add(Arrays.asList(1.0,0.0,1.0,0.0));
		
		//Total tardiness vs Avg. Flow time
//		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
//		measures.add(PerformanceMeasures.AVERAGE_FLOW_TIME);
//		fuzzys.add(Arrays.asList(2367.0,0.0,2367.0,0.0));
//		fuzzys.add(Arrays.asList(111.0,5.6,111.0,5.6));
//		fuzzys.add(Arrays.asList(1.0,0.0,1.0,0.0));

		//Total tardiness vs Avg. Flow time
//		measures.add(PerformanceMeasures.MAXIMUM_TARDINESS);
//		measures.add(PerformanceMeasures.AVERAGE_FLOW_TIME);
//		fuzzys.add(Arrays.asList(215.0,0.0,215.0,0.0));
//		fuzzys.add(Arrays.asList(111.0,5.6,111.0,5.6));
//		fuzzys.add(Arrays.asList(1.0,0.0,1.0,0.0));
		
//		measures.add(PerformanceMeasures.MAXIMUM_TARDINESS);
//		measures.add(PerformanceMeasures.AVERAGE_FLOW_TIME);
//		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
//		fuzzys.add(Arrays.asList(215.0,0.0,215.0,0.0));
//		fuzzys.add(Arrays.asList(111.0,5.6,111.0,5.6));
//		fuzzys.add(Arrays.asList(2367.0,0.0,2367.0,0.0));
		
		measures.add(PerformanceMeasures.NUM_TARDY_JOB);
//		measures.add(PerformanceMeasures.AVERAGE_FLOW_TIME);
		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
		fuzzys.add(Arrays.asList(140.0,0.0,136.0,0.0));
//		fuzzys.add(Arrays.asList(111.0,5.6,111.0,5.6));
		fuzzys.add(Arrays.asList(683096.0,0.0,683092.0,0.0));
		// fuzzys.add(Arrays.asList(1.0,0.0,1.0,0.0));
//		measures.add(PerformanceMeasures.NUM_TARDY_JOB);
//		measures.add(PerformanceMeasures.MAXIMUM_TARDINESS);
//		measures.add(PerformanceMeasures.TOTAL_TARDINESS);
//		fuzzys.add(Arrays.asList(20.0,0.0,20.0,0.0));
//		fuzzys.add(Arrays.asList(215.0,0.0,215.0,0.0));
//		fuzzys.add(Arrays.asList(2367.0,0.0,2367.0,0.0));
		
		long[] IDs= {};
		TerminationCondition[] term= { populationData -> populationData.getBestCandidateFitness()>=2.0,new GenerationCount(500)};
		int steps[]= {10,10,1};
		GASolverWrapper ob = 
				new GASolverWrapper(new DataGenerator("D:\\GA and BioInspired Algo\\Test_Cases\\Test140_1_70.csv", 0, 0, 0).createProb(measures),//new ScheduleProblem(jobs,measures),
							(a,b)->a+b,
							 operators,
							 new Double[]{0.2, 0.5},
							 new Selector(new RankSelection()), 
							 true, 
							 new Random(),
							 sol->{},
							 pop_data->{
								 System.out.println(pop_data.getBestCandidateFitness()+" "+
									 Arrays.toString(pop_data.getBestCandidate().getObjectives())+"--->");
							 		/*Solution s=pop_data.getBestCandidate();
							 		for(int i=0;i<s.getNumberOfVariables();i++)
							 			System.out.print(s.getVariable(i)+" ");
							 		System.out.println();*/
									 }, 
							 fuzzys,// 100,
							 steps, IDs,
							 job->job.setDueDate(job.getDueDate()),
							 term,
							 2);
		
		double threshold=0.0;
		//List<Solution> totalPop=ob.getTotalPop(false);
		List<Solution> totalPop=ob.getBestCandidates(threshold,true,false);
		List<List<Double>> list=new ArrayList<List<Double>>();
		int c=0;
		for(Solution i:totalPop)
		{
			//System.out.println("Solution "+c++ + "  : " + Arrays.toString(i.getObjectives()));
			 List<Double> temp = Arrays.asList(ArrayUtils.toObject(i.getObjectives()));
			list.add(temp);
		}
		CSVManager wr=new CSVManager("D:\\Torrents\\test.csv",false);
		String [] h={measures.get(0).name(),measures.get(1).name()};//,measures.get(2).name()};
		wr.appendData(list,h);
		//System.out.println(list.size());
		wr.appendPareto(list,h);
		// SwingUtilities.invokeLater(() -> {
		//  	String []s={measures.get(0).name(),measures.get(1).name()};
		//  ScatterPlot pareto = new ScatterPlot(list,"Scatter Chart",s,0);
	    //   pareto.setSize(800, 400);
	    //   pareto.setLocationRelativeTo(null);
	    //   pareto.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    //   pareto.setVisible(true);	     		      
	    // });
		Thread.sleep(50000);
	}
}
