package com.nrp.app.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

/**
 * @author Edward McNealy <edwardmcn64@gmail.com> - Oct 26, 2015
 *
 */
public class DataFile {

	private static double COST_RATIO = 0.4;
	private int currentLine;

	private String fileName;
	private List<String> lines;
	private List<Customer> customers;
	private Map<Integer, Integer> requirements;
	private double budget;

	/**
	 * @param fileName
	 * @param lines
	 */
	@Inject
	public DataFile(String fileName, List<String> lines) {
		this.fileName = fileName;
		this.lines = lines;
		customers = new ArrayList<Customer>();
		requirements = new HashMap<Integer, Integer>();
	}

	/**
	 * Reads the data from the file and parses the data into a usable format.
	 */
	public void parseFile() {
		// First line contains the levels of requirements
		int levelOfRequirements = Integer.valueOf(lines.get(0));
		currentLine = 1; // The next line after finding the level of requirements
		int requirementIndex = 1;
		// Calculate each of the requirements, then multiply the budget by the cost ratio after all requirements have
		// been handled.
		for (int x = 0; x < levelOfRequirements; x++) {
			requirementIndex = calculateRequirements(requirementIndex);
		}
		budget *= COST_RATIO;

		// Number of dependencies (ignore)
		int numberOfDependencies = Integer.valueOf(lines.get(currentLine));
		currentLine += numberOfDependencies + 1;

		// Number of customers
		int numberOfCustomers = Integer.valueOf(lines.get(currentLine));
		for (int x = 0; x < numberOfCustomers; x++) {
			Customer customer = calculateCustomers(x + 1);
			calculateRequirementValue(customer);
			customers.add(customer);
		}
	}

	/**
	 * Calculate the weight of each requirement for a customer.
	 * 
	 * @param customer
	 *            The customer to calculate the weight for.
	 */
	public void calculateRequirementValue(Customer customer) {
		int n = customer.getRequirements().size();
		double lowestValue = 100 / n;

		List<Double> values = new ArrayList<Double>();
		values.add(lowestValue);
		double next = lowestValue;
		double sum = lowestValue;
		for (int x = 1; x < n; x++) {
			next += lowestValue;
			sum += next;
			values.add(next);
		}
		double scale = sum / 100;
		for (int x = 0; x < n; x++) {
			double weight = values.get(x) / scale;
			int index = (n - x) - 1;
			customer.getRequirements().get(index).setWeight(weight);
		}
	}

	public void normalize() {
		int max = 0;
		for (Customer customer : customers) {
			max += customer.getProfit();
		}
		for (Customer customer : customers) {
			double normalized = customer.getProfit() / max;
			System.out.println("Customer[" + customer.getId() + "] noramilized is: " + normalized);
		}
	}

	/**
	 * Find the requirements. The requirements are stored in Map with the key being their id, and the value being the
	 * cost. The budget is also increased with every new requirement added.
	 * 
	 * @param index
	 *            The starting index of the requirement for a level.
	 * @return The ending index of the requirement.
	 */
	private int calculateRequirements(int index) {
		String[] split = lines.get(currentLine + 1).split(" ");
		for (String s : split) {
			int value = Integer.valueOf(s);
			// Add this requirement's value to the budget and assign it to the map with the next index.
			budget += value;
			requirements.put(index++, value);
		}
		// Move the current line up by 2
		currentLine += 2;
		return index;
	}

	/**
	 * Find the customer and their requirements. The first index in the line is the customer profit. The second index is
	 * the number of requirements. Starting from the third index, the requirements are listed.
	 * 
	 * @return
	 */
	private Customer calculateCustomers(int customerId) {
		currentLine++;
		String[] split = lines.get(currentLine).split(" ");
		int profit = Integer.valueOf(split[0]);
		int numberOfRequests = Integer.valueOf(split[1]);
		List<Requirement> requirements = new ArrayList<Requirement>();

		int index = 2; // Start at index 2
		for (int x = 0; x < numberOfRequests; x++) {
			Integer id = Integer.valueOf(split[index++]);
			int cost = this.requirements.get(id);
			Requirement requirement = new Requirement(id, cost);
			requirements.add(requirement);
		}
		return new Customer(customerId, profit, requirements);
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the lines
	 */
	public List<String> getLines() {
		return lines;
	}

	/**
	 * @param lines
	 *            the lines to set
	 */
	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	/**
	 * @return the customers
	 */
	public List<Customer> getCustomers() {
		return customers;
	}

	/**
	 * @return the requirements
	 */
	public Map<Integer, Integer> getRequirements() {
		return requirements;
	}

	/**
	 * @return the budget
	 */
	public double getBudget() {
		return budget;
	}

	@Override
	public String toString() {
		return "Data File: " + fileName;
	}
}
