package edu.ohiou.mfgresearch.moiga;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.indicator.AdditiveEpsilonIndicator;
import org.moeaframework.core.indicator.Spacing;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.OperatorProvider;
import org.moeaframework.util.TypedProperties;
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

public class ParetoFinderTest {
	
	//Hybrid Sum --> 	
	
	@Test
	public void test() throws Exception
	{
				List<List<Double>> bounds=new LinkedList<List<Double>>();
				List<Double> temp=new LinkedList<Double>();
				temp=new LinkedList<Double>();
				temp.add(140.0);temp.add(0.0);temp.add(136.0);temp.add(0.0);
				bounds.add(temp);
				
				temp=new LinkedList<Double>();
				temp.add(683096.0);temp.add(0.0);temp.add(683092.0);temp.add(0.0);
				bounds.add(temp);
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

				measures.add(PerformanceMeasures.NUM_TARDY_JOB);
				//measures.add(PerformanceMeasures.MAXIMUM_TARDINESS);
//				measures.add(PerformanceMeasures.AVERAGE_FLOW_TIME);
				measures.add(PerformanceMeasures.TOTAL_TARDINESS);

				TerminationCondition[] term= { populationData -> populationData.getBestCandidateFitness()>=2.0,
						new GenerationCount(1000)};
				//Problem prob=new DataGenerator("/META-INF/single/Test140_1_70.csv", 0, 0, 0).createProb(measures);
				Problem prob = new ScheduleProblem(jobs,measures);
				ParetoFinder ob1=new ParetoFinder
						(bounds,prob,
								(a,b)->a+b,
								 operators,
								 new Double[]{0.2, 0.5},
								 new Selector(new RankSelection()), true, 
								 new Random(), 
								 sol->{},
								 pop_data->{
//									 System.out.println(pop_data.getBestCandidateFitness()+" "+
//										 Arrays.toString(pop_data.getBestCandidate().getObjectives())+"--->");
								 		/*Solution s=pop_data.getBestCandidate();
								 		for(int i=0;i<s.getNumberOfVariables();i++)
								 			System.out.print(s.getVariable(i)+" ");
								 		System.out.println();*/
										 },
								 val->val, term,0.5,true,50);
				
				CSVManager man=new CSVManager("/META-INF/single/paretoresult.csv", false);
				
				//System.out.println(temp==null);
				List<List<Double>> list=ob1.findPareto();
				System.out.println(Arrays.toString(list.toArray()));
//				list.remove(0);
//				list.remove(0);
				System.out.println(Arrays.toString(list.toArray()));
				man.appendData(list, new String[] {"asd","asd","asd"});
				OperatorFactory.getInstance().addProvider(new OperatorProvider() {
				    public String getMutationHint(Problem problem) {
				        return "SwapMutation";
				    }

				    public String getVariationHint(Problem problem) {
				        return "PMXCrossover";
				    }

				    public Variation getVariation(String name, Properties properties, Problem problem) {
				        TypedProperties typedProperties = new TypedProperties(properties);

				        if (name.equalsIgnoreCase("PMXCrossover")) {
				            return new PMXCrossover();
				        } else if (name.equalsIgnoreCase("SwapMutation")) {
				            return new SwapMutation(
				                typedProperties.getDouble("SwapMutation.Rate", 0.5));
				        }

				        // No match, return null
				        return null;
				    }
				});
				//Standard set
				NondominatedPopulation result=new Executor()
			    .withProblem(prob)
			    .withAlgorithm("NSGAII")
			    .withProperty("operator","PMXCrossover+SwapMutation")
			    .withMaxEvaluations(2000)
			    .run();
				
				PrintMetrics met=new PrintMetrics(prob, new Spacing(prob),new AdditiveEpsilonIndicator(prob, result));
				met.printMetrics(list);
				//  	String []s={measures.get(0).name(),measures.get(1).name()};
				//  ScatterPlot pareto = new ScatterPlot(list,"0.3-alpha",s,0);
			    //   pareto.setSize(800, 400);
			    //   pareto.setLocationRelativeTo(null);
			    //   pareto.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			    //   pareto.setVisible(true);
			    //   JFrame f=new JFrame();  
			    //   JOptionPane.showMessageDialog(f,"Continue ?");  
			    //   pareto.setVisible(false);
				
	}
	
}
