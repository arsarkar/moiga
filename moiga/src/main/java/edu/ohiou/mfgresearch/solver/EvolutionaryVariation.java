package edu.ohiou.mfgresearch.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.uncommons.maths.combinatorics.CombinationGenerator;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;

/**
 * Wrapper class for selecting parents as per arity
 * @author sarkara1
 *
 */
public class EvolutionaryVariation implements Variation{
	

    List<NumberGenerator<Probability>> selectionProbablities;

	/**
	 * The variation operators in the order they are applied.
	 */
	private final List<Variation> operators;
	
	/**
	 * The name of this variation operator.
	 */
	private String name;

	/**
	 * Constructs a compound variation operator with no variation operators.
	 */
	public EvolutionaryVariation() {
		super();
		operators = new ArrayList<Variation>();
		selectionProbablities = new LinkedList<NumberGenerator<Probability>>();
	}

	/**
	 * Constructs a compound variation operator with the specified variation
	 * operators.
	 * 
	 * @param operators the variation operators in the order they are applied
	 */
	public EvolutionaryVariation(double selectionProbability, Variation... operators) {
		this();
		for (Variation operator : operators) {
			appendOperator(operator);
			selectionProbablities.add(new ConstantGenerator<Probability>(new Probability(selectionProbability)));
		};
	}
	
	public void setOperator(Variation operator, double selectionProbability){
		appendOperator(operator);
		selectionProbablities.add(new ConstantGenerator<Probability>(new Probability(selectionProbability)));
	}
	
	/**
	 * Returns the name of this variation operator.  If no name has been
	 * assigned through {@link #setName(String)}, a name is generated which
	 * identifies the underlying operators.
	 * 
	 * @return the name of this variation operator
	 */
	public String getName() {
		if (name == null) {
			StringBuilder sb = new StringBuilder();
			
			for (Variation operator : operators) {
				if (sb.length() > 0) {
					sb.append('+');
				}
				
				sb.append(operator.getClass().getSimpleName());
			}
			
			return sb.toString();
		} else {
			return name;
		}
	}
	
	/**
	 * Sets the name of this variation operator.
	 * 
	 * @param name the name of this variation operator
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Appends the specified variation operator to this compound operator.
	 * 
	 * @param variation the variation operator to append
	 */
	public void appendOperator(Variation variation) {
		operators.add(variation);
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution[] result = Arrays.copyOf(parents, parents.length);
		int i = 0;
		for (Variation operator : operators) {
			if (result.length == operator.getArity()) {
				result = operator.evolve(parents);
//			} else if (operator.getArity() == 1) {
//				for (int j = 0; j < result.length; j++) {
//					result[j] = operator.evolve(new Solution[] { result[j] })[0];
//				}
			}
			else if(operator.getArity()>0){
				result = evolveCandidates(operator, parents, selectionProbablities.get(i));				
			} 
			else {
				throw new FrameworkException("invalid arity");
			}
			i++;
		}

		return result;
	}

	public Solution[] evolveCandidates(Variation operator, Solution[] parents, NumberGenerator<Probability> numberGenerator) {
		// evolution is not influenced by any ordering artifacts from previous
        // operations.
		Random rng = new Random();
        List<Solution> selectionClone = Arrays.asList(parents.clone());
        Collections.shuffle(selectionClone, rng);
        List<Solution> result = new LinkedList<Solution>();
        CombinationGenerator<Solution> comb = new CombinationGenerator<Solution>(selectionClone, operator.getArity());
		Iterator<List<Solution>> it  = comb.iterator();
		while(it.hasNext()){
			List<Solution> candidate = it.next();
			if(numberGenerator.nextValue().nextEvent(rng)){
				Solution[] offspring = operator.evolve(candidate.toArray(new Solution[candidate.size()]));
				result.addAll(Arrays.asList(offspring));
			}
			else{
				result.addAll(candidate);
			}
			if(result.size()>=parents.length){
				break;
			}
		}
        return result.toArray(new Solution[result.size()]);
	}

	@Override 
	public int getArity() {
		return operators.size();
	}

}
