package com.nrp.app.opt4j;

import org.opt4j.core.genotype.BooleanGenotype;

import com.nrp.app.model.DataFile;

/**
 * @author Edward McNealy <edwardmcn64@gmail.com> - Oct 27, 2015
 *
 */
public class NRPProblem {

	private BooleanGenotype genotype;
	private DataFile dataFile;

	/**
	 * @param genotype
	 * @param dataFile
	 */
	public NRPProblem(BooleanGenotype genotype, DataFile dataFile) {
		this.genotype = genotype;
		this.dataFile = dataFile;
	}

	/**
	 * @return the genotype
	 */
	public BooleanGenotype getGenotype() {
		return genotype;
	}

	/**
	 * @param genotype
	 *            the genotype to set
	 */
	public void setGenotype(BooleanGenotype genotype) {
		this.genotype = genotype;
	}

	/**
	 * @return the dataFile
	 */
	public DataFile getDataFile() {
		return dataFile;
	}

	/**
	 * @param dataFile
	 *            the dataFile to set
	 */
	public void setDataFile(DataFile dataFile) {
		this.dataFile = dataFile;
	}

}
