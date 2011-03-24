package com.kumquat.hybris;

import com.kumquat.hybris.databases.ItemDatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Ingredients {
	private String name;
	private String quantity;
	private String quantity_metric;
	private Context context;
	private int item_id;
	
	public Ingredients(String name, String quantity, String quantityMetric, Context context) {
		this.name = name;
		this.quantity = quantity;
		this.quantity_metric = quantityMetric;
		this.context = context;
		this.item_id = findItemId(name);
	}
	
	private int findItemId(String name) {
		ItemDatabaseHelper idbhelper = new ItemDatabaseHelper(context.getApplicationContext());
		SQLiteDatabase db = idbhelper.getReadableDatabase();
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
