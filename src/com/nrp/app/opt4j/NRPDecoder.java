package com.nrp.app.opt4j;

import org.opt4j.core.genotype.BooleanGenotype;
import org.opt4j.core.problem.Decoder;

/**
 * @author Edward McNealy <edwardmcn64@gmail.com> - Oct 27, 2015
 *
 */
public class NRPDecoder implements Decoder<BooleanGenotype, String> {
	
	@Override
	public String decode(BooleanGenotype genotype) {
		String phenotype = "";
		for (int i = 0; i < genotype.size(); i++) {
			if (genotype.get(i) == true)
				phenotype += "1";
			else
				phenotype += "0";
		}
		return phenotype;
	}
}
