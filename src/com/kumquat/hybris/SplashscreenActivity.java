package com.kumquat.hybris;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.database.sqlite.*;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;
import com.kumquat.hybris.databases.InventoryDatabaseHelper;

public class SplashscreenActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		final ProgressDialog dialog = ProgressDialog.show(SplashscreenActivity.this, "", 
                "Loading. Please wait...", true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		dialog.show();
		// This should go away after 5 seconds
		new Thread(new Runnable() {
			public void run() {
				dialog.show();
				/*try { Thread.sleep(5000); }
				catch(Exception e) { }*/
				HybrisDatabaseHelper hdh = new HybrisDatabaseHelper(getApplicationContext());
				SQLiteDatabase db = hdh.getReadableDatabase();
				Log.d("DBG_OUT", "Splash: Loading items");
				while(hdh.isPopulating()) {
					Log.d("DBG_OUT", "Splash: Poll");
					try { Thread.sleep(100); }
					catch(Exception e) { }
				}
				db.close();
				hdh.close();
				
				InventoryDatabaseHelper idh = new InventoryDatabaseHelper(getApplicationContext());
				SQLiteDatabase db2 = idh.getReadableDatabase();
				try { Thread.sleep(100); }
				catch(Exception e) { }
				db2.close();
				idh.close();
				
				Log.d("DBG_OUT", "Splash: Done loading items");
				
				dialog.cancel();
				finish();
			}
		}).start();
	}
}
