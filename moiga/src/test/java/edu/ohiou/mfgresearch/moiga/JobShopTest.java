package edu.ohiou.mfgresearch.moiga;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.junit.Test;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.indicator.Spacing;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.OperatorProvider;
import org.moeaframework.util.TypedProperties;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.selection.RankSelection;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import edu.ohiou.mfgresearch.fuzzy.FuzzyMembershipT;
import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;
import edu.ohiou.mfgresearch.lambda.Omni;
import edu.ohiou.mfgresearch.operators.GPMXCrossover;
import edu.ohiou.mfgresearch.operators.SwapMutation;
import edu.ohiou.mfgresearch.schedule.Job;
import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.JobT;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic;
import edu.ohiou.mfgresearch.solver.GASolver;
import edu.ohiou.mfgresearch.solver.Selector;
import edu.ohiou.mfgresearch.solver.algorithm.FuzzyNSGAProvider;

public class JobShopTest {

	@Test
	public void test() throws Exception {
		final LinkedList<PerformanceMeasures> measure1 = new LinkedList<PerformanceMeasures>();
		measure1.add(PerformanceMeasures.NUM_TARDY_JOB);
		measure1.add(PerformanceMeasures.TOTAL_TARDINESS);
		final DataGenerator ob = new DataGenerator("/META-INF/jobshop/JobShopTest3_3_1_10.csv", 1, 10, 3, 3);
		// ob.generateJobShop();
		final JobShopProblem prob = (JobShopProblem) ob.createJobShopProb(measure1);
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
	public void fileConversion() throws Exception {
		DataGenerator.amendTaillardToJobShop(
				getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.csv").getFile(),
				Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.MAKESPAN).toList(), 0.4, 0.8);

	}

	@Test
	public void testHeuristic() throws Exception {
		final LinkedList<PerformanceMeasures> measure1 = new LinkedList<PerformanceMeasures>();
		measure1.add(PerformanceMeasures.NUM_TARDY_JOB);
		measure1.add(PerformanceMeasures.TOTAL_TARDINESS);
		final URL file1 = getClass().getResource("/META-INF/jobshop/JobShopTest3_3_1_10.csv");
		System.out.println(file1.getFile());
		final DataGenerator ob = new DataGenerator(file1.getFile(), 1, 10, 3, 3);
		// ob.generateJobShop(0.4,0.6);
		JobShopProblem prob;
		prob = (JobShopProblem) ob.createJobShopProb(measure1);
		final URL file2 = getClass().getResource("/META-INF/jobshop/ta_15_15.txt");
		// prob = DataGenerator.amendTaillardToJobShop(file2.getFile(), measure1, 0.4,
		// 0.6);
		final List<JobT> ii = prob.getJobs();
		// ii.forEach(i->System.out.println("->"+i));
		final List<JobT> sol = ScheduleHeuristic.FCFS.evaluate(ii, 3, 3);
		// for(int i=0;i<sol.getNumberOfVariables();i++)
		// System.out.println(sol.getVariable(i));
		// Solution sol1=prob.newSolution();
		ii.forEach(i -> System.out.println(i));
	}

	@Test
	public void testNumTardy() {
		try {
			final JobShopProblem prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.csv").getFile(),
					Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.MAKESPAN).toList(), 0.4, 0.8);

			final List<JobT> js = prob.getJobs();
			final List<JobT> sol = ScheduleHeuristic.DDate.evaluate(js, 15, 15);
			final double nt = PerformanceMeasures.NUM_TARDY_JOB
					.evaluate(sol.stream().map(j -> (Job) j).collect(Collectors.toList()));
			final double tm = PerformanceMeasures.MAXIMUM_TARDINESS
					.evaluate(sol.stream().map(j -> (Job) j).collect(Collectors.toList()));
			final double ms = PerformanceMeasures.MAKESPAN
					.evaluate(sol.stream().map(j -> (Job) j).collect(Collectors.toList()));
			final double af = PerformanceMeasures.AVERAGE_FLOW_TIME
					.evaluate(sol.stream().map(j -> (Job) j).collect(Collectors.toList()));
			System.out.println("Number of tardy jobs: " + nt);
			System.out.println("Maximum Tardiness: " + tm);
			System.out.println("Makespan: " + ms);
			System.out.println("Avg flow time: " + af);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_JS_GS_Tal() {
		JobShopProblem prob;
		try {
			prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/taillard/ta1_15_15.txt").getFile(),
					Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.AVERAGE_FLOW_TIME).toList(), 0.4,
					0.8);

