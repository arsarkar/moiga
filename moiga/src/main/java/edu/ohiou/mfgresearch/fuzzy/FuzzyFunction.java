package edu.ohiou.mfgresearch.fuzzy;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FuzzyFunction implements BiFunction<List<Double>,List<? extends List<Double>>,Double>
{
	
	Function<List<Double>,Double> func;
	FuzzyMembership fuzzy;
	/**
	 * @param func The evaluation function
	 * @param fuzzy fuzzy membership strategy
	 * */
	public FuzzyFunction(Function<List<Double>,Double> func,FuzzyMembership fuzzy)
	{
		this.func=func;
		this.fuzzy=fuzzy;
	}
	public double getFuzzySatisfaction(List<Double> value)
	{
		return fuzzy.findSatisfaction(func.apply(value));
	}
	@Override
	public Double apply(List<Double> candidate,List<? extends List<Double>> population) 
	{
		return getFuzzySatisfaction(candidate);
	}
	
}
