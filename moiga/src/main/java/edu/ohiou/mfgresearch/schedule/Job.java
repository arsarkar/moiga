package edu.ohiou.mfgresearch.schedule;

import org.moeaframework.core.Variable;

public class Job implements Variable
{
	private static final long serialVersionUID = -4416607294906423992L;
	public final long jobID;
	public final int machineID;
	protected double processingTime;
	 protected double dueDate;
	protected double completionTime;
	 protected double readyTime;
	 /**
	  * 
	  * @param ID Job ID
	  * @param proc_time Processing time should be >= 0
	  * @param dueDate Due date should be >= 0
	  */
	 public Job(long ID,double proc_time,double dueDate)
	 {
		 this.jobID=ID;
		 this.setProcessingTime(proc_time);
		 this.setDueDate(dueDate);
		 this.machineID=0;
	 }
	 /**
	  *
	  * @param ID Job ID
	  * @param proc_time Processing time should be >= 0
	  * @param dueDate Due date should be >= 0
	  * @param readyTime Ready Time should be >= 0
	  */
	 public Job(long ID,double proc_time,double dueDate,double readyTime)
	 {
		 this.jobID=ID;
		 this.setProcessingTime(proc_time);
		 this.setDueDate(dueDate);
		 this.readyTime=readyTime;
		 this.machineID=0;
	 }
	 public Job(long ID,int mID,double proc_time,double dueDate,double readyTime)
	 {
		 this.jobID=ID;
		 this.setProcessingTime(proc_time);
		 this.setDueDate(dueDate);
		 this.readyTime=readyTime;
		 this.machineID=mID;
	 }
	 
	 /**
	  * @param ID Job ID
	  * @param proc_time Processing time should be >= 0
	  * @param dueDate Due date should be >= 0
	  * @param comp_time Completion Time
	  * @param readyTime Ready Time should be >= 0
	  */
	 Job(long ID,double proc_time,double dueDate,double comp_time,double readyTime)
	 {
		 this.jobID=ID;
		 this.setProcessingTime(proc_time);
		 this.setDueDate(dueDate);
		 this.setCompletionTime(comp_time);
		 this.readyTime=readyTime;
		 this.machineID=0;
	 }
	 Job(long ID,int mID,double proc_time,double dueDate,double comp_time,double readyTime)
	 {
		 this.jobID=ID;
		 this.setProcessingTime(proc_time);
		 this.setDueDate(dueDate);
		 this.setCompletionTime(comp_time);
		 this.readyTime=readyTime;
		 this.machineID=mID;
	 }
	public double getProcessingTime() {
		return processingTime;
	}
	public double getReadyTime() {
		return readyTime;
	}
	public void setProcessingTime(double processingTime) {
		this.processingTime = processingTime;
	}
	public double getDueDate() {
		return dueDate;
	}
	public void setDueDate(double dueDate) {
		this.dueDate = dueDate;
	}
	public double getCompletionTime() {
		return completionTime;
	}
	public void setreadyTime(double readyTime) {
		this.readyTime = readyTime;
	}
	public void setCompletionTime(double completionTime) {
		this.completionTime = completionTime;
	}
	double getTardyTime()
	{
		if(completionTime<dueDate)
			return 0.0;
		return Math.abs(completionTime-dueDate);
	}
	double getLateTime()
	{
		if(completionTime<dueDate)
			return 0.0;
		return completionTime-dueDate;
					
	}
	double getWaitTime()
	{
		return completionTime - processingTime- readyTime;
	}
	double getFlowTime()
	{
		return completionTime - readyTime;
	}
	public String toString(){
		String s;
		s = jobID+"\t"+machineID+"\t"+this.processingTime+"\t"+this.readyTime+"\t"+this.dueDate+"\t"+this.completionTime+"\n";
		return s;
	}
	public boolean isEqualTo(Job T)
	{
		Job j = T;
		if (j.jobID==jobID)
			return true;
		else
			return false;
	}
	public boolean isEqualTo(long jobID)
	{
		if (this.jobID==jobID)
			return true;
		else
			return false;
	}
	@Override
	public Variable copy() {
		return new Job(jobID,this.machineID,processingTime, dueDate, completionTime,readyTime);
	}
	@Override
	public void randomize() {
		//nothing to randomize! jobs are immutable
	}
	
}
