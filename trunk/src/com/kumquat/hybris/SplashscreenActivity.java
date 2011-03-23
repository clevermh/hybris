package com.kumquat.hybris;

import android.app.Activity;
import android.os.Bundle;

public class SplashscreenActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		// This should go away after 5 seconds
		new Thread(new Runnable() {
			public void run() {
				try { Thread.sleep(5000); }
				catch(Exception e) { }
				
				finish();
			}
		}).start();
	}
}
