package edu.ohiou.mfgresearch.moiga.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.uncommons.maths.random.Probability;

import edu.ohiou.mfgresearch.lambda.Omni;
import edu.ohiou.mfgresearch.moiga.DataGenerator;
import edu.ohiou.mfgresearch.operators.GOXCrossover;
import edu.ohiou.mfgresearch.operators.GPMXCrossover;
import edu.ohiou.mfgresearch.operators.PPXCrossover;
import edu.ohiou.mfgresearch.operators.SwapMutation;
import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.JobT;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic;
import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic.Heuristic;

public class google3X3 {

    JobShopProblem prob;

    @Before
    public void readFile() throws Exception {
        List<JobT> jobs = DataGenerator.readTaillardToJobShopWithDD(
                            getClass().getResource("/META-INF/jobshop/google_3mf.txt").getFile());
        prob = new JobShopProblem(jobs, Omni.of(PerformanceMeasures.NUM_TARDY_JOB).toList());
    }


    @Test
    public void testGOXCrossover_google3X3() throws Exception {

        Solution p1 = prob.newSolution();
        Solution p2 = prob.newSolution();

        System.out.println("P1 -> " + prob.writeSolution(p1));
        System.out.println("P2 -> " + prob.writeSolution(p2));

        GOXCrossover cx = new GOXCrossover(prob);
        Solution[] offsprings = cx.evolve(new Solution[]{p1, p2});        

        System.out.println("O1 -> " + prob.writeSolution(offsprings[0]));
        System.out.println("O2 -> " + prob.writeSolution(offsprings[1]));
    }

    @Test
    public void testGPMXCrossover_google3X3() throws Exception {

        Solution p1 = prob.newSolution();
        Solution p2 = prob.newSolution();

        System.out.println("P1 -> " + prob.writeSolution(p1));
        System.out.println("P2 -> " + prob.writeSolution(p2));

        GPMXCrossover cx = new GPMXCrossover(prob);
        Solution[] offsprings = cx.evolve(new Solution[]{p1, p2});        

        System.out.println("O1 -> " + prob.writeSolution(offsprings[0]));
        System.out.println("O2 -> " + prob.writeSolution(offsprings[1]));
    }

    @Test
    public void testPPXCrossover_google3X3() throws Exception {

        Solution p1 = prob.newSolution();
        Solution p2 = prob.newSolution();

        System.out.println("P1 -> " + prob.writeSolution(p1));
        System.out.println("P2 -> " + prob.writeSolution(p2));

        PPXCrossover cx = new PPXCrossover(prob);
        Solution[] offsprings = cx.evolve(new Solution[]{p1, p2});        

        System.out.println("O1 -> " + prob.writeSolution(offsprings[0]));
        System.out.println("O2 -> " + prob.writeSolution(offsprings[1]));
    }

    @Test
    public void testSwapMutation_google3X3() throws Exception {

        Solution p1 = prob.newSolution();

        System.out.println("P1 -> " + prob.writeSolution(p1));

        SwapMutation cx = new SwapMutation(prob);
        Solution[] offsprings = cx.evolve(new Solution[]{p1});        

        System.out.println("O1 -> " + prob.writeSolution(offsprings[0]));
    }

    @Test
    public void testRandom(){
        for(int i = 0; i< 20; i++){
            System.out.println(PRNG.nextInt(0, 10));
        }
    }

    @Test
    public void testHeuristic() throws Exception {
        List<JobT> schedule = new ArrayList<JobT>();
        ScheduleHeuristic heu = new ScheduleHeuristic(prob.getJobs());
        heu.addScheme(Heuristic.FOR);
        heu.addScheme(Heuristic.MARP);
        heu.addScheme(Heuristic.LARP);
        heu.addScheme(Heuristic.MRP);
        while(heu.hasNext()){
            JobT j = heu.next();
            if(j!=null) schedule.add(j);
        }
        schedule.forEach(j->System.out.println(j));
    } 

    @Test
    public void testIntegerPartition(){
        int totalOps = 36, n = 16;
        //randomly fill up P(i)
        int[] ps = new int[n];
        int total = 0;
        for(int i=0; i< n; i++){
            int p = PRNG.nextInt(0, totalOps);
            total += p;
            if(total >= totalOps){
                ps[i] = 0;
                total -= p;
            }
            else{
                ps[i] = p;
            }
        }
        int diff = totalOps - total;
        while(diff != 0){
            int i = PRNG.nextInt(n);
            if(ps[i]>= totalOps) {
                continue;
            }
            ps[i] += 1;
            diff -= 1;
        }
        PRNG.shuffle(ps);
        System.out.println(Arrays.toString(ps));
        System.out.println(IntStream.of(ps).reduce(0, (a,b)->a+b));
    }

    @Test
    public void testProbablity(){
        Probability p = new Probability(0.8);
        for(int i=0; i<20; i++){
            System.out.print(p.nextEvent(new Random()) + " ");
        }
    }    
}
