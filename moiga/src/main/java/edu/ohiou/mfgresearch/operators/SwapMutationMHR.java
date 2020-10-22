package edu.ohiou.mfgresearch.operators;

import java.util.List;
import java.util.Random;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.util.tree.While;
import org.uncommons.maths.random.Probability;

import edu.ohiou.mfgresearch.schedule.MHR;
import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic.Heuristic;

public class SwapMutationMHR implements Variation {

    List<Heuristic> schemes;
    Probability prob;

    public SwapMutationMHR(List<Heuristic> schemes, Probability prob) {
        this.schemes = schemes;
        this.prob = prob;
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public Solution[] evolve(Solution[] parents) {
        Solution s = parents[0];
        Heuristic[] nhs = new Heuristic[s.getNumberOfVariables()];
        int[] cs = new int[s.getNumberOfVariables()];
        for (int i = 0; i < s.getNumberOfVariables(); i++) {
            Heuristic h = ((MHR) s.getVariable(i)).getH();
            if (prob.nextEvent(new Random())) {
                // select a random heuristic other than in this gene
                Heuristic nh = h;
                while (nh == h) {
                    nh = schemes.get(PRNG.nextInt(schemes.size()));
                    nhs[i] = nh;
                }
            } else {
                // just stick to the existing scheme
                nhs[i] = h;
            }
            // store the existing count
            cs[i] = ((MHR) s.getVariable(i)).getP();
        }
        // swap two positions
        int i = PRNG.nextInt(s.getNumberOfVariables());
        int buf = cs[i];
        int j = PRNG.nextInt(s.getNumberOfVariables());
        cs[i] = cs[j];
        cs[j] = buf;

        // now make a new solution
        Solution os = new Solution(s.getNumberOfVariables(), s.getNumberOfObjectives());
        for (int k = 0; k < s.getNumberOfVariables(); k++) {
            os.setVariable(k, new MHR(nhs[k], cs[k]));
        }
        return new Solution[]{os};
    }
}
