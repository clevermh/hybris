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
	private Ingredient[] ingredients = new Ingredient[0];
	
	public Inventory(Context con) {
		context = con;
		
		loadInventory();
	}
	
	private void loadInventory() {
		InventoryDatabaseHelper idh = new InventoryDatabaseHelper(context);
		SQLiteDatabase db = idh.getReadableDatabase();
		String sql = "SELECT item_id, qty, qty_metric FROM Inventory ORDER BY item_id";
		
		Cursor c = db.rawQuery(sql, null);
		if(c == null) { db.close(); return; }
		
		if(c.getCount() < 1) { c.close(); db.close(); return; }
		
		ingredients = new Ingredient[c.getCount()];
		c.moveToFirst();
	
		HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(context);
		SQLiteDatabase odb = hdh.getReadableDatabase();
		
		for(int a = 0; a < c.getCount(); a++) {
			int id = c.getInt(0);
			int qty = c.getInt(1);
			String qtymet = c.getString(2);
			String name = Item.getNameFromID(odb, id);
			ingredients[a] = new Ingredient(id, name, qty, qtymet);
			
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
	
	public int[] getAllItemIDs() {
		int[] res = new int[ingredients.length];
		
		for(int a = 0; a < ingredients.length; a++) {
			res[a] = ingredients[a].getItemId();
		}
		
		return res;
	}
	
	public Ingredient getItem(int which) {
		if(ingredients == null) { return null; }
		
		if(which >= ingredients.length) { return null; }
		
		return ingredients[which];
	}
	
	public boolean addItem(Ingredient ni) {
		InventoryDatabaseHelper idh = new InventoryDatabaseHelper(context);
		SQLiteDatabase db = idh.getWritableDatabase();
		
		for(int a = 0; a < ingredients.length; a++) {
			Ingredient i = ingredients[a];
			if(i.getItemId() == ni.getItemId()) {
				Ingredient addition = new Ingredient(i.getItemId(), i.getName(), i.getQuantity() + ni.getQuantity(), i.getQuantityMetric());
				
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
			Ingredient[] old = ingredients;
			ingredients = new Ingredient[old.length + 1];
			for(int a = 0; a < old.length; a++) { ingredients[a] = old[a]; }
			
			ingredients[ingredients.length - 1]= ni;
			
			return true;
		}

		return false;
	}
}
