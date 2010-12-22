package br.com.tribotech.autocare.model;

import java.util.Date;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import br.com.tribotech.autocare.providers.MaintenanceProvider;

public class Maintenance implements BaseColumns {
	
	public static final Uri CONTENT_URI = Uri.parse("content://"+ MaintenanceProvider.AUTHORITY + "/maintenance");
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.autocare.maintenance";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.autocare.maintenance";
	
	public static final int PREVENTIVE = 0;
	public static final int CORRECTIVE = 1;
	public static final int PREDICTIVE = 3;
	
	public static final String COLUMN_ID_VEHICLE = "id_veiculo";
	public static final String COLUMN_ID_WORKSHOP = "id_oficina";
	public static final String COLUMN_TYPE = "tipo";
	public static final String COLUMN_ID_PARTSET = "id_conjunto";
	public static final String COLUMN_DESCRIPTION = "descricao";
	public static final String COLUMN_KM_PERIODIC = "km_periodico";
	public static final String COLUMN_KM_REALIZED = "km_realizado";
	public static final String COLUMN_DATE_REALIZED = "data_realizado";
	
	private Integer id;
	private Integer idVehicle;
	private Integer idWorkshop;
	private Integer idPartSet;
	private Integer type;
	private String description;
	private Integer kmPeriodic;
	private Integer kmRealized;
	private Date dateRealized;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = new Integer(id);
	}
	public Integer getIdVehicle() {
		return idVehicle;
	}
	public void setIdVehicle(Integer idVehicle) {
		this.idVehicle = idVehicle;
	}
	public Integer getIdWorkshop() {
		return idWorkshop;
	}
	public void setIdWorkshop(Integer idWorkshop) {
		this.idWorkshop = idWorkshop;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getIdPartSet() {
		return idPartSet;
	}
	public void setIdPartSet(Integer type_part) {
		this.idPartSet = type_part;
	}
	public Integer getKmPeriodic() {
		return kmPeriodic;
	}
	public void setKmPeriodic(Integer kmPeriodic) {
		this.kmPeriodic = kmPeriodic;
	}
	public Integer getKmRealized() {
		return kmRealized;
	}
	public void setKmRealized(Integer kmRealized) {
		this.kmRealized = kmRealized;
	}
	
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		
		values.put(Maintenance.COLUMN_TYPE, type);
		values.put(Maintenance.COLUMN_ID_VEHICLE, idVehicle);
		values.put(Maintenance.COLUMN_ID_WORKSHOP, idWorkshop);
		values.put(Maintenance.COLUMN_ID_PARTSET, idPartSet);
		values.put(Maintenance.COLUMN_DESCRIPTION, description);
		values.put(Maintenance.COLUMN_KM_PERIODIC, kmPeriodic);
		values.put(Maintenance.COLUMN_KM_REALIZED, kmRealized);
		values.put(Maintenance.COLUMN_DATE_REALIZED, dateRealized.getTime());
		
		return values;
	}

}
