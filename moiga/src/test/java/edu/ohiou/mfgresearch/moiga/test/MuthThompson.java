package edu.ohiou.mfgresearch.moiga.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import edu.ohiou.mfgresearch.operators.GOXCrossover;
import edu.ohiou.mfgresearch.operators.GPMXCrossover;
import edu.ohiou.mfgresearch.operators.SwapMutation;
import edu.ohiou.mfgresearch.operators.SwapMutationMHR;
import edu.ohiou.mfgresearch.operators.UniformCrossoverMHR;
import edu.ohiou.mfgresearch.schedule.JSDispMHR;
import edu.ohiou.mfgresearch.schedule.Job;
import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.JobT;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic;
import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic.Heuristic;
import edu.ohiou.mfgresearch.solver.algorithm.FuzzyNSGAProvider;


public class MuthThompson {

    JobShopProblem prob;

    @Before
    public void initialize() throws Exception {
        List<JobT> jobs = DataGenerator.readTaillardToJobShopWithDD(
                            getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.txt").getFile());
        prob = new JobShopProblem(jobs, Omni.of(PerformanceMeasures.NUM_TARDY_JOB, 
                                                    PerformanceMeasures.MAKESPAN, 
                                                    PerformanceMeasures.TOTAL_TARDINESS, 
                                                    PerformanceMeasures.AVERAGE_FLOW_TIME).toList());
    }

    @Test
    public void testNewSolution() throws IOException {
        Solution s = prob.newSolution();
        for(int i=0; i<s.getNumberOfVariables(); i++){
            System.out.println(s.getVariable(i).toString());
        }
        prob.evaluate(s);
        List<Job> jlist = prob.getJobs().stream().map(ii -> (Job) ii).collect(Collectors.toList());	
        jlist.forEach(j->System.out.println("J" + j.jobID + " ct: " + j.getCompletionTime()));
        System.out.println("Num Tardy = " + s.getObjective(0));	
        System.out.println("makespan = " + s.getObjective(1));	
        System.out.println("Total tardiness = " + s.getObjective(2));	
        System.out.println("Avg Flow = " + s.getObjective(3));

        FileWriter fw = new FileWriter(getClass().getResource("/META-INF/jobshop/gantt1.csv").getFile());
        prob.writeGantt(fw, s);
        fw.flush();
        fw.close();
    }

    @Test
    public void testGOXCrossover2() throws IOException {
        Solution p1 = prob.newSolution();
        Solution p2 = prob.newSolution();

        GOXCrossover cx = new GOXCrossover(prob);
        Solution[] offsprings = cx.evolve(new Solution[]{p1, p2});

        FileWriter fw = new FileWriter(getClass().getResource("/META-INF/jobshop/gantt1.csv").getFile());
        prob.writeGantt(fw, offsprings[0]);
        fw.flush();
        fw.close();

        fw = new FileWriter(getClass().getResource("/META-INF/jobshop/gantt2.csv").getFile());
        prob.writeGantt(fw, offsprings[1]);
        fw.flush();
        fw.close();
    }

    @Test
    public void testSwapMutation() throws Exception {

        Solution p1 = prob.newSolution();

        System.out.println("P1 -> " + prob.writeSolution(p1));

        SwapMutation cx = new SwapMutation(prob);
        Solution[] offsprings = cx.evolve(new Solution[]{p1});        

        System.out.println("O1 -> " + prob.writeSolution(offsprings[0]));
    }

    /**
 * NSGA Benchmark (Muth Thompson Suer)
 * Makespan = 55
 * # tardy = 2
 * Total Tardiness = 8
 * Max Tardiness = 3
 * Avg Flow = 46.50
 */
    @Test
    public void Fuzzy1ObjJS1() throws Exception {

        PerformanceMeasures pm = PerformanceMeasures.AVERAGE_FLOW_TIME;
        Fuzzyficator fuzzB = new Fuzzyficator(new FuzzyMembershipT(6, 0, 0, 0));
        int populationSize = 100;
        int generationCount = 100;
            
        List<JobT> jobs = DataGenerator.readTaillardToJobShopWithDD(
            getClass().getResource("/META-INF/jobshop/muth_thompson_6_6_Suer.txt").getFile());
        prob = new JobShopProblem(jobs, Omni.of(pm).toList());

        OperatorFactory.getInstance().addProvider(new OperatorProvider() {
            public String getMutationHint(final Problem problem) {
                return null;
            }

            public String getVariationHint(final Problem problem) {
                return null;
            }

            public Variation getVariation(final String name, final Properties properties, final Problem problem) {
                if (name.equalsIgnoreCase("MyCrossover")) {
                    return new GPMXCrossover(prob);
                } else if (name.equalsIgnoreCase("MyMutation")) {
                    return new SwapMutation(prob);
                }
                // No match, return null
                return null;
            }
        });

        AlgorithmFactory.getInstance().addProvider(new FuzzyNSGAProvider(Omni.of(fuzzB).toList(), null));

        final NondominatedPopulation result = new Executor().withProblem(prob).withAlgorithm("NSGAII")
                                                            .withProperty("populationSize", populationSize)
                                                            .withProperty("operator", "MyCrossover+MyMutation")                                                            
                                                            .withMaxEvaluations(populationSize*generationCount).run();
        result.forEach(s->{
                                System.out.printf("Obj = %.2f, satisfaction = %.2f \n", s.getObjective(0), 
                                fuzzB.apply(s.getObjective(0)));
                                // System.out.println("Chromosome -->");
                                // IntStream.range(0, s.getNumberOfVariables()-1)
                                //          .forEach(i->System.out.println(s.getVariable(i).toString()));
                          });       
        
    }


    @Test
    public void Fuzzy2ObjJS1() throws Exception {

        PerformanceMeasures pm1 = PerformanceMeasures.NUM_TARDY_JOB;
        PerformanceMeasures pm2 = PerformanceMeasures.AVERAGE_FLOW_TIME;
        Fuzzyficator fuzzB1 = new Fuzzyficator(new FuzzyMembershipT(6, 0, 0, 0));
        Fuzzyficator fuzzB2 = new Fuzzyficator(new FuzzyMembershipT(200, 0, 0, 0));
        int populationSize = 100;
        int generationCount = 100;
            
        List<JobT> jobs = DataGenerator.readTaillardToJobShopWithDD(
            getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.txt").getFile());
        prob = new JobShopProblem(jobs, Omni.of(pm1, pm2).toList());

        OperatorFactory.getInstance().addProvider(new OperatorProvider() {
            public String getMutationHint(final Problem problem) {
                return null;
            }

            public String getVariationHint(final Problem problem) {
                return null;
            }

            public Variation getVariation(final String name, final Properties properties, final Problem problem) {
                if (name.equalsIgnoreCase("MyCrossover")) {
                    return new GPMXCrossover(prob);
                } else if (name.equalsIgnoreCase("MyMutation")) {
                    return new SwapMutation(prob);
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
                                System.out.printf("Obj1 = %.2f(%.2f), Obj2 = %.2f(%.2f) \n", 
                                s.getObjective(0), fuzzB1.apply(s.getObjective(0)), 
                                s.getObjective(1), fuzzB1.apply(s.getObjective(1)));
                                // System.out.println("Chromosome -->");
                                // IntStream.range(0, s.getNumberOfVariables()-1)
                                //          .forEach(i->System.out.println(s.getVariable(i).toString()));
                          });       
        
    }

    @Test
    public void testHeuristic() throws Exception {
        List<JobT> schedule = new ArrayList<JobT>();
        ScheduleHeuristic heu = new ScheduleHeuristic(prob.getJobs());
        heu.addScheme(Heuristic.LRP);
        heu.addScheme(Heuristic.EDD);
        heu.addScheme(Heuristic.MARP);
        heu.addScheme(Heuristic.MOR);
        while(heu.hasNext()){
            JobT j = heu.next();
            if(j!=null) schedule.add(j);
        }
        schedule.forEach(j->System.out.println(j));
    } 

    public void testHyperHeuristic(){


    }

}
