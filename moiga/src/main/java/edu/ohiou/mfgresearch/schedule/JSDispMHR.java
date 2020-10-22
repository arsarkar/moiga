package edu.ohiou.mfgresearch.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;

import edu.ohiou.mfgresearch.schedule.ScheduleHeuristic.Heuristic;

public class JSDispMHR extends JobShopProblem{

    public int MAX_EVAL = 1000;
    List<Heuristic> heuristics;

    public JSDispMHR(List<JobT> jobs, List<PerformanceMeasures> measures, List<Heuristic> heuristics) throws Exception {
        super(jobs, measures);
        this.heuristics = heuristics;
    }

    @Override
    public String getName() {
        return "JobShopHeuristicRep-MHR";
    }

    /**
     * As given by Vázquez-Rodríguez et. al (2010)
     */
    @Override
    public int getNumberOfVariables() {
        int totalOps = jobs.stream().map(j->j.getTaskCount()).reduce(0, (a,b)->a+b);
        int size = (int) Math.floor(1.5034 * (-1) + 0.0002 * totalOps + 6.1314 * Math.log10(MAX_EVAL));
        if (size > totalOps -1){
            return totalOps;
        }
        return size;
    }

    @Override
    public int getNumberOfObjectives() {
        return measures.size();
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public void evaluate(Solution solution) {
        //prepare heurictic based scheduler
        List<JobT> jlist = jobs.stream().map(j -> (JobT) j.copy()).collect(Collectors.toList());
        ScheduleHeuristic sh = new ScheduleHeuristic(jlist);
        for(int i=0; i<solution.getNumberOfVariables(); i++){
            int count = ((MHR) solution.getVariable(i)).p;
            for(int j=0; j<count; j++){
                sh.addScheme(((MHR) solution.getVariable(i)).h);
            }
        }
        //make schedule by applying dispatching rules
        List<JobT> sched = new ArrayList<JobT>();
        while(sh.hasNext()){
            JobT j = sh.next();
            if(j!=null) sched.add(j);
        }
        //System.out.println(sched.stream().map(j->Long.toString(j.getJobID())).collect(Collectors.joining(",")));
        //set completion time
        Map<Long, JobT> jobCT = setCompletionTimes(sched);
        //System.out.println(jobCT.values().stream().map(j->j.toString()).collect(Collectors.joining(",")));
        //convert List<JobT> to List<Job>
		List<Job> jlist1 = jobCT.values().stream().map(ii -> (Job) ii).collect(Collectors.toList());	
		//update solution objective	
		for (int i = 0; i < measures.size(); i++) {
			solution.setObjective(i, measures.get(i).evaluate(jlist1));
		}
    }

    @Override
    public Solution newSolution() {
        int totalOps = jobs.stream().map(j->j.getTaskCount()).reduce(0, (a,b)->a+b);
        Solution s = new Solution(getNumberOfVariables(), getNumberOfObjectives());

        int[] counts = getCountDist(totalOps, getNumberOfVariables());
        for(int i = 0; i< counts.length; i++){
            //get a scheme randomly
            Heuristic h = heuristics.get(PRNG.nextInt(heuristics.size()));
            s.setVariable(i, new MHR(h, counts[i]));
        }
        return s;
    }

    /**
     * Returns randomly distributed integers of length size
     * which sums up to total
     * @param total
     * @param size
     * @return
     */
    private int[] getCountDist(int total, int size){
        //randomly fill up P(i)
        int[] ps = new int[size];
        int t = 0;
        for(int i=0; i< size; i++){
            int p = PRNG.nextInt(total);
            t += p;
            if(t >= total){
                ps[i] = 0;
                t -= p;
            }
            else{
                ps[i] = p;
            }
        }
        int diff = total - t;
        while(diff != 0){
            int i = PRNG.nextInt(size);
            if(ps[i]>= total) {
                continue;
            }
            ps[i] += 1;
            diff -= 1;
        }
        PRNG.shuffle(ps);
        return ps;
    }

    @Override
    public void close() {

    }

    public String toSolutionString(Solution s){
        return
        IntStream.range(0, s.getNumberOfVariables())
                 .mapToObj(i-> (MHR) s.getVariable(i))
                 .map(m->"("+m.h.name()+","+m.p+")")
                 .collect(Collectors.joining(","));
    }
   
}
