package com.kumquat.hybris;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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
import android.os.Environment;
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
			String[] item = getItemFromBarcode(code);
			if(item != null) {
            	setTextViewText(R.id.item, "Item: " + item[0]);
            	setTextViewText(R.id.other, "Amt: " + item[1] + "\nType: " + item[2]);
            }
		}
	};
	
	private OnClickListener aboutClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			toaster("Dumping DB to file").show();
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			Cursor c = db.query("upctable", null, "1 = 1", null, null, null, null);
			
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				String data = "";
				do {
					for(int a = 0; a < c.getColumnCount(); a++) {
						if(a > 0) { data += ", "; }
						data += c.getString(a);
					}
					data += "\n";
				} while(c.moveToNext());
				
				String st = Environment.getExternalStorageState();
				if(Environment.MEDIA_MOUNTED.equals(st)) {
					File esd = Environment.getExternalStorageDirectory();
					File wrt = new File(esd, "Android/data/com.kumquat.hybris/files/upcdb_dump.txt");
					try {
						wrt.mkdirs();
						wrt.createNewFile();
						BufferedWriter bw = new BufferedWriter(new FileWriter(wrt));
						bw.write(data);
						bw.flush();
						bw.close();
					} catch(IOException e) {
						setTextViewText(R.id.error, e.toString());
					}
				}
			}
			
			db.close();
			c.close();
		}
	};
	
	private String[] getItemFromBarcode(String code) {
		String[] res = getItemByDatabase(code);
		setTextViewText(R.id.error, "In DB :D");
		if(res != null) { return res; }
		
		setTextViewText(R.id.error, "Not in DB :(");
		res = getItemByInternet(code);
		if(res != null) {
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put("upc", code);
			cv.put("item", res[0]);
			cv.put("amount", res[1]);
			cv.put("amount_type", res[2]);
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
	
	private String[] getItemByDatabase(String code) {
		String[] res = new String[3];
		
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor c = db.query("upctable", new String[]{"item", "amount", "amount_type"}, "upc = \"" + code + "\"", null, null, null, null);
		
		if(c == null || c.getCount() == 0) { return null; }
		
		c.moveToFirst();
		res[0] = c.getString(0);
		res[1] = c.getString(1);
		res[2] = c.getString(2);
		
		c.close();
		db.close();
		
		return res;
	}
	
	private String[] getItemByInternet(String code) {
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
				
				String[] res = new String[3];
				res[0] = page.substring(st, en);
				
				st = page.indexOf("<td></td>", en + 2);
				st += 13;
				en = page.indexOf("</td>", st);
				String tmp = page.substring(st, en);
				
				String[] tmp2 = tmp.split("\\s", 2);
				if(tmp2.length == 2) {
					res[1] = tmp2[0];
					res[2] = tmp2[1];
				} else {
					res[1] = res[2] = "";
				}
				
				return res;
			}
		} catch (MalformedURLException e) {
			setTextViewText(R.id.error, e.toString());
			toaster("MalformedURLException").show();
		} catch (IOException e) {
			setTextViewText(R.id.error, e.toString());
			toaster("IOException").show();
		}
		
		return null;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            setTextViewText(R.id.code, "Code: " + intent.getStringExtra("SCAN_RESULT"));
	            String[] item = getItemFromBarcode(intent.getStringExtra("SCAN_RESULT"));
	            if(item != null) {
	            	setTextViewText(R.id.item, "Item: " + item[0]);
	            	setTextViewText(R.id.other, "Amt: " + item[1] + "\nType: " + item[2]);
	            }
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
		Cursor c = db.query("upctable", new String[]{"upc"}, "1 = 1", null, null, null, null);
		
		setTextViewText(R.id.error, "Items in DB: " + c.getCount());
		
		c.close();
		db.close();
    }
}