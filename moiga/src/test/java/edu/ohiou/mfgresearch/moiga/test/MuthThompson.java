package edu.ohiou.mfgresearch.moiga.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Solution;
import org.moeaframework.util.tree.For;

import edu.ohiou.mfgresearch.lambda.Omni;
import edu.ohiou.mfgresearch.moiga.DataGenerator;
import edu.ohiou.mfgresearch.schedule.Job;
import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.JobT;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;

public class MuthThompson {

    JobShopProblem prob;

    @Before
    public void initialize() throws Exception {
        prob = DataGenerator.readTaillardToJobShopWithDD(
                getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.txt").getFile(),
                Omni.of(PerformanceMeasures.NUM_TARDY_JOB, 
                        PerformanceMeasures.MAKESPAN, 
                        PerformanceMeasures.TOTAL_TARDINESS, 
                        PerformanceMeasures.AVERAGE_FLOW_TIME).toList(), 0.4, 0.8);
    }

    @Test
    public void testNewSolution() throws IOException {
        Solution s = prob.newSolution();
        prob.evaluate(s);
        // for(int i=0; i<s.getNumberOfVariables(); i++){
        //     System.out.println(s.getVariable(i).toString());
        // }
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
    public void testCustomSolution() throws IOException {
        Solution s = new Solution(prob.getNumberOfVariables(), prob.getNumberOfObjectives());
        int ix = -1;
        s.setVariable(++ix, prob.getJob(1L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(5L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(5L).generateJobOperation(2L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(2L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(1L).generateJobOperation(2L));
        s.setVariable(++ix, prob.getJob(1L).generateJobOperation(3L));
        s.setVariable(++ix, prob.getJob(5L).generateJobOperation(3L));
        s.setVariable(++ix, prob.getJob(1L).generateJobOperation(4L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(2L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(3L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(2L));
        s.setVariable(++ix, prob.getJob(1L).generateJobOperation(5L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(4L));
        s.setVariable(++ix, prob.getJob(5L).generateJobOperation(4L));
        s.setVariable(++ix, prob.getJob(2L).generateJobOperation(2L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(5L));
        s.setVariable(++ix, prob.getJob(5L).generateJobOperation(5L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(3L));
        s.setVariable(++ix, prob.getJob(2L).generateJobOperation(3L));
        s.setVariable(++ix, prob.getJob(4L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(6L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(4L));
        s.setVariable(++ix, prob.getJob(1L).generateJobOperation(6L));
        s.setVariable(++ix, prob.getJob(5L).generateJobOperation(6L));
        s.setVariable(++ix, prob.getJob(2L).generateJobOperation(4L));
        s.setVariable(++ix, prob.getJob(2L).generateJobOperation(5L));
        s.setVariable(++ix, prob.getJob(4L).generateJobOperation(2L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(5L));
        s.setVariable(++ix, prob.getJob(2L).generateJobOperation(6L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(6L));
        s.setVariable(++ix, prob.getJob(4L).generateJobOperation(3L));
        s.setVariable(++ix, prob.getJob(4L).generateJobOperation(4L));
        s.setVariable(++ix, prob.getJob(4L).generateJobOperation(5L));
        s.setVariable(++ix, prob.getJob(4L).generateJobOperation(6L));
        prob.evaluate(s);
        System.out.println("Objective = " + s.getObjective(0));
        for(int i=0; i<s.getNumberOfVariables(); i++){
            System.out.println(s.getVariable(i).toString());
        }
        FileWriter fw = new FileWriter(getClass().getResource("/META-INF/jobshop/gantt1.csv").getFile());
        prob.writeGantt(fw, s);
        fw.flush();
        fw.close();
    }   

}
