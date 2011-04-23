package com.kumquat.hybris;

import android.database.sqlite.SQLiteDatabase;

/**
 * An object that represents an ingredient of a recipe
 */
public class Ingredient {
	private String name;
	private double quantity;
	private String quantity_metric;
	private int item_id;
	
	/**
	 * Creates a new Ingredient with the given name, quantity, and metric. Uses the given database to find the item ID.
	 * @param name name of this ingredient
	 * @param quantity quantity of this ingredient
	 * @param quantityMetric the unit used for the quantity
	 * @param db the database to use to find the item ID
	 */
	public Ingredient(String name, double quantity, String quantityMetric, SQLiteDatabase db) {
		this.name = name;
		this.quantity = quantity;
		this.quantity_metric = quantityMetric;
		this.item_id = Item.findIDFromDatabase(db, name);
	}
	
	/**
	 * Creates a new Ingredient with the given item ID, quantity, and metric. Uses the given database to find the item name.
	 * @param item_id ID of this ingredient
	 * @param quantity quantity of this ingredient
	 * @param quantityMetric the unit used for the quantity
	 * @param db the database to use to find the item name
	 */
	public Ingredient(int item_id, double quantity, String quantityMetric, SQLiteDatabase db) {
		this.name = Item.findNameFromID(db, item_id);
		this.quantity = quantity;
		this.quantity_metric = quantityMetric;
		this.item_id = item_id;
	}
	
	/**
	 * Creates a new Ingredient with the given item ID, name, quantity, and metric.
	 * @param item_id ID of this ingredient
	 * @param name name of this ingredient
	 * @param quantity quantity of this ingredient
	 * @param quantityMetric the unit used for the quantity
	 */
	public Ingredient(int item_id, String name, double quantity, String quantityMetric) {
		this.name = name;
		this.quantity = quantity;
		this.quantity_metric = quantityMetric;
		this.item_id = item_id;
	}
	
	/**
	 * Gets the name of this ingredient
	 * @return the name of this ingredient
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the quantity of this ingredient
	 * @return the quantity of this ingredient
	 */
	public double getQuantity() {
		return this.quantity;
	}
	
	/**
	 * Gets the unit used for the quantity of this ingredient
	 * @return the unit used for the quantity of this ingredient
	 */
	public String getQuantityMetric() {
		return this.quantity_metric;
	}
	
	/**
	 * Gets the item ID for this ingredient
	 * @return the item ID for this ingredient
	 */
	public int getItemId() {
		return this.item_id;
	}
	
	@Override
	public String toString() {
		return this.name + " (" + this.quantity + " " + this.quantity_metric + ")";
	}
}
