package com.nrp.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward McNealy <edwardmcn64@gmail.com> - Oct 26, 2015
 *
 */
public class Customer {

	private int id;
	private int profit;
	private List<Requirement> requirements;

	public Customer() {
		requirements = new ArrayList<Requirement>();
	}
	
	/**
	 * @param profit
	 * @param requirements
	 */
	public Customer(int id, int profit, List<Requirement> requirements) {
		this.id = id;
		this.profit = profit;
		this.requirements = requirements;
	}
	
	public Requirement getRequirementById(int id) {
		for (Requirement r : requirements) {
			if (r.getId() == id)
				return r;
		}
		return null;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the profit
	 */
	public int getProfit() {
		return profit;
	}

	/**
	 * @param profit
	 *            the profit to set
	 */
	public void setProfit(int profit) {
		this.profit = profit;
	}

	/**
	 * @return the requirements
	 */
	public List<Requirement> getRequirements() {
		return requirements;
	}

	/**
	 * @param requirements
	 *            the requirements to set
	 */
	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}
	
	@Override
	public String toString() {
		return "Customer[" + id + "] - Profit: " + profit + "; Number of Requirements: " + requirements.size();
	}

}
