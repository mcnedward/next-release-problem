package com.nrp.app.opt4j;

import org.opt4j.core.problem.ProblemModule;
import org.opt4j.core.start.Constant;

/**
 * @author Edward McNealy <edwardmcn64@gmail.com> - Oct 27, 2015
 *
 */
public class NRPModule extends ProblemModule {

	@Constant(value = "populationSize")
	protected int populationSize = 100;
	@Constant(value = "fileName")
	protected String fileName = "nrp1.txt"; 

	protected void config() {
		bindProblem(NRPCreator.class, NRPDecoder.class, NRPEvaluator.class);
	}

	/**
	 * @return the populationSize
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * @param populationSize
	 *            the populationSize to set
	 */
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
