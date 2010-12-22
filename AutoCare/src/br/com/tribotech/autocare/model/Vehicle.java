package br.com.tribotech.autocare.model;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import br.com.tribotech.autocare.providers.VehicleProvider;

public class Vehicle implements BaseColumns {
	
	public static final Uri CONTENT_URI = Uri.parse("content://"+ VehicleProvider.AUTHORITY + "/vehicle");
	public static final String CONTENT_TYPE = "br.com.tribotech.autocare/vehicle";
	
	public static final int CAR = 0;
	public static final int MOTORCYCLE = 1;
	public static final int TRUCK = 2;
	
	public static final String COLUMN_ID_MANUFACTURER = "id_marca";
	public static final String COLUMN_ID_MODEL = "id_modelo";
	public static final String COLUMN_TYPE = "tipo";
	public static final String COLUMN_YEAR_MAKE = "ano_fabricacao";
	public static final String COLUMN_YEAR_MODEL = "ano_modelo";
	public static final String COLUMN_DESCRIPTION = "descricao";
	
	private Integer id;
	private Integer type;
	private Integer idManufacturer;
	private Integer idModel;
	private Integer yearMake;
	private Integer yearModel;
	private String description;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setId(String id) {
		if(id != null){
			this.id = Integer.parseInt(id);
		}
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getIdManufacturer() {
		return idManufacturer;
	}

	public void setIdManufacturer(Integer idManufacturer) {
		this.idManufacturer = idManufacturer;
	}
	
	public void setIdManufacturer(String id) {
		if(id != null){
			this.idManufacturer = Integer.parseInt(id);
		}
	}

	public Integer getIdModel() {
		return idModel;
	}

	public void setIdModel(Integer idModel) {
		this.idModel = idModel;
	}
	
	public void setIdModel(String id) {
		if(id != null){
			this.idModel = Integer.parseInt(id);
		}
	}

	public Integer getYearMake() {
		return yearMake;
	}

	public void setYearMake(Integer yearMake) {
		this.yearMake = yearMake;
	}

	public Integer getYearModel() {
		return yearModel;
	}

	public void setYearModel(Integer yearModel) {
		this.yearModel = yearModel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(Vehicle.COLUMN_TYPE, type);
		values.put(Vehicle.COLUMN_ID_MANUFACTURER, idManufacturer);
		values.put(Vehicle.COLUMN_ID_MODEL, idModel);
		values.put(Vehicle.COLUMN_YEAR_MAKE, yearMake);
		values.put(Vehicle.COLUMN_YEAR_MODEL, yearModel);
		values.put(Vehicle.COLUMN_DESCRIPTION, description);

		return values;
	}


}
