package edu.ohiou.mfgresearch.schedule;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
	 * Schedule a solution by determining the completion time for each job
	 * 
	 * @param sched
	 */
	public void setCompletionTimes(Solution s) {
		// create the machine allocation
		// each entry in the map with key as machine ID contains the job allocations for
		// that machine
		Map<Long, List<JobT>> machAlloc = new HashMap<>();
		Map<Long, Double> lastCT = new HashMap<Long, Double>();
		// iterate over all Job Variables
		for (int i = 0; i < s.getNumberOfVariables(); i++) {
			// get the operation for this job
			JobT jop = (JobT) s.getVariable(i);
			Operation o = jop.operations.get(0);
			// create the machine if not already present or retirve it
			List<JobT> mal = new ArrayList<JobT>();
			if (!machAlloc.containsKey(o.getMachineID())) {
				machAlloc.put(o.getMachineID(), mal);
			} else {
				mal = machAlloc.get(o.getMachineID());
			}
			// set complettion time for the job operation
			if (mal.isEmpty()) {
				// if first job operation then completion time is equal to the processing time of the operation
				jop.setCompletionTime(o.getProcessingTime());
			} else {
				//calculate the starting time, it should be greater of last allocated job-operation in this machine or 
				//the completion time of the last allocated operation of this job (if there!)
				if(lastCT.containsKey(jop.jobID)){
					if (mal.get(mal.size() - 1).completionTime < lastCT.get(jop.jobID)){
						jop.setCompletionTime(lastCT.get(jop.jobID) + o.getProcessingTime());
					}
				}
				else{
					jop.setCompletionTime(mal.get(mal.size() - 1).completionTime + o.getProcessingTime());
				}
			}
			//update lastCT
			if(!lastCT.containsKey(jop.jobID)){
				lastCT.put(jop.jobID, jop.completionTime);
			}
			else{
				lastCT.replace(jop.jobID, jop.completionTime);
			}
			// scheule job at the machine
			mal.add(jop);
		}

		// now calculate the completion time
		// System.out.println("--Setting Ct---");
		// double machHorizon[] = new double[this.machineCount];
		// double lmt[] = new double[this.jobs.size()];
		// int tcnt[] = new int[this.jobs.size()];
		// for (Job i : sched) {
		// //get machine ID for the task
		// int machID = ((JobT) i).task[tcnt[(int) i.jobID]][0];
		// if (time[machID] < lmt[(int) i.jobID])
		// time[machID] = lmt[(int) i.jobID];
		// else if (time[machID] < i.getReadyTime())
		// time[machID] = i.getReadyTime();
		// time[machID] += ((JobT) i).task[tcnt[(int) i.jobID]++][1];
		// lmt[(int) i.jobID] = time[machID];
		// i.setCompletionTime(Math.max(time[machID], i.getCompletionTime()));
		// }
	}

	public void writeGantt(FileWriter fw, Solution s) {
		// create the machine allocation
		// each entry in the map with key as machine ID contains the job allocations for
		// that machine
		Map<Integer, List<JobT>> machAlloc = new HashMap<>();
		// iterate over all Job Variables
		for (int i = 0; i < s.getNumberOfVariables(); i++) {
			// get the operation for this job
			JobT jop = (JobT) s.getVariable(i).copy();
			Operation o = jop.operations.get(0);
			jop.setProcessingTime(o.getProcessingTime()); //set the processing time of the job as the processing time of the operation
			// create the machine if not already present or retirve it
			List<JobT> mal = new ArrayList<JobT>();
			if (!machAlloc.containsKey(Long.valueOf(o.getMachineID()).intValue())) {
				machAlloc.put(Long.valueOf(o.getMachineID()).intValue(), mal);
			} else {
				mal = machAlloc.get(Long.valueOf(o.getMachineID()).intValue());
			}
			// scheule job at the machine
			mal.add(jop);
		}

		// schedule over horizon
		int t = 0; // time counter
		boolean hasJob = true;
		// gantt chart
		Map<Integer, StringBuilder> gantt = new HashMap<Integer, StringBuilder>();
		Map<Integer, Integer> jobsProcessing = new HashMap<Integer, Integer>();
		for (int mix : machAlloc.keySet()) {
			// make a new String builder starting with machine ID
			gantt.put(mix, new StringBuilder().append("J").append(mix).append(","));
		}

		while (hasJob) {
			t++; // increment time period by one
			hasJob = false; // if there is no job found for any machine ID then turn it false
			for (int mix : machAlloc.keySet()) {
				// if(busy.get(mix)) continue; //if the machine is busy then skip
				if (!machAlloc.get(mix).isEmpty()) {
					JobT jop = machAlloc.get(mix).get(0);
					hasJob = true; // has job to be scheduled
					//if the job is already being processed in some other machine simply skip
					if(jobsProcessing.containsKey(Long.valueOf(jop.jobID).intValue())){
						if(jobsProcessing.get(Long.valueOf(jop.jobID).intValue()) != mix) {
							gantt.get(mix).append(" ,");
							continue;
						}
					}				
					// schedule the job
					gantt.get(mix).append(jop.jobID) //.append(":").append(job.getOperation(0).getOperationID())
								  .append(",");	
					//add in job-processing
					if(!jobsProcessing.containsKey(Long.valueOf(jop.jobID).intValue())){
						 jobsProcessing.put(Long.valueOf(jop.jobID).intValue(), Long.valueOf(jop.operations.get(0).getMachineID()).intValue());
					}
					// decrease processing time by 1
					jop.setProcessingTime(jop.processingTime - 1);
					// delete the job if processing time (remaining) is 0
					if (jop.processingTime == 0){
						machAlloc.get(mix).remove(jop);
						jobsProcessing.remove(Long.valueOf(jop.jobID).intValue());
					}
				}
			}
		}

		// write
		gantt.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> {
			try {
				fw.append(x.getValue());
			} catch (IOException e) {
				e.printStackTrace();
			}
			 });

	}

	@Override
	public void evaluate(Solution solution) {	
		//first calculate the completion time for each job-operation in the solution
		setCompletionTimes(solution);
		//update global job completion time (first clear all completion times)
		jobs.forEach(j->j.setCompletionTime(0.0));
		for(int i=0; i<solution.getNumberOfVariables(); i++){
			//get the operation for this job
			JobT jop = (JobT) solution.getVariable(i);
			//get the job from job list
			JobT job = jobs.stream().filter(j->j.getJobID()==jop.getJobID()).findFirst().get();
			if(job.getCompletionTime()<=jop.completionTime){
				job.setCompletionTime(jop.completionTime);
			}
		}
		//convert List<JobT> to List<Job>
		List<Job> jlist = jobs.stream().map(ii -> (Job) ii).collect(Collectors.toList());		
		for (int i = 0; i < measures.size(); i++) {
			solution.setObjective(i, measures.get(i).evaluate(jlist));
		}
	}

	@Override
	public Solution newSolution() {
		Solution ob = new Solution(this.getNumberOfVariables(), this.getNumberOfObjectives());
		//get all job and operation keys
		List<JobT> jops = new LinkedList<JobT>();
		for(JobT j: jobs){
			 jops.addAll(j.generateJobOperations());
		}
		PRNG.shuffle(jops);
		int ix = 0;
		for(JobT jox: jops){
			ob.setVariable(ix, jox);
			ix += 1;
		}
		return ob;
	}

	public void close() {
		isClosed = true;
	}
}
