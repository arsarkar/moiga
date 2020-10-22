package edu.ohiou.mfgresearch.moiga.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.OperatorProvider;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.misc.Binh;
import org.moeaframework.problem.misc.Binh2;

import edu.ohiou.mfgresearch.fuzzy.FuzzyMembershipT;
import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;
import edu.ohiou.mfgresearch.lambda.Omni;
import edu.ohiou.mfgresearch.moiga.DataGenerator;
import edu.ohiou.mfgresearch.operators.GPMXCrossover;
import edu.ohiou.mfgresearch.operators.SwapMutation;
import edu.ohiou.mfgresearch.schedule.Job;
import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.JobT;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.solver.algorithm.FuzzyNSGAProvider;

public class MOEAFunction {
    
    @Test
    public void MOEA_NSGA_Functions() throws Exception {

        //Problems: Binh, Binh2, Binh3, Fonseca, Fonseca2, Kursawe
        NondominatedPopulation result = new Executor().withProblem("Binh2")
                                                      .withAlgorithm("NSGAII")
                                                      .withProperty("populationSize", 50)
                                                      .withMaxEvaluations(1000)
                                                      .run();

        // result.forEach(s->{
        //     System.out.println(s.getObjective(0) + " " + s.getObjective(1));
        // });
    }

    @Test
    public void MOEA_Fuzzy_Functions() throws Exception {

        final List<Fuzzyficator> fuzzyFictators = new LinkedList<Fuzzyficator>();
        fuzzyFictators.add(new Fuzzyficator(new FuzzyMembershipT(120, 0, 60, 40)));
        fuzzyFictators.add(new Fuzzyficator(new FuzzyMembershipT(50, 0, 20, 10)));

        AlgorithmFactory.getInstance().addProvider(new FuzzyNSGAProvider(fuzzyFictators, new BinaryOperator<Double>() {

            @Override
            public Double apply(final Double t, final Double u) {
                return t + u;
            }

            public String toString() {
                return "Sum";
            }
        }));
        
        //Problems: Binh, Binh2, Binh3, Fonseca, Fonseca2, 
        NondominatedPopulation result = new Executor().withProblem("Binh2")
                                                      .withAlgorithm("FuzzyNSGAII")
                                                      .withProperty("populationSize", 10)
                                                      .withMaxEvaluations(50)
                                                      .run();

        result.forEach(s->{
            System.out.println(s.getObjective(0) + " " + s.getObjective(1));
        });
    }

    @Test
    public void testNewSolution() throws Exception {
        List<JobT> jobs = DataGenerator.readTaillardToJobShopWithDD(
                            getClass().getResource("/META-INF/jobshop/google_3m.txt").getFile());
        JobShopProblem  prob = new JobShopProblem(jobs, Omni.of(PerformanceMeasures.NUM_TARDY_JOB, 
                                                                PerformanceMeasures.MAKESPAN, 
                                                                PerformanceMeasures.TOTAL_TARDINESS, 
                                                                PerformanceMeasures.AVERAGE_FLOW_TIME).toList());
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

    /**
     * Result
     * muth_thompson_6_6.csv
     * - NUM_TARDY_JOB = 3.0
     * - AVERAGE_FLOW_TIME = 32.83
     * - MAXIMUM_TARDINESS = 18.00
     * - MAXIMUM_EARLINESS = 0.0
     * - MAXIMUM_LATENESS = 48.0
     * - MAKESPAN = 47.0
     * - TOTAL_TARDINESS = 22.0
     */
    @Test
    public void Fuzzy1ObjJS1() throws Exception {

        PerformanceMeasures pm = PerformanceMeasures.NUM_TARDY_JOB;
        Fuzzyficator fuzzB = new Fuzzyficator(new FuzzyMembershipT(6, 0, 0, 0));
        List<JobT> jobs =DataGenerator.readTaillardToJobShopWithDD(
            getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.csv").getFile());
        JobShopProblem prob = new JobShopProblem(jobs, Omni.of(pm).toList());
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

        AlgorithmFactory.getInstance().addProvider(new FuzzyNSGAProvider(Omni.of(fuzzB).toList(), new BinaryOperator<Double>() {

            @Override
            public Double apply(final Double t, final Double u) {
                return t + u;
            }

            public String toString() {
                return "Sum";
            }
        }));

        final NondominatedPopulation result = new Executor().withProblem(prob).withAlgorithm("FuzzyNSGAII")
                                                            .withProperty("populationSize", 1000)
                                                            .withProperty("operator", "MyCrossover+MyMutation")                                                            
                                                            .withMaxEvaluations(1000*50).run();
        result.forEach(s->{
                                System.out.printf("Obj = %.2f, satisfaction = %.2f \n", s.getObjective(0), 
                                fuzzB.apply(s.getObjective(0)));
                                System.out.println("Chromosome -->");
                                IntStream.range(0, s.getNumberOfVariables()-1)
                                         .forEach(i->System.out.println(s.getVariable(i).toString()));
                          });
        
        
    }

    @Test
    public void MOEAFNSGAJS1() throws Exception {
        List<JobT> jobs = DataGenerator.readTaillardToJobShopWithDD(
            getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.csv").getFile());
        JobShopProblem prob = new JobShopProblem(jobs, 
            Omni.of(PerformanceMeasures.NUM_TARDY_JOB, PerformanceMeasures.AVERAGE_FLOW_TIME).toList());
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

        final List<Fuzzyficator> fuzzyFictators = new LinkedList<Fuzzyficator>();
        fuzzyFictators.add(new Fuzzyficator(new FuzzyMembershipT(6, 0, 2, 0)));
        fuzzyFictators.add(new Fuzzyficator(new FuzzyMembershipT(100, 0, 100, 100)));

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
                                                            .withMaxEvaluations(2000).run();
        result.forEach(s->{
                                System.out.printf(" %.2f, %.2f, %.2f, %.2f \n", s.getObjective(0), s.getObjective(1), 
                                fuzzyFictators.get(0).apply(s.getObjective(0)), fuzzyFictators.get(1).apply(s.getObjective(1)));
                          });
    }
}
