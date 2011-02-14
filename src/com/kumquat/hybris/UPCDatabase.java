package com.kumquat.hybris;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.util.Log;

public class UPCDatabase extends ContentProvider {
	public static final int VERSION = 1;
	
	private UPCDatabaseOpenHelper openhelper;
	
	public UPCDatabase(Context c) {
		openhelper = new UPCDatabaseOpenHelper(c);
	}
	
	@Override
	public int delete(Uri uri, String where, String[] args) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues init) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		openhelper = new UPCDatabaseOpenHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] args,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] args) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public class UPCDatabaseOpenHelper extends SQLiteOpenHelper {
	    
		private SQLiteDatabase database;
		private final Context context;
		
		private static final String createDB = "CREATE TABLE upctable (" +
												"upc			varchar(10) primary key" +
												", item			varchar(255)" +
												", amount		int" +
												", amount_type	varchar(255)" +
												");";
		
		public UPCDatabaseOpenHelper(Context context) {
			super(context, "UPCDatabase", null, VERSION);
			// TODO Auto-generated constructor stub
			
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			database = db;
			database.execSQL(createDB);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w("UPCDatabase", "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS upctable");
	        onCreate(db);
		}

	}
}
