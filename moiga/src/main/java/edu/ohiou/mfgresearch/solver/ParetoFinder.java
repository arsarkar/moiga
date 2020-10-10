package edu.ohiou.mfgresearch.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;

import edu.ohiou.mfgresearch.fuzzy.FuzzyMembershipT;
import edu.ohiou.mfgresearch.fuzzy.Fuzzyficator;

public class ParetoFinder
{
	private List<List<Double>> bounds;
	private ParetoRepo seedRepo;
	Problem prob;
	BinaryOperator<Double> aggregator; 
	List<Variation> operators; 
	Double[] probabilities;
	Selector selection; 
	boolean isNatural; 
	Random rng;
	Consumer<Solution> solutionObserver;
	Consumer<PopulationData<? extends Solution>> dataObserver;
	Function<Double,Double> convergenceStrategy;
	TerminationCondition[] term;
	double alpha;
	int noOfPts;
	boolean adaptiveAlpha;
	public ParetoFinder(List<List<Double>> bounds,
			Problem prob, 
			BinaryOperator<Double> aggregator, 
			List<Variation> operators, 
			Double[] probabilities,
			Selector selection, 
			boolean isNatural, 
			Random rng, 
			Consumer<Solution> solutionObserver,
			Consumer<PopulationData<? extends Solution>> dataObserver,
			Function<Double,Double> convergenceStrategy,
			TerminationCondition[] term,
			double alpha,
			boolean adaptiveAlpha,
			int noOfSeedPts)
	{
		this.bounds=bounds;
		this.noOfPts=noOfSeedPts;
		this.prob=prob;
		this.aggregator=aggregator;
		this.operators=operators;
		this.probabilities=probabilities;
		this.selection=selection;
		this.isNatural=isNatural;
		this.rng=rng;
		this.solutionObserver=solutionObserver;
		this.dataObserver=dataObserver;
		this.convergenceStrategy=convergenceStrategy;
		this.term=term;
		this.alpha=alpha;
		seedRepo=new ParetoRepo();
		this.adaptiveAlpha=adaptiveAlpha;
	}
	public List<List<Double>> findPareto()
	{

		List<List<Double>> Xtemp=new LinkedList<List<Double>>();
		//param -> bounds => String[] O 
		boolean g = true,h=true, h1=true;
		List<List<Double>> X = new LinkedList<List<Double>>(),X1;
		List<List<List<Double>>> S=new ArrayList<List<List<Double>>>(),
				S1=new ArrayList<List<List<Double>>>();
		Set<String> permutes;
		List<Double> r;
		double delta=1.0,lowBet=1.0;
		for(int i=0;i<bounds.size();i++)
		{
			List<Double> temp=new LinkedList<Double>();
			for(int j=0;j<bounds.size();j++)
				if(i==j)
					temp.add(bounds.get(i).get(0)-delta);
				else
					temp.add(bounds.get(i).get(1)+lowBet);
			
			//this portion run ga on initial bound
//			List<List<Double>> fuzzys=this.createFuzzyBound(temp, delta);					
//			List<Fuzzyficator> fuzzyFictators = new LinkedList<Fuzzyficator>();
//			for(List<Double> j:fuzzys)
//			{
//				fuzzyFictators.add(new Fuzzyficator(new FuzzyMembershipT(
//						j.get(0),
//						j.get(1),
//						j.get(2),
//						j.get(3)
//						)));
//			}
			
			
			X.add(temp);
		}
		

		
		X1=X;
		Xtemp.addAll(X);
		int c=0;
		outer:while(h1)
		{
			//Generating Groups for making bounding box
			System.out.println("Gen#"+c++);

			int k=Math.min(X.size(), X.size()-bounds.size());
			permutes=new TreeSet<String>();
			String bitString="";
			while(k-->0)
				bitString+="0";
			k=bounds.size();
			while(k-->0)
				bitString+="1";
			permute(bitString,0,bitString.length()-1,permutes);
			//Converting all strings in permute to corresponding lists
			S.clear();
			//int cnt=1;//NOT IN ALGO
			for(String s:permutes)
			{
				//if(cnt++>=Math.min(25,s.length*0.25))//NOT IN ALGO
					//break;//NOT IN ALGO
				List<List<Double>> temp=new LinkedList<List<Double>>();
				for(int i=0;i<s.length();i++)
					if(s.charAt(i)=='1')
						//try{
							temp.add(X.get(i));
							//}catch(Exception e) {System.out.println(s+" "+X.size());}
					S.add(temp);
				//System.out.println(temp.toString());
			}

			System.out.println("Number of combination --> " + S.size());
			//S<-S/S'
			for(int i=0;i<S1.size();i++)
				S.remove(S1.get(i));
//			if(S1.size()==S.size()){
//				h1 =false;
//				continue;
//			}
//			System.out.println("in G "+" |S|="+S.size()+" |X|="+X.size());	
			System.out.println("Permutations generated");
			h1 = false;
			for(List<List<Double>> s:S)
			{	
				System.out.println("combination --> "+s.toString());
				r=getBoundingBox(s);				
				System.out.println("bounding box --> "+r.toString());				
				h=true;
				for(List<Double> x:X)
					if(!s.contains(x) && containsPoint(x,r))
					{
						System.out.println("combination contains "+ x.toString());
						h=false;
						break;
					}
//				List<List<Double>> Xtemp2 = new LinkedList<List<Double>>();
//				Xtemp2.addAll(X);
//				for(List<Double> x:X){
//					if(x.equals(s.get(0))) Xtemp2.remove(x);
//					if(x.equals(s.get(1))) Xtemp2.remove(x);
//				}						
//				int count = containsPointCount(r, Xtemp2);
//				if(count>0){
//					h=false;
//					break;
//				}
				
				if(h)
				{
					h1 = true;
					//List<List<Double>> fuzzys=this.createFuzzyBound(r, delta);					
					List<List<Double>> fuzzys=this.createFuzzyBoundAlt(r, getTrailingBox(s), this.alpha);
					
					if(adaptiveAlpha)
					{
					//ALGO to modify alpha
						if(alpha>=0.4)
							alpha-=0.0934*X.size();
						else if(alpha<=0.3)
							alpha+=0.0934*X.size();
					//ALGO ends
					}
					
					System.out.println("fuzzy bound --> "+fuzzys.toString());
					//fuzzyFictator conversion included in GARun to 
		
					//x1<-runGA(f)
					List<Double> x1=GARun(fuzzys);
					//System.out.println(x1.toString().replace('[',' ').replace(']',' '));
					g=true;
					List<List<Double>> Xtemp1 = new LinkedList<List<Double>>();
					Xtemp1.addAll(Xtemp);
					for(int i=0;i<Xtemp.size();i++)
					{
						List<Double> x=Xtemp.get(i);
						if(this.isDominant(x1,x))
						{	
							System.out.println("Removed : "+x.toString());
							Xtemp1.removeIf(o->{
								for(int j=0;j<o.size();j++){
									if(x.get(j)!=o.get(j)) return false;
								}
								return true;
							});
						}
						if(this.isDominant(x,x1))
						{
							g=false;
							S1.add(s);
						}
					}
					if(g){
						System.out.println("new solution added.");
						Xtemp1.add(x1);	
					}
					Xtemp.clear();
					Xtemp.addAll(Xtemp1);
				}
				System.out.println("XTemp after this combination = "+Xtemp.toString());
				//System.out.println(h+" "+s.toString());
			}
			X.clear();
			X.addAll(Xtemp);
			System.out.println("X after Gen#"+c + " "+ X.toString());
			
		}
		//X<-X/X'
		return X;
	}
	List<Double> getBoundingBox(List<List<Double>> S)
	{
		List<Double> r=new LinkedList<Double>();
		double d;
		for(int i=0;i<S.get(0).size();i++)//looping from 1->|O|
		{
			d=0.0;
			for(int j=0;j<S.size();j++)//looping through all points
				if(S.get(j).get(i)>d)
					d=S.get(j).get(i);
			r.add(d);
		}
		return r;
	}
	
