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

	/**
	 * Find the amount of profit that would be earned from fulfilling the specified list of requirements. The weight of
	 * each requirement in the list that is required by the customer will be taken into account, then the profit for
	 * that customer will be scaled to determine the amount of profit for this customer.
	 * 
	 * @param requirements
	 *            The list of requirements that have been selected.
	 * @return The profit from fulfilling this customer's requirements, scaled to match the weight of their
	 *         requirements.
	 */
	public double calculateProfit(List<Requirement> requirements) {
		List<Requirement> requirementsToBeFilled = new ArrayList<Requirement>();
		for (Requirement r : requirements) {
			Requirement requirement = getRequirementById(r.getId());
			if (requirement != null)
				requirementsToBeFilled.add(r);
		}
		double weight = 0;
		for (Requirement requirement : requirementsToBeFilled) {
			weight += requirement.getWeight();
		}
		if (weight == 0)
			return 0;
		double scale = (100 / weight);
		double profitForRequirements = profit * scale;
		return profitForRequirements;
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
	 * @param id
	 *            the id to set
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
