package edu.ohiou.mfgresearch.moiga;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.moeaframework.core.Problem;

import edu.ohiou.mfgresearch.schedule.FlowShopProblem;
import edu.ohiou.mfgresearch.schedule.Job;
import edu.ohiou.mfgresearch.schedule.JobShopProblem;
import edu.ohiou.mfgresearch.schedule.JobT;
import edu.ohiou.mfgresearch.schedule.PerformanceMeasures;
import edu.ohiou.mfgresearch.schedule.ScheduleProblem;

public class DataGenerator {
	String path;
	int PTmin, PTmax, machines;
	int n;

	/**
	 * @param path  file path . Format should be as follows -
	 *              <ol>
	 *              <li>Single Machine :
	 *              Test&#60;Jobcount&#62;_&#60;PTmin&#62;_&#60;PTmax&#62;.csv</li>
	 *              <li>Flow Shop :
	 *              FlowShopTest&#60;Jobcount&#62;_&#60;PTmin&#62;_&#60;PTmax&#62;.csv</li>
	 *              <li>Job Shop :
	 *              JobShopTest&#60;Jobcount&#62;_&#60;Machinecount&#62_&#60;PTmin&#62;_&#60;PTmax&#62;.csv</li>
	 *              </ol>
	 * @param PTmin min Processing Time
	 * @param PTmax max Processing Time
	 * @param n     Number of Jobs
	 */
	public DataGenerator(String path, int PTmin, int PTmax, int n) {
		this.path = path;
		this.PTmin = PTmin;
		this.PTmax = PTmax;
		this.n = n;
		this.machines = 1;
	}

	/**
	 * @param path  file path . Format should be as follows -
	 *              <ol>
	 *              <li>Single Machine :
	 *              Test&#60;Jobcount&#62;_&#60;PTmin&#62;_&#60;PTmax&#62;.csv</li>
	 *              <li>Flow Shop :
	 *              FlowShopTest&#60;Jobcount&#62;_&#60;PTmin&#62;_&#60;PTmax&#62;.csv</li>
	 *              <li>Job Shop :
	 *              JobShopTest&#60;Jobcount&#62;_&#60;Machinecount&#62_&#60;PTmin&#62;_&#60;PTmax&#62;.csv</li>
	 *              </ol>
	 * @param PTmin min Processing Time
	 * @param PTmax max Processing Time
	 * @param n     Number of Jobs
	 * @param m     Number of Machines
	 */
	public DataGenerator(String path, int PTmin, int PTmax, int n, int m) {
		this.path = path;
		this.PTmin = PTmin;
		this.PTmax = PTmax;
		this.n = n;
		this.machines = m;
	}

	public void generate() throws IOException {
		int c = 0, k = 0;
		FileWriter fw = new FileWriter(path);
		fw.write("JobID,Processing Time,Ready Time,Due Date\n");
		for (int i = 1; i <= n; i++) {
			fw.write(i + ",");
			k = (int) (PTmin + Math.random() * (PTmax - PTmin));
			fw.write(k + ",");
			fw.write(c + ",");
			c += k;
			fw.write(c + "\n");
		}
		fw.close();
	}

	public void generateFlowShop() throws IOException {
		int pi = 0, mi = 0;
		FileWriter fw = new FileWriter(path);
		int machine[] = new int[this.machines];
		fw.write("JobID,MachineID,Processing Time,Ready Time,Due Date\n");
		for (int i = 1; i <= n; i++) {
			fw.write(i + ",");
			mi = (int) (Math.random() * (this.machines));
			fw.write((mi + 1) + ",");
			pi = (int) (PTmin + Math.random() * (PTmax - PTmin));
			fw.write(pi + ",");
			fw.write(machine[mi] + ",");
			machine[mi] += pi;
			fw.write(machine[mi] + "\n");
		}
		fw.close();
	}

	public void generateJobShop(double t, double r) throws IOException {
		int mi = 0, ri = 0, dd = 0, nTask = 0, p = 0, pi = 0;
		FileWriter fw = new FileWriter(path);
		int machine[] = new int[this.machines];
		fw.write("JobID,Processing Time,Ready Time,Due Date\n");
		for (int jID = 1; jID <= n; jID++) {
			nTask = 1 + (int) (Math.random() * (this.machines - 1));
			int task[][] = new int[nTask][2];
			dd = 0;
			for (int i = 0; i < nTask; i++) {
				mi = (int) (Math.random() * this.machines);
				pi = (int) (PTmin + Math.random() * (PTmax - PTmin));
				task[i][0] = mi;
				task[i][1] = pi;
				machine[mi] += pi;
				p += pi;
				dd = (int) Math.max(dd, machine[mi]);
				// dd=(int)
				// (Math.ceil(pi*(1-t-r/2.0))+Math.random()*(Math.ceil(pi*(1-t+r/2.0))-Math.ceil(pi*(1-t-r/2.0))));
			}
			fw.write(jID + "," + p + "," + 0 + "," + dd + "\n");
			ri = dd;
			fw.write(nTask + "\n");
			for (int i = 0; i < nTask; i++)
				fw.write((task[i][0] + 1) + "," + task[i][1] + "\n");
		}
		fw.close();
	}

	/**
	 * Read a Talliard dataset in the format given in
	 * http://mistic.heig-vd.ch/taillard/problemes.dir/ordonnancement.dir/jobshop.dir/tai15_15.txt
	 * No due date is added.
	 * 
	 * @param path
	 * @param measures
	 * @param t
	 * @param r
	 * @return
	 * @throws Exception
	 */
	// public static JobShopProblem amendTaillardToJobShop(String path,List<PerformanceMeasures> measures,double t,double r) throws Exception
	// {
	// 	Scanner sc=new Scanner(new File(path));
	// 	sc.nextLine();
	// 	String ss[]=sc.nextLine().trim().split(",");
	// 	// System.out.println(Arrays.deepToString(ss));
	// 	int jcnt=Integer.parseInt(ss[0]);
	// 	int mcnt=Integer.parseInt(ss[1]);
	// 	sc.nextLine();
	// 	int time[][]=new int[jcnt][mcnt];
	// 	List<JobT> jobs = new ArrayList<JobT>(mcnt);
	// 	for(int i=0;i<jcnt;i++)
	// 	{
	// 	String[]a=sc.nextLine().trim().split(" ");
	// 	int j=0;
	// 	for(int k=0;k<a.length;k++)
	// 	{
	// 	if(a[k].equals(""))
	// 	continue;
	// 	time[i][j++]=Integer.parseInt(a[k]);
	// 	}
	// 	}
	// 	sc.nextLine();
	// 	int mtime[]=new int[mcnt];
	// 	for(int i=0;i<jcnt;i++)
	// 	{
	// 	int task[][]=new int[mcnt][2];
	// 	int DD=0;
	// 	String[]a=sc.nextLine().trim().split(" ");
	// 	// System.out.println(Arrays.deepToString(a));
	// 	int j=0;
	// 	for(int k=0;k<a.length;k++)
	// 	{
	// 	if(a[k].equals(""))
	// 	continue;
	// 	int ind=Integer.parseInt(a[k])-1;
	// 	// System.out.println(ind+" "+i+" "+j);
	// 	task[ind][0]=j;
	// 	task[ind][1]=time[i][j];
	// 	mtime[j]+=time[i][j];
	// 	// DD=(int)Math.max(DD,mtime[j]);
	// 	// DD=(int)
	// 	(Math.ceil(time[i][j]*(1-t-r/2.0))+Math.random()*(Math.ceil(time[i][j]*(1-t+r/2.0))-Math.ceil(time[i][j]*(1-t-r/2.0))));
	// 	j++;
	// 	}
	// 	jobs.add(new JobT(i,DD,task));
	// 	}
	// 	int DD=Arrays.stream(mtime).max().getAsInt()*3;
	// 	for(int i=0;i<jcnt;i++)
	// 	{
	// 	int k=(int)
	// 	(Math.ceil(DD*(1-t-r/2.0))+Math.random()*(Math.ceil(DD*(1-t+r/2.0))-Math.ceil(DD*(1-t-r/2.0))));
	// 	jobs.get(i).setDueDate(k);
	// 	}
	// 	System.out.println("Number of jobs = " + jcnt);
	// 	System.out.println("Number of machines = " + mcnt);
	// 	for (int row = 0; row < time.length; row++) {
	// 	System.out.printf("J%2d| D:%f P->", row, jobs.get(row).getDueDate());
	// 	for (int col = 0; col < time[row].length; col++) {
	// 	System.out.printf("%4d", time[row][col]);
	// 	}
	// 	System.out.println();
	// 	}
	// 	sc.close();
	// 	return new JobShopProblem(jcnt,jobs,measures);
	// }

