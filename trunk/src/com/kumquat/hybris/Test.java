package com.kumquat.hybris;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Test extends Activity {
	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	            packageManager.queryIntentActivities(intent,
	                    PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
	private boolean scanAvailable;
	
	private OnClickListener buttonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Context con = getApplicationContext();
			CharSequence test = "Button pressed!";
			int dur = Toast.LENGTH_SHORT;
			
			Toast tst = Toast.makeText(con, test, dur);
			tst.show();
			
			if(scanAvailable) {
				Toast.makeText(con, "You could be scanning something!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(con, "Sorry you can't scan", Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        
        Button b = (Button)findViewById(R.id.buttongo);
        b.setOnClickListener(buttonClick);
        
        scanAvailable = isIntentAvailable(this, "com.google.zxing.client.android.SCAN");
        CharSequence avail = scanAvailable ? "Barcode scanner installed" : "Barcode scanner not installed";
        Toast toast = Toast.makeText(this, avail, Toast.LENGTH_SHORT);
        toast.show();
    }
}