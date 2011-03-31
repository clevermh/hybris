package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;
import com.kumquat.hybris.databases.InventoryDatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Inventory {
	private Context context;
	private Ingredients[] ingredients = new Ingredients[0];
	
	public Inventory(Context con) {
		context = con;
		
		loadInventory();
	}
	
	private void loadInventory() {
		InventoryDatabaseHelper idh = new InventoryDatabaseHelper(context);
		SQLiteDatabase db = idh.getReadableDatabase();
		String sql = "SELECT item_id, qty, qty_metric FROM Inventory ORDER BY item_id";
		
		Cursor c = db.rawQuery(sql, null);
		if(c == null) { return; }
		
		if(c.getCount() < 1) { c.close(); return; }
		
		ingredients = new Ingredients[c.getCount()];
		c.moveToFirst();
	
		HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(context);
		SQLiteDatabase odb = hdh.getReadableDatabase();
		
		for(int a = 0; a < c.getCount(); a++) {
			int id = c.getInt(0);
			int qty = c.getInt(1);
			String qtymet = c.getString(2);
			String name = Item.getNameFromID(odb, id);
			ingredients[a] = new Ingredients(name, qty, qtymet, id);
			
			c.moveToNext();
		}
		
		odb.close();
		
		c.close();
		db.close();
	}
	
	public int getCount() {
		if(ingredients == null) { return 0; }
		
		return ingredients.length;
	}
	
	public Ingredients getItem(int which) {
		if(ingredients == null) { return null; }
		
		if(which >= ingredients.length) { return null; }
		
		return ingredients[which];
	}
	
	public boolean addItem(Ingredients ni) {
		InventoryDatabaseHelper idh = new InventoryDatabaseHelper(context);
		SQLiteDatabase db = idh.getWritableDatabase();
		
		for(int a = 0; a < ingredients.length; a++) {
			Ingredients i = ingredients[a];
			if(i.getItemId() == ni.getItemId()) {
				Ingredients addition = new Ingredients(i.getName(), i.getQuantity() + ni.getQuantity(), i.getQuantityMetric(), i.getItemId());
				
				// update the DB
				String sql = "UPDATE Inventory SET qty = " + addition.getQuantity() + " WHERE item_id = " + addition.getItemId();
				int res;
				Cursor c = db.rawQuery(sql, null);
				if(c == null || c.getCount() < 1) { res = 0; }
				else { res = c.getInt(0); }
				
				if(c != null) { c.close(); }
				
				db.close();
				
				Log.d("DBG_OUT", "Item already exists, updating (" + res + ")");
				
				if(res == 0) {
					ingredients[a] = addition;
					return true;
				} else { return false; }
			}
		}
		
		Log.d("DBG_OUT", "Item does not exist, adding");
		ContentValues cv = new ContentValues();
		cv.put("item_id", ni.getItemId());
		cv.put("qty", ni.getQuantity());
		cv.put("qty_metric", ni.getQuantityMetric());
		long res = db.insertOrThrow("Inventory", null, cv);
		db.close();
		
		if(res > -1) {
			//Ingredients[] newlist = new Ingredients[ingredients.length + 1];
			Ingredients[] old = ingredients;
			ingredients = new Ingredients[old.length + 1];
			for(int a = 0; a < old.length; a++) { ingredients[a] = old[a]; }
			
			ingredients[ingredients.length - 1]= ni;
			
			return true;
		}

		return false;
	}
}
