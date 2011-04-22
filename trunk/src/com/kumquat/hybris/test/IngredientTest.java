package com.kumquat.hybris.test;

import com.kumquat.hybris.Ingredient;
import junit.framework.TestCase;

public class IngredientTest extends TestCase {
	public void testGetName() {
		Ingredient ing = new Ingredient(1, "Tomatoe", 1, "whole");
		assert(ing.getName().equals("Tomatoe"));
	}
	
	public void testGetQuantity() {
		Ingredient ing = new Ingredient(1, "Tomatoe", 1, "whole");
		assert(ing.getQuantity() == 1);
	}
	
	public void testGetQuantityMetric() {
		Ingredient ing = new Ingredient(1, "Tomatoe", 1, "whole");
		assert(ing.getQuantityMetric().equals("whole"));
	}
	
	public void testGetItemId() {
		Ingredient ing = new Ingredient(1, "Tomatoe", 1, "whole");
		assert(ing.getItemId() == 1);
	}
	
}
