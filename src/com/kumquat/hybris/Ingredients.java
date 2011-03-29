package com.kumquat.hybris;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Ingredients {
	private String name;
	private String quantity;
	private String quantity_metric;
	private int item_id;
	
	public Ingredients(String name, String quantity, String quantityMetric, SQLiteDatabase db) {
		this.name = name;
		this.quantity = quantity;
		this.quantity_metric = quantityMetric;
		this.item_id = findItemId(db, name);
	}
	
	public Ingredients(String name, String quantity, String quantityMetric, int item_id) {
		this.name = name;
		this.quantity = quantity;
		this.quantity_metric = quantityMetric;
		this.item_id = item_id;
	}
	
	private int findItemId(SQLiteDatabase db, String name) {
		String sql_statement = "SELECT id FROM Items WHERE specific_type = " + name;
        Cursor c = db.rawQuery(sql_statement, null);
        c.moveToFirst();
        
        int items = c.getInt(0);
        
        c.close();
		db.close();
		
		return items;
	}
	
	
	public String getName() {
		return this.name;
	}
	
	public String getQuantity() {
		return this.quantity;
	}
	
	public String getQuantityMetric() {
		return this.quantity_metric;
	}
	
	public int getItemId() {
		return this.item_id;
	}
	
}
