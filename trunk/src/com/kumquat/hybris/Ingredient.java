package com.kumquat.hybris;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Ingredient {
	private String name;
	private double quantity;
	private String quantity_metric;
	private int item_id;
	
	public Ingredient(String name, double quantity, String quantityMetric, SQLiteDatabase db) {
		this.name = name;
		this.quantity = quantity;
		this.quantity_metric = quantityMetric;
		this.item_id = Item.findIDFromDatabase(db, name);
	}
	
	public Ingredient(int item_id, double quantity, String quantityMetric, SQLiteDatabase db) {
		this.name = Item.findNameFromID(db, item_id);
		this.quantity = quantity;
		this.quantity_metric = quantityMetric;
		this.item_id = item_id;
	}
	
	public Ingredient(int item_id, String name, double quantity, String quantityMetric) {
		this.name = name;
		this.quantity = quantity;
		this.quantity_metric = quantityMetric;
		this.item_id = item_id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getQuantity() {
		return this.quantity;
	}
	
	public String getQuantityMetric() {
		return this.quantity_metric;
	}
	
	public int getItemId() {
		return this.item_id;
	}
	
	public void printToDebug() {
		Log.d("DBG_OUT", name + "(" + item_id + ")");
		Log.d("DBG_OUT", quantity + " " + quantity_metric);
	}
}
