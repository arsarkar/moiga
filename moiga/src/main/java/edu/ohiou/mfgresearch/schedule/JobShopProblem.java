package edu.ohiou.mfgresearch.schedule;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.print.attribute.standard.JobPriority;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;

import edu.ohiou.mfgresearch.schedule.JobT.Operation;

public class JobShopProblem implements Problem {
	private boolean isClosed;
	List<JobT> jobs;
	List<Long> machines = new LinkedList<Long>();
	List<PerformanceMeasures> measures = new LinkedList<PerformanceMeasures>();

	// Map<Long, Long[]> operationPool = new HashMap<Long, Long[]>();
	/**
	 * @param m        a Machines object
	 * @param jobs     List of jobs to be scheduled.
	 * @param measures List of performance measures to be used for evaluation
	 * @throws Exception
	 */
	public JobShopProblem(List<JobT> jobs, List<PerformanceMeasures> measures) throws Exception {
		// this.machineCount = m;
		this.jobs = jobs;
		if (measures.size() == 0) {
			throw new Exception("No objectives have been provided");
		} else {
			this.measures.addAll(measures);
		}
		this.isClosed = false;
		// create pool of operations
		// Long ix = 1L;
		for (JobT j : jobs) {
			for (Operation o : j.operations) {
				if (!machines.contains(o.getMachineID()))
					machines.add(o.getMachineID());
				// operationPool.put(ix, new Long[]{j.getJobID(), o.getOperationID()});
				// ix += 1; //increment key
			}
		}
		System.out.println(
				"Job shop problem created with " + jobs.size() + " jobs and " + machines.size() + " machines.");
	}

	public String getName() {
		return "JobShopProblem";
	}

	@Override
	public int getNumberOfVariables() {
		return jobs.size() * machines.size();
	}

	@Override
	public int getNumberOfObjectives() {
		return measures.size();
	}

	@Override
	public int getNumberOfConstraints() {
		return 0;
	}

	public int getNumberOfJobs() {
		return this.jobs.size();
	}

	public List<JobT> getJobs() {
		return jobs;
	}

	public JobT getJob(Long jobID){
		return jobs.stream().filter(j->j.jobID == jobID).findFirst().get();
	}

	/**
	 * Schedule a solution by determining the completion time for each job for each
	 * job in sequence rule 1:
	 * 
	 * @param sched
	 * @return
	 */
	public Map<Long, JobT> setCompletionTimes(Solution s) {

		Map<Long, Integer> gantt = new HashMap<Long, Integer>();
		Map<Long, JobT> lastAlloctedJoPCT = new HashMap<Long, JobT>();

		//initialize gantt
		for(Long mix:machines){
			gantt.put(mix, 0);
		}

		for(int i=0; i < s.getNumberOfVariables(); i++){
			//get the job-operation
			JobT jop = (JobT) s.getVariable(i);
			//get the machine to be allocated
			Long machID = jop.getFirstOperation().getMachineID();
			//get the completion time of last allocated job
			Integer lastCT = 0;
			if(lastAlloctedJoPCT.containsKey(jop.jobID)){
				lastCT = (int) lastAlloctedJoPCT.get(jop.jobID).getCompletionTime();
				lastAlloctedJoPCT.replace(jop.jobID, jop);
			}
			else{
				lastAlloctedJoPCT.put(jop.jobID, jop);
			}
			//calculate the starting time of the job
			int startT = Math.max(lastCT, gantt.get(machID));
			int ct = startT + (int) jop.getFirstOperation().getProcessingTime();
			//update the completion time for the machine
			gantt.replace(machID, ct);
			//update the completion time
			lastAlloctedJoPCT.get(jop.jobID).setCompletionTime(startT + jop.getFirstOperation().getProcessingTime());
		}
		return lastAlloctedJoPCT;
	}

