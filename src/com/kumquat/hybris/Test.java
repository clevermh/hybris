package com.kumquat.hybris;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
	
	private OnClickListener scanClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(scanAvailable) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		        intent.setPackage("com.google.zxing.client.android");
		        intent.putExtra("SCAN_MODE", "UPC_A");
		        startActivityForResult(intent, 0);
			} else {
				toaster("Sorry you can't scan").show();
			}
		}
	};
	// 661195562003
	
	private OnClickListener manualClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			toaster("Manual entry").show();
			
			EditText txt = (EditText)findViewById(R.id.manualentry);
			
			String code = txt.getText().toString();
			setTextViewText(R.id.code, "Code: " + code);
            setTextViewText(R.id.type, "Type: " + "?");
            setTextViewText(R.id.item, "Item: " + getItemFromBarcode(code));
		}
	};
	
	private String getItemFromBarcode(String code) {
		String res = "Who knows";
		
		try {
			URL url = new URL("http://www.upcdatabase.com/item/" + code);
			HttpURLConnection urlconnect = (HttpURLConnection)url.openConnection();
			InputStream in = new BufferedInputStream(urlconnect.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String page = "";
			String line = br.readLine();
			while(line != null) {
				page += line;
				line = br.readLine();
			}
			
			int st, en;
			st = page.indexOf("<td>Description") + 15 + 18;
			if(st != -1) {
				en = page.indexOf("</td>", st);
				
				res = page.substring(st, en);
			}
		} catch (MalformedURLException e) {
			setTextViewText(R.id.error, e.toString());
			toaster("MalformedURLException").show();
		} catch (IOException e) {
			setTextViewText(R.id.error, e.toString());
			toaster("IOException").show();
		}
		
		return res;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	        	Bundle bnd = intent.getExtras();
	        	Set<String> keys = bnd.keySet();
	        	
	        	String str = "";
	        	for(String s : keys) {
	        		str += s + " : " + bnd.getString(s) + "\n";
	        	}
	        	setTextViewText(R.id.error, str);
	        	// Handle successful scan
	            setTextViewText(R.id.code, "Code: " + intent.getStringExtra("SCAN_RESULT"));
	            setTextViewText(R.id.type, "Type: " + intent.getStringExtra("SCAN_RESULT_FORMAT"));
	            setTextViewText(R.id.item, "HI");
	            //setTextViewText(R.id.item, "Item: " + getItemFromBarcode(intent.getStringExtra("SCAN_RESULT")));
	        } else if (resultCode == RESULT_CANCELED) {
	            // Handle cancel
	        	// Do nothing
	        }
	    }
	}
	
	private void setTextViewText(int id, String text) {
		TextView tv = (TextView)findViewById(id);
		if(tv != null) { tv.setText(text); }
	}
	
	private Toast toaster(String msg) {
		Context con = getApplicationContext();
		return Toast.makeText(con, msg, Toast.LENGTH_SHORT);
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        
        Button sc = (Button)findViewById(R.id.scan);
        sc.setOnClickListener(scanClick);
        
        Button ma = (Button)findViewById(R.id.manual);
        ma.setOnClickListener(manualClick);
        
        scanAvailable = isIntentAvailable(this, "com.google.zxing.client.android.SCAN");
        String bsavail = scanAvailable ? "Barcode scanner installed" : "Barcode scanner not installed";
        toaster(bsavail).show();
        
        if(!scanAvailable) { sc.setEnabled(false); }
        
        setTextViewText(R.id.code, "Code: 300054451705");
        setTextViewText(R.id.type, "Type: UPC_A");
        setTextViewText(R.id.item, "Item: " + getItemFromBarcode("300054451705"));
    }
}