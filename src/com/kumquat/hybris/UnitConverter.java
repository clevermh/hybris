package com.kumquat.hybris;

import java.util.HashMap;

public class UnitConverter {

	private static HashMap<String, HashMap<String, Double>> conversions;
	
	static {
		conversions = new HashMap<String, HashMap<String, Double>>();
		addConversion("", "", 1);
		addConversion("cup", "cup", 1);
		addConversion("teaspoon", "teaspoon", 1);
		addConversion("tablespoon", "tablespoon", 1);
		addConversion("pound", "pound", 1);
		addConversion("whole", "whole", 1);
		addConversion("pinch", "pinch", 1);
	}
	
	/**
	 * Adds a conversion factor to the map. Also adds the reverse conversion
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
	 * Check whether or not a specific unit is known
	 * @param unit The unit to check
	 * @return True if the unit is know, false otherwise
	 */
	public static boolean knownUnit(String unit) {
		return conversions.containsKey(unit);
	}
	
	/**
	 * Gets the conversion factor from unit1 to unit2
	 * @param unit1 Unit to convert from
	 * @param unit2 Unit to convert to
	 * @return Conversion factor
	 */
	public static double getConversion(String unit1, String unit2) {
		if(conversions.containsKey(unit1)) {
			HashMap<String, Double> cm = conversions.get(unit1);
			if(cm.containsKey(unit2)) { return cm.get(unit2); }
		}
		
		return 0;
	}
}
