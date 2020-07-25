package edu.ohiou.mfgresearch;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.JobT;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic;

public class JobShopTest
{
	@Test
	public void test() throws Exception
	{
		LinkedList<PerformanceMeasures> measure1=new LinkedList<PerformanceMeasures>();
		measure1.add(PerformanceMeasures.NUM_TARDY_JOB);
		measure1.add(PerformanceMeasures.TOTAL_TARDINESS);
		DataGenerator ob=new DataGenerator("/META-INF/jobshop/JobShopTest3_3_1_10.csv",1,10,3,3);
		//ob.generateJobShop();
		JobShopProblem prob=(JobShopProblem)ob.createJobShopProb(measure1);
//		List<JobT> ii=prob.getJobs();
//		prob.setCompletionTimes(ii);
//		Solution sol1=prob.newSolution();
//		Solution sol2=prob.newSolution();
//		GOXCrossover op=new GOXCrossover(prob.getNumberOfJobs());
//		Solution s[]=op.evolve(new Solution[] {sol1,sol2});
//		
//		for(int i=0;i<sol1.getNumberOfVariables();i++)
//			System.out.print(((JobT)(sol1.getVariable(i))).jobID+"	");
//		System.out.println();
//		
//		for(int i=0;i<sol2.getNumberOfVariables();i++)
//			System.out.print(((JobT)(sol2.getVariable(i))).jobID+"	");
//		System.out.println();
//		
//		for(int i=0;i<s[0].getNumberOfVariables();i++)
//			System.out.print(((JobT)(s[0].getVariable(i))).jobID+"	");
//		System.out.println();
//		
//		for(int i=0;i<s[1].getNumberOfVariables();i++)
//			System.out.print(((JobT)(s[1].getVariable(i))).jobID+"	");
//		System.out.println();
//		
//		prob.evaluate(sol);
//		System.out.println("jobID\tmachineID\tprocessingTime\treadyTime\tdueDate\tcompletionTime\n");
//		for(int i=0;i<sol.getNumberOfVariables();i++)
//			System.out.println(sol.getVariable(i));
//		System.out.println(sol.getObjective(0)+" "+sol.getObjective(1));
		
		//ii.forEach(i->System.out.println(i));
		
	}
	
	@Test
	public void testHeuristic() throws Exception
	{
		LinkedList<PerformanceMeasures> measure1=new LinkedList<PerformanceMeasures>();
		measure1.add(PerformanceMeasures.NUM_TARDY_JOB);
		measure1.add(PerformanceMeasures.TOTAL_TARDINESS);
		URL file1 = getClass().getResource("/META-INF/jobshop/JobShopTest3_3_1_10.csv");
		DataGenerator ob=new DataGenerator(file1.getFile(),1,10,3,3);
//		ob.generateJobShop(0.4,0.6);
		JobShopProblem prob;
//		prob=(JobShopProblem)ob.createJobShopProb(measure1);
		URL file2 = getClass().getResource("/META-INF/jobshop/ta_15_15.txt");
		prob=ob.taillardToJobShop(file2.getFile(), measure1,0.4,0.6);
		List<JobT> ii=prob.getJobs();
//		ii.forEach(i->System.out.println("->"+i));
		System.out.println(ScheduleHeuristic.DDate.evaluate(ii,15,15));
//		Solution sol1=prob.newSolution();
//		ii.forEach(i->System.out.println(i));
		System.out.println(ScheduleHeuristic.TT.evaluate(ii,10,10));
		ii.forEach(i->System.out.println(i));
	}

}
