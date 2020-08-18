package edu.ohiou.mfgresearch.schedule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.moeaframework.core.Solution;

public enum ScheduleHeuristic
{
    FCFS(1)
    {
    	public Solution evaluate(List<JobT> jobs,int jcnt,int mcnt)
    	{
    		
    		double mtime[]=new double[mcnt];
            for(JobT jid:jobs)
            {
            	double prev=0;
            	for(int i=0;i<jid.getTaskCount();i++)
            	{
            		if(mtime[jid.task[i][0]]<prev)
            			mtime[jid.task[i][0]]=prev;
            		mtime[jid.task[i][0]]+=jid.task[i][1];
            		prev=mtime[jid.task[i][0]];
            	}  
            	for(int k=0;k<mcnt;k++)
            		System.out.print(mtime[k]+" ");
            	System.out.println();
            	jid.completionTime=Arrays.stream(mtime).max().getAsDouble();
            }
//            jobs.forEach(i->System.out.println(i));
            Solution temp=new Solution(jobs.size(),5);
            for(int i=0;i<jobs.size();i++)
            {
            	temp.setVariable(i,jobs.get(i).copy());
            	jobs.get(i).completionTime=0.0;
            }
            return temp;
    	}
    },
	
	SPT(2)
    {
    	public Solution evaluate(List<JobT> jobs,int jcnt,int mcnt)
    	{
            double mtime[]=new double[mcnt];
            Collections.sort(jobs,new Comparator<JobT>(){

				@Override
				public int compare(JobT o1, JobT o2) {
					if(o1.processingTime>o2.processingTime)
						return 1;
					else if(o1.processingTime<o2.processingTime)
						return -1;
					return 0;
				}
            	
            	
            });
            for(JobT jid:jobs)
            {
//            	System.out.println(jid.jobID);
            	double prev=0;
            	for(int i=0;i<jid.getTaskCount();i++)
            	{
            		if(mtime[jid.task[i][0]]<prev)
            			mtime[jid.task[i][0]]=prev;
            		mtime[jid.task[i][0]]+=jid.task[i][1];
            		prev=mtime[jid.task[i][0]];
            	}  
            	for(int k=0;k<mcnt;k++)
            		System.out.print(mtime[k]+" ");
        		System.out.println();
            	jid.completionTime=Arrays.stream(mtime).max().getAsDouble();
            }
//            jobs.forEach(i->System.out.println(i));
            Solution temp=new Solution(jobs.size(),5);
            for(int i=0;i<jobs.size();i++)
            {
            	temp.setVariable(i,jobs.get(i).copy());
            	jobs.get(i).completionTime=0.0;
            }
            return temp;
    	}
    },
    LWKR(3)
    {
    	public Solution evaluate(List<JobT> jobs,int jcnt,int mcnt)
    	{
            int tcnt[]=new int[jcnt];
            double mtime[]=new double[mcnt];
            double lmt[]=new double[jcnt];
            int ttemp[]=null;
            boolean flag=true;
            System.out.println(jobs+" "+jcnt);
            while(flag)
            {
            	flag=false;
            	double temp=Integer.MAX_VALUE;
            	JobT jid=null;
            	for(int i=0;i<jcnt;i++)
            		if(tcnt[(int) jobs.get(i).jobID]<jobs.get(i).getTaskCount() )
            		{	
            			double wkr=0;
            			flag=true;
            			double wtemp[]=new double[mcnt];
            			for(int k=tcnt[(int) jobs.get(i).jobID];k<jobs.get(i).getTaskCount();k++)
            				wtemp[jobs.get(i).task[k][0]]+=jobs.get(i).task[k][1];
            			wkr=Arrays.stream(wtemp).max().getAsDouble();
            			if(temp>wkr)
            			{
	            			jid=jobs.get(i);
	            			temp=wkr;
            			}
            		}
            	if(jid==null)
            		break;
            	ttemp=jid.task[tcnt[(int)jid.jobID]];
            	
            	if(mtime[ttemp[0]]<lmt[(int) jid.jobID])
            		mtime[ttemp[0]]=lmt[(int) jid.jobID];
            	
            	mtime[ttemp[0]]+=ttemp[1];
            	lmt[(int) jid.jobID]=mtime[ttemp[0]];
            	tcnt[(int) jid.jobID]++;
            	jid.completionTime=Math.max(jid.completionTime,mtime[ttemp[0]]);

                for(int i=0;i<mcnt;i++)
                	System.out.print(mtime[i]+" ");
                System.out.println();
            }
//            jobs.forEach(i->System.out.println(i));
            Solution temp=new Solution(jobs.size(),5);
            for(int i=0;i<jobs.size();i++)
            {
            	temp.setVariable(i,jobs.get(i).copy());
            	jobs.get(i).completionTime=0.0;
            }
            return temp;
    	}
    },
    DDate(4)
    {
    	public Solution evaluate(List<JobT> jobs,int jcnt,int mcnt)
    	{
//            int tcnt[]=new int[jcnt];
            double mtime[]=new double[mcnt];
            Collections.sort(jobs,new Comparator<JobT>(){

				@Override
				public int compare(JobT o1, JobT o2) {
					if(o1.dueDate>o2.dueDate)
						return 1;
					else if(o1.dueDate<o2.dueDate)
						return -1;
					return 0;
				}
            	
            	
            });
            for(JobT i:jobs)
            {
            	double prev=0;
            	for(int k=0;k<i.getTaskCount();k++)
            	{
            		if(mtime[i.task[k][0]]<prev)
            			mtime[i.task[k][0]]=prev;
	            	mtime[i.task[k][0]]+=i.task[k][1];
            		prev=mtime[i.task[k][0]];
	            	i.completionTime=Math.max(i.completionTime,mtime[i.task[k][0]]);
            	}
            	for(int k=0;k<mcnt;k++)
            		System.out.print(mtime[k]+" ");
            	System.out.println();
            }
            Solution temp=new Solution(jobs.size(),5);
            for(int i=0;i<jobs.size();i++)
            {
            	temp.setVariable(i,jobs.get(i).copy());
            	jobs.get(i).completionTime=0.0;
            }
            return temp;
    	}
    };
    public int abv;
    ScheduleHeuristic(int abv)
    {
           this.abv = abv;
    }
    public Solution evaluate(List<JobT> jobs,int m,int n)
	{
    	return null;
	}
}