	List<Double> getTrailingBox(List<List<Double>> S)
	{
		List<Double> r=new LinkedList<Double>();
		double d;
		for(int i=0;i<S.get(0).size();i++)//looping from 1->|O|
		{
			d=S.get(i).get(i);
			for(int j=0;j<S.size();j++)//looping through all points
				if(S.get(j).get(i)<d)
					d=S.get(j).get(i);
			r.add(d);
		}
		return r;
	}
	
	public boolean containsPoint(List<Double> x,List<Double> r)
	{
		int c=0;
		for(int i=0;i<x.size();i++)
			if(x.get(i)>r.get(i))
				return false;
		return true;
	}
	public int containsPointCount(List<Double> r,List<List<Double>> X)
	{
		int c=0;
		outer:for(List<Double> x:X)
			if(this.containsPoint(x, r))
				c++;
		return c;
	}
	List<List<Double>> createFuzzyBound(List<Double> r,double delta)
	{
		List<List<Double>> F=new LinkedList<List<Double>>();
		for(int i=0;i<bounds.size();i++)
		{
			List<Double> temp=new LinkedList<Double>();
			temp.add(bounds.get(i).get(0));
			temp.add(bounds.get(i).get(1));
			
			//The convergence strategy .. 
			temp.add(
					this.convergenceStrategy.apply(r.get(i))
					);
			temp.add(bounds.get(i).get(1));
			F.add(temp);
		}
		return F;
	}
	
