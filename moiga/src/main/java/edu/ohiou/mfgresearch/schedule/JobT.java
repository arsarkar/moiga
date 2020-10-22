package edu.ohiou.mfgresearch.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.moeaframework.core.Variable;

public class JobT extends Job {

	private static final long serialVersionUID = -4412957802165045238L;
	// public final long jobID;
	public List<Operation> operations = new ArrayList<Operation>();
	public Integer operationCount = 0;

	public JobT(long ID) {
		super(ID, 0, 0);
	}

	public JobT(long ID, Long machID, double pT) {
		this(ID);
		addOperation(machID, pT);
		processingTime = pT;
		operationCount = 1;
	}

	/**
	 * Add an operation to this job
	 * 
	 * @param machID
	 * @param pT
	 */
	public void addOperation(Long machID, double pT) {
		operations.add(new Operation(operations.size(), machID, pT));
		processingTime += pT;
		operationCount += 1;
	}

	/**
	 * @return number of operations in this job
	 */
	public int getTaskCount() {
		return this.operations.size();
	}

	/**
	 * Return the operation at the given position
	 * 
	 * @param position
	 * @return Operation at the given position
	 */
	public Operation getOperation(Integer opID) {
		return operations.get(opID);
	}

	/**
	 * Get the only operation (required for job-operation)
	 * 
	 * @return
	 */
	public Operation getFirstOperation() {
		return operations.get(0);
	}

	public List<Operation> getAllOperation() {
		return operations;
	}

	public String toString() {
		String s;
		s = "[J" + jobID + ": {"
				+ operations.stream().map(o -> o.toString()).collect(Collectors.joining(",")).toString() + "}, D:"
				+ this.dueDate + ", cT:" + this.completionTime + "]";
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
		operations.stream().forEach(o -> j.addOperation(o.getMachineID(), o.getProcessingTime()));
		;
		j.setDueDate(dueDate);
		j.setreadyTime(readyTime);
		j.setCompletionTime(completionTime);
		return j;
	}

	@Override
	public void randomize() {
		// nothing to randomize! jobs are immutable
	}

	@Override
	public double getProcessingTime() {
		return operations.stream().mapToDouble(o->o.getProcessingTime()).reduce(0, (a,b)->a+b);
	}

	public class Operation {

		private final Integer operationID;
		private final long machineID;
		private final double processingTime;

		public Operation(Integer operationID, long machineID, double processingTime) {
			this.operationID = operationID;
			this.machineID = machineID;
			this.processingTime = processingTime;
		}

		@Override
		public String toString() {
			return "(" + machineID + ", " + processingTime + ")";
		}

		public Integer getOperationID() {
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
