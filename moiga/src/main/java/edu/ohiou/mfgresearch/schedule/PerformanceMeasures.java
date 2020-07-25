package edu.ohiou.mfgresearch.schedule;

import java.util.List;

public enum PerformanceMeasures
{
    AVERAGE_COMPLETION_TIME(1)
    {
    	public double evaluate(List<Job> jobs)
    	{
            double avgCompletionTime = 0.00;
            for (int i = 0; i < jobs.size(); i++)
            {
                avgCompletionTime = avgCompletionTime + jobs.get(i).getCompletionTime();
            }
            return (avgCompletionTime / jobs.size());
    	}
    },
    AVERAGE_FLOW_TIME(2)
    {
    	public double evaluate(List<Job> jobs)
    	{
            double avgflowtime = 0.00;
            for (int i = 0; i < jobs.size(); i++)
            {
                avgflowtime = avgflowtime + jobs.get(i).getFlowTime();
            }
            return (avgflowtime / jobs.size());
    	}
    },
    AVERAGE_WAITING_TIME(3)
    {
    	public double evaluate(List<Job> jobs)
    	{
            double avgwaitingtime = 0.00;
            for (int i = 0; i < jobs.size(); i++)
            {
                avgwaitingtime = avgwaitingtime + jobs.get(i).getWaitTime();
            }
            return (avgwaitingtime / jobs.size());
    	}
    },
    MAXIMUM_LATENESS(4)
    {
    	public double evaluate(List<Job> jobs)
    	{
            double maxlateness = 0.00;
            for (int i = 0; i < jobs.size(); i++)
            {
                if (jobs.get(i).getLateTime() > maxlateness)
                {
                    maxlateness = jobs.get(i).getLateTime();
                }
            }
            return maxlateness;

    	}
    },
    MAKESPAN(5)
    {
    	public double evaluate(List<Job> jobs)
    	{
            double makespan = 0.00;
            for (int i = 0; i < jobs.size(); i++)
            {
                if (jobs.get(i).getCompletionTime() > makespan)
                {
                    makespan = jobs.get(i).getCompletionTime();
                }
            }
            return makespan;

    	}
    },
    NUM_EARLY_JOB(6)
    {
    	public double evaluate(List<Job> jobs)
    	{
            int noearlyjob = 0;
            for (int i = 0; i < jobs.size(); i++)
            {
                if (jobs.get(i).getLateTime() <= 0.00)
                {
                    noearlyjob++;
                }
            }
            return noearlyjob;

    	}
    },
    NUM_TARDY_JOB(7)
    {
    	public double evaluate(List<Job> jobs)
    	{
            int notardyjob = 0;
            for (int i = 0; i < jobs.size(); i++)
            {
                if (jobs.get(i).getLateTime() > 0.00)
                {
                    notardyjob++;
                }
            }
            return notardyjob;

    	}
    },
    MAXIMUM_EARLINESS(8)
    {
    	public double evaluate(List<Job> jobs)
    	{
            double maxearlyness = 0.00;
            for (int i = 0; i < jobs.size(); i++)
            {
                if (jobs.get(i).getLateTime() <= 0.00 & maxearlyness < (jobs.get(i).getLateTime() * -1))
                {
                    maxearlyness = (jobs.get(i).getLateTime() * -1);
                }
            }
            return maxearlyness;

    	}
    },
    MAXIMUM_TARDINESS(9)
    {
    	public double evaluate(List<Job> jobs)
    	{
            double maxtardyness = 0.00;
            for (int i = 0; i < jobs.size(); i++)
            {
                if (jobs.get(i).getLateTime() > 0.00 & maxtardyness < jobs.get(i).getLateTime())
                {
                    maxtardyness = jobs.get(i).getLateTime();
                }
            }
            return maxtardyness;

    	}
    },
    TOTAL_EARLINESS(10)
    {
    	public double evaluate(List<Job> jobs)
    	{
            double totalearlyness = 0.00;
            for (int i = 0; i < jobs.size(); i++)
            {
                if (jobs.get(i).getLateTime() <= 0.00)
                {
                    totalearlyness = totalearlyness + (jobs.get(i).getLateTime() * -1);
                }
            }
            return totalearlyness;
    	}
    },
    TOTAL_TARDINESS(11)
    {
    	public double evaluate(List<Job> jobs)
    	{
            double totaltardyness = 0.00;
            for (int i = 0; i < jobs.size(); i++)
            {
                if (jobs.get(i).getLateTime() > 0.00)
                {
                    totaltardyness = totaltardyness + jobs.get(i).getLateTime();
                }
            }
            return totaltardyness;
    	}
    };
    public int abv;
    PerformanceMeasures(int abv) {
           this.abv = abv;
    }
	public double evaluate(List<Job> jobs)
	{
		return 0.0;
	}
}
