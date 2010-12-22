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
import br.com.tribotech.autocare.model.VehicleModel;

public class VehicleModelProvider extends ContentProvider {

	public static final String AUTHORITY = "br.com.tribotech.autocare.providers.VehicleModelProvider";

	private static final UriMatcher sUriMatcher;

	private static final int MODELS = 1;
    private static final int MODEL_ID = 2;   

	private static HashMap<String, String> modelProjectionMap;
	
	private SQLiteDatabase db;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, VehicleDatabaseHelper.TABLE_MODEL, MODELS);

		modelProjectionMap = new HashMap<String, String>();
		modelProjectionMap.put(VehicleModel._ID, VehicleModel._ID);
		modelProjectionMap.put(VehicleModel.COLUMN_ID_MANUFACTURER, VehicleModel.COLUMN_ID_MANUFACTURER);
		modelProjectionMap.put(VehicleModel.COLUMN_MODEL, VehicleModel.COLUMN_MODEL);
		
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count=0;
		switch (sUriMatcher.match(uri)){
			case MODELS:
				count = db.delete(
						VehicleDatabaseHelper.TABLE_MODEL,
						selection,
						selectionArgs);
				break;
			case MODEL_ID:
				String id = uri.getPathSegments().get(1);
				count = db.delete(
						VehicleDatabaseHelper.TABLE_MODEL,
						MODEL_ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
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
	        case MODELS:
	           return "vnd.android.cursor.dir/vnd.autocare.vehiclemodel";
	        //---get a particular---
	        case MODEL_ID:                
	           return "vnd.android.cursor.item/vnd.autocare.vehiclemodel";
	        default:
	           throw new IllegalArgumentException("Unsupported URI: " + uri);        
		}  
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = db.insert(VehicleDatabaseHelper.TABLE_MODEL, "", values);

		//---if added successfully---
		if (rowID > 0){
			Uri _uri = ContentUris.withAppendedId(VehicleModel.CONTENT_URI, rowID);
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
		sqlBuilder.setTables(VehicleDatabaseHelper.TABLE_MODEL);

		if (sUriMatcher.match(uri) == MODEL_ID)
			//---if getting a particular book---
			sqlBuilder.appendWhere(VehicleModel._ID + " = " + uri.getPathSegments().get(1));                

		if (sortOrder == null || sortOrder == "")
			sortOrder = VehicleModel.COLUMN_MODEL;

		Cursor c = sqlBuilder.query(db, projection, selection,	selectionArgs, null, null, sortOrder);

		//---register to watch a content URI for changes---
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)){
			case MODELS:
				count = db.update(
						VehicleDatabaseHelper.TABLE_MODEL,
						values,
						selection, 
						selectionArgs);
				break;
			case MODEL_ID:                
				count = db.update(
						VehicleDatabaseHelper.TABLE_MODEL, 
						values,
						VehicleModel._ID + " = " + uri.getPathSegments().get(1) + 
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
