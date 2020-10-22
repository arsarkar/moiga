package edu.ohiou.mfgresearch.schedule;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

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
		return jobs.stream().map(j->j.getTaskCount()).reduce(0, (a,b)->a+b);
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

	public void setPerformanceMeasures(List<PerformanceMeasures> measures){
		this.measures = measures;
	}

	/**
	 * Schedule a solution by determining the completion time for each job for each
	 * job in sequence rule 1:
	 * 
	 * @param sched
	 * @return
	 */
	public Map<Long, JobT> setCompletionTimes(List<JobT> s) {

		Map<Long, Integer> gantt = new HashMap<Long, Integer>();
		Map<Long, JobT> lastAlloctedJoPCT = new HashMap<Long, JobT>();

		//initialize gantt
		for(Long mix:machines){
			gantt.put(mix, 0);
		}

		for(int i=0; i < s.size(); i++){
			//get the job-operation
			JobT jop = (JobT) s.get(i);
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
		List<JobT> jops = IntStream.range(0, solution.getNumberOfVariables())
								   .mapToObj(i->(JobT) solution.getVariable(i))
								   .collect(Collectors.toList());
		//first calculate the completion time for each job-operation in the solution
		Map<Long, JobT> jobCT = setCompletionTimes(jops);
		//convert List<JobT> to List<Job>
		List<Job> jlist = jobs.stream().map(ii -> (Job) ii).collect(Collectors.toList());
		//update completion time to joblist
		for(Job job: jlist){
			job.setCompletionTime(jobCT.get(job.jobID).completionTime);
			// System.out.println("CT->"+job.toString());
		}	
		//update solution objective	
		for (int i = 0; i < measures.size(); i++) {
			solution.setObjective(i, measures.get(i).evaluate(jlist));
		}
	}

	@Override
	public Solution newSolution() {
		Solution ob = new Solution(this.getNumberOfVariables(), this.getNumberOfObjectives());

		//add each jobID for as many operations it has
		List<Long> jixs = new LinkedList<Long>();
		for(JobT j: jobs){
			 for(Operation o : j.getAllOperation()){
				 jixs.add(j.jobID);
			 }
		}
		//shuffle the list
		PRNG.shuffle(jixs);

		//assign the operations to this job
		List<JobT> jops = assignOperations(jixs);

		int ix = 0;
		for(JobT jop: jops){
			ob.setVariable(ix, jop);
			ix += 1;
		}
		return ob;
	}

	/**
	 * Assign operations to a candidate (permutation based representation)
	 * @param List of jobID
	 * @return
	 */
	public List<JobT> assignOperations(List<Long> jids) {
		Map<Long, Integer> lastOperationforJob = new HashMap<Long, Integer>();
		List<JobT> jops = new ArrayList<JobT>();
		for(Long jid:jids){
			//get the job from global
			JobT job = getJob(jid);
			//create new job
			JobT nJob = new JobT(jid);
			if(lastOperationforJob.containsKey(jid)){
				//add the next operation of the last operation added
				nJob.addOperation(job.getOperation(lastOperationforJob.get(jid)+1).getMachineID(), 
								  job.getOperation(lastOperationforJob.get(jid)+1).getProcessingTime());				  
				lastOperationforJob.replace(jid, lastOperationforJob.get(jid)+1); //increment the operation index
			}
			else{
				//add the first operation
				nJob.addOperation(job.getOperation(0).getMachineID(), 
								  job.getOperation(0).getProcessingTime());
				lastOperationforJob.put(jid, 0); 
			}
			jops.add(nJob);
		}
		return jops;
	}

	/**
	 * Write solution in format (J1-M2), (J2-M1), ...
	 * @param s
	 * @return
	 */
	public String writeSolution(Solution s){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i < s.getNumberOfVariables(); i++){
			JobT j = (JobT) s.getVariable(i);
			sb.append("(J")
			  .append(j.jobID)
			  .append("-M")
			  .append(j.getFirstOperation().getMachineID())
			  .append("), ");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	public void close() {
		isClosed = true;
	}
}
