package com.nrp.app.opt4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Evaluator;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;
import com.nrp.app.DataCreator;
import com.nrp.app.Main;
import com.nrp.app.model.Customer;
import com.nrp.app.model.DataFile;
import com.nrp.app.model.Requirement;

/**
 * @author Edward McNealy <edwardmcn64@gmail.com> - Oct 27, 2015
 *
 */
public class NRPEvaluator implements Evaluator<String> {

	private Random random;
	private DataFile dataFile;

	private Map<String, Double> fitnessMap;

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
		random = new Random();
		Objectives objectives = new Objectives();
		fitnessMap = new HashMap<String, Double>();

		evaluateFitnesses(phenotype);

		objectives.add("Cost To Fulfill All", Sign.MIN, fitnessMap.get("costFitness"));
		objectives.add("Over/Under Budget", Sign.MIN, fitnessMap.get("costVariance"));
		objectives.add("Profit", Sign.MAX, fitnessMap.get("totalProfit"));
		objectives.add("Fulfilled Requirements", Sign.MAX, fitnessMap.get("requirementCoverageFitness"));
		return objectives;
	}

	/**
	 * Evaluate all of the fitnesses.
	 * 
	 * @param phenotype
	 *            The phenotype
	 * @return A map of all of the fitness values with the name being the key.
	 */
	private Map<String, Double> evaluateFitnesses(String phenotype) {
		// If there are more booleans in the phenotype than there are requirements, remove the extra booleans
		int maxRequirementSize = dataFile.getRequirements().size();
		if (phenotype.length() > maxRequirementSize) {
			phenotype = phenotype.substring(0, maxRequirementSize);
		}

		List<Integer> ids = findRandomRequirements(phenotype.length());
		List<Integer> requirementIds = new ArrayList<Integer>();

		for (int x = 0; x < phenotype.length() - 1; x++) {
			// If the random phenotype is a 0, remove the corresponding requirement id from the list.
			if (phenotype.charAt(x) == '1') {
				requirementIds.add(ids.get(x));
			}
		}

		double costFitness = evaluateCostFitness(requirementIds);
		fitnessMap.put("costFitness", costFitness);

		double costVariance = evaluateCostVarianceFitness();
		fitnessMap.put("costVariance", costVariance);

		double totalProfit = evaluateProfitFitness(requirementIds);
		fitnessMap.put("totalProfit", totalProfit);

		double requirementCoverageFitness = evaluateRequirementCoverageFitness(requirementIds);
		fitnessMap.put("requirementCoverageFitness", requirementCoverageFitness);

		return fitnessMap;
	}

	/**
	 * Evaluate the cost fitness. This will determine the total amount to fulfill all customer requirements.
	 * 
	 * @param requirementIds
	 *            The list of requirements that have been selected
	 * @return The cost fitness
	 */
	private double evaluateCostFitness(List<Integer> requirementIds) {
		int fitness = 0;

		for (Customer customer : dataFile.getCustomers()) {
			double customerCost = findCostsForCustomer(customer, requirementIds);
			fitness += customerCost;
		}

		return fitness;
	}

	/**
	 * Evaluate the cost variance. This will determine how much over or under budget the data set went.
	 * 
	 * @return The cost variance fitness
	 */
	private double evaluateCostVarianceFitness() {
		double cost = fitnessMap.get("costFitness");
		double budget = dataFile.getBudget();
		double costVariance = budget - cost;
		Main.print("Cost Variance: " + costVariance + "; Budget: " + budget + "; Cost: " + cost);
		return costVariance;
	}

	/**
	 * Evaluate profit. This will find the weighted profit from each customer, and add that to the total
	 * profit. That profit is then reduced from the budget, and the remaining amount will determine the amount of profit
	 * that was made from this data set.
	 * 
	 * @param requirements
	 *            The list of requirements that have been selected
	 * @return The profit fitness
	 */
	private double evaluateProfitFitness(List<Integer> requirements) {
		double totalProfit = 0;
		for (Customer customer : dataFile.getCustomers()) {
			totalProfit += customer.calculateProfit(requirements);
		}
		double profit = totalProfit - dataFile.getBudget();
		Main.print("Total Profit: " + profit, false);
		return profit;
	}

	/**
	 * Evaluate the requirement coverage. This will find the count of fulfilled customer requirements, then divide that
	 * by the total amount of requirements. The result is the percentage of fulfilled requirements.
	 * 
	 * @param requirementIds
	 *            The list of requirements that have been selected
	 * @return The requirement coverage fitness
	 */
	private double evaluateRequirementCoverageFitness(List<Integer> requirementIds) {
		int count = 0;
		int numberOfRequirements = 0;
		for (Customer customer : dataFile.getCustomers()) {
			numberOfRequirements += customer.getRequirements().size();
			for (Integer id : requirementIds) {
				Requirement requirement = customer.getRequirementById(id);
				if (requirement != null)
					count++;
			}
		}
		double requirementCoverage = ((double) count / numberOfRequirements) * 100;
		Main.print("Requirement Coverage: " + requirementCoverage, false);
		return requirementCoverage;
	}


	/**
	 * Calculate the cost for all of the matching requirements for a customer.
	 * 
	 * @param customer
	 *            The customer to calculate costs for.
	 * @param requirementIds
	 *            The list of requirement ids to use.
	 * @return The cost of all the requirements that this customer has.
	 */
	private double findCostsForCustomer(Customer customer, List<Integer> requirementIds) {
		int cost = 0;
		for (Integer id : requirementIds) {
			Requirement requirement = customer.getRequirementById(id);
			if (requirement != null) {
				cost += requirement.getCost();
			}
		}
		return cost;
	}

	/**
	 * Find a random list of requirement ids.
	 * 
	 * @param size
	 *            The size of the random list.
	 * @return A list of random requirement ids.
	 */
	private List<Integer> findRandomRequirements(int size) {
		List<Integer> requirementIds = new ArrayList<Integer>();
		Map<Integer, Integer> dataFileMap = dataFile.getRequirements();
		// Get just the keys
		Integer[] keys = dataFileMap.keySet().toArray(new Integer[dataFileMap.keySet().size()]);

		// If the size is greater than the population, use the population's max size instead, in order to keep unique
		// requirements
		if (size >= keys.length) {
			return Arrays.asList(keys);
		}
		for (int x = 0; x < size; x++) {
			int index = random.nextInt(((keys.length - 1) - 1) + 1) + 1;
			Integer requirementId = keys[index];
			// Don't add if already in there!
			if (requirementIds.contains(requirementId)) {
				x--;
				continue;
			}
			requirementIds.add(requirementId);
		}
		return requirementIds;
	}

}
