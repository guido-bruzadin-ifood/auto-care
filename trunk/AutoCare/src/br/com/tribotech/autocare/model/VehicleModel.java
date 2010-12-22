package br.com.tribotech.autocare.model;

import android.net.Uri;
import android.provider.BaseColumns;
import br.com.tribotech.autocare.providers.VehicleModelProvider;

public class VehicleModel implements BaseColumns {
	
	public static final Uri CONTENT_URI = Uri.parse("content://"+ VehicleModelProvider.AUTHORITY + "/vehicle_model");
	public static final String CONTENT_TYPE = "br.com.tribotech.autocare/vehicle_model";
	
	public static final String COLUMN_ID_MANUFACTURER = "id_marca";
	public static final String COLUMN_MODEL = "modelo";
	

}
