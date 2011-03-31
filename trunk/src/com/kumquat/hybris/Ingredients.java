package com.kumquat.hybris;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Ingredients {
	private String name;
	private int quantity;
	private String quantity_metric;
	private int item_id;
	
	public Ingredients(String name, int quantity, String quantityMetric, SQLiteDatabase db) {
		this.name = name;
		this.quantity = quantity;
		this.quantity_metric = quantityMetric;
		this.item_id = Item.findIDFromDatabase(db, name);
	}
	
	public Ingredients(String name, int quantity, String quantityMetric, int item_id) {
		this.name = name;
		this.quantity = quantity;
		this.quantity_metric = quantityMetric;
		this.item_id = item_id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getQuantity() {
		return this.quantity;
	}
	
	public String getQuantityMetric() {
		return this.quantity_metric;
	}
	
	public int getItemId() {
		return this.item_id;
	}
	
}
