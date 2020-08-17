package edu.ohiou.mfgresearch.schedule;

import java.util.Arrays;

import org.moeaframework.core.Variable;

public class JobT extends Job  
{

	private static final long serialVersionUID = -4412957802165045238L;
	public final long jobID;
	int[][] task;//list containing tuples of (machine ID,pT)	 
//	 private double processingTime,dueDate;
//	 private double completionTime;
//	 private double readyTime;
	 /**
	  * 
	  * @param ID Job ID
	  * @param proc_time Processing time should be >= 0
	  * @param dueDate Due date should be >= 0
	  */
	 public JobT(long ID,double dueDate,int task[][])
	 {
		 super(ID,0, dueDate);
		 this.jobID=ID;
		 this.task=new int[task.length][2];
		 for(int i=0;i<task.length;i++)
		 {
			 this.task[i][0]=task[i][0];
			 this.task[i][1]=task[i][1];
		 }
	 }
	 /**
	  *
	  * @param ID Job ID
	  * @param proc_time Processing time should be >= 0
	  * @param dueDate Due date should be >= 0
	  * @param readyTime Ready Time should be >= 0
	  */
	 public JobT(long ID,double dueDate,double readyTime,int task[][])
	 {
		 super(ID,0,dueDate,readyTime);
		 this.jobID=ID;
		 this.task=new int[task.length][2];
		 for(int i=0;i<task.length;i++)
		 {
			 this.task[i][0]=task[i][0];
			 this.task[i][1]=task[i][1];
			 this.processingTime+=task[i][1];
		 }
			 
	 }
	 
	 /**
	  * @param ID Job ID
	  * @param proc_time Processing time should be >= 0
	  * @param dueDate Due date should be >= 0
	  * @param comp_time Completion Time
	  * @param readyTime Ready Time should be >= 0
	  */
	 JobT(long ID,double dueDate,double comp_time,double readyTime,int task[][])
	 {
		 super(ID,0,0,dueDate,readyTime);
		 this.jobID=ID;
		 this.readyTime=readyTime;
		 this.task=new int[task.length][2];
		 for(int i=0;i<task.length;i++)
		 {
			 this.task[i][0]=task[i][0];
			 this.task[i][1]=task[i][1];
			 this.processingTime+=task[i][1];
		 }
		 this.completionTime=comp_time;
	 }
	 public int getTaskCount()
	 {
			return this.task.length;
	 }
	public String toString(){
		String s;
		s = "<JobID : "+jobID+"\t Task : "+Arrays.deepToString(task)+"\t Pt : "+this.processingTime+
				"\tRt : "+this.readyTime+"\t DD : "+this.dueDate+"\t Ct : "+this.completionTime+">";
		return s;
	}
	public boolean isEqualTo(JobT T)
	{
		JobT j = T;
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
	public Variable copy()
	{
		return new JobT(jobID, dueDate, completionTime, readyTime, task);
		
	}
	@Override
	public void randomize() {
		//nothing to randomize! jobs are immutable
	}
	
}
