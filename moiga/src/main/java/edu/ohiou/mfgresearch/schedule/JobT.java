package edu.ohiou.mfgresearch.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.moeaframework.core.Variable;

public class JobT extends Job {

	private static final long serialVersionUID = -4412957802165045238L;
	//public final long jobID;
	public List<Operation> operations = new ArrayList<Operation>();
	//int[][] task;// list containing tuples of (machine ID,pT)
	// private double processingTime,dueDate;
	// private double completionTime;
	// private double readyTime;

	/**
	 * 
	 * @param ID        Job ID
	 * @param proc_time Processing time should be >= 0
	 * @param dueDate   Due date should be >= 0
	 */
	// public JobT(long ID, double dueDate, int task[][]) {
	// 	super(ID, 0, dueDate);
	// 	this.jobID = ID;
	// 	this.task = new int[task.length][2];
	// 	for (int i = 0; i < task.length; i++) {
	// 		this.task[i][0] = task[i][0];
	// 		this.task[i][1] = task[i][1];
	// 	}
	// }

	/**
	 *
	 * @param ID        Job ID
	 * @param proc_time Processing time should be >= 0
	 * @param dueDate   Due date should be >= 0
	 * @param readyTime Ready Time should be >= 0
	 */
	// public JobT(long ID, double dueDate, double readyTime, int task[][]) {
	// 	super(ID, 0, dueDate, readyTime);
	// 	this.jobID = ID;
	// 	this.task = new int[task.length][2];
	// 	for (int i = 0; i < task.length; i++) {
	// 		this.task[i][0] = task[i][0];
	// 		this.task[i][1] = task[i][1];
	// 		this.processingTime += task[i][1];
	// 	}

	// }

	/**
	 * @param ID        Job ID
	 * @param proc_time Processing time should be >= 0
	 * @param dueDate   Due date should be >= 0
	 * @param comp_time Completion Time
	 * @param readyTime Ready Time should be >= 0
	 */
	// JobT(long ID, double dueDate, double comp_time, double readyTime, int task[][]) {
	// 	super(ID, 0, 0, dueDate, readyTime);
	// 	this.jobID = ID;
	// 	this.readyTime = readyTime;
	// 	this.task = new int[task.length][2];
	// 	for (int i = 0; i < task.length; i++) {
	// 		this.task[i][0] = task[i][0];
	// 		this.task[i][1] = task[i][1];
	// 		this.processingTime += task[i][1];
	// 	}
	// 	this.completionTime = comp_time;
	// }

	public JobT(long ID){
		super(ID, 0, 0);
	}

	public void addOperation(Long opID, Long machID, double pT){
		operations.add(new Operation(opID, machID, pT));
	}

	/**
	 * @return number of operations in this job
	 */
	public int getTaskCount() {
		return this.operations.size();
	}

	/**
	 * Return the operation at the given position
	 * @param position
	 * @return Operation at the given position
	 */
	public Operation getOperation(Long opID){
		return operations.stream().filter(o->o.operationID == opID).findFirst().get();
	}

	/**
	 * Generates list of JobT instances for each operations in this job
	 * for using as a variable in the solution
	 * @return List of JobT instances with only one operation
	 */
	public List<JobT> generateJobOperations(){
		List<JobT> jops = new ArrayList<JobT>();
		for(Operation o: operations){
			JobT j = new JobT(jobID);
			j.addOperation(o.operationID, o.machineID, o.processingTime);
			jops.add(j);
		}
		return jops;
	}

	public JobT generateJobOperation(Long opID){
		JobT j = new JobT(jobID);
		Operation o = getOperation(opID);
		j.addOperation(o.operationID, o.machineID, o.processingTime);
		return j;
	}

	public String toString() {
		String s;
		s = "[J" + jobID + ": {" + 
			operations.stream().map(o->o.toString()).collect(Collectors.joining(",")).toString() + 
			"}, D:" + this.dueDate + 
			", cT:" + this.completionTime + "]";
		return s;
	}

	public boolean isEqualTo(JobT T) {
		JobT j = T;
		if (j.jobID == jobID)
			return true;
		else
			return false;
	}

	public boolean isEqualTo(long jobID) {
		if (this.jobID == jobID)
			return true;
		else
			return false;
	}

	@Override
	public Variable copy() {
		JobT j = new JobT(jobID);
		operations.stream().forEach(o->j.addOperation(o.getOperationID(), o.getMachineID(), o.getProcessingTime()));;
		j.setDueDate(dueDate);
		j.setreadyTime(readyTime);
		j.setCompletionTime(completionTime);
		return j;
	}

	@Override
	public void randomize() {
		// nothing to randomize! jobs are immutable
	}

	class Operation {
		
		private final long operationID;
		private final long machineID;
		private final double processingTime;

		public Operation(long operationID, long machineID, double processingTime) {
			this.operationID = operationID;
			this.machineID = machineID;
			this.processingTime = processingTime;
		}		

		@Override
		public String toString() {
			return "(" + machineID + ", " + processingTime + ")";
		}

		public long getOperationID() {
			return operationID;
		}

		public long getMachineID() {
			return machineID;
		}

		public double getProcessingTime() {
			return processingTime;
		}

		
	}
}
