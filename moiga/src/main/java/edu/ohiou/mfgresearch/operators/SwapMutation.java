package edu.ohiou.mfgresearch.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.JobT;

public class SwapMutation implements Variation {
	JobShopProblem problem;

	public SwapMutation(JobShopProblem prob) {
		this.problem = prob;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {

		List<JobT> p1List = IntStream.range(0, parents[0].getNumberOfVariables())
									.mapToObj(i->(JobT) parents[0].getVariable(i))
									.collect(Collectors.toList());
		//get the jobIDs		
		List<Long> jids = p1List.stream().map(j->j.jobID).collect(Collectors.toList());

		//randomly select first position
		int pos1 = PRNG.nextInt(0, p1List.size()-1);
		Long jid1 = p1List.get(pos1).jobID;
		//randomly select another position which is not same as first position
		int pos2 = pos1;
		while(pos1==pos2){
			pos2 = PRNG.nextInt(0, p1List.size()-1);
		}
		Long jid2 = p1List.get(pos2).jobID;	
		//switch place
		List<Long> jidSwapped = new ArrayList<Long>();
		for(int i = 0; i < jids.size(); i++){
			if(i==pos1){
				jidSwapped.add(jid2);
			}
			else if(i==pos2){
				jidSwapped.add(jid1);
			} 
			else{
				jidSwapped.add(jids.get(i));
			}
		}
		//reassign the operations
		List<JobT> p2Clone = problem.assignOperations(jidSwapped); 

		Solution offspring = new Solution(parents[0].getNumberOfVariables(), parents[0].getNumberOfObjectives());
		for (int i = 0; i < offspring.getNumberOfVariables(); i++) {
			offspring.setVariable(i, p2Clone.get(i));
		}
		return new Solution[]{offspring};
	}

	@Override
	public int getArity() {
		return 1;
	}
}
