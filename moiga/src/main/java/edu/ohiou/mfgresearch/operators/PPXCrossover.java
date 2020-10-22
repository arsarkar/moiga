package edu.ohiou.mfgresearch.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.moeaframework.core.Variation;
import org.moeaframework.core.Solution;

import edu.ohiou.mfgresearch.schedule.JobT;
import edu.ohiou.mfgresearch.schedule.JobShopProblem;

/**
 * Precedence Preservative Crossover (PPX) perfectly respects the absolute order
 * of genes in parental chromosomes. First the offspring chromosome is
 * initialized empty. Then a vector of length n is randomly filled with elements
 * of the set f1; 2g. This vector de nes the order in which genes are drawn from
 * parent 1 and parent 2 respectively. After a gene is drawn from one parent and
 * deleted in the other one, it is appended to the offspring chromosome. This
 * step is repeated until both parent chromosomes are empty and the o spring
 * contains all genes involved.
 */
public class PPXCrossover implements Variation{
    JobShopProblem problem;

	public PPXCrossover(JobShopProblem prob) {
		this.problem = prob;
	}
    
    @Override
    public int getArity(){
        return 2;
    }

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution o1 = this.getOffspring(parents[0], parents[1]);
		Solution o2 = this.getOffspring(parents[1], parents[0]);
		return new Solution[] { o1, o2 };
	}

    Solution getOffspring(Solution p1, Solution p2) {

        int size = p1.getNumberOfVariables();
        int[] drawOrders = new int[size];
        
        //assign draw order randomly from {1,2}
        for(int i=0; i < size; i++){
            if(Math.random() <= 0.5){
                drawOrders[i] = 1;
            }
            else{
                drawOrders[i] = 2;
            }
        }

        System.out.println("Draw order -> " + IntStream.of(drawOrders).mapToObj(i->String.valueOf(i)).collect(Collectors.joining(",")));

        List<JobT> p1List = IntStream.range(0, size)
                                     .mapToObj(i->(JobT) p1.getVariable(i))
                                     .collect(Collectors.toList());

        List<JobT> p2List = IntStream.range(0, size)
                                     .mapToObj(i->(JobT) p2.getVariable(i))
                                     .collect(Collectors.toList());

        //add offspring list
        List<JobT> offList = new ArrayList<JobT>();
        for(int i=0; i < size; i++){
            if(drawOrders[i] == 1){
                //pick from P1 and delete from both list
                JobT j = p1List.get(0);
                p1List.remove(0);
                for(int k=0; k<p2List.size(); k++){
                    if(p2List.get(k).jobID == j.jobID &&
                        p2List.get(k).getFirstOperation().getMachineID() == j.getFirstOperation().getMachineID()){
                        p2List.remove(k);
                        break;
                    }
                }
                offList.add(j);
            }
            else{
                //pick from P2 and delete from both list
                JobT j = p2List.get(0);
                p2List.remove(0);
                for(int k=0; k<p1List.size(); k++){
                    if(p1List.get(k).jobID == j.jobID &&
                        p1List.get(k).getFirstOperation().getMachineID() == j.getFirstOperation().getMachineID()){
                        p1List.remove(k);
                        break;
                    }
                }
                offList.add(j);
            }
        }

		//reassign the operations
		List<Long> jids = offList.stream().map(j->j.jobID).collect(Collectors.toList());
		p2List = problem.assignOperations(jids); 

		Solution offspring = new Solution(p1.getNumberOfVariables(), p1.getNumberOfObjectives());
		for (int i = 0; i < offspring.getNumberOfVariables(); i++) {
			offspring.setVariable(i, p2List.get(i));
		}
		return offspring;
    }
}
