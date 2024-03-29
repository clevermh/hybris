package com.kumquat.hybris;

import java.util.ArrayList;
import java.util.HashMap;

public class UnitConverter {

	private static HashMap<String, HashMap<String, Double>> conversions = new HashMap<String, HashMap<String, Double>>();
	private static ArrayList<String> standards = new ArrayList<String>();
	
	/**
	 * Add a quantity metric to the list of standards
	 * @param st The quantity metric to add
	 */
	public static void addStandard(String st) {
		if(!standards.contains(st)) {
			standards.add(st);
		}
	}
	
	/**
	 * Adds a conversion factor to the map. Also adds the reverse conversion. This overrides whatever may already be there.
	 * @param unit1 Unit to convert from
	 * @param unit2 Unit to convert to
	 * @param conversion Conversion factor between the two units
	 */
	public static void addConversion(String unit1, String unit2, double conversion) {
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
		if(unit1.equals(unit2)) { return true; }
		
		if(!conversions.containsKey(unit1)) { return false; }
		if(conversions.get(unit1).containsKey(unit2)) { return true; }
		
		if(standards.contains(unit1) || standards.contains(unit2)) { return false; }
		
		for(String s : standards) {
			if(knownConversion(unit1, s) && knownConversion(s, unit2)) { return true; }
		}
		
		return false;
	}
	
	/**
	 * Gets the conversion factor from unit1 to unit2.
	 * @param unit1 Unit to convert from
	 * @param unit2 Unit to convert to
	 * @return Conversion factor
	 */
	public static double getConversionFactor(String unit1, String unit2) {
		if(unit1.equals(unit2)) { return 1; }
		
		if(conversions.containsKey(unit1)) {
			HashMap<String, Double> cm = conversions.get(unit1);
			if(cm.containsKey(unit2)) { return cm.get(unit2); }
		}
		
		if(standards.contains(unit1) || standards.contains(unit2)) { return 0; }
		
		for(String s : standards) {
			if(knownConversion(unit1, s) && knownConversion(s, unit2)) {
				return conversions.get(unit1).get(s) * conversions.get(s).get(unit2);
			}
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
		if (knownConversion(unit1,unit2)){
			double factor = UnitConverter.getConversionFactor(unit1, unit2);
			return (amount * factor);
		}
		
		// This should no longer be needed but I will leave it in for now
		for (String sUnit: standards){
			if (knownConversion(unit1,sUnit) && knownConversion(sUnit,unit2)){
				double factor1 = UnitConverter.getConversionFactor(unit1, sUnit);
				double factor2 = UnitConverter.getConversionFactor(sUnit, unit2);
				return (amount * factor1 * factor2);
			}
		}
			
		return Double.POSITIVE_INFINITY;
	}
}
