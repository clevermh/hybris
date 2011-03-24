package com.kumquat.hybris;

public class Recipe {
	private Ingredients[] ingredients;
	private String[] directions;
	String prep_time;
	String cook_time;
	String serving_size;
	String type;
	
	public Recipe(Ingredients[] ingredients, String[] directions, String prep_time, String cook_time, String serving_size, String type) {
		this.ingredients = ingredients;
		this.directions = directions;
		this.prep_time = prep_time;
		this.cook_time = cook_time;
		this.serving_size = serving_size;
		this.type = type;
	}
	
}
