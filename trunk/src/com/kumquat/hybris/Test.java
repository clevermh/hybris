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
import java.util.HashMap;
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
			UPCObject upc_object = getItemFromBarcode(code);
            if(upc_object != null) {
            	setTextViewText(R.id.item, "Item: " + upc_object.getDescription());
            	setTextViewText(R.id.other, "Amt: " + upc_object.getAmount() + "\nType: " + upc_object.getProductType());
            }
		}
	};
	
	private OnClickListener aboutClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			toaster("Dumping DB to file").show();
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			
			String sql_statement = "SELECT * FROM upctable";
	        Cursor c = db.rawQuery(sql_statement, null);
			
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
					//File wrt = new File(esd, "Android/data/com.kumquat.hybris/files/upcdb_dump.txt");
					File wrt = new File(esd, "upcdb_dump.txt");
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
	
	private UPCObject getItemFromBarcode(String code) {
		UPCObject upc_object = getItemByDatabase(code);
		setTextViewText(R.id.error, "In DB :D");
		if(upc_object != null) { return upc_object; }
		
		setTextViewText(R.id.error, "Not in DB :(");
		upc_object = getItemByInternet(code);
		if(upc_object != null) {
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put("upc_code", upc_object.getUPCCode());
			cv.put("upc_e", upc_object.getUPCECode());
			cv.put("ean_code", upc_object.getEANCode());
			cv.put("description", upc_object.getDescription());
			cv.put("product_type", upc_object.getProductType());
			cv.put("amount", upc_object.getAmount());
			cv.put("sub_type", upc_object.getSubType());
			cv.put("specific_type", upc_object.getSpecificType());
			
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
			
			return upc_object;
		}
		
		return null;
	}
	
	private UPCObject getItemByDatabase(String code) {
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String sql_statement = "SELECT upc_code, upc_e, ean_code, description, amount FROM upctable " +
								"WHERE upc_code = ?";
		Cursor c = db.rawQuery(sql_statement, new String[]{code});
		
		if(c == null || c.getCount() == 0) { if(c != null) { c.close(); } return null; }
		
		HashMap <String, String> upc_info = new HashMap<String, String>();
		c.moveToFirst();
		upc_info.put("upc_code", c.getString(0));
		upc_info.put("upc_e", c.getString(1));
		upc_info.put("ean_code", c.getString(2));
		upc_info.put("description", c.getString(3));
		upc_info.put("amount", c.getString(4));
		
		UPCObject upc_object = new UPCObject(upc_info.get("upc_code"));
        upc_object.addUPCInformation(upc_info);
		
		c.close();
		db.close();
		
		return upc_object;
	}
	
	private String[] splitHtmlPage(String page) {
		String[] info = page.split("[<>]+");

        int counter = 0;
        String[] new_info = new String[info.length];
        for (int i = 0; i < info.length; i++) {
            String val = info[i].toLowerCase();
            if (val.compareTo("td") == 0 || val.compareTo("/td") == 0 || val.compareTo("tr") == 0 || val.compareTo("/tr") == 0 
                || val.indexOf("td width") != -1 || val.compareTo("h2") == 0 || val.compareTo("/h2") == 0 
                || val.indexOf("script type") != -1 || val.compareTo("/script") == 0 || val.indexOf("table class") != -1) {
                continue;
            } else {
                new_info[counter] = info[i];
                counter += 1;
            }
        }
        
        return new_info;
	}
	
	private HashMap <String, String> populateUPCObject(String[] new_info) {
		HashMap <String, String> upc_info = new HashMap<String, String>();
        int begin_index = 0;
        for (int i = 0; i < new_info.length; i++) {
            String val = new_info[i].toLowerCase();

            if (val.compareTo("item record") == 0) {
                begin_index = i;
                break;
            }
        }

        for (int i = begin_index; i < new_info.length; i++) {
            String val = new_info[i].toLowerCase();

            if(val.indexOf("var defaultupc =") != -1) {
                String[] string_info = new_info[i].split("[\\D]+");
                upc_info.put("upc_code", string_info[string_info.length-1]);
            } else if (val.indexOf("upc-e") != -1) {
                i += 1;
                String[] string_info = new_info[i].split("[\\D]+");
                upc_info.put("upc_e", string_info[string_info.length-1]);
            } else if (val.indexOf("upc-a") != -1) {
                i += 1;
                String[] string_info = new_info[i].split("[\\D]+");
                upc_info.put("upc_code", string_info[string_info.length-1]);
            } else if (val.indexOf("ean/ucc-13") != -1) {
                i += 1;
                String[] string_info = new_info[i].split("[\\D]+");
                upc_info.put("ean_code", string_info[string_info.length-1]);
            } else if (val.indexOf("description") != -1) {
                i += 1;
                upc_info.put("description", new_info[i]);
            } else if (val.indexOf("size/weight") != -1) {
                i += 1;
                upc_info.put("amount", new_info[i]);
            } else if (val.compareTo("last modified") == 0) {
                break;
            }
        }
        
        return upc_info;
	}
	
	private UPCObject getItemByInternet(String code) {
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
            
            if(page.indexOf("UPC Database: Item Not Found") != -1) { return null; }
           
            String[] new_info = splitHtmlPage(page);
            HashMap <String, String> upc_info = populateUPCObject(new_info);
            UPCObject upc_object = new UPCObject(upc_info.get("upc_code"));
            upc_object.addUPCInformation(upc_info);
 
            return upc_object;
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
	            UPCObject upc_object = getItemFromBarcode(intent.getStringExtra("SCAN_RESULT"));
	            if(upc_object != null) {
	            	setTextViewText(R.id.item, "Item: " + upc_object.getDescription());
	            	setTextViewText(R.id.other, "Amt: " + upc_object.getAmount() + "\nType: " + upc_object.getProductType());
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
        String sql_statement = "SELECT COUNT(*) FROM upctable";
        Cursor c = db.rawQuery(sql_statement, null);
        c.moveToFirst();
        
        //Log.v("error", "size of upctable = " + c.getString(0)); 
		setTextViewText(R.id.error, "Items in DB: " + c.getString(0));
		c.close();
		db.close();
    }
}