package edu.ohiou.mfgresearch.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.JobT;

/**
 * Generalized Order Crossover
 * assemble one offspring from two parental chromosomes
 * (donator and receiver). In both techniques a substring
 * is chosen from the donating chromosome. Then all genes 
 * of the substring are deleted with respect to their index 
 * of occurrence in the receiving chromosome.
 * GOX implants the substring into the receiver at the 
 * position where the first gene of the substring has occured 
 * (before deletion) in the receiver.
 */
public class GOXCrossover implements Variation {
	JobShopProblem problem;

	public GOXCrossover(JobShopProblem prob) {
		this.problem = prob;
	}

	@Override
	public int getArity() {
		return 2;
	}

	// To Be Fixed
	public Solution[] evolve(Solution[] parents) {

		Solution o1 = this.getOffspring(parents[0], parents[1]);
		Solution o2 = this.getOffspring(parents[1], parents[0]);
		return new Solution[] { o1, o2 };
	}

	Solution getOffspring(Solution p1, Solution p2) {
		int size = p1.getNumberOfVariables();

		// 1/3*size <= cut length <= 1/2*size
		int cutLen = (int) Math.abs(0.33 * size + Math.random() * 0.17 * size);
		//start position of substring (should be consecutive)
		int ind = (int) (Math.random() * (size-cutLen));
		// System.out.println(ind+"----"+cutLen);

		List<JobT> subString = new ArrayList<JobT>();
		//get the substring s1 from donating parent (p1)
		for (int k = ind; k < ind + cutLen; k++) {
			subString.add((JobT) p1.getVariable(k));
		}
		//System.out.println("P1sub -> " + subString.stream().map(j->String.valueOf(j.jobID)).collect(Collectors.joining(",")));
		//detect the position for insertion in receiving parent (p2)
		//that is the position where the first gene of the substring has occured
		//first get the reciving solution in a list
		List<JobT> p2List = IntStream.range(0, size)
									 .mapToObj(i->(JobT) p2.getVariable(i))
									 .collect(Collectors.toList());

		int insertPosition = 0; 
		//rememeber the insert position for unremoved receiving solution
		for(int k=0; k<p2List.size(); k++){
			if(p2List.get(k).jobID == subString.get(0).jobID &&
				p2List.get(k).getFirstOperation().getMachineID() == subString.get(0).getFirstOperation().getMachineID()){
					insertPosition = k;
					break;
				}
		}
		
		//flag all genes to be removed from receiving parent (p2) which is in the subString
		List<Integer> delPos = new ArrayList<Integer>();
		for(int j=0; j<subString.size(); j++){
			for(int k=0; k<p2List.size(); k++){
				if(p2List.get(k).jobID == subString.get(j).jobID &&
					p2List.get(k).getFirstOperation().getMachineID() == subString.get(j).getFirstOperation().getMachineID()){
					delPos.add(k);
					break;
				}
			}
		}

		//make a new solution from the receiving gene by inserting the substring at insert position
		//and deleting all genes from the delpos
		List<JobT> p2Clone = new ArrayList<JobT>();
		for(int k=0; k<p2List.size(); k++){
			//if k is in delPos list then don't add
			if(!delPos.contains(k) || k == insertPosition){
				//if this is the insert position then add the entire substring
				if(k == insertPosition){
					p2Clone.addAll(subString);
				}
				else{
					p2Clone.add(p2List.get(k));
				}
			}
		}

		//reassign the operations
		List<Long> jids = p2Clone.stream().map(j->j.jobID).collect(Collectors.toList());
		p2Clone = problem.assignOperations(jids); 

		Solution offspring = new Solution(p1.getNumberOfVariables(), p1.getNumberOfObjectives());
		for (int i = 0; i < offspring.getNumberOfVariables(); i++) {
			offspring.setVariable(i, p2Clone.get(i));
		}
		return offspring;
	}
}
