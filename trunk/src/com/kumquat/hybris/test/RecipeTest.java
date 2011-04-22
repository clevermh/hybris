package com.kumquat.hybris.test;

import com.kumquat.hybris.Ingredient;
import com.kumquat.hybris.Recipe;

import junit.framework.TestCase;

public class RecipeTest extends TestCase {
	private Recipe rec;
	
	public RecipeTest() {
		Ingredient ing[] = new Ingredient[5];
		ing[0] = new Ingredient(1, "Chicken Breast", 2, "pieces");
		ing[1] = new Ingredient(2, "Lemon", 1, "whole");
		ing[2] = new Ingredient(3, "Olive Oil", 2, "tablespoon");
		ing[3] = new Ingredient(4, "Oregano", 1, "pinch");
		ing[4] = new Ingredient(5, "Parsley", 2, "whole");
		
		String dir[] = new String[7];
		dir[0] = new String("Cut lemon in half, and squeeze juice from 1/2 lemon on chicken");
		dir[1] = new String("Season with salt to taste");
		dir[2] = new String("Let sit while you heat oil in a small skillet over medium low heat");
		dir[3] = new String("When oil is hot, put chicken in skillet");
		dir[4] = new String("As you saute chicken, add juice from other 1/2 lemon, pepper to taste, and oregano");
		dir[5] = new String("Saute for 5 to 10 minutes each side, or until juices run clear");
		dir[6] = new String("Serve with parsley for garnish");
		
		String prep_time = "10";
		String cook_time = "15";
		String serving_size = "2";
		String type = "Dinner";
		String name = "Simple Lemon Herb Chicken";
		
		this.rec = new Recipe(name, ing, dir, prep_time, cook_time, serving_size, type);
	}
	
	
	public void testNumIngredients() {
		assert(this.rec.numIngredients() == 5);
	}
	
	public void testNumDirections() {
		assert(this.rec.numDirections() == 7);
	}
	
	public void testGetDirection() {
		assert(this.rec.getDirection(0).equals("Cut lemon in half, and squeeze juice from 1/2 lemon on chicken"));
		assert(this.rec.getDirection(1).equals("Season with salt to taste"));
		assert(this.rec.getDirection(2).equals("Let sit while you heat oil in a small skillet over medium low heat"));
		assert(this.rec.getDirection(3).equals("When oil is hot, put chicken in skillet"));
		assert(this.rec.getDirection(4).equals("As you saute chicken, add juice from other 1/2 lemon, pepper to taste, and oregano"));
		assert(this.rec.getDirection(5).equals("Saute for 5 to 10 minutes each side, or until juices run clear"));
		assert(this.rec.getDirection(6).equals("Serve with parsley for garnish"));
	}
	
	public void testGetDirections() {
		String[] dir = this.rec.getDirections();
		assert(dir[0].equals("Cut lemon in half, and squeeze juice from 1/2 lemon on chicken"));
		assert(dir[1].equals("Season with salt to taste"));
		assert(dir[2].equals("Let sit while you heat oil in a small skillet over medium low heat"));
		assert(dir[3].equals("When oil is hot, put chicken in skillet"));
		assert(dir[4].equals("As you saute chicken, add juice from other 1/2 lemon, pepper to taste, and oregano"));
		assert(dir[5].equals("Saute for 5 to 10 minutes each side, or until juices run clear"));
		assert(dir[6].equals("Serve with parsley for garnish"));
	}
	
	public void testGetPrepTime() {
		assert(this.rec.getPrepTime().equals("10"));
	}
	
	public void testGetCookTime() {
		assert(this.rec.getCookTime().equals("15"));
	}
	
	public void testGetServingSize() {
		assert(this.rec.getServingSize().equals("2"));
	}
	
	public void testGetType() {
		assert(this.rec.getType().equals("Dinner"));
	}
	
	public void testGetName() {
		assert(this.rec.getName().equals("Simple Lemon Herb Chicken"));
	}
	
}
