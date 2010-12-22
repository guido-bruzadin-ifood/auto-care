package br.com.tribotech.autocare.providers;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import br.com.tribotech.autocare.helpers.FuelFillDatabaseHelper;
import br.com.tribotech.autocare.model.FuelFill;


public class FuelFillProvider extends ContentProvider {

	public static final String AUTHORITY = "br.com.tribotech.autocare.providers.FuelFillProvider";

	private static final UriMatcher sUriMatcher;

	private static final int FUELFILLS = 1;
    private static final int FUELFILL_ID = 2;   

	private static HashMap<String, String> fuelFillProjectionMap;
	
	private SQLiteDatabase db;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, FuelFillDatabaseHelper.TABLE_NAME, FUELFILLS);

		fuelFillProjectionMap = new HashMap<String, String>();
		fuelFillProjectionMap.put(FuelFill._ID, FuelFill._ID);
		fuelFillProjectionMap.put(FuelFill.COLUMN_ID_VEHICLE, FuelFill.COLUMN_ID_VEHICLE);
		fuelFillProjectionMap.put(FuelFill.COLUMN_FUEL_TYPE, FuelFill.COLUMN_FUEL_TYPE);
		fuelFillProjectionMap.put(FuelFill.COLUMN_FUEL_QTD, FuelFill.COLUMN_FUEL_QTD);
		fuelFillProjectionMap.put(FuelFill.COLUMN_FUEL_PRICE, FuelFill.COLUMN_FUEL_PRICE);
		fuelFillProjectionMap.put(FuelFill.COLUMN_ODOMETER, FuelFill.COLUMN_ODOMETER);
		fuelFillProjectionMap.put(FuelFill.COLUMN_DATE, FuelFill.COLUMN_DATE);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count=0;
		switch (sUriMatcher.match(uri)){
			case FUELFILLS:
				count = db.delete(
						FuelFillDatabaseHelper.TABLE_NAME,
						selection,
						selectionArgs);
				break;
			case FUELFILL_ID:
				String id = uri.getPathSegments().get(1);
				count = db.delete(
						FuelFillDatabaseHelper.TABLE_NAME,
						FUELFILL_ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
						selectionArgs);
				break;
			default: throw new IllegalArgumentException("Unknown URI " + uri);    
		}       
		getContext().getContentResolver().notifyChange(uri, null);
		return count;      

	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)){
	        //---get all---
	        case FUELFILLS:
	           return "vnd.android.cursor.dir/vnd.autocare.FuelFill";
	        //---get a particular---
	        case FUELFILL_ID:                
	           return "vnd.android.cursor.item/vnd.autocare.FuelFill";
	        default:
	           throw new IllegalArgumentException("Unsupported URI: " + uri);        
		}  
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = db.insert(FuelFillDatabaseHelper.TABLE_NAME, "", values);

		//---if added successfully---
		if (rowID > 0){
			Uri _uri = ContentUris.withAppendedId(FuelFill.CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			
			return _uri;                
		}        
		
		throw new SQLException("Failed to insert row into " + uri);

	}
	
	@Override
	public boolean onCreate() {
		FuelFillDatabaseHelper dbHelper = new FuelFillDatabaseHelper(getContext());

		db = dbHelper.getWritableDatabase();
		return (db == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(FuelFillDatabaseHelper.TABLE_NAME);

		if (sUriMatcher.match(uri) == FUELFILL_ID)
			//---if getting a particular book---
			sqlBuilder.appendWhere(FuelFill._ID + " = " + uri.getPathSegments().get(1));                

		if (sortOrder == null || sortOrder == "")
			sortOrder = FuelFill.COLUMN_DATE;

		Cursor c = sqlBuilder.query(db, projection, selection,	selectionArgs, null, null, sortOrder);

		//---register to watch a content URI for changes---
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)){
			case FUELFILLS:
				count = db.update(
						FuelFillDatabaseHelper.TABLE_NAME,
						values,
						selection, 
						selectionArgs);
				break;
			case FUELFILL_ID:                
				count = db.update(
						FuelFillDatabaseHelper.TABLE_NAME, 
						values,
						FuelFill._ID + " = " + uri.getPathSegments().get(1) + 
						(!TextUtils.isEmpty(selection) ? " AND (" + 
								selection + ')' : ""), 
								selectionArgs);
				break;
			default: throw new IllegalArgumentException(
					"Unknown URI " + uri);    
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

}
