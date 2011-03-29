package com.kumquat.hybris;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.database.sqlite.*;

import com.kumquat.hybris.databases.HybrisDatabaseHelper;

public class SplashscreenActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		// This should go away after 5 seconds
		new Thread(new Runnable() {
			public void run() {
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
				
				finish();
			}
		}).start();
	}
}
