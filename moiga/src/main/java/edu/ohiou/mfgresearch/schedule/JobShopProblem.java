package edu.ohiou.mfgresearch.schedule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;

public class JobShopProblem implements Problem
{
	private boolean isClosed;
	int machineCount;
	List<JobT> jobs=new ArrayList<JobT>();
	List<PerformanceMeasures> measures = new LinkedList<PerformanceMeasures>();
	/**
	 * @param m a Machines object
	 * @param jobs List of jobs to be scheduled.
	 * @param measures List of performance measures to be used for evaluation
	 * @throws Exception 
	 * */
	public JobShopProblem(int m,List<JobT> jobs,List<PerformanceMeasures> measures) throws Exception
	{
		this.machineCount=m;
		this.jobs.addAll(jobs);
		
		if(measures.size()==0)
		{
			throw new Exception("No objectives have been provided");
		}
		this.measures.addAll(measures);
		this.isClosed=false;
		
	}
	public String getName() {
		return "JobShopProblem";
	}

	@Override
	public int getNumberOfVariables()
	{
		return jobs.stream().mapToInt(i->i.task.length).sum();
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
	public List<JobT> getJobs()
	{
		List<JobT> temp=new ArrayList<JobT>();
		for(JobT i:jobs)
			temp.add((JobT)i.copy());
		return temp;
	}
	public void setCompletionTimes(List<JobT> sched)
	{
//		System.out.println("--Setting Ct---");
		double time[]=new double[this.machineCount];
		double lmt[]=new double[this.jobs.size()];
		int tcnt[]=new int[this.jobs.size()];
		for(Job i:sched)
		{
			int mid=((JobT)i).task[tcnt[(int) i.jobID]][0];
			if(time[mid]<lmt[(int) i.jobID])
				time[mid]=lmt[(int) i.jobID];
			else if(time[mid]<i.getReadyTime())
				time[mid]=i.getReadyTime();
			time[mid]+=((JobT)i).task[tcnt[(int) i.jobID]++][1];
			lmt[(int) i.jobID]=time[mid];
				
			i.setCompletionTime(Math.max(time[mid],i.getCompletionTime()));
		}
	}

	@Override
	public void evaluate(Solution solution)
	{
		List<JobT> jlist=new ArrayList<JobT>();
		for(int i=0;i<solution.getNumberOfVariables();i++)
		{
			jlist.add((JobT)solution.getVariable(i));
		}
		setCompletionTimes(jlist);
//		System.out.println("--->"+jlist);
		jlist.clear();
		boolean cnt[]=new boolean[this.jobs.size()];
		for(int i=0;i<solution.getNumberOfVariables();i++)
		{
			JobT temp=(JobT)solution.getVariable(i);
			if(!cnt[(int) temp.jobID])
			{
				jlist.add(temp);
				cnt[(int) temp.jobID]=true;
			}
		}
		List<Job> temp=jlist.stream().map(ii->(Job)ii).collect(Collectors.toList());
		for(int i=0;i<measures.size();i++)
		{
			solution.setObjective(i, measures.get(i).evaluate(temp));
		}
	}

	@Override
	public Solution newSolution()
	{
		Solution ob=new Solution(this.getNumberOfVariables(),this.getNumberOfObjectives());
		List<Variable> jlist=this.jobs.stream().map(i->i.copy()).collect(Collectors.toList());
		
		int []cnt=new int[jobs.size()];
		for(int i=0;i<getNumberOfVariables();i++)
		{
			int ind;
			do{
				ind=PRNG.nextInt(cnt.length);
			}while(cnt[ind]>=jobs.get(ind).getTaskCount());
			ob.setVariable(i,jlist.get(ind));
			cnt[ind]++;
		}
		return ob;
	}

	public void close() {
		isClosed = true;
	}
}
