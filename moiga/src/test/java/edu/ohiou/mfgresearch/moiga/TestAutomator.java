package edu.ohiou.mfgresearch.moiga;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.indicator.Spacing;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.selection.RankSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import edu.ohiou.mfgresearch.operators.PMXCrossover;
import edu.ohiou.mfgresearch.operators.SwapMutation;
import edu.ohiou.mfgresearch.schedule.Job;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.schedule.ScheduleProblem;
import edu.ohiou.mfgresearch.solver.ParetoFinder;
import edu.ohiou.mfgresearch.solver.Selector;

public class TestAutomator
{
	double [] alpha;
	int genCount[];
	double [] seed;
	List<BinaryOperator<Double>> agg;
	Problem prob;
	List<List<Double>> bounds;
	PrintWriter fw;
	Spacing met1;
	Hypervolume met2;
	String path;
	private int numRepeat;
	public TestAutomator(List<List<Double>> bounds,
						 double []alpha,
						 int genCount[],
						 double [] seed,
						 Problem prob,
						 List<BinaryOperator<Double>> agg, 
						 int numRepeat, 
						 String path)
	{
		this.alpha=alpha;
		this.genCount=genCount;
		this.agg=agg;
		this.path=path;
		this.seed=seed;
		this.prob=prob;
		this.bounds=bounds;
		this.numRepeat = numRepeat;
		met1=new Spacing(prob);
		double[] min=new double[bounds.size()];
		double[] max=new double[bounds.size()];
		for(int i=0;i<bounds.size();i++)
		{
			min[i]=bounds.get(i).get(1);
			max[i]=bounds.get(i).get(0);
		}
		met2=new Hypervolume(prob,min,max);
		try {
			fw=new PrintWriter(new File(path));
			if(bounds.size()==3)
				fw.write("Case_ID(No of jobs_alpha_gen_agg_seed%),Best(F1),Best(F2),Best(F3),Avg(F1),Avg(F2),Avg(F3),Spacing,Hypervolume\n");
			else
				fw.write("Case_ID(No of jobs_alpha_gen_agg_seed%),Best(F1),Best(F2),Avg(F1),Avg(F2),Spacing,Hypervolume\n");			
		}
		catch (IOException e)
		{
			e.printStackTrace();	
		}
		
	}
	public void test() throws Exception
	{
		this.test(Stream.of(new PMXCrossover(), new SwapMutation(0.5)).collect(Collectors.toList()));
	}

	public void test(List<Variation> operators) throws Exception
	{
		double spacing = 0, hypervolume = 0;
		for(int gc:genCount)
				for(double alph:alpha)
					for(double s:seed)
						for(BinaryOperator<Double> ag:agg)
						{							
							System.out.println("Config-------------------------->"+gc+" "+alph+" "+s+" "+ag.toString());
							List<List<Double>> X=new LinkedList<List<Double>>();
							List<List<Double>> res=run(ag,gc,s,alph,operators);
							System.out.println("--->"+res);
							res.remove(0);
							res.remove(0);
							X.addAll(res);
							for(int i=0;i<numRepeat;i++)
							{
								res=run(ag,gc,s,alph,operators);
								res.remove(0);
								res.remove(0);
								X.addAll(res);
								spacing = spacing + met1.evaluate(PrintMetrics.apply(res));
								// if (sp > spacing){
								// 	spacing = sp;
								// }
								hypervolume = hypervolume + met2.evaluate(PrintMetrics.apply(res));
								// if (hv > hypervolume){
								// 	hypervolume = hv;
								// }
							}
							spacing = spacing / numRepeat;
							hypervolume = hypervolume / numRepeat;
//							fw.append("Case_"+(((ScheduleProblem)prob).getNumberOfVariables())+
							fw.write("Case_"+(prob.getNumberOfVariables())+
									"_"+alph+
									"_"+gc+
									"_"+ag.toString()+
									"_"+s+",");
							//Best (F1,F2,F3) printed
							for(int i=0;i<bounds.size();i++)
							{
								double min=Integer.MAX_VALUE;
								for(List<Double> l:X)
									if(min>l.get(i))
										min=l.get(i);
								fw.write(min+",");
							}
							//Avg (F1,F2,F3) printed
							for(int i=0;i<bounds.size();i++)
							{
								double avg=0.0;
								for(List<Double> l:X)
										avg+=l.get(i);
								avg/=X.size();
								fw.write(avg+",");
							}
							fw.write(spacing +",");
							fw.write(hypervolume+"\n");
							spacing = 0; hypervolume = 0;
						}
		fw.flush();					
		fw.close();
	}
	
	List<List<Double>> run(BinaryOperator<Double> agg,
			int genCount,double seed,double alpha,List<Variation> operators) throws Exception
	{
		TerminationCondition[] term= { populationData -> populationData.getBestCandidateFitness()>=2.0, new GenerationCount(genCount)};
		ParetoFinder ob1=new ParetoFinder
				(bounds,prob,
						agg,
						 operators,
						 new Double[]{0.2, 0.5},
						 new Selector(new RankSelection()), true, 
						 new Random(), 
						 sol->{},
						 pop_data->{},
						 val->val, term,(alpha>=1)?0.3:alpha,(alpha>=1)?true:false,
						(int)Math.ceil(100*seed));
		return ob1.findPareto();
	}
	public static double findTTmax(List<Job> jobs) throws Exception
	{
		Collections.sort(jobs, new Comparator<Job>() {
			@Override
			public int compare(Job o1, Job o2) {
				if(o1.jobID<o2.jobID)
					return 1;
				return -1;
			}});
		ScheduleProblem.setCompletionTimes(jobs);
		return PerformanceMeasures.TOTAL_TARDINESS.evaluate(jobs);
	}
	public static double findFavmin(List<Job> jobs)
	{
		Collections.sort(jobs, new Comparator<Job>() {

			@Override
			public int compare(Job o1, Job o2) {
				if(o1.getProcessingTime()>=o2.getProcessingTime())
					return 1;
				return -1;
			}});
		//System.out.println(Arrays.toString(jobs.toArray()));
		ScheduleProblem.setCompletionTimes(jobs);
		return PerformanceMeasures.AVERAGE_FLOW_TIME.evaluate(jobs);
	}
	public static double findFavmax(List<Job> jobs)
	{
		Collections.sort(jobs, new Comparator<Job>() {

			@Override
			public int compare(Job o1, Job o2) {
				if(o1.getProcessingTime()<o2.getProcessingTime())
					return 1;
				return -1;
			}});
//		System.out.println(Arrays.toString(jobs.toArray()));
		ScheduleProblem.setCompletionTimes(jobs);
		return PerformanceMeasures.AVERAGE_FLOW_TIME.evaluate(jobs);
	}
	public static double findnTmax(List<Job> jobs)
	{
		return jobs.size();
	}
	public static double findTmaxmax(List<Job> jobs)
	{
		Collections.sort(jobs, new Comparator<Job>() {

			@Override
			public int compare(Job o1, Job o2) {
				if(o1.jobID<o2.jobID)
					return 1;
				return -1;
			}});
		ScheduleProblem.setCompletionTimes(jobs);
		return PerformanceMeasures.MAXIMUM_TARDINESS.evaluate(jobs);
	}
}
