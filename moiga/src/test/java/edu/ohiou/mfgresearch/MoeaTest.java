package edu.ohiou.mfgresearch;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.problem.ZDT.ZDT1;
import org.moeaframework.problem.ZDT.ZDT3;
import org.moeaframework.problem.misc.Binh2;
import org.moeaframework.problem.misc.Schaffer2;

public class MoeaTest {

	@Test
	public void Binh2() {
		final NondominatedPopulation result = new Executor().withProblem(new Binh2()).withAlgorithm("NSGAII")
				.withProperty("populationSize", 100).withMaxEvaluations(500).run();
		result.forEach(res -> System.out.printf("f1 = %f, f2 = %f \n", res.getObjective(0), res.getObjective(1)));
	}

	@Test
	public void ZDT3() {
		final ZDT3 zdt = new ZDT3();
		final Solution s = zdt.newSolution();
		final RandomInitialization init = new RandomInitialization(zdt, 1000);
		final Solution[] sols = init.initialize();
		for (int i = 0; i < 100; i++) {
			zdt.evaluate(sols[i]);
			System.out.println(sols[i].getObjective(0) + ", " + sols[i].getObjective(1));
		}
	}

	@Test
	public void Schaffer2() {
		final Schaffer2 sh = new Schaffer2();
		final Solution s = sh.newSolution();
		final RandomInitialization init = new RandomInitialization(sh, 1000);
		final Solution[] sols = init.initialize();
		for (int i = 0; i < 100; i++) {
			sh.evaluate(sols[i]);
			System.out.println(sols[i].getObjective(0) + ", " + sols[i].getObjective(1));
		}
	}

	@Test
	public void ZDT1() {
		final NondominatedPopulation result = new Executor().withProblem(new ZDT1()).withAlgorithm("GeneticAlgorithm")
				.withProperty("populationSize", 100).withMaxEvaluations(100000).run();
		result.forEach(res -> {
			for (int i = 0; i < res.getNumberOfVariables(); i++)
				System.out.println("Var " + i + ":" + res.getVariable(i));
			System.out.printf("f1 = %f, f2 = %f \n", res.getObjective(0), res.getObjective(1));
		});
	}
	// //@Test
	// public void GASolverMultZDT1(){
	// List<Fuzzyficator> fuzzys = new LinkedList<Fuzzyficator>();
	// fuzzys.add(new Fuzzyficator(new FuzzyAsympMembership(1.0,0.0)));
	// fuzzys.add(new Fuzzyficator(new FuzzyAsympMembership(1.0,0.0)));
	// List<Variation> operators = Stream.of(new ArithmeticCrossover(0.6),new
	// UM(0.9))
	// .collect(Collectors.toList());
	// GASolver solver =
	// new GASolver(new ZDT1(),
	// fuzzys,
	// (a,b)->a+b,
	// operators,
	// new Double[]{0.01, 0.01},
	// new Selector(new RankSelection()),
	// true,
	// new Random(),
	// pop->{});
	// Solution sol=solver.evolve(100, 0, new GenerationCount(100)).copy();
	// for(int i=0;i<sol.getNumberOfVariables();i++)
	// System.out.println(sol.getVariable(i));
	// System.out.println(sol.getObjective(0)+","+
	// sol.getObjective(1));
	//
	// }

	@Test
	public void testRand() {
		System.out.println("Testing Rand");
		final RandomInitialization init = new RandomInitialization(new ZDT1(), 1);
		final Solution[] sols = init.initialize();
		for (int i = 0; i < sols.length; i++) {
			new ZDT1().evaluate(sols[i]);
			System.out.println(sols[i].getObjective(0) + " " + sols[i].getObjective(1));
		}
	}
}
