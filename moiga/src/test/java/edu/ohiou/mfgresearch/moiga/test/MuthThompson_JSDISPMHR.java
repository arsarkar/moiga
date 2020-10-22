package edu.ohiou.mfgresearch.moiga.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.OperatorProvider;
import org.uncommons.maths.random.Probability;

import edu.ohiou.mfgresearch.fuzzy.FuzzyMembershipT;
import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;
import edu.ohiou.mfgresearch.lambda.Omni;
import edu.ohiou.mfgresearch.moiga.DataGenerator;
import edu.ohiou.mfgresearch.schedule.JSDispMHR;
import edu.ohiou.mfgresearch.schedule.JobT;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic;
import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic.Heuristic;
import edu.ohiou.mfgresearch.solver.algorithm.FuzzyNSGAProvider;
import edu.ohiou.mfgresearch.operators.SwapMutationMHR;
import edu.ohiou.mfgresearch.operators.UniformCrossoverMHR;

public class MuthThompson_JSDISPMHR {
    List<JobT> jobs;
    JSDispMHR prob;
    List<Heuristic> schemes;

    @Before
    public void initialize() throws Exception {
        schemes = new LinkedList<Heuristic>();
        schemes.add(Heuristic.SPT);
        schemes.add(Heuristic.FOR);
        schemes.add(Heuristic.EDD);
        schemes.add(Heuristic.LRP);
        schemes.add(Heuristic.LARP);
        schemes.add(Heuristic.LPT);
        schemes.add(Heuristic.MOR);
        schemes.add(Heuristic.MRP);
        schemes.add(Heuristic.MARP);
        schemes.add(Heuristic.EDD);
        schemes.add(Heuristic.LDD);
        schemes.add(Heuristic.LST);
        schemes.add(Heuristic.LARST);
        jobs = DataGenerator.readTaillardToJobShopWithDD(
            getClass().getResource("/META-INF/jobshop/muth_thompson_6_6_Suer.txt").getFile());
        prob = new JSDispMHR(jobs, Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.AVERAGE_FLOW_TIME).toList(), schemes);
    }

    @Test
    public void testHeuristicDueDate() throws Exception{
        ScheduleHeuristic heu = new ScheduleHeuristic(jobs);
        heu.addScheme(Heuristic.LARST);
        heu.addScheme(Heuristic.LST);
        heu.addScheme(Heuristic.EDD);
        // heu.addScheme(Heuristic.LRP);
        List<JobT> schedule = new ArrayList<JobT>();
        while(heu.hasNext()){
            JobT j = heu.next();
            if(j!=null) schedule.add(j);
        }
        schedule.forEach(j->System.out.println(j));
        Map<Long, JobT> jobs = prob.setCompletionTimes(schedule);
        System.out.println(jobs.values().stream().map(j->j.toString()).collect(Collectors.joining(",")));
        System.out.println(PerformanceMeasures.NUM_TARDY_JOB.evaluate(jobs.values().stream().collect(Collectors.toList())));
    }

    @Test
    public void DispMHRnewSolution() throws Exception {

        Solution s = prob.newSolution();
        System.out.println(prob.toSolutionString(s));
        prob.evaluate(s);
        System.out.println(s.getObjective(0));
    }

    @Test
    public void testUniformCrossoverMHR(){
        Solution s1 = prob.newSolution();
        prob.evaluate(s1);
        System.out.println(prob.toSolutionString(s1));
        Solution s2 = prob.newSolution();
        prob.evaluate(s2);
        System.out.println(prob.toSolutionString(s2));
        UniformCrossoverMHR uc = new UniformCrossoverMHR(new Probability(0.3));
        Solution[] offsprings = uc.evolve(new Solution[]{s1, s2});
        System.out.println("O1->"+prob.toSolutionString(offsprings[0]));
        System.out.println("O2->"+prob.toSolutionString(offsprings[1]));
    }

    @Test
    public void testSwapMutationMHR(){
        Solution s1 = prob.newSolution();
        prob.evaluate(s1);
        System.out.println(prob.toSolutionString(s1));
        SwapMutationMHR uc = new SwapMutationMHR(schemes, new Probability(0.1));
        Solution[] offsprings = uc.evolve(new Solution[]{s1});
        System.out.println("O1->"+prob.toSolutionString(offsprings[0]));
    }

    @Test
    public void DISP1ObjJS1_NSGA() throws Exception {
        int populationSize = 100;
        int generationCount = 1000;
        prob.MAX_EVAL = generationCount;
        prob.setPerformanceMeasures(Omni.of(PerformanceMeasures.NUM_TARDY_JOB).toList());
        OperatorFactory.getInstance().addProvider(new OperatorProvider() {
            public String getMutationHint(final Problem problem) {
                return null;
            }

            public String getVariationHint(final Problem problem) {
                return null;
            }

            public Variation getVariation(final String name, final Properties properties, final Problem problem) {
                if (name.equalsIgnoreCase("MyCrossover")) {
                    return new UniformCrossoverMHR(new Probability(0.5));
                } else if (name.equalsIgnoreCase("MyMutation")) {
                    return new SwapMutationMHR(schemes, new Probability(0.1));
                }
                // No match, return null
                return null;
            }
        });

        final NondominatedPopulation result = new Executor().withProblem(prob).withAlgorithm("NSGAII")
                                                            .withProperty("populationSize", populationSize)
                                                            .withProperty("operator", "MyCrossover+MyMutation")                                                            
                                                            .withMaxEvaluations(populationSize*generationCount).run();
        result.forEach(s->{
                                System.out.printf("Obj = %.2f\n", s.getObjective(0));
                          });       
        
    }

    @Test
    public void FuzzyDISP2ObjJS1_NSGA() throws Exception {
        Fuzzyficator fuzzB1 = new Fuzzyficator(new FuzzyMembershipT(200, 0, 0, 0));
        Fuzzyficator fuzzB2 = new Fuzzyficator(new FuzzyMembershipT(200, 0, 0, 0));
        int populationSize = 1000;
        int generationCount = 1000;
        prob.MAX_EVAL = generationCount;
        prob.setPerformanceMeasures(Omni.of(PerformanceMeasures.MAXIMUM_TARDINESS, PerformanceMeasures.AVERAGE_FLOW_TIME).toList());
        OperatorFactory.getInstance().addProvider(new OperatorProvider() {
            public String getMutationHint(final Problem problem) {
                return null;
            }

            public String getVariationHint(final Problem problem) {
                return null;
            }

            public Variation getVariation(final String name, final Properties properties, final Problem problem) {
                if (name.equalsIgnoreCase("MyCrossover")) {
                    return new UniformCrossoverMHR(new Probability(0.5));
                } else if (name.equalsIgnoreCase("MyMutation")) {
                    return new SwapMutationMHR(schemes, new Probability(0.1));
                }
                // No match, return null
                return null;
            }
        });

        AlgorithmFactory.getInstance().addProvider(new FuzzyNSGAProvider(Omni.of(fuzzB1, fuzzB2).toList(), new BinaryOperator<Double>() {

            @Override
            public Double apply(final Double t, final Double u) {
                return t + u;
            }

            public String toString() {
                return "Sum";
            }
        }));

        final NondominatedPopulation result = new Executor().withProblem(prob).withAlgorithm("FuzzyNSGAII")
                                                            .withProperty("populationSize", populationSize)
                                                            .withProperty("operator", "MyCrossover+MyMutation")                                                            
                                                            .withMaxEvaluations(populationSize*generationCount).run();
        result.forEach(s->{
                                System.out.printf("[(Obj1 = %.2f, satisfaction1 = %.2f), (Obj2 = %.2f, satisfaction2 = %.2f)] \n", 
                                s.getObjective(0), fuzzB1.apply(s.getObjective(0)), 
                                s.getObjective(1), fuzzB2.apply(s.getObjective(1)));
                                // System.out.println("Chromosome -->");
                                // IntStream.range(0, s.getNumberOfVariables()-1)
                                //          .forEach(i->System.out.println(s.getVariable(i).toString()));
                          });       
        
    }
}
