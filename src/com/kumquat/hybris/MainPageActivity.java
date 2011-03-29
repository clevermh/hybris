package com.kumquat.hybris;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainPageActivity extends Activity {
	private boolean hasScannerApp;
	static final int DIALOG_ADD = 0;
	static final int DIALOG_DEVICES = 1;
	final CharSequence[] cookingDevices = {"Oven", "Stove","Microwave", "Cheese Machine"};
	boolean[] checkedDevices = new boolean[cookingDevices.length];
	boolean[] checkedDevicesBackup = new boolean[checkedDevices.length];
	
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    switch(id) {
	    case DIALOG_ADD:
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("How would you like to add an item?")
	    	       .setCancelable(false)
	    	       .setPositiveButton("Scan Item", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   toaster("Scan").show();
	    	           }
	    	       })
	    	       .setNegativeButton("Manual Add", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   Intent manualadd = new Intent(getApplicationContext(), ManualAddActivity.class);
	    	        	   startActivity(manualadd);
	    	           }
	    	       });
	    	dialog = builder.create();
	    		
	        break;
	    case DIALOG_DEVICES:
	    	for(int i = 0; i < checkedDevices.length; i++){
	    		checkedDevicesBackup[i] = checkedDevices[i];
	    	}
	    	AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
	    	builder2.setTitle("Select Cooking Devices");
	    	builder2.setMultiChoiceItems(cookingDevices, checkedDevices, new OnMultiChoiceClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which,
						boolean isChecked) {
					
				}

				
	    	});
	    	builder2.setPositiveButton( "OK", new android.content.DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					switch( which ) {
					case DialogInterface.BUTTON_POSITIVE:
						toaster("Ok!").show();
						break;
					}	
				}
	    	});
	    	builder2.setNegativeButton( "Cancel", new android.content.DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					switch( which ) {
					case DialogInterface.BUTTON_NEGATIVE:
						toaster("Cancel!").show();
						for(int i = 0; i < checkedDevices.length; i++){
							checkedDevices[i] = checkedDevicesBackup[i];
				    	}
						break;
					}	
				}
	    	});

	    	dialog = builder2.create();
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
		// Not sure if we need this yet
	}
	
	private Toast toaster(String msg) {
		return Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.front);
		
		if(savedInstanceState != null) {
			if(savedInstanceState.getBoolean("scanchecked")) {
				hasScannerApp = savedInstanceState.getBoolean("hasscanner");
			} else {
				hasScannerApp = isIntentAvailable(this, "com.google.zxing.client.android.SCAN");
			}
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
		
		Button remove = (Button)findViewById(R.id.front_remove);
		remove.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Go to the remove page
				toaster("Remove button").show();
			}
		});
		
		Button recipes = (Button)findViewById(R.id.front_recipes);
		recipes.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Go to the recipes page
				toaster("Recipes button").show();
			}
		});
		
		Button devices = (Button)findViewById(R.id.front_devices);
		devices.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Go to the devices page
				showDialog(DIALOG_DEVICES);
			}
		});
		
		Intent splash = new Intent(this, SplashscreenActivity.class);
		startActivity(splash);
	}
	
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("scanchecked", true);
		outState.putBoolean("hasscanner", hasScannerApp);
	}
	
	public void onPause() {
		super.onPause();
	}
}
