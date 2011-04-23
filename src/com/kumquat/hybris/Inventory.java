package com.kumquat.hybris;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;
import com.kumquat.hybris.databases.InventoryDatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * An object that represents the user's inventory
 */
public class Inventory {
	private Context context;
	private Ingredient[] ingredients = new Ingredient[0];
	
	/**
	 * Make a new inventory object and load the items from a database accessible by the given Context.
	 * @param con the context with which to get the database
	 */
	public Inventory(Context con) {
		context = con;
		
		loadInventory();
	}
	
	private void loadInventory() {
		InventoryDatabaseHelper idh = new InventoryDatabaseHelper(context);
		SQLiteDatabase db = idh.getReadableDatabase();
		String sql = "SELECT item_id, qty, qty_metric FROM Inventory WHERE qty > 0 ORDER BY item_id";
		
		Cursor c = db.rawQuery(sql, null);
		if(c == null) { db.close(); return; }
		
		if(c.getCount() < 1) { c.close(); db.close(); return; }
		
		ingredients = new Ingredient[c.getCount()];
		c.moveToFirst();
	
		HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(context);
		SQLiteDatabase odb = hdh.getReadableDatabase();
		
		for(int a = 0; a < c.getCount(); a++) {
			int id = c.getInt(0);
			double qty = c.getDouble(1);
			String qtymet = c.getString(2);
			String name = Item.findNameFromID(odb, id);
			ingredients[a] = new Ingredient(id, name, qty, qtymet);
			
			c.moveToNext();
		}
		
		odb.close();
		
		c.close();
		db.close();
	}
	
	/**
	 * @return how many items are in the inventory
	 */
	public int getCount() {
		if(ingredients == null) { return 0; }
		
		return ingredients.length;
	}
	
	/**
	 * @return an array containing the ID of every item in the inventory
	 */
	public int[] getAllItemIDs() {
		int[] res = new int[ingredients.length];
		
		for(int a = 0; a < ingredients.length; a++) {
			res[a] = ingredients[a].getItemId();
		}
		
		return res;
	}
	
	/**
	 * @param which which item in the array to return
	 * @return the specified item
	 */
	public Ingredient getItem(int which) {
		if(ingredients == null) { return null; }
		
		if(which >= ingredients.length) { return null; }
		
		return ingredients[which];
	}
	
	/**
	 * Checks whether the inventory contains at least the given ingredient
	 * @param i The ingredient to check
	 * @return True if there is a greater or equal quantity of the given ingredient, false otherwise.
	 */
	public boolean containsAtLeast(Ingredient i) {
		for(int a = 0; a < ingredients.length; a++) {
			if(ingredients[a].getItemId() == i.getItemId()) {
				double amt = UnitConverter.getConvertedAmount(i.getQuantityMetric(), ingredients[a].getQuantityMetric(), i.getQuantity());
				return amt <= ingredients[a].getQuantity();
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the given recipe can be made from this inventory
	 * @param r The recipe to check
	 * @return True if the recipe can be made, false otherwise
	 */
	public boolean canMake(Recipe r) {
		for(int a = 0; a < r.numIngredients(); a++) {
			if(!containsAtLeast(r.getIngredient(a))) { return false; }
		}
		
		return true;
	}
	
	/**
	 * Adds an item to the inventory and the database
	 * @param ni the item to add
	 * @return whether or not the add was successful
	 */
	public boolean updateItem(Ingredient ni) {
		InventoryDatabaseHelper idh = new InventoryDatabaseHelper(context);
		SQLiteDatabase db = idh.getWritableDatabase();
		
		for(int a = 0; a < ingredients.length; a++) {
			Ingredient i = ingredients[a];
			if(i.getItemId() == ni.getItemId()) {
				if(!UnitConverter.knownConversion(ni.getQuantityMetric(), i.getQuantityMetric())) { return false; }
				
				double newqty = i.getQuantity() + UnitConverter.getConvertedAmount(ni.getQuantityMetric(), i.getQuantityMetric(), ni.getQuantity());
				Ingredient addition = new Ingredient(i.getItemId(), i.getName(), Math.max(0, newqty), i.getQuantityMetric());
				
				// update the DB
				String sql = "UPDATE Inventory SET qty = " + addition.getQuantity() + " WHERE item_id = " + addition.getItemId();
				int res;
				Cursor c = db.rawQuery(sql, null);
				if(c == null || c.getCount() < 1) { res = 0; }
				else { res = c.getInt(0); }
				
				if(c != null) { c.close(); }
				
				db.close();
				
				if(res == 0) {
					if(addition.getQuantity() == 0) {
						// This should be removed from the inventory so it doesn't show up
						Ingredient[] old = ingredients;
						ingredients = new Ingredient[old.length - 1];
						for(int b = 0; b < old.length; b++) {
							if(b == a) { continue; }
							else if(b < a) { ingredients[b] = old[b]; }
							else { ingredients[b - 1] = old[b]; }
						}
					} else {
						ingredients[a] = addition;
					}
					
					return true;
				} else { return false; }
			}
		}
		
		ContentValues cv = new ContentValues();
		cv.put("item_id", ni.getItemId());
		cv.put("qty", ni.getQuantity());
		cv.put("qty_metric", ni.getQuantityMetric());
		long res = db.insertOrThrow("Inventory", null, cv);
		db.close();
		
		if(res > -1) {
			Ingredient[] old = ingredients;
			ingredients = new Ingredient[old.length + 1];
			for(int a = 0; a < old.length; a++) { ingredients[a] = old[a]; }
			
			ingredients[ingredients.length - 1]= ni;
			
			return true;
		}

		return false;
	}
}