	public static JobShopProblem readTaillardToJobShopWithDD(String path, List<PerformanceMeasures> measures, double t,	double r) throws Exception {
		Scanner sc = new Scanner(new File(path));
		sc.nextLine();
		String ss[] = sc.nextLine().trim().split(",");
		// System.out.println(Arrays.deepToString(ss));
		int jcnt = Integer.parseInt(ss[0]);
		int mcnt = Integer.parseInt(ss[1]);
		sc.nextLine();
		int pt[][] = new int[jcnt][mcnt];
		List<JobT> jobs = new ArrayList<JobT>(mcnt);
		for (int i = 0; i < jcnt; i++) {
			String[] a = sc.nextLine().trim().split(" ");
			int j = 0;
			for (int k = 0; k < a.length; k++) {
				if (a[k].equals(""))
					continue;
				pt[i][j++] = Integer.parseInt(a[k]);
			}
		}
		sc.nextLine();
		// int mtime[] = new int[mcnt];
		for (int i = 0; i < jcnt; i++) {
			JobT job = new JobT(i + 1);
			// int task[][] = new int[mcnt][2];
			String[] machix = sc.nextLine().trim().split(" ");
			int j = 0;
			// loop over all machine index array except the last element which is duedate
			for (int k = 0; k < machix.length - 1; k++) {
				// if (machix[k].equals(""))
				// continue;
				int mix = Integer.parseInt(machix[k]); // machine ID
				// System.out.println(ind+" "+i+" "+j);
				job.addOperation(Long.valueOf(k + 1), Long.valueOf(mix), pt[i][k]);
			}
			int DD = Integer.parseInt(machix[machix.length - 1]);
			jobs.add(job);
		}
		jobs.forEach(j -> System.out.println(j.toString()));
		sc.close();
		return new JobShopProblem(jobs, measures);
	}

	public Problem createProb(List<PerformanceMeasures> measures) throws Exception {
		Scanner sc = new Scanner(new File(path));
		sc.nextLine();
		String ss = path.substring(path.lastIndexOf('\\') + 1).replace("Test", "");
		int cnt = Integer.parseInt(ss.substring(0, ss.indexOf('_')));
		List<Job> jobs = new LinkedList<Job>();
		while (cnt-- > 0) {
			String[] a = sc.nextLine().split(",");
			jobs.add(new Job(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[3]),
					Integer.parseInt(a[2])));
		}
		sc.close();
		return new ScheduleProblem(jobs, measures);
	}

	public Problem createFlowShopProb(List<PerformanceMeasures> measures) throws Exception {
		Scanner sc = new Scanner(new File(path));
		sc.nextLine();
		String ss = path.substring(path.lastIndexOf('\\') + 1).replace("FlowShopTest", "");
		int cnt = Integer.parseInt(ss.substring(0, ss.indexOf('_')));
		List<Job> jobs = new LinkedList<Job>();
		while (cnt-- > 0) {
			String[] a = sc.nextLine().split(",");
			jobs.add(new Job(Integer.parseInt(a[0]), Integer.parseInt(a[1]) - 1, Integer.parseInt(a[2]),
					Integer.parseInt(a[4]), Integer.parseInt(a[3])));
		}
		sc.close();
		return new FlowShopProblem(jobs, measures, this.machines);
	}

	public Problem createJobShopProb(List<PerformanceMeasures> measures) throws Exception {
		Scanner sc = new Scanner(new File(path));
		sc.nextLine();
		String ss[] = path.substring(path.lastIndexOf('/') + 1).replace("JobShopTest", "").replace(".csv", "")
				.split("_");
		int cnt = Integer.parseInt(ss[0]);
		List<JobT> jobs = new LinkedList<JobT>();
		while (cnt-- > 0) {
			String[] a = sc.nextLine().split(",");
			long jID = Integer.parseInt(a[0]) - 1;
			double pT = Integer.parseInt(a[1]);
			double DD = Integer.parseInt(a[3]);
			double rT = Integer.parseInt(a[2]);

			// System.out.println(Arrays.deepToString(a));
			String s = sc.nextLine();
			int task[][] = new int[Integer.parseInt(s)][2];
			for (int i = 0; i < task.length; i++) {
				a = sc.nextLine().split(",");
				task[i][0] = Integer.parseInt(a[0]) - 1;
				task[i][1] = Integer.parseInt(a[1]);
			}
			// jobs.add(new JobT(jID, DD, rT, task));
		}
		sc.close();
		// return new JobShopProblem(Integer.parseInt(ss[1]), jobs, measures);
		return null;
	}
}
