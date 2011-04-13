package com.kumquat.hybris;

import java.util.HashMap;

public class UnitConverter {

	private static HashMap<String, HashMap<String, Double>> conversions;
	
	// This is so horrible but we are doing it anyways.
	static {
		conversions = new HashMap<String, HashMap<String, Double>>();
		addConversion("", "", 1);
		addConversion("cup", "cup", 1);
		addConversion("teaspoon", "teaspoon", 1);
		addConversion("tablespoon", "tablespoon", 1);
		addConversion("pound", "pound", 1);
		addConversion("whole", "whole", 1);
		addConversion("pinch", "pinch", 1);
		
		addConversion("gallon", "fluid ounce", 128);
		addConversion("quart", "fluid ounce", 32);
		addConversion("pint", "fluid ounce", 16);
		addConversion("cup", "fluid ounce", 8);
		addConversion("gill", "fluid ounce", 4);
		addConversion("fluid ounce", "tablespoon", 2);
		addConversion("fluid ounce", "teaspoon", 6);
	}
	
	/**
	 * Adds a conversion factor to the map. Also adds the reverse conversion. This overrides whatever may already be there.
	 * @param unit1 Unit to convert from
	 * @param unit2 Unit to convert to
	 * @param conversion Conversion factor
	 */
	private static void addConversion(String unit1, String unit2, double conversion) {
		if(conversions.containsKey(unit1)) {
			HashMap<String, Double> cm = conversions.get(unit1);
			cm.put(unit2, conversion);
		} else {
			HashMap<String, Double> nm = new HashMap<String, Double>();
			nm.put(unit2, conversion);
			conversions.put(unit1, nm);
		}
		
		if(conversions.containsKey(unit2)) {
			HashMap<String, Double> cm = conversions.get(unit2);
			cm.put(unit1, 1 / conversion);
		} else {
			HashMap<String, Double> nm = new HashMap<String, Double>();
			nm.put(unit1, 1 / conversion);
			conversions.put(unit2, nm);
		}
	}
	
	/**
	 * Check whether or not a specific unit is known.
	 * @param unit The unit to check
	 * @return True if the unit is know, false otherwise
	 */
	public static boolean knownUnit(String unit) {
		return conversions.containsKey(unit);
	}
	
	/**
	 * Check whether a specific conversions is known
	 * @param unit1 Unit to convert from
	 * @param unit2 Unit to convert to
	 * @return True if a conversion exists for this, false otherwise
	 */
	public static boolean knownConversion(String unit1, String unit2) {
		if(!conversions.containsKey(unit1)) { return false; }
		return conversions.get(unit1).containsKey(unit2);
	}
	
	/**
	 * Gets the conversion factor from unit1 to unit2.
	 * @param unit1 Unit to convert from
	 * @param unit2 Unit to convert to
	 * @return Conversion factor
	 */
	public static double getConversionFactor(String unit1, String unit2) {
		if(conversions.containsKey(unit1)) {
			HashMap<String, Double> cm = conversions.get(unit1);
			if(cm.containsKey(unit2)) { return cm.get(unit2); }
		}
		
		return 0;
	}
	
	/**
	 * Converts the given amount of unit1 to unit2.
	 * @param unit1 Unit to convert from
	 * @param unit2 Unit to convert to
	 * @param amount The starting amount
	 * @return The converted amount
	 */
	public static double getConvertedAmount(String unit1, String unit2, double amount) {
		
		return 0;
	}
}
