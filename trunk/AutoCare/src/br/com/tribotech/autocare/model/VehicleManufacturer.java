package br.com.tribotech.autocare.model;

import android.net.Uri;
import android.provider.BaseColumns;
import br.com.tribotech.autocare.providers.VehicleManufacturerProvider;

public class VehicleManufacturer implements BaseColumns {
	
	public static final Uri CONTENT_URI = Uri.parse("content://"+ VehicleManufacturerProvider.AUTHORITY + "/vehicle_manufacturer");
	public static final String CONTENT_TYPE = "br.com.tribotech.autocare/vehicle_manufacturer";
		
	public static final String COLUMN_MANUFACTURER = "marca";

}