			final BinaryOperator<Double> aggr = new BinaryOperator<Double>() {

				@Override
				public Double apply(final Double t, final Double u) {
					return t + u;
				}

				public String toString() {
					return "Sum";
				}
			};
			final List<List<Double>> bounds = new LinkedList<List<Double>>();
			bounds.add(Arrays.asList(15.0, 1.0, 2.0, 1.0));
			bounds.add(Arrays.asList(5000.0, 1.0, 100.0, 1.0));

			final TerminationCondition[] term = { populationData -> populationData.getBestCandidateFitness() >= 2.0,
					new GenerationCount(1000) };
			final List<Fuzzyficator> fuzzyFictators = new LinkedList<Fuzzyficator>();
			for (final List<Double> i : bounds) {
				fuzzyFictators.add(new Fuzzyficator(new FuzzyMembershipT(i.get(0), i.get(1), i.get(2), i.get(3))));
			}

			final List<Variation> ops1 = new ArrayList<Variation>();
			ops1.add(new GPMXCrossover(15));
			ops1.add(new SwapMutation(0.5));

			final GASolver solver = new GASolver(prob, fuzzyFictators, aggr, ops1, new Double[] { 0.2, 0.5 },
					new Selector(new RankSelection()), true, new Random(), sol -> {
						// System.out.println(sol.getObjective(0) + "," + sol.getObjective(1));
					}, pop_data -> {
					});	

			FitnessEvaluator<Solution> evaluator = GASolver.createFitnessEvaluator(prob, fuzzyFictators, aggr, true);

			final List<Solution> pop = Arrays.stream(solver.evolvePopulation(50, 0, term).toArray())
					.map(ii -> ((EvaluatedCandidate<Solution>) ii).getCandidate()).collect(Collectors.toList());
			final Solution bs = pop.get(0);
			pop.stream().forEach(s -> {
				System.out.println(s.getObjective(0) + "," + s.getObjective(1) + "," + fuzzyFictators.get(0).apply(s.getObjective(0))
																			   + "," + fuzzyFictators.get(1).apply(s.getObjective(1))
																			   + "," + evaluator.getFitness(s, null));
			});
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testJS_GS_MT() {
		JobShopProblem prob;
		try {
			prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.csv").getFile(),
					Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.AVERAGE_FLOW_TIME).toList(), 0.4, 0.8);

			final BinaryOperator<Double> aggr = new BinaryOperator<Double>() {

				@Override
				public Double apply(final Double t, final Double u) {
					return t + u;
				}

				public String toString() {
					return "Sum";
				}
			};
			final List<List<Double>> bounds = new LinkedList<List<Double>>();
			bounds.add(Arrays.asList(6.0, 0.0, 2.0, 0.0));
			bounds.add(Arrays.asList(100.0, 0.0, 35.0, 0.0));

			final TerminationCondition[] term = { populationData -> populationData.getBestCandidateFitness() >= 2.0, new GenerationCount(2000) };
			final List<Fuzzyficator> fuzzyFictators = new LinkedList<Fuzzyficator>();
			for (final List<Double> i : bounds) {
				fuzzyFictators.add(new Fuzzyficator(new FuzzyMembershipT(i.get(0), i.get(1), i.get(2), i.get(3))));
			}

			final List<Variation> ops1 = new ArrayList<Variation>();
			ops1.add(new GPMXCrossover(prob.getNumberOfJobs()));
			ops1.add(new SwapMutation(0.5));

