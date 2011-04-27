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
	 * @param con The context with which to get the database and to be used for modifying the inventory.
	 */
	public Inventory(Context con) {
		context = con;
		
		loadInventory();
	}
	
	/**
	 * Used by the constructor to load the inventory from the database.
	 */
	private void loadInventory() {
		InventoryDatabaseHelper idh = new InventoryDatabaseHelper(context);
		SQLiteDatabase inventory_db = idh.getReadableDatabase();
		String sql = "SELECT item_id, qty, qty_metric FROM Inventory WHERE qty > 0 ORDER BY item_id";
		
		// Query the DB for the inventory info
		Cursor cursor = inventory_db.rawQuery(sql, null);
		
		// If the cursor is null or empty then leave the inventory empty and return
		if(cursor == null) { inventory_db.close(); return; }
		if(cursor.getCount() < 1) { cursor.close(); inventory_db.close(); return; }
		
		ingredients = new Ingredient[cursor.getCount()];
		cursor.moveToFirst();
	
		// This is needed to get the item ID from the other database
		HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(context);
		SQLiteDatabase hybris_db = hdh.getReadableDatabase();
		
		// Add each ingredient to the inventory
		for(int a = 0; a < cursor.getCount(); a++) {
			int item_id = cursor.getInt(0);
			double qty = cursor.getDouble(1);
			String qtymet = cursor.getString(2);
			String name = Item.findNameFromID(hybris_db, item_id);
			ingredients[a] = new Ingredient(item_id, name, qty, qtymet);
			
			cursor.moveToNext();
		}
		
		hybris_db.close();
		cursor.close();
		inventory_db.close();
	}
	
	/**
	 * @return How many ingredients are in the inventory
	 */
	public int getCount() {
		if(ingredients == null) { return 0; }
		
		return ingredients.length;
	}
	
	/**
	 * @return An array containing the ID of every item in the inventory
	 */
	public int[] getAllItemIDs() {
		int[] res = new int[ingredients.length];
		
		for(int a = 0; a < ingredients.length; a++) {
			res[a] = ingredients[a].getItemId();
		}
		
		return res;
	}
	
	/**
	 * @param which The location in the array of the ingredient to return
	 * @return The specified ingredient
	 */
	public Ingredient getItem(int which) {
		if(ingredients == null) { return null; }
		
		if(which >= ingredients.length) { return null; }
		
		return ingredients[which];
	}
	
	/**
	 * Checks whether the inventory contains at least the given quantity of the given ingredient
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
	 * Updates an item in the inventory and the database. If you try removing more than exists, the item will be removed from the database.
	 * @param ni The ingredient to add
	 * @return Whether or not the add was successful
	 */
	public boolean updateItem(Ingredient ni) {
		InventoryDatabaseHelper idh = new InventoryDatabaseHelper(context);
		SQLiteDatabase db = idh.getWritableDatabase();
		
		// Check if this ingredient already exists in the inventory and update it
		for(int a = 0; a < ingredients.length; a++) {
			Ingredient i = ingredients[a];
			if(i.getItemId() == ni.getItemId()) {
				if(!UnitConverter.knownConversion(ni.getQuantityMetric(), i.getQuantityMetric())) { return false; }
				
				double newqty = i.getQuantity() + UnitConverter.getConvertedAmount(ni.getQuantityMetric(), i.getQuantityMetric(), ni.getQuantity());
				Ingredient addition = new Ingredient(i.getItemId(), i.getName(), Math.max(0, newqty), i.getQuantityMetric());
				
				// update the DB with the new quantity.
				// TODO eventually make a delete statement if the quantity is <= 0
				//		for now it just gets ignored when loading the DB.
				String sql = "UPDATE Inventory SET qty = " + addition.getQuantity() + " WHERE item_id = " + addition.getItemId();
				int res;
				Cursor c = db.rawQuery(sql, null);
				if(c == null || c.getCount() < 1) { res = 0; }
				else { res = c.getInt(0); }
				
				if(c != null) { c.close(); }
				
				db.close();
				
				if(res == 0) {
					if(addition.getQuantity() <= 0) {
						// If the quantity <= 0, remove it from the inventory so it doesn't show up
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
		
		// If we have gotten to this point then the ingredient is not already in the inventory and should be added
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
