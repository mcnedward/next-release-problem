package com.nrp.app.opt4j;

import java.util.Random;

import org.opt4j.core.genotype.BooleanGenotype;
import org.opt4j.core.problem.Creator;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

/**
 * @author Edward McNealy <edwardmcn64@gmail.com> - Oct 27, 2015
 *
 */
public class NRPCreator implements Creator<BooleanGenotype> {

	private Random random;
	private final int populationSize;

	@Inject
	public NRPCreator(@Constant(value = "populationSize") int populationSize) {
		this.populationSize = populationSize;
		random = new Random();
	}

	@Override
	public BooleanGenotype create() {
		BooleanGenotype genotype = new BooleanGenotype();
		genotype.init(random, populationSize);
		return genotype;
	}
}
