package edu.ohiou.mfgresearch.schedule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public class ScheduleProblem implements Problem
{
	private boolean isClosed;
	List<Job> jobs = new LinkedList<Job>();
	List<PerformanceMeasures> measures = new LinkedList<PerformanceMeasures>();
	/**
	 * @param jobs List of jobs to be scheduled
	 * @param measures List of performance measures to be used for evaluation
	 * @throws Exception 
	 * */
	public ScheduleProblem(List<Job> jobs,List<PerformanceMeasures> measures) throws Exception
	{
		this.jobs.addAll(jobs);
		if(measures.size()==0)
		{
			throw new Exception("No objectives have been provided");
		}
		this.measures.addAll(measures);
		this.isClosed=false;
	}
	public String getName() {
		return "ScheduleProblem";
	}

	@Override
	public int getNumberOfVariables() {
		return jobs.size();
	}

	@Override
	public int getNumberOfObjectives() {
		return measures.size();
	}

	@Override
	public int getNumberOfConstraints() {
		return 0;
	}
	public void modifyJob(long ID,Consumer<Job> con) throws Exception
	{
		Job ob=null;
		try {
				ob=getJob(ID);
		}
		catch(IndexOutOfBoundsException e)
		{
			throw new Exception("Invalid Job ID");
		}
		con.accept(ob);
		
	}
	Job getJob(long ID)
	{
		return jobs.stream().filter(ob->ob.isEqualTo(ID)).collect(Collectors.toList()).get(0);
	}
	public List<Job> getJobs()
	{
		List<Job> temp=new ArrayList<Job>();
		for(Job i:jobs)
			temp.add((Job)i.copy());
		return temp;
	}
	public static void setCompletionTimes(List<Job> jobs)
	{
		double time=0.0;
		for(int i=0;i<jobs.size();i++)
		{
			if(time<jobs.get(i).getReadyTime())
				time=jobs.get(i).getReadyTime();
				time+=jobs.get(i).getProcessingTime();
			jobs.get(i).setCompletionTime(time);
		}
	}
	@Override
	public void evaluate(Solution solution)
	{
		List<Job> job=new ArrayList<Job>();
		for(int i=0;i<solution.getNumberOfVariables();i++)
		{
			long ID=(long)((Job)solution.getVariable(i)).jobID;
			//job.add(getJob(ID));
			job.add((Job)solution.getVariable(i));
		}
		setCompletionTimes(job);
		for(int i=0;i<measures.size();i++)
		{
			solution.setObjective(i, measures.get(i).evaluate(job));
		}
	}

	@Override
	public Solution newSolution() {
		
		Solution ob=new Solution(this.getNumberOfVariables(),this.getNumberOfObjectives());
		boolean []ID=new boolean[jobs.size()];
		for(int i=0;i<getNumberOfVariables();i++)
		{
			int ind;
			do{
				ind=PRNG.nextInt(ID.length);
			}while(ID[ind]);
			ob.setVariable(i,jobs.get(ind).copy());
			ID[ind]=true;
		}
		
		return ob;
	}

	public void close() {
		isClosed = true;
	}
}