	List<List<Double>> createFuzzyBoundAlt(List<Double> r, List<Double> r1, double delta)
	{
		
		List<List<Double>> F=new LinkedList<List<Double>>();
		for(int i=0;i<bounds.size();i++)
		{
			double alpha=delta;
			
			List<Double> temp=new LinkedList<Double>();
			temp.add(bounds.get(i).get(0));
			temp.add(bounds.get(i).get(1));
			temp.add(r.get(i)*alpha + r1.get(i)*(1-alpha) );
//			temp.add(r1.get(i) );
			temp.add(bounds.get(i).get(1)+0.1);
//			System.out.println(temp.toString());			
			F.add(temp);
		}
		return F;
	}	
	
	//Checks is r1 is dominating r2
	public static boolean isDominant(List<Double> r1,List<Double> r2)
	{
		int count =0,eq=0;
		for(int k= 0; k<r1.size() ; k++)
		{	
			if(r1.get(k)<=r2.get(k))
				count ++;
			if(r1.get(k)==r2.get(k))
				eq++;
		}
		if(count==r1.size() && eq!=r1.size()){
			System.out.println(r1.toString() + " is dominating " + r2.toString());
			return true;
		}
		return false;
	}

    List<Double> GARun(List<List<Double>> fuzzys)
    {
    	List<Fuzzyficator> fuzzyFictators = new LinkedList<Fuzzyficator>();
		for(List<Double> i:fuzzys)
		{
			fuzzyFictators.add(new Fuzzyficator(new FuzzyMembershipT(
					i.get(0),
					i.get(1),
					i.get(2),
					i.get(3)
					)));
		}
		
    	GASolver solver = 
				new GASolver(this.prob, 
							 fuzzyFictators, 
							 this.aggregator,
							 this.operators,
							 new Double[]{0.2, 0.5},
							 this.selection, 
							 true, 
							 this.rng,
							 this.solutionObserver,
							 this.dataObserver);
    	//Get seeds from seed Repo
    	List<Solution> seed=seedRepo.getPoints(fuzzys,noOfPts);
    	
//    	System.out.println("Seed size : "+seed.size()+" "+noOfPts);
    	
    	List<Solution> pop;
    	if(seed==null || seed.size()==0)
    		pop=Arrays.stream(
    						//solver.evolvePopulation(100, 0, seed,term).toArray())
    							solver.evolvePopulation(100, 0, term).toArray())
    						.map(ii->((EvaluatedCandidate<Solution>)ii).getCandidate())
    						.collect(Collectors.toList());
    	else
    		pop=Arrays.stream(
						solver.evolvePopulation(100, 0, seed, 
								term).toArray())
					.map(ii->((EvaluatedCandidate<Solution>)ii).getCandidate())
					.collect(Collectors.toList());
    	
    	//Add seeds to seed Repo
    	seed=new ArrayList<Solution>();
    	for(int i=0;i<5;i++)
    		seed.add(pop.get(i));
    	this.seedRepo.put(fuzzys, seed);
		return DoubleStream.of(
				pop.get(0).getObjectives()
				).boxed().collect(Collectors.toList());
    }
    /** 
     * permutation function 
     * @param str string to calculate permutation for 
     * @param l starting index 
     * @param r end index 
     */
    public void permute(String str, int l, int r,Set<String> S) 
    { 
        if (l == r)
        {
            	S.add(str);
        }
        else
        { 
            for (int i = l; i <= r; i++) 
            { 
                str = swap(str,l,i); 
                permute(str, l+1, r,S); 
                str = swap(str,l,i); 
            } 
        } 
    }
    String swap(String a, int i, int j) 
    { 
        char temp; 
        char[] charArray = a.toCharArray(); 
        temp = charArray[i] ; 
        charArray[i] = charArray[j]; 
        charArray[j] = temp; 
        return String.valueOf(charArray); 
    }
}
