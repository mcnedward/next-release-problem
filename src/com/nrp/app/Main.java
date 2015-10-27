package com.nrp.app;

import java.util.List;

import org.opt4j.core.genotype.BooleanGenotype;

import com.nrp.app.model.Customer;
import com.nrp.app.model.DataFile;
import com.nrp.app.opt4j.NRPCreator;
import com.nrp.app.opt4j.NRPDecoder;
import com.nrp.app.opt4j.NRPEvaluator;

/**
 * @author Edward McNealy <edwardmcn64@gmail.com> - Oct 27, 2015
 *
 */
public class Main {
	private final static String FILE_NAME = "nrp1.txt";

	private static DataCreator creator;
	
	public static void main(String[] args) {
		creator = new DataCreator();
		runOpt4J();
	}

	private static void runOpt4J() {
		NRPCreator creator = new NRPCreator(1000);
		NRPDecoder decoder = new NRPDecoder();
		NRPEvaluator evaluator = new NRPEvaluator(FILE_NAME);

		for (int x = 0; x < 10000; x++) {
			BooleanGenotype genotype = creator.create();
			String phenotype = decoder.decode(genotype);
			evaluator.evaluate(phenotype);
		}
	}

	@SuppressWarnings("unused")
	private static void run() {
		List<DataFile> files = creator.getFiles();
		for (DataFile file : files) {
			file.parseFile();
		}

		DataFile file = files.get(0);
		Customer customer = file.getCustomers().get(12);
		file.calculateRequirementValue(customer);

		file.normalize();
	}

}
