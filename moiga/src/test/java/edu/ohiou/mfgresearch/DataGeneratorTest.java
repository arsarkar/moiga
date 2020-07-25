package edu.ohiou.mfgresearch;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BinaryOperator;

import org.junit.Test;
import org.moeaframework.core.Problem;

import edu.ohiou.mfgresearch.schedule.Job;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.schedule.ScheduleProblem;

public class DataGeneratorTest
{
	List<PerformanceMeasures> measure1,measure2,measure3,measure4;
	List<Double> nT,TT,Fav,Tmax;
	{
		measure1=new LinkedList<PerformanceMeasures>();
		measure1.add(PerformanceMeasures.NUM_TARDY_JOB);
		measure1.add(PerformanceMeasures.TOTAL_TARDINESS);
		
		measure2=new LinkedList<PerformanceMeasures>();
		measure2.add(PerformanceMeasures.AVERAGE_FLOW_TIME);
		measure2.add(PerformanceMeasures.NUM_TARDY_JOB);
		
		measure3=new LinkedList<PerformanceMeasures>();
		measure3.add(PerformanceMeasures.NUM_TARDY_JOB);
		measure3.add(PerformanceMeasures.MAXIMUM_TARDINESS);
		
		measure4=new LinkedList<PerformanceMeasures>();
		measure4.add(PerformanceMeasures.TOTAL_TARDINESS);
		measure4.add(PerformanceMeasures.MAXIMUM_TARDINESS);
		
		nT=new LinkedList<Double>();
		TT=new LinkedList<Double>();
		Fav=new LinkedList<Double>();
		Tmax=new LinkedList<Double>();
		
	}
	//@Test
	public void test() throws IOException
	{
		for(int i=20;i<=140;i+=40)
		{DataGenerator ob=new DataGenerator("D:\\GA and BioInspired Algo\\Test_Cases\\Test"+i+"_"+"1_"+((int)(i*0.5))+".csv", 1,(int)(i*0.5), i);
		ob.generate();}
		DataGenerator ob=new DataGenerator("D:\\GA and BioInspired Algo\\Test_Cases\\Test"+40+"_"+"1_"+"20.csv", 1,20, 40);
		ob.generate();
	}
	@Test
	public void testDG_JobShop() throws Exception
	{
		DataGenerator ob=new DataGenerator("", 0, 0, 0);
		// JobShopProblem prob=ob.taillardToJobShop("D:\\GA and BioInspired Algo\\Test_Cases\\ta_15_15.txt", measure1);
		// prob.getJobs().forEach(i->System.out.println(i));
	}
	//@Test
	public void test1() throws Exception
	{
		Problem prob;
		DataGenerator ob=new DataGenerator("D:\\GA and BioInspired Algo\\Test_Cases\\Test20_1_10.csv", 0, 0, 0);
//		prob=ob.createProb(measure1);
//		prob=ob.createProb(measure2);
//		prob=ob.createProb(measure3);
//		prob=ob.createProb(measure4);
		
		ob=new DataGenerator("D:\\GA and BioInspired Algo\\Test_Cases\\Test40_1_20.csv", 0, 0, 0);
		prob=ob.createProb(measure1);
//		prob=ob.createProb(measure2);
//		prob=ob.createProb(measure3);
//		prob=ob.createProb(measure4);
//		
		ob=new DataGenerator("D:\\GA and BioInspired Algo\\Test_Cases\\Test60_1_30.csv", 0, 0, 0);
//		prob=ob.createProb(measure1);
//		prob=ob.createProb(measure2);
//		prob=ob.createProb(measure3);
//		prob=ob.createProb(measure4);
//		
		ob=new DataGenerator("D:\\GA and BioInspired Algo\\Test_Cases\\Test100_1_50.csv", 0, 0, 0);
//		prob=ob.createProb(measure1);
//		prob=ob.createProb(measure2);
//		prob=ob.createProb(measure3);
//		prob=ob.createProb(measure4);
//		
		ob=new DataGenerator("D:\\GA and BioInspired Algo\\Test_Cases\\Test140_1_70.csv", 0, 0, 0);
//		prob=ob.createProb(measure1);
//		prob=ob.createProb(measure2);
//		prob=ob.createProb(measure3);
//		prob=ob.createProb(measure4);
//		
		double alpha[]= {0.3,0.5,0.7,2.0};
		int genCount[]= {100,300,600,1000};
		double seedP[]= {0.0,0.1,0.25,0.5};
		List<BinaryOperator<Double>> ops=new LinkedList<BinaryOperator<Double>>();
		ops.add(new BinaryOperator<Double>() {

			@Override
			public Double apply(Double t, Double u) {
				return t+u;
			}
			public String toString()
			{
				return "Sum";
			}
		});
		ops.add(new BinaryOperator<Double>()
		{

			@Override
			public Double apply(Double result, Double element) {
				return result+element + ((element>=1)?1:0);
			}
			public String toString()
			{
				return "Hybrid_Sum";
			}
		});
		List<Job> jobs=((ScheduleProblem) prob).getJobs();
		List<List<Double>> bounds=new LinkedList<List<Double>>();
		
		nT.add(TestAutomator.findnTmax(jobs));nT.add(0.0);nT.add(TestAutomator.findnTmax(jobs)-4.0);nT.add(0.0);
		
		double temp=TestAutomator.findTTmax(jobs);
		TT.add(temp);TT.add(0.0);TT.add(temp-4.0);TT.add(0.0);
		
		temp=TestAutomator.findTmaxmax(jobs);
		Tmax.add(temp);Tmax.add(0.0);Tmax.add(temp-4.0);Tmax.add(0.0);
		
		temp=TestAutomator.findFavmax(jobs);
		Fav.add(TestAutomator.findFavmax(jobs));Fav.add(temp);Fav.add(TestAutomator.findFavmax(jobs)-4.0);Fav.add(temp);
		
		bounds.add(nT);
		bounds.add(TT);
		System.out.println(Arrays.toString(bounds.toArray()));
		TestAutomator test=new TestAutomator(bounds, alpha, genCount, seedP, prob, ops, 
				"D:\\GA and BioInspired Algo\\Test_Cases\\Result_40_measure1.csv");
		test.test();
	}
}
