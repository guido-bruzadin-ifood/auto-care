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
import br.com.tribotech.autocare.model.Vehicle;


public class VehicleProvider extends ContentProvider {

	public static final String AUTHORITY = "br.com.tribotech.autocare.providers.VehicleProvider";

	private static final UriMatcher sUriMatcher;

	private static final int VEHICLES = 1;
    private static final int VEHICLE_ID = 2;   

	private static HashMap<String, String> vehicleProjectionMap;
	
	private SQLiteDatabase db;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, VehicleDatabaseHelper.TABLE_NAME, VEHICLES);

		vehicleProjectionMap = new HashMap<String, String>();
		vehicleProjectionMap.put(Vehicle._ID, Vehicle._ID);
		vehicleProjectionMap.put(Vehicle.COLUMN_ID_MANUFACTURER, Vehicle.COLUMN_ID_MANUFACTURER);
		vehicleProjectionMap.put(Vehicle.COLUMN_ID_MODEL, Vehicle.COLUMN_ID_MODEL);
		vehicleProjectionMap.put(Vehicle.COLUMN_TYPE, Vehicle.COLUMN_TYPE);
		vehicleProjectionMap.put(Vehicle.COLUMN_YEAR_MAKE, Vehicle.COLUMN_YEAR_MAKE);
		vehicleProjectionMap.put(Vehicle.COLUMN_YEAR_MODEL, Vehicle.COLUMN_YEAR_MODEL);
		vehicleProjectionMap.put(Vehicle.COLUMN_DESCRIPTION, Vehicle.COLUMN_DESCRIPTION);
		
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count=0;
		switch (sUriMatcher.match(uri)){
			case VEHICLES:
				count = db.delete(
						VehicleDatabaseHelper.TABLE_NAME,
						selection,
						selectionArgs);
				break;
			case VEHICLE_ID:
				String id = uri.getPathSegments().get(1);
				count = db.delete(
						VehicleDatabaseHelper.TABLE_NAME,
						VEHICLE_ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
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
	        case VEHICLES:
	           return "vnd.android.cursor.dir/vnd.autocare.vehicle";
	        //---get a particular---
	        case VEHICLE_ID:                
	           return "vnd.android.cursor.item/vnd.autocare.vehicle";
	        default:
	           throw new IllegalArgumentException("Unsupported URI: " + uri);        
		}  
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = db.insert(VehicleDatabaseHelper.TABLE_NAME, "", values);

		//---if added successfully---
		if (rowID > 0){
			Uri _uri = ContentUris.withAppendedId(Vehicle.CONTENT_URI, rowID);
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
		sqlBuilder.setTables(VehicleDatabaseHelper.TABLE_NAME);

		if (sUriMatcher.match(uri) == VEHICLE_ID)
			//---if getting a particular book---
			sqlBuilder.appendWhere(Vehicle._ID + " = " + uri.getPathSegments().get(1));                

		if (sortOrder == null || sortOrder == "")
			sortOrder = Vehicle.COLUMN_DESCRIPTION;

		Cursor c = sqlBuilder.query(db, projection, selection,	selectionArgs, null, null, sortOrder);

		//---register to watch a content URI for changes---
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)){
			case VEHICLES:
				count = db.update(
						VehicleDatabaseHelper.TABLE_NAME,
						values,
						selection, 
						selectionArgs);
				break;
			case VEHICLE_ID:                
				count = db.update(
						VehicleDatabaseHelper.TABLE_NAME, 
						values,
						Vehicle._ID + " = " + uri.getPathSegments().get(1) + 
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
