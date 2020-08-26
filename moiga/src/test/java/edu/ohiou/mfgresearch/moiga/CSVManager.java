package edu.ohiou.mfgresearch.moiga;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CSVManager
{
	String path;
	String paretoPath;
	private boolean printHeader;
	public CSVManager(String path,boolean printHeader)
	{
		this.path=path;
		this.paretoPath = path.trim().replaceAll(".csv", "-pareto.csv");
		this.printHeader=printHeader;
	}
	public void appendData(List<List<Double>> data,String []headers) throws IOException
	{
		FileWriter ob = new FileWriter(path);
		if(printHeader)
		{
			for(int i=0;i<headers.length;i++)
			{
				ob.append(headers[i]);
				if(i!=headers.length-1)
					ob.append(",");
			}
			ob.append("\n");
		}
		for(List<Double> i:data)
		{
			for(int j=0;j<i.size();j++)
			{
				ob.append(Double.toString(i.get(j)));
				if(j!=i.size()-1)
					ob.append(",");
			}
			ob.append("\n");	
		}
		ob.close();
	}
	
	public void appendPareto(List<List<Double>> data,String []headers) throws IOException
	{
		FileWriter ob = new FileWriter(paretoPath);
		List<List<Double>> pareto = new LinkedList<List<Double>>();
		//List<Double> r1,r2;

		while(true)
		{
			outer:for(List<Double> r1:data)
			{
				//r1=data.get(i);
				for(List<Double> r2:data)
				{
					if(r1==r2)
						continue;
					if(isDominant(r2,r1))
						continue outer;
				}
				pareto.add(r1);
			}
			if(data.size()==pareto.size())
				break;
			data=pareto;
			pareto = new ArrayList<List<Double>>();
		}
		if(printHeader)
		{
			for(int i=0;i<headers.length;i++)
			{
				ob.append(headers[i]);
				if(i!=headers.length-1)
					ob.append(",");
			}
			ob.append("\n");
		}
		//System.out.println(pareto.size());
		for(List<Double> i:pareto)
		{
			for(int j=0;j<i.size();j++)
			{
				ob.append(Double.toString(i.get(j)));
				if(j!=i.size()-1)
					ob.append(",");
			}
			ob.append("\n");	
		}
		ob.close();
	}
	boolean isDominant(List<Double> r1,List<Double> r2)
	{
		int count =0;
		for(int k= 0; k<r1.size() ; k++)
			if(r1.get(k)<=r2.get(k))
				count ++;
		if(count==r1.size())
			return true;
		return false;
	}
}
