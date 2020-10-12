package edu.ohiou.mfgresearch.moiga.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Solution;
import org.moeaframework.util.tree.For;

import edu.ohiou.mfgresearch.lambda.Omni;
import edu.ohiou.mfgresearch.moiga.DataGenerator;
import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.JobT;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;

public class MuthThompson {

    JobShopProblem prob;

    @Before
    public void initialize() throws Exception {
        prob = DataGenerator.readTaillardToJobShopWithDD(
                getClass().getResource("/META-INF/jobshop/muth_thompson_6_6.txt").getFile(),
                Omni.of(PerformanceMeasures.NUM_TARDY_JOB).toList(), 0.4, 0.8);
    }

    @Test
    public void testNewSolution() throws IOException {
        Solution s = prob.newSolution();
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

    @Test
    public void testCustomSolution() throws IOException {
        Solution s = new Solution(prob.getNumberOfVariables(), prob.getNumberOfObjectives());
        int ix = -1;
        s.setVariable(++ix, prob.getJob(1L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(1L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(5L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(1L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(4L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(4L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(1L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(1L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(5L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(4L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(2L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(5L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(5L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(5L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(3L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(4L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(4L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(2L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(6L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(2L).generateJobOperation(1L));
        s.setVariable(++ix, prob.getJob(2L).generateJobOperation(1L));
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
