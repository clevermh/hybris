package com.kumquat.hybris;

import java.util.List;

import java.util.Random;
import com.kumquat.hybris.databases.HybrisDatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * An object that handles user input and controls actions on the main page
 * Extends the android Activity class
 */

public class MainPageActivity extends Activity {
	private boolean hasScannerApp;
	static final int DIALOG_ADD = 0;
	static final int DIALOG_DEVICES = 1;
	static final int DIALOG_RECIPE_VIEW = 2;
	
	final CharSequence[] cookingDevices = {"Oven", "Stove", "Microwave", "Cheese Machine"};
	boolean[] checkedDevices = new boolean[cookingDevices.length];
	boolean[] checkedDevicesBackup = new boolean[checkedDevices.length];
	
	/**
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    switch(id) {
	    case DIALOG_ADD:
	    	// This is for if the barcode scanner is installed, the user should
	    	// be given the choice to add things manually
	    	builder.setMessage("How would you like to add an item?")
	    	       .setCancelable(false)
	    	       .setPositiveButton("Scan Bar Code", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	    			       intent.setPackage("com.google.zxing.client.android");
	    			       intent.putExtra("SCAN_MODE", "UPC_A");
	    			       startActivityForResult(intent, 0);
	    	           }
	    	       })
	    	       .setNegativeButton("By Name", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   Intent manualadd = new Intent(getApplicationContext(), ManualAddListActivity.class);
	    	        	   startActivity(manualadd);
	    	           }
	    	       });
	    	dialog = builder.create();
	    		
	        break;
	    case DIALOG_DEVICES:
	    	// This is not used anymore, it was for a canceled feature
	    	builder.setTitle("Select Cooking Devices");
	    	builder.setMultiChoiceItems(cookingDevices, checkedDevices, new OnMultiChoiceClickListener() {
	    		@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					
				}
	    	});
	    	
	    	builder.setPositiveButton( "OK", new android.content.DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					switch( which ) {
					case DialogInterface.BUTTON_POSITIVE:
						break;
					}	
				}
	    	});
	    	
	    	builder.setNegativeButton( "Cancel", new android.content.DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					switch( which ) {
					case DialogInterface.BUTTON_NEGATIVE:
						dialog.cancel();
						break;
					}	
				}
	    	});
	    	
	    	builder.setOnCancelListener(new OnCancelListener() {
	    		@Override
				public void onCancel(DialogInterface arg0) {
	    			for(int i = 0; i < checkedDevices.length; i++){
						checkedDevices[i] = checkedDevicesBackup[i];
			    	}
				}
	    	});
	    	
	    	builder.setCancelable(true);
	    	dialog = builder.create();
	        break;
	    case DIALOG_RECIPE_VIEW:
	    	// When looking at recipes, the user should be given a choice
	    	// as to whether they want to see all recipes or just recipes
	    	// that they can make given the current inventory
	    	builder.setMessage("Look Up Which Recipes?")
	    	       .setCancelable(true)
	    	       .setPositiveButton("All", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   // Go to the recipes page
	    					toaster("All Recipes").show();
	    					
	    					Intent recipeViewer = new Intent(getApplicationContext(), RecipeListActivity.class);
	    					recipeViewer.putExtra("com.kumquat.hybris.devices", checkedDevices);
	    					recipeViewer.putExtra("com.kumquat.hybris.useInventory", false);
	    					startActivity(recipeViewer);
	    	           }
	    	       })
	    	       .setNeutralButton("Using Inventory", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   // Go to the recipes page
	    					toaster("Recipes button: Ones I can make right now").show();
	    					
	    					Intent recipeViewer = new Intent(getApplicationContext(), RecipeListActivity.class);
	    					recipeViewer.putExtra("com.kumquat.hybris.devices", checkedDevices);
	    					recipeViewer.putExtra("com.kumquat.hybris.useInventory", true);
	    					startActivity(recipeViewer);
	    	           }
	    	       });
	    	dialog = builder.create();
	    		
	        break;
	    default:
	        dialog = null;
	    }
	    
	    return dialog;
	}
	
	/**
	 * This code is from the Android Dev website. It checks if a given Intent is available
	 * @param context The Context to check for the Intent
	 * @param action The name of the Intent to check for
	 * @return True if the Intent is available, false otherwise
	 */
	private static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
	/**
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// Code 0 is the barcode scanner (not like we have other codes used)
		if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	        	// Get the scan result
	        	String upc = intent.getStringExtra("SCAN_RESULT");
	        	
	        	HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
	        	SQLiteDatabase db = hdh.getReadableDatabase();
	        	
	        	// Find the Item from the UPC
	        	Item item = Item.findItemFromUPC(db, upc);
	        	
	        	// If the UPC is known, add the item to the inventory
	        	if(item != null) {
	            	Inventory invent = new Inventory(getApplicationContext());
	            	Ingredient ing = new Ingredient(item.getID(), item.getName(), 1, "ton");
	            	
	            	// Try to add the item, notify the user of success or failure
	            	if(invent.updateItem(ing)) {
	            		toaster(item.getName() + " added").show();
	            	} else {
	            		toaster("Error adding " + item.getName()).show();
	            	}
	            } else {
	            	toaster("No item with that barcode found").show();
	            }
	        } else if (resultCode == RESULT_CANCELED) {
	            // Handle cancel
	        	// Do nothing
	        }
	    }
	}
	
	/**
	 * This is a dumb little funtion I wrote to make Toasts
	 * @param msg The message for the Toast
	 * @return A Toast with the given message
	 */
	private Toast toaster(String msg) {
		return Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
	}
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.front);
		
		boolean showsplash = true;
		
		// Load information from the saved state if it exists
		if(savedInstanceState != null) {
			if(savedInstanceState.getBoolean("scanchecked")) {
				hasScannerApp = savedInstanceState.getBoolean("hasscanner");
			} else {
				hasScannerApp = isIntentAvailable(this, "com.google.zxing.client.android.SCAN");
			}
			
			showsplash = !savedInstanceState.getBoolean("splashShown");
		} else {
			hasScannerApp = isIntentAvailable(this, "com.google.zxing.client.android.SCAN");
		}

		// Show the add item dialog if the scanner exists, otherwise just start the ManualAddListActivity
		Button add = (Button)findViewById(R.id.front_add);
		add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (hasScannerApp){
					showDialog(DIALOG_ADD);
				}
				else{
					Intent manualadd = new Intent(getApplicationContext(), ManualAddListActivity.class);
		        	startActivity(manualadd);
				}
				
			}
		});
		
		// Go to the InventoryActivity
		Button inventory = (Button)findViewById(R.id.front_inventory);
		inventory.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent invent = new Intent(getApplicationContext(), InventoryActivity.class);
				startActivity(invent);
			}
		});
		
		// Show the recipe dialog
		Button recipes = (Button)findViewById(R.id.front_recipes);
		recipes.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_RECIPE_VIEW);
			}
		});
		
		// Choose a random recipe and start the RecipeActivity with that recipe
		Button random = (Button)findViewById(R.id.front_random);
		random.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
				SQLiteDatabase db = hdh.getReadableDatabase();
				String[] names = Recipe.getAllRecipeNames(db);
				db.close();
				
				Random r = new Random();
				
				Intent viewer = new Intent(getApplicationContext(), RecipeActivity.class);
				viewer.putExtra("com.kumquat.hybris.rName", names[r.nextInt(names.length)]);
				startActivity(viewer);
			}
		});
		
		// This was the cooking devices thing but that feature was cut
		/*Button devices = (Button)findViewById(R.id.front_devices);
		devices.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Go to the devices page
				for(int i = 0; i < checkedDevices.length; i++){
		    		checkedDevicesBackup[i] = checkedDevices[i];
		    	}
				showDialog(DIALOG_DEVICES);
			}
		});*/
		
		// If we need to, show the splash screen
		if(showsplash) {
			Intent splash = new Intent(this, SplashscreenActivity.class);
			startActivity(splash);
		}
	}
	
	/**
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("scanchecked", true);
		outState.putBoolean("hasscanner", hasScannerApp);
		outState.putBoolean("splashShown", true);
	}
	
	/**
	 * @see android.app.Activity#onPause()
	 */
	public void onPause() {
		super.onPause();
	}
}