			final GASolver solver = new GASolver(prob, fuzzyFictators, aggr, ops1, new Double[] { 0.2, 0.5 },
					new Selector(new TournamentSelection(new Probability(1.0))), true, new Random(), sol -> {
						// System.out.println(sol.getObjective(0) + "," + sol.getObjective(1));
					}, pop_data -> {
					});	

			FitnessEvaluator<Solution> evaluator = GASolver.createFitnessEvaluator(prob, fuzzyFictators, aggr, true);

			final List<Solution> pop = Arrays.stream(solver.evolvePopulation(100, 0, term).toArray())
					.map(ii -> ((EvaluatedCandidate<Solution>) ii).getCandidate()).collect(Collectors.toList());
			final Solution bs = pop.get(0);
			pop.stream().forEach(s -> {
				System.out.println(s.getObjective(0) + "," + s.getObjective(1) + "," + fuzzyFictators.get(0).apply(s.getObjective(0))
																			   + "," + fuzzyFictators.get(1).apply(s.getObjective(1))
																			   + "," + evaluator.getFitness(s, null));
			});
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/**
	 * test for jo shop pareto finder algorithm
	 */
	public void testJobShopGA1() {
		JobShopProblem prob;
		try {
			prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/taillard/ta3_15_15.txt").getFile(),
					Omni.of(PerformanceMeasures.MAXIMUM_TARDINESS, PerformanceMeasures.AVERAGE_FLOW_TIME).toList(), 0.4, 0.8);

			final double alpha[] = {1};
			final int genCount[] = {100}; //, 
			final double seedP[] = {0.0, 0.1, 0.3, 0.5, 0.7, 0.9};
			final List<BinaryOperator<Double>> ops = new LinkedList<BinaryOperator<Double>>();
			ops.add(new BinaryOperator<Double>() {

				@Override
				public Double apply(final Double t, final Double u) {
					return t + u;
				}

				public String toString() {
					return "Sum";
				}
			});
			final List<List<Double>> bounds = new LinkedList<List<Double>>();
			bounds.add(Arrays.asList(5000.0, 1.0, 5000.0, 1.0));
			bounds.add(Arrays.asList(5000.0, 1.0, 5000.0, 1.0));
			final TestAutomator test = new TestAutomator(bounds, alpha, genCount, seedP, prob, ops, 1,
					getClass().getResource("/META-INF/jobshop/ta_15_15_dd_res.csv").getFile());

			final List<Variation> ops1 = new ArrayList<Variation>();
			ops1.add(new GPMXCrossover(prob.getNumberOfJobs()));
			ops1.add(new SwapMutation(0.5));
			test.test(ops1);

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/**
	 * test for jo shop pareto finder algorithm
	 */
	public void testJobShopGA1_MT() {
		JobShopProblem prob;
		try {
			prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.csv").getFile(),
					Omni.of(PerformanceMeasures.MAKESPAN, PerformanceMeasures.AVERAGE_FLOW_TIME).toList(), 0.4, 0.8);

			final double alpha[] = {0.5};
			final int genCount[] = {100}; //, 
			final double seedP[] = {0.0};
			final List<BinaryOperator<Double>> ops = new LinkedList<BinaryOperator<Double>>();
			ops.add(new BinaryOperator<Double>() {

				@Override
				public Double apply(final Double t, final Double u) {
					return t + u;
				}

				public String toString() {
					return "Sum";
				}
			});
			final List<List<Double>> bounds = new LinkedList<List<Double>>();
			bounds.add(Arrays.asList(200.0, 1.0, 200.0, 1.0));
			bounds.add(Arrays.asList(200.0, 1.0, 200.0, 1.0));
			final TestAutomator test = new TestAutomator(bounds, alpha, genCount, seedP, prob, ops, 1,
					getClass().getResource("/META-INF/jobshop/ta_15_15_dd_res.csv").getFile());

			final List<Variation> ops1 = new ArrayList<Variation>();
			ops1.add(new GPMXCrossover(prob.getNumberOfJobs()));
			ops1.add(new SwapMutation(0.5));
			test.test(ops1);

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testJobShopGA3() {
		JobShopProblem prob;
		try {
			prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/taillard/ta1_15_15.txt").getFile(),
					Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.MAXIMUM_TARDINESS, PerformanceMeasures.TOTAL_TARDINESS, PerformanceMeasures.AVERAGE_FLOW_TIME).toList(), 0.4,
					0.8); 

					final double alpha[] = {0.3};
					final int genCount[] = {100}; //, 
					final double seedP[] = {0.3};
					final List<BinaryOperator<Double>> ops = new LinkedList<BinaryOperator<Double>>();
					ops.add(new BinaryOperator<Double>() {
		
						@Override
						public Double apply(final Double t, final Double u) {
							return t + u;
						}
		
						public String toString() {
							return "Sum";
						}
					});
			final List<List<Double>> bounds = new LinkedList<List<Double>>();
			bounds.add(Arrays.asList(16.0, 1.0, 16.0, 1.0));
			bounds.add(Arrays.asList(5000.0, 1.0, 5000.0, 1.0));			
			bounds.add(Arrays.asList(15000.0, 1.0, 15000.0, 1.0));
			bounds.add(Arrays.asList(5000.0, 1.0, 5000.0, 1.0));

			final TestAutomator test = new TestAutomator(bounds, alpha, genCount, seedP, prob, ops, 1,
			getClass().getResource("/META-INF/jobshop/ta_15_15_dd_res.csv").getFile());

			final List<Variation> ops1 = new ArrayList<Variation>();
			ops1.add(new GPMXCrossover(15));
			ops1.add(new SwapMutation(0.5));
			test.test(ops1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMOEAJS3Obj() {
		JobShopProblem prob;
		try {
			OperatorFactory.getInstance().addProvider(new OperatorProvider() {
				public String getMutationHint(final Problem problem) {
					return null;
				}

				public String getVariationHint(final Problem problem) {
					return null;
				}

				public Variation getVariation(final String name, final Properties properties, final Problem problem) {
					if (name.equalsIgnoreCase("MyCrossover")) {
						return new GPMXCrossover(15);
					} else if (name.equalsIgnoreCase("MyMutation")) {
						return new SwapMutation(0.5);
					}
					// No match, return null
					return null;
				}
			});
			prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/taillard/ta9_15_15.txt").getFile(),
					Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.AVERAGE_FLOW_TIME, PerformanceMeasures.MAXIMUM_TARDINESS).toList(), 0.4, 0.8);
			final NondominatedPopulation result = new Executor().withProblem(prob).withAlgorithm("NSGAII")
																.withProperty("populationSize", 100)
																.withProperty("operator", "MyCrossover+MyMutation")
																.withMaxEvaluations(1000).run();												
			// final Analyzer analyzer = new Analyzer().withProblem(prob).includeAllMetrics().showStatisticalSignificance();
			// analyzer.add("NSGAII", result);
			// analyzer.printAnalysis();
			result.forEach(res -> System.out.printf("f1 = %f, f2 = %f, f2 = %f \n", res.getObjective(0), res.getObjective(1), res.getObjective(2)));
			double[] min = {1, 1, 1};
			double[] max = {16, 5000, 5000};
			Spacing sp = new Spacing(prob);
			Hypervolume hv = new Hypervolume(prob, min, max);
			System.out.println("Spacing = " + sp.evaluate(result));
			System.out.println("Hypervolume = " + hv.evaluate(result));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMOEAJobShop() {
		JobShopProblem prob;
		try {
			OperatorFactory.getInstance().addProvider(new OperatorProvider() {
				public String getMutationHint(final Problem problem) {
					return null;
				}

				public String getVariationHint(final Problem problem) {
					return null;
				}

				public Variation getVariation(final String name, final Properties properties, final Problem problem) {
					if (name.equalsIgnoreCase("MyCrossover")) {
						return new GPMXCrossover(15);
					} else if (name.equalsIgnoreCase("MyMutation")) {
						return new SwapMutation(0.5);
					}
					// No match, return null
					return null;
				}
			});
			prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/taillard/ta1_15_15.txt").getFile(),
					Omni.of(PerformanceMeasures.MAXIMUM_TARDINESS, PerformanceMeasures.AVERAGE_FLOW_TIME).toList(), 0.4, 0.8);
			final NondominatedPopulation result = new Executor().withProblem(prob).withAlgorithm("NSGAII")
																.withProperty("populationSize", 100)
																.withProperty("operator", "MyCrossover+MyMutation")
																.withMaxEvaluations(1000).run();												
			// final Analyzer analyzer = new Analyzer().withProblem(prob).includeAllMetrics().showStatisticalSignificance();
			// analyzer.add("NSGAII", result);
			// analyzer.printAnalysis();
			result.forEach(res -> System.out.printf("f1 = %f, f2 = %f \n", res.getObjective(0), res.getObjective(1)));
			double[] min = {1, 1};
			double[] max = {15000, 20000};
			Spacing sp = new Spacing(prob);
			Hypervolume hv = new Hypervolume(prob, min, max);
			System.out.println("Spacing = " + sp.evaluate(result));
			System.out.println("Hypervolume = " + hv.evaluate(result));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMOEAJobShop_MT() {
		JobShopProblem prob;
		try {
			prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.csv").getFile(),
					Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.AVERAGE_FLOW_TIME).toList(), 0.4, 0.8);
			OperatorFactory.getInstance().addProvider(new OperatorProvider() {
				public String getMutationHint(final Problem problem) {
					return null;
				}

				public String getVariationHint(final Problem problem) {
					return null;
				}

				public Variation getVariation(final String name, final Properties properties, final Problem problem) {
					if (name.equalsIgnoreCase("MyCrossover")) {
						return new GPMXCrossover(prob.getNumberOfJobs());
					} else if (name.equalsIgnoreCase("MyMutation")) {
						return new SwapMutation(0.5);
					}
					// No match, return null
					return null;
				}
			});

			// AlgorithmFactory.getInstance().addProvider(new FuzzyNSGAProvider());

			final NondominatedPopulation result = new Executor().withProblem(prob).withAlgorithm("NSGAII")
																.withProperty("populationSize", 100)
																.withProperty("operator", "MyCrossover+MyMutation")
																.withMaxEvaluations(1000).run();												
			// final Analyzer analyzer = new Analyzer().withProblem(prob).includeAllMetrics().showStatisticalSignificance();
			// analyzer.add("NSGAII", result);
			// analyzer.printAnalysis();
			result.forEach(res -> System.out.printf("f1 = %f, f2 = %f \n", res.getObjective(0), res.getObjective(1)));
			double[] min = {1, 1};
			double[] max = {15000, 20000};
			Spacing sp = new Spacing(prob);
			Hypervolume hv = new Hypervolume(prob, min, max);
			System.out.println("Spacing = " + sp.evaluate(result));
			System.out.println("Hypervolume = " + hv.evaluate(result));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void comp_PF_MOEA_NT_TT() {

		JobShopProblem prob;
		final double alpha[] = {0.5};
		final int genCount[] = {100}; //
		final double seedP[] = {0.0, 0.1, 0.3, 0.5, 0.7};
		int repeatation = 1;
		try {
			prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/taillard/ta1_20_15.txt").getFile(),
					Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.AVERAGE_FLOW_TIME).toList(), 0.4, 0.8);
			final List<BinaryOperator<Double>> aggr = new LinkedList<BinaryOperator<Double>>();
			aggr.add(new BinaryOperator<Double>() {

				@Override
				public Double apply(final Double t, final Double u) {
					return t + u;
				}

				public String toString() {
					return "Sum";
				}
			});
			final List<List<Double>> bounds = new LinkedList<List<Double>>();
			bounds.add(Arrays.asList(15.0, 1.0, 15.0, 1.0));
			bounds.add(Arrays.asList(5000.0, 1.0, 5000.0, 1.0));
			final TestAutomator test = new TestAutomator(bounds, alpha, genCount, seedP, prob, aggr, repeatation,
					getClass().getResource("/META-INF/jobshop/ta_15_15_dd_res.csv").getFile());

			final List<Variation> ops1 = new ArrayList<Variation>();
			ops1.add(new GPMXCrossover(prob.getNumberOfJobs()));
			ops1.add(new SwapMutation(0.5));
			test.test(ops1);

			//MOEA
			OperatorFactory.getInstance().addProvider(new OperatorProvider() {
				public String getMutationHint(final Problem problem) {
					return null;
				}

				public String getVariationHint(final Problem problem) {
					return null;
				}

				public Variation getVariation(final String name, final Properties properties, final Problem problem) {
					if (name.equalsIgnoreCase("MyCrossover")) {
						return new GPMXCrossover(prob.getNumberOfJobs());
					} else if (name.equalsIgnoreCase("MyMutation")) {
						return new SwapMutation(0.5);
					}
					// No match, return null
					return null;
				}
			});
			double spacing = 0, hypervolume = 0;
			for(int i = 0; i< repeatation; i++) {
				final NondominatedPopulation result = new Executor().withProblem(prob).withAlgorithm("NSGAII")
																	.withProperty("populationSize", 100)
																	.withProperty("operator", "MyCrossover+MyMutation")
																	.withMaxEvaluations(1000).run();
				System.out.println("---------------------------------------------------------------");	
				result.forEach(res -> System.out.printf("f1 = %f, f2 = %f \n", res.getObjective(0), res.getObjective(1)));
				double[] min = {bounds.get(0).get(1), bounds.get(1).get(1)};
				double[] max = {bounds.get(0).get(0), bounds.get(1).get(0)};
				Spacing sp = new Spacing(prob);
				Hypervolume hv = new Hypervolume(prob, min, max);
				spacing += sp.evaluate(result);
				hypervolume += hv.evaluate(result);
			}			
			System.out.println("Spacing = " + spacing/repeatation);
			System.out.println("Hypervolume = " + hypervolume/repeatation);										

		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void comp_PF_MOEA_NT_TT_MuthThomp() {

		JobShopProblem prob;
		final double alpha[] = {0.5};
		final int genCount[] = {100}; //
		final double seedP[] = {0.0};
		int repeatation = 5;
		try {
			prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.csv").getFile(),
					Omni.of(PerformanceMeasures.AVERAGE_FLOW_TIME, PerformanceMeasures.MAKESPAN).toList(), 0.4, 0.8);
			final List<BinaryOperator<Double>> aggr = new LinkedList<BinaryOperator<Double>>();
			aggr.add(new BinaryOperator<Double>() {

				@Override
				public Double apply(final Double t, final Double u) {
					return t + u;
				}

				public String toString() {
					return "Sum";
				}
			});
			final List<List<Double>> bounds = new LinkedList<List<Double>>();
			bounds.add(Arrays.asList(100.0, 0.0, 100.0, 0.0));
			bounds.add(Arrays.asList(100.0, 0.0, 100.0, 0.0));
			final TestAutomator test = new TestAutomator(bounds, alpha, genCount, seedP, prob, aggr, repeatation,
					getClass().getResource("/META-INF/jobshop/ta_15_15_dd_res.csv").getFile());

			final List<Variation> ops1 = new ArrayList<Variation>();
			ops1.add(new GPMXCrossover(prob.getNumberOfJobs()));
			ops1.add(new SwapMutation(0.5));
			test.test(ops1);

			//MOEA
			OperatorFactory.getInstance().addProvider(new OperatorProvider() {
				public String getMutationHint(final Problem problem) {
					return null;
				}

				public String getVariationHint(final Problem problem) {
					return null;
				}

				public Variation getVariation(final String name, final Properties properties, final Problem problem) {
					if (name.equalsIgnoreCase("MyCrossover")) {
						return new GPMXCrossover(prob.getNumberOfJobs());
					} else if (name.equalsIgnoreCase("MyMutation")) {
						return new SwapMutation(0.5);
					}
					// No match, return null
					return null;
				}
			});
			double spacing = 0, hypervolume = 0;
			for(int i = 0; i< repeatation; i++) {
				final NondominatedPopulation result = new Executor().withProblem(prob).withAlgorithm("NSGAII")
																	.withProperty("populationSize", 100)
																	.withProperty("operator", "MyCrossover+MyMutation")
																	.withMaxEvaluations(1000).run();
				System.out.println("---------------------------------------------------------------");	
				result.forEach(res -> System.out.printf("f1 = %f, f2 = %f \n", res.getObjective(0), res.getObjective(1)));
				double[] min = {bounds.get(0).get(1), bounds.get(1).get(1)};
				double[] max = {bounds.get(0).get(0), bounds.get(1).get(0)};
				Spacing sp = new Spacing(prob);
				Hypervolume hv = new Hypervolume(prob, min, max);
				spacing += sp.evaluate(result);
				hypervolume += hv.evaluate(result);
			}			
			System.out.println("Spacing = " + spacing/repeatation);
			System.out.println("Hypervolume = " + hypervolume/repeatation);										

		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testFuzzyNSGA1() {
		JobShopProblem prob;
		try {
			prob = DataGenerator.readTaillardToJobShopWithDD(
					getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.csv").getFile(),
					Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.AVERAGE_FLOW_TIME).toList(), 0.4, 0.8);
			OperatorFactory.getInstance().addProvider(new OperatorProvider() {
				public String getMutationHint(final Problem problem) {
					return null;
				}

				public String getVariationHint(final Problem problem) {
					return null;
				}

				public Variation getVariation(final String name, final Properties properties, final Problem problem) {
					if (name.equalsIgnoreCase("MyCrossover")) {
						return new GPMXCrossover(prob.getNumberOfJobs());
					} else if (name.equalsIgnoreCase("MyMutation")) {
						return new SwapMutation(0.5);
					}
					// No match, return null
					return null;
				}
			});

			final List<Fuzzyficator> fuzzyFictators = new LinkedList<Fuzzyficator>();
			fuzzyFictators.add(new Fuzzyficator(new FuzzyMembershipT(6, 0, 0, 0)));
			fuzzyFictators.add(new Fuzzyficator(new FuzzyMembershipT(100, 0, 100, 60)));

			AlgorithmFactory.getInstance().addProvider(new FuzzyNSGAProvider(fuzzyFictators, new BinaryOperator<Double>() {

				@Override
				public Double apply(final Double t, final Double u) {
					return t + u;
				}

				public String toString() {
					return "Sum";
				}
			}));

			final NondominatedPopulation result = new Executor().withProblem(prob).withAlgorithm("FuzzyNSGAII")
																.withProperty("populationSize", 100)
																.withProperty("operator", "MyCrossover+MyMutation")
																.withMaxEvaluations(1000).run();												
			// final Analyzer analyzer = new Analyzer().withProblem(prob).includeAllMetrics().showStatisticalSignificance();
			// analyzer.add("NSGAII", result);
			// analyzer.printAnalysis();
			result.forEach(res -> System.out.printf("f1 = %f, f2 = %f \n", res.getObjective(0), res.getObjective(1)));
			double[] min = {0, 0};
			double[] max = {6, 100};
			Spacing sp = new Spacing(prob);
			Hypervolume hv = new Hypervolume(prob, min, max);
			System.out.println("Spacing = " + sp.evaluate(result));
			System.out.println("Hypervolume = " + hv.evaluate(result));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}

