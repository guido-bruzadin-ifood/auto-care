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
import br.com.tribotech.autocare.helpers.VehicleDatabaseHelper;
import br.com.tribotech.autocare.model.VehicleManufacturer;

public class VehicleManufacturerProvider extends ContentProvider {

	public static final String AUTHORITY = "br.com.tribotech.autocare.providers.VehicleManufacturerProvider";

	private static final UriMatcher sUriMatcher;

	private static final int MANUFACTURERS = 1;
    private static final int MANUFACTURER_ID = 2;   

	private static HashMap<String, String> manufacturerProjectionMap;
	
	private SQLiteDatabase db;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, VehicleDatabaseHelper.TABLE_MANUFACTURER, MANUFACTURERS);

		manufacturerProjectionMap = new HashMap<String, String>();
		manufacturerProjectionMap.put(VehicleManufacturer._ID, VehicleManufacturer._ID);
		manufacturerProjectionMap.put(VehicleManufacturer.COLUMN_MANUFACTURER, VehicleManufacturer.COLUMN_MANUFACTURER);
		
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count=0;
		switch (sUriMatcher.match(uri)){
			case MANUFACTURERS:
				count = db.delete(
						VehicleDatabaseHelper.TABLE_MANUFACTURER,
						selection,
						selectionArgs);
				break;
			case MANUFACTURER_ID:
				String id = uri.getPathSegments().get(1);
				count = db.delete(
						VehicleDatabaseHelper.TABLE_MANUFACTURER,
						MANUFACTURER_ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
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
	        case MANUFACTURERS:
	           return "vnd.android.cursor.dir/vnd.autocare.vehiclemanufacturer";
	        //---get a particular---
	        case MANUFACTURER_ID:                
	           return "vnd.android.cursor.item/vnd.autocare.vehiclemanufacturer";
	        default:
	           throw new IllegalArgumentException("Unsupported URI: " + uri);        
		}  
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = db.insert(VehicleDatabaseHelper.TABLE_MANUFACTURER, "", values);

		//---if added successfully---
		if (rowID > 0){
			Uri _uri = ContentUris.withAppendedId(VehicleManufacturer.CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			
			return _uri;                
		}        
		
		throw new SQLException("Failed to insert row into " + uri);

	}
	
	@Override
	public boolean onCreate() {
		VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());

		db = dbHelper.getWritableDatabase();
		return (db == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(VehicleDatabaseHelper.TABLE_MANUFACTURER);

		if (sUriMatcher.match(uri) == MANUFACTURER_ID)
			//---if getting a particular book---
			sqlBuilder.appendWhere(VehicleManufacturer._ID + " = " + uri.getPathSegments().get(1));                

		if (sortOrder == null || sortOrder == "")
			sortOrder = VehicleManufacturer.COLUMN_MANUFACTURER;

		Cursor c = sqlBuilder.query(db, projection, selection,	selectionArgs, null, null, sortOrder);

		//---register to watch a content URI for changes---
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)){
			case MANUFACTURERS:
				count = db.update(
						VehicleDatabaseHelper.TABLE_MANUFACTURER,
						values,
						selection, 
						selectionArgs);
				break;
			case MANUFACTURER_ID:                
				count = db.update(
						VehicleDatabaseHelper.TABLE_MANUFACTURER, 
						values,
						VehicleManufacturer._ID+ " = " + uri.getPathSegments().get(1) + 
						(!TextUtils.isEmpty(selection) ? " AND (" + 
								selection + ')' : ""), 
								selectionArgs);
				break;
			
			default: throw new IllegalArgumentException("Unknown URI " + uri);    
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

}
