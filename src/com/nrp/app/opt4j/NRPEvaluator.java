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

		Map<String, Double> fitnessMap = evaluateFitnesses(phenotype);

		objectives.add("Cost Fitness", Sign.MIN, fitnessMap.get("costFitness"));
		objectives.add("Value Fitness", Sign.MAX, fitnessMap.get("valueFitness"));
		objectives.add("Fault Coverage", Sign.MIN, fitnessMap.get("faultCoverageFitness"));
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
		Map<String, Double> fitnessMap = new HashMap<String, Double>();

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

		double costFitness = evaluateCostFitness(phenotype, requirementIds);
		fitnessMap.put("costFitness", costFitness);

		double valueFitness = evaluateValueFitness(requirementIds);
		fitnessMap.put("valueFitness", valueFitness);

		double faultCoverageFitness = evaluateFaultCoverage(requirementIds);
		fitnessMap.put("faultCoverageFitness", faultCoverageFitness);

		return fitnessMap;
	}

	/**
	 * Evaluate the cost fitness.
	 * 
	 * @param phenotype
	 * @param requirementIds
	 * @return
	 */
	private int evaluateCostFitness(String phenotype, List<Integer> requirementIds) {
		int fitness = 0;

		for (Customer customer : dataFile.getCustomers()) {
			int customerCost = findCostsForCustomer(customer, requirementIds);
			fitness += customerCost;
		}

		if (fitness == 0)
			System.out.println("Cost for all customers was 0!");
		
		int budget = dataFile.getBudget();
		if (fitness > budget)
			System.out.println("Went over budget...\nBudget - " + budget + "; Total cost - " + fitness);

		return fitness;
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
	private int findCostsForCustomer(Customer customer, List<Integer> requirementIds) {
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
			// System.out.println("Original population of " + size + " is greater than or equal to requirements total.
			// Using the max amount of requirements: "
			// + dataFileMap.size());
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

	/**
	 * Evaluate the fitness of the value of all the requirements of a customer, based on the weight that those
	 * requirements are for the customer.
	 * 
	 * @param requirementIds
	 *            The list of requirement ids that are used.
	 * @return The value fitness.
	 */
	private double evaluateValueFitness(List<Integer> requirementIds) {
		double fitness = 0;

		double weight = 0;
		int count = 0;
		for (Customer customer : dataFile.getCustomers()) {
			boolean counted = false;
			for (Integer id : requirementIds) {
				Requirement requirement = customer.getRequirementById(id);
				if (requirement != null) {
					weight += requirement.getWeight();
					if (!counted) {
						count++;
						counted = true;
					}
				}
			}
		}

		if (weight == 0 && count == 0) {
			System.out.println("Value was 0!");
			return 0;
		} else {
			fitness = weight / count;
			return fitness;
		}
	}

	/**
	 * Evaluate the fitness for the coverage of faults.
	 * 
	 * @param requirementIds
	 *            The requirement ids that are used.
	 * @return The fitness for fault coverage.
	 */
	private double evaluateFaultCoverage(List<Integer> requirementIds) {
		double fitness = requirementIds.size();
		if (fitness == 0)
			System.out.println("Fault coverage was 0!");
		return fitness;
	}

}
