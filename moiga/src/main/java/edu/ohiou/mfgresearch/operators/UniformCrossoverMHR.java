package edu.ohiou.mfgresearch.operators;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.uncommons.maths.random.Probability;

import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;
import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.MHR;
import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic.Heuristic;
import jmetal.core.Problem;
import jmetal.core.Variable;

public class UniformCrossoverMHR implements Variation {

    Probability probability;
    // List<Fuzzyficator> fuzzyficators;
    // BinaryOperator<Double> aggregator;

    public UniformCrossoverMHR(Probability probability) {
        this.probability = probability;
        // this.fuzzyficators = fuzzyficators;
        // this.aggregator = aggregator;
    }

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public Solution[] evolve(Solution[] parents) {
		Solution o1 = this.evolve(parents[0], parents[1]);
		Solution o2 = this.evolve(parents[1], parents[0]);
		return new Solution[] { o1, o2 };
    }

    private Solution evolve(Solution p1, Solution p2) {

        Heuristic[] schemes = new Heuristic[p1.getNumberOfVariables()];
        int[] counts = new int[p1.getNumberOfVariables()];
        //assign gene from P1 with probability (prob) and from p2 with probability (1-prob)
        for(int i = 0; i < p1.getNumberOfVariables(); i++){
            if(probability.nextEvent(new Random())){
                MHR v = (MHR) p1.getVariable(i);
                schemes[i] = v.getH(); 
                counts[i] = v.getP();
            }
            else{
                MHR v = (MHR) p2.getVariable(i);
                schemes[i] = v.getH(); 
                counts[i] = v.getP();             
            }
        }

        //adjust the counts to equal to total number of operation
        int nt = Arrays.stream(counts).sum();
        int total = IntStream.range(0, p1.getNumberOfVariables()).mapToObj(i-> (MHR) p1.getVariable(i))
                             .mapToInt(m->m.getP()).sum();
        int diff = total - nt;
        while(diff != 0){
            int i = PRNG.nextInt(p1.getNumberOfVariables());
            if(diff < 0){
                if(counts[i]>0){
                    counts[i] -= 1;
                    diff += 1;
                }
            }
            else{
                counts[i] += 1;
                diff -= 1;
            }
        }
        //now make a new solution
        Solution os = new Solution(p1.getNumberOfVariables(), p1.getNumberOfObjectives());
        for(int i = 0; i < p1.getNumberOfVariables(); i++){
            os.setVariable(i, new MHR(schemes[i], counts[i]));
        }
        return os;
    }
}
