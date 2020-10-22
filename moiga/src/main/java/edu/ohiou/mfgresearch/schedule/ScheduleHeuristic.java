package edu.ohiou.mfgresearch.schedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.moeaframework.core.Solution;

import edu.ohiou.mfgresearch.schedule.JobT.Operation;
import jmetal.core.Operator;

public class ScheduleHeuristic implements Iterator<JobT> {

	public enum Heuristic {
		SPT, // Shortest processing time
		FOR, // Fewest remaining number of operation
		EDD, // Earliest due date
		LDD, // Latest due date
		LRP, // Least remaining processing time
		LARP, // Least average remaining processing time
		LPT, // LPT – largest processing time
		MOR, // Most remaining number of operation
		MRP, // Most remaining processing time
		MARP, // Most average remaining processing time
		LST, //least slack time
		LARST, //least average remaining slack time
		HOLD // Do not process any job
	}

	List<JobT> jobList = new ArrayList<JobT>();
	List<JobT> archiveJobs;
	List<Heuristic> schemes = new ArrayList<Heuristic>();
	int counter = -1;

	public ScheduleHeuristic(List<JobT> jops) {
		jobList = jops;
	}

	/**
	 * Add heuristic scheme
	 * more than one scheme will be applied repeatedly
	 * @param h
	 */
	public void addScheme(Heuristic h){
		schemes.add(h);
	}

	@Override
	public boolean hasNext() {
		if(!jobList.isEmpty()){
			counter += 1; //increment scheme counter
			//backup original job list because jobs are going to be removed from jobList
			if(archiveJobs == null){
				archiveJobs = jobList.stream().map(j-> (JobT) j.copy()).collect(Collectors.toList());
			}
			return true;
		}
		else{
			counter = 0; //reset scheme counter
			//populate jobList from archive to reset this iterable
			jobList = archiveJobs.stream().map(j-> (JobT) j.copy()).collect(Collectors.toList());
			archiveJobs = null;
			return false;			
		}
	}

	@Override
	public JobT next() {
		//for hold heuristic do not schedule any job, skip
		if(schemes.get(counter%schemes.size()) == Heuristic.HOLD){
			return null;
		}
		jobList.sort(new Comparator<JobT>(){
			@Override
			public int compare(JobT o1, JobT o2) {

				Function<JobT, Double> func = null;
				switch (schemes.get(counter%schemes.size())) {
					case SPT:
						func = j->j.processingTime;
						break;
					case FOR:
						func = j->(double) j.getTaskCount();
						break;
					case EDD:
						func = j->(double) j.getDueDate();
						break;
					case LDD:
						func = j->(double) (-1) * j.getDueDate();
						break;
					case LRP:
						func = j->(double) j.getProcessingTime();
						break;
					case LARP:
						func = j->(double) j.getProcessingTime()/j.getTaskCount();
						break;
					case LPT:
						func = j->(double) -1 * j.processingTime;
						break;
					case MOR:
						func = j->(double) -1 * j.getTaskCount();
						break;
					case MRP:
						func = j->(double) -1 * j.getProcessingTime();
						break;
					case MARP:
						func = j->(double) -1 * j.getProcessingTime()/j.getTaskCount();
						break;
					case LST:
						func = j->(double) j.dueDate - j.processingTime;
						break;
					case LARST:
						func = j->(double) (j.dueDate - j.processingTime)/j.getTaskCount();
						break;
					default:
						break;
				}
	
				if (func.apply(o1) == func.apply(o2)) {
					return 0;
				} else if (func.apply(o1) < func.apply(o2)) {
					return -1;
				}
				return 1;
			}			
		});
		//get the first operation of the first job from the jobList 
		Operation o = jobList.get(0).getFirstOperation();
		//remove this operation
		jobList.get(0).getAllOperation().remove(0);
		Long jid = jobList.get(0).jobID;
		//if there is no more operation left in the job then remove from the list
		if(jobList.get(0).getProcessingTime()==0){
			jobList.remove(0);
		}
		//create a new job for the operation
		return new JobT(jid, o.getMachineID(), o.getProcessingTime());
	}
}
