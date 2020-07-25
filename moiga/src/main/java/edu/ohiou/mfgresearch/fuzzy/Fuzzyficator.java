package edu.ohiou.mfgresearch.fuzzy;

import java.util.function.Function;

public class Fuzzyficator implements Function<Double, Double>
{
	FuzzyMembership fuzzy;
	
	/**
	 * @param func The evaluation function
	 * @param fuzzy fuzzy membership strategy
	 * */
	public Fuzzyficator(FuzzyMembership fuzzy)
	{
		this.fuzzy=fuzzy;
	}
	
	@Override
	public Double apply(Double objective) 
	{
		return fuzzy.findSatisfaction(objective);
	}
	
	@Override
	public String toString() {
		return fuzzy.toString();
	}
}
