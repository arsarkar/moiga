package edu.ohiou.mfgresearch;

import java.net.URL;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.moeaframework.core.Solution;

import edu.ohiou.mfgresearch.lambda.Omni;
import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.JobT;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic;

public class JobShopTest {
//	@Test
	public void test() throws Exception {
		LinkedList<PerformanceMeasures> measure1 = new LinkedList<PerformanceMeasures>();
		measure1.add(PerformanceMeasures.NUM_TARDY_JOB);
		measure1.add(PerformanceMeasures.TOTAL_TARDINESS);
		DataGenerator ob = new DataGenerator("/META-INF/jobshop/JobShopTest3_3_1_10.csv", 1, 10, 3, 3);
		// ob.generateJobShop();
		JobShopProblem prob = (JobShopProblem) ob.createJobShopProb(measure1);
		// List<JobT> ii=prob.getJobs();
		// prob.setCompletionTimes(ii);
		// Solution sol1=prob.newSolution();
		// Solution sol2=prob.newSolution();
		// GOXCrossover op=new GOXCrossover(prob.getNumberOfJobs());
		// Solution s[]=op.evolve(new Solution[] {sol1,sol2});
		//
		// for(int i=0;i<sol1.getNumberOfVariables();i++)
		// System.out.print(((JobT)(sol1.getVariable(i))).jobID+" ");
		// System.out.println();
		//
		// for(int i=0;i<sol2.getNumberOfVariables();i++)
		// System.out.print(((JobT)(sol2.getVariable(i))).jobID+" ");
		// System.out.println();
		//
		// for(int i=0;i<s[0].getNumberOfVariables();i++)
		// System.out.print(((JobT)(s[0].getVariable(i))).jobID+" ");
		// System.out.println();
		//
		// for(int i=0;i<s[1].getNumberOfVariables();i++)
		// System.out.print(((JobT)(s[1].getVariable(i))).jobID+" ");
		// System.out.println();
		//
		// prob.evaluate(sol);
		// System.out.println("jobID\tmachineID\tprocessingTime\treadyTime\tdueDate\tcompletionTime\n");
		// for(int i=0;i<sol.getNumberOfVariables();i++)
		// System.out.println(sol.getVariable(i));
		// System.out.println(sol.getObjective(0)+" "+sol.getObjective(1));

		// ii.forEach(i->System.out.println(i));

	}

	@Test
	public void testHeuristic() throws Exception
	{
		LinkedList<PerformanceMeasures> measure1 = new LinkedList<PerformanceMeasures>();
		measure1.add(PerformanceMeasures.NUM_TARDY_JOB);
		measure1.add(PerformanceMeasures.TOTAL_TARDINESS);
		URL file1 = getClass().getResource("/META-INF/jobshop/JobShopTest3_3_1_10.csv");
		System.out.println(file1.getFile());
		DataGenerator ob = new DataGenerator(file1.getFile(), 1, 10, 3, 3);
		// ob.generateJobShop(0.4,0.6);
		JobShopProblem prob;
		 prob=(JobShopProblem)ob.createJobShopProb(measure1);
		URL file2 = getClass().getResource("/META-INF/jobshop/ta_15_15.txt");
//		prob = DataGenerator.amendTaillardToJobShop(file2.getFile(), measure1, 0.4, 0.6);
		List<JobT> ii = prob.getJobs();
		// ii.forEach(i->System.out.println("->"+i));
		Solution sol=ScheduleHeuristic.FCFS.evaluate(ii, 3,3);
		for(int i=0;i<sol.getNumberOfVariables();i++)
			System.out.println(sol.getVariable(i));
		// Solution sol1=prob.newSolution();
		 ii.forEach(i->System.out.println(i));
	}

//	@Test
	public void testNumTardy() {
		try {
			JobShopProblem prob = 
			DataGenerator.readTaillardToJobShopWithDD(getClass().getResource("/META-INF/jobshop/ta_15_15_dd.txt").getFile(),
			Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.MAKESPAN).toList(), 0.4, 0.8);

			Solution sched1 = prob.newSolution() ;
			prob.evaluate(sched1);
			List<JobT> jobs = new LinkedList<JobT>();
			for(int i= 0; i < sched1.getNumberOfVariables(); i++){
				jobs.add((JobT)sched1.getVariable(i));
			}
			jobs.sort(Comparator.comparing(JobT::getJobID));
			jobs.stream().distinct().forEach(j->{
				System.out.println(j.toString());
			});
			System.out.println("Number of tardy jobs: " + sched1.getObjective(0));
			System.out.println("Makespan: " + sched1.getObjective(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 



}