	/**
	 * Write gantt at the given CSV writer.
	 * Solution doesn't need to be evaluated.
	 * @param fw
	 * @param s
	 */
	public void writeGantt(FileWriter fw, Solution s) {
		
		Map<Long, List<Integer>> gantt = new HashMap<Long, List<Integer>>();
		Map<Long, JobT> lastAlloctedJoP = new HashMap<Long, JobT>();

		//initialize gantt
		for(Long mix:machines){
			gantt.put(mix, new ArrayList<Integer>());
		}

		for(int i=0; i < s.getNumberOfVariables(); i++){
			//get the job-operation
			JobT jop = (JobT) s.getVariable(i);
			//get the machine to be allocated
			Long machID = jop.getFirstOperation().getMachineID();
			//get the completion time of last allocated job
			Integer lastCT = 0;
			if(lastAlloctedJoP.containsKey(jop.jobID)){
				lastCT = (int) lastAlloctedJoP.get(jop.jobID).getCompletionTime();
				lastAlloctedJoP.replace(jop.jobID, jop);
			}
			else{
				lastAlloctedJoP.put(jop.jobID, jop);
			}
			//calculate the starting time of the job
			int startT = Math.max(lastCT, gantt.get(machID).size()-1);
			while(gantt.get(machID).size()<=startT){
				gantt.get(machID).add(0);
			}
			//fill up the list 
			for(int j=0; j<jop.getFirstOperation().getProcessingTime(); j++){
				gantt.get(machID).add(Long.valueOf(jop.jobID).intValue());
			}
			//update the completion time
			lastAlloctedJoP.get(jop.jobID).setCompletionTime(startT + jop.getFirstOperation().getProcessingTime());
		}

		//write
		int t = 0;
		//get the longest machince allocation as the limit in the horizon
		for(Long k: gantt.keySet()){
			if(gantt.get(k).size() > t) t = gantt.get(k).size();
		}
		for(int i=0; i< t; i++){
			try {
				if(i==0){
					fw.append("t->,");
				}
				else{
					fw.append(String.valueOf(i)).append(",");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fw.append("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(Long mix:machines){
			List<Integer> maloc = gantt.get(mix);
			for(int i=0; i< t; i++){
				try {
					if(i==0){
						fw.append("M").append(mix.toString()).append(",");
					}
					else{
						if(maloc.size()>i){
							fw.append(String.valueOf(maloc.get(i))).append(",");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				fw.append("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	 }

	@Override
	public void evaluate(Solution solution) {	
		//first calculate the completion time for each job-operation in the solution
		Map<Long, JobT> jobCT = setCompletionTimes(solution);
		//convert List<JobT> to List<Job>
		List<Job> jlist = jobs.stream().map(ii -> (Job) ii).collect(Collectors.toList());
		//update completion time to joblist
		for(Job job: jlist){
			job.setCompletionTime(jobCT.get(job.jobID).completionTime);
			System.out.println("CT->"+job.toString());
		}	
		//update solution objective	
		for (int i = 0; i < measures.size(); i++) {
			solution.setObjective(i, measures.get(i).evaluate(jlist));
		}
	}

	@Override
	public Solution newSolution() {
		Solution ob = new Solution(this.getNumberOfVariables(), this.getNumberOfObjectives());
		List<JobT> jopBucket = new LinkedList<JobT>();
		Map<Long, Long> opSeqed = new HashMap<Long, Long>(); 	
		//add initial job operations to bucket
		jobs.forEach(j -> {
			jopBucket.add(j.generateJobOperation(1L));
			opSeqed.put(j.jobID, 1L);	
		});
		int ix = -1;
		while(ix < getNumberOfVariables()-1){
			//first put available job-operations in bucket
			for(JobT job: jobs){
				if(!jopBucket.stream().anyMatch(j->j.jobID == job.jobID)){
					if(opSeqed.get(job.jobID) < job.getTaskCount()){
						jopBucket.add(job.generateJobOperation(opSeqed.get(job.jobID) + 1));
						opSeqed.replace(job.jobID, opSeqed.get(job.jobID)+1);
					}
				}
			}
			//shuffle the bucket and pick one
			PRNG.shuffle(jopBucket);
			ob.setVariable(++ix, jopBucket.get(0));
			jopBucket.remove(0);
		}

		//get all job and operation keys
		// List<JobT> jops = new LinkedList<JobT>();
		// for(JobT j: jobs){
		// 	 jops.addAll(j.generateJobOperations());
		// }
		// PRNG.shuffle(jops);
		// int ix = 0;
		// for(JobT jox: jops){
		// 	ob.setVariable(ix, jox);
		// 	ix += 1;
		// }
		return ob;
	}

	public void close() {
		isClosed = true;
	}
}
