package com.nrp.app.opt4j;

import java.util.ArrayList;
import java.util.List;

import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Evaluator;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;
import com.nrp.app.DataCreator;
import com.nrp.app.model.Customer;
import com.nrp.app.model.DataFile;
import com.nrp.app.model.Requirement;

/**
 * @author Edward McNealy <edwardmcn64@gmail.com> - Oct 27, 2015
 *
 */
public class NRPEvaluator implements Evaluator<String> {

	private DataFile dataFile;

	List<Requirement> requirements;

	@Inject
	public NRPEvaluator(@Constant(value = "fileName") String fileName) {
		DataCreator creator = new DataCreator();

		dataFile = creator.createDataFile("nrp1.txt");
		if (dataFile == null)
			System.out.println("No data files created from " + fileName + "...");
		System.out.println("CREATED FILE");
	}

	@Override
	public Objectives evaluate(String phenotype) {
		Objectives objectives = new Objectives();
		requirements = new ArrayList<Requirement>();

		getRequirements(phenotype);

		objectives.add("Cost", Sign.MIN, evaluateCostFitness());
		objectives.add("Value", Sign.MAX, evaluateValueFitness());
		return objectives;
	}

	private double evaluateCostFitness() {
		double fitness = 0;

		for (Requirement r : requirements) {
			for (Customer customer : dataFile.getCustomers()) {
				for (Requirement cr : customer.getRequirements()) {
					if (cr.getId() == r.getId())
						fitness += r.getCost();
				}
			}
		}
		// System.out.println("Cost: " + fitness);
		return fitness;
	}

	/**
	 * Evaluate value. This will find the weighted profit from each customer, and add that to the total profit. That
	 * profit is then reduced from the budget, and the remaining amount will determine the amount of profit that was
	 * made from this data set.
	 * 
	 * @param requirements
	 *            The list of requirements that have been selected
	 * @return The profit fitness
	 */
	private double evaluateValueFitness() {
		double totalProfit = 0;
		for (Customer customer : dataFile.getCustomers()) {
			totalProfit += customer.calculateProfit(requirements);
		}
		double profit = totalProfit - dataFile.getBudget();
		// System.out.println("Profit: " + profit);
		return profit;
	}

	/**
	 * Evaluate all of the fitnesses.
	 * 
	 * @param phenotype
	 *            The phenotype
	 * @return A map of all of the fitness values with the name being the key.
	 */
	private void getRequirements(String phenotype) {
		// If there are more booleans in the phenotype than there are requirements, remove the extra booleans
		int maxRequirementSize = dataFile.getRequirements().size();
		if (phenotype.length() > maxRequirementSize) {
			phenotype = phenotype.substring(0, maxRequirementSize);
		}

		List<Requirement> requirementsList = dataFile.getRequirements();

		for (int x = 0; x < phenotype.length() - 1; x++) {
			// If the random phenotype is a 0, remove the corresponding requirement id from the list.
			if (phenotype.charAt(x) == '1') {
				requirements.add(requirementsList.get(x));
			}
		}
	}

}
