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

public class MainPageActivity extends Activity {
	private boolean hasScannerApp;
	static final int DIALOG_ADD = 0;
	static final int DIALOG_DEVICES = 1;
	static final int DIALOG_RECIPE_VIEW = 2;
	final CharSequence[] cookingDevices = {"Oven", "Stove","Microwave", "Cheese Machine"};
	boolean[] checkedDevices = new boolean[cookingDevices.length];
	boolean[] checkedDevicesBackup = new boolean[checkedDevices.length];
	Inventory userInventory;
	
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    switch(id) {
	    case DIALOG_ADD:
	    	//AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("How would you like to add an item?")
	    	       .setCancelable(false)
	    	       .setPositiveButton("Barcode Scanner", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	    			       intent.setPackage("com.google.zxing.client.android");
	    			       intent.putExtra("SCAN_MODE", "UPC_A");
	    			       startActivityForResult(intent, 0);
	    	           }
	    	       })
	    	       .setNegativeButton("Manually", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   Intent manualadd = new Intent(getApplicationContext(), ManualAddActivity.class);
	    	        	   startActivity(manualadd);
	    	           }
	    	       });
	    	dialog = builder.create();
	    		
	        break;
	    case DIALOG_DEVICES:
	    	
	    	//AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
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
					// TODO Auto-generated method stub
	    			for(int i = 0; i < checkedDevices.length; i++){
						checkedDevices[i] = checkedDevicesBackup[i];
			    	}
				}
	    	});
	    	
	    	builder.setCancelable(true);
	    	dialog = builder.create();
	        break;
	    case DIALOG_RECIPE_VIEW:
	    	//AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
	
	private static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	        	String upc = intent.getStringExtra("SCAN_RESULT");
	        	
	        	HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
	        	SQLiteDatabase db = hdh.getReadableDatabase();
	        	
	        	Item item = Item.findItemFromUPC(db, upc);
	        	
	        	if(item != null) {
	            	Inventory invent = new Inventory(getApplicationContext());
	            	Ingredient ing = new Ingredient(item.getID(), item.getSpecificType(), 1, " units");
	            	if(invent.addItem(ing)) {
	            		toaster(item.getSpecificType() + " added").show();
	            	} else {
	            		toaster("Error adding " + item.getSpecificType()).show();
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
	
	private Toast toaster(String msg) {
		return Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.front);
		
		boolean showsplash = true;
		
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

		Button add = (Button)findViewById(R.id.front_add);
		add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (hasScannerApp){
					showDialog(DIALOG_ADD);
				}
				else{
					Intent manualadd = new Intent(getApplicationContext(), ManualAddActivity.class);
		        	startActivity(manualadd);
				}
				
			}
		});
		
		Button inventory = (Button)findViewById(R.id.front_inventory);
		inventory.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Go to the remove page
				toaster("Inventory button").show();
				
				Intent invent = new Intent(getApplicationContext(), InventoryActivity.class);
				startActivity(invent);
			}
		});
		
		Button recipes = (Button)findViewById(R.id.front_recipes);
		recipes.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_RECIPE_VIEW);
			}
		});
		
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
		
		Button devices = (Button)findViewById(R.id.front_devices);
		devices.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Go to the devices page
				for(int i = 0; i < checkedDevices.length; i++){
		    		checkedDevicesBackup[i] = checkedDevices[i];
		    	}
				showDialog(DIALOG_DEVICES);
			}
		});
		
		if(showsplash) {
			Intent splash = new Intent(this, SplashscreenActivity.class);
			startActivity(splash);
		}
	}
	
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("scanchecked", true);
		outState.putBoolean("hasscanner", hasScannerApp);
		outState.putBoolean("splashShown", true);
	}
	
	public void onPause() {
		super.onPause();
	}
}
