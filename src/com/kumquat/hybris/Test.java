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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
	private UPCDatabaseHelper dbhelper;
	
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
	
	private OnClickListener aboutClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			toaster("Credits yo").show();
			setContentView(R.layout.credits);
		}
	};
	
	private String getItemFromBarcode(String code) {
		String res = getItemByDatabase(code);
		setTextViewText(R.id.error, "In DB :D");
		if(res != null) { return res; }
		
		setTextViewText(R.id.error, "Not in DB :(");
		
		toaster("Not in DB").show();
		res = getItemByInternet(code);
		if(res != null) {
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put("upc", code);
			cv.put("item", res);
			try {
				long ret = db.insertOrThrow("upctable", null, cv);
				
				if(ret == -1) {
					toaster("Error adding " + code).show();
					Log.e("DB", "Error adding new item");
					setTextViewText(R.id.error, "Error adding item");
				}
			} catch (SQLException e) {
				setTextViewText(R.id.error, e.toString());
			}
			db.close();
			
			return res;
		}
		
		return null;
	}
	
	private String getItemByDatabase(String code) {
		String res = null;
		
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor c = db.query("upctable", new String[]{"item"}, "upc = \"" + code + "\"", null, null, null, null);
		
		if(c == null || c.getCount() == 0) { return null; }
		
		c.moveToFirst();
		res = c.getString(0);
		
		db.close();
		c.close();
		
		return res;
	}
	
	private String getItemByInternet(String code) {
		String res = null;
		
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
			st = page.indexOf("<td>Description");
			if(st != -1) {
				st += 15 + 18;
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
	            setTextViewText(R.id.code, "Code: " + intent.getStringExtra("SCAN_RESULT"));
	            setTextViewText(R.id.type, "Type: " + intent.getStringExtra("SCAN_RESULT_FORMAT"));
	            setTextViewText(R.id.item, "Item: " + getItemFromBarcode(intent.getStringExtra("SCAN_RESULT")));
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
        
        Button ab = (Button)findViewById(R.id.creds);
        ab.setOnClickListener(aboutClick);
        
        scanAvailable = isIntentAvailable(this, "com.google.zxing.client.android.SCAN");
        String bsavail = scanAvailable ? "Barcode scanner installed" : "Barcode scanner not installed";
        toaster(bsavail).show();
        
        if(!scanAvailable) { sc.setEnabled(false); }
        
        dbhelper = new UPCDatabaseHelper(getApplicationContext());
        
        SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor c = db.query("upctable", new String[]{"upc", "item"}, "1 = 1", null, null, null, null);
		
		if(c != null && c.getCount() > 0) {
			c.moveToFirst();
			String res = "";
			do {
				res += "{ " + c.getString(0) + ", " + c.getString(1) + " }\n";
			} while(c.moveToNext());
			
			setTextViewText(R.id.error, res);
		} else {
			setTextViewText(R.id.error, "DB is empty");
		}
		
		db.close();
		c.close();
    }
}