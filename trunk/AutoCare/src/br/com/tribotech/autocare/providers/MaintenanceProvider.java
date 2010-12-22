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
import br.com.tribotech.autocare.helpers.MaintenanceDatabaseHelper;
import br.com.tribotech.autocare.model.Maintenance;

public class MaintenanceProvider extends ContentProvider {

	public static final String AUTHORITY = "br.com.tribotech.autocare.providers.MaintenanceProvider";

	private static final UriMatcher sUriMatcher;

	private static final int MAINTENANCES = 1;
    private static final int MAINTENANCE_ID = 2;   

	private static HashMap<String, String> maintenanceProjectionMap;
	
	private SQLiteDatabase db;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, "maintenance", MAINTENANCES);
		sUriMatcher.addURI(AUTHORITY, "maintenance/#", MAINTENANCE_ID);

		maintenanceProjectionMap = new HashMap<String, String>();
		maintenanceProjectionMap.put(Maintenance._ID, Maintenance._ID);
		maintenanceProjectionMap.put(Maintenance.COLUMN_ID_VEHICLE, Maintenance.COLUMN_ID_VEHICLE);
		maintenanceProjectionMap.put(Maintenance.COLUMN_ID_WORKSHOP, Maintenance.COLUMN_ID_WORKSHOP);
		maintenanceProjectionMap.put(Maintenance.COLUMN_TYPE, Maintenance.COLUMN_TYPE);
		maintenanceProjectionMap.put(Maintenance.COLUMN_ID_PARTSET, Maintenance.COLUMN_ID_PARTSET);
		maintenanceProjectionMap.put(Maintenance.COLUMN_DESCRIPTION, Maintenance.COLUMN_DESCRIPTION);
		maintenanceProjectionMap.put(Maintenance.COLUMN_KM_PERIODIC, Maintenance.COLUMN_KM_PERIODIC);
		maintenanceProjectionMap.put(Maintenance.COLUMN_KM_REALIZED, Maintenance.COLUMN_KM_REALIZED);
		maintenanceProjectionMap.put(Maintenance.COLUMN_DATE_REALIZED, Maintenance.COLUMN_DATE_REALIZED);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count=0;
		
		System.out.println(sUriMatcher.match(uri));
		
		switch (sUriMatcher.match(uri)){
			case MAINTENANCES:
				count = db.delete(MaintenanceDatabaseHelper.TABLE_NAME,	selection, selectionArgs);
				break;
			case MAINTENANCE_ID:
				String id = uri.getPathSegments().get(1);
				count = db.delete(MaintenanceDatabaseHelper.TABLE_NAME,	Maintenance._ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
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
	        case MAINTENANCES:
	           return Maintenance.CONTENT_TYPE;
	        //---get a particular---
	        case MAINTENANCE_ID:                
	           return Maintenance.CONTENT_ITEM_TYPE;
	        default:
	           throw new IllegalArgumentException("Unsupported URI: " + uri);        
		}  
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = db.insert(MaintenanceDatabaseHelper.TABLE_NAME, "", values);

		//---if added successfully---
		if (rowID > 0){
			Uri _uri = ContentUris.withAppendedId(Maintenance.CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			
			return _uri;                
		}        
		
		throw new SQLException("Failed to insert row into " + uri);

	}
	
	@Override
	public boolean onCreate() {
		MaintenanceDatabaseHelper dbHelper = new MaintenanceDatabaseHelper(getContext());

		db = dbHelper.getWritableDatabase();
		return (db == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(MaintenanceDatabaseHelper.TABLE_NAME);

		if (sUriMatcher.match(uri) == MAINTENANCE_ID)
			//---if getting a particular book---
			sqlBuilder.appendWhere(Maintenance._ID + " = " + uri.getPathSegments().get(1));                

		if (sortOrder == null || sortOrder == "")
			sortOrder = Maintenance.COLUMN_TYPE;

		Cursor c = sqlBuilder.query(db, projection, selection,	selectionArgs, null, null, sortOrder);

		//---register to watch a content URI for changes---
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)){
			case MAINTENANCES:
				count = db.update(
						MaintenanceDatabaseHelper.TABLE_NAME,
						values,
						selection, 
						selectionArgs);
				break;
			case MAINTENANCE_ID:                
				count = db.update(
						MaintenanceDatabaseHelper.TABLE_NAME, 
						values,
						Maintenance._ID + " = " + uri.getPathSegments().get(1) + 
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
	
	public int nada(){
		return 0;
	}

}
