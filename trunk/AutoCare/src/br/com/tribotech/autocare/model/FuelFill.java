package br.com.tribotech.autocare.model;

import java.util.Date;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import br.com.tribotech.autocare.providers.FuelFillProvider;

public class FuelFill implements BaseColumns {
	
	public static final Uri CONTENT_URI = Uri.parse("content://"+ FuelFillProvider.AUTHORITY + "/fuelfill");
	public static final String CONTENT_TYPE = "br.com.tribotech.autocare/fuelfill";
	
	public static Integer ALCOHOL = 0;
	public static Integer GASOLINE = 1;
	public static Integer DIESEL = 2;
	public static Integer GAS = 3;
		
	public static final String COLUMN_ID_VEHICLE = "id_vehicle";
	public static final String COLUMN_FUEL_TYPE = "tipo";
	public static final String COLUMN_FUEL_QTD = "quantidade";
	public static final String COLUMN_FUEL_PRICE = "preco";
	public static final String COLUMN_ODOMETER = "odometro";
	public static final String COLUMN_FULLTANk = "tanque_cheio";
	public static final String COLUMN_NEW_MARK = "nova_marca";
	public static final String COLUMN_DATE = "data";
	
	private Integer id;
	private Integer idVehicle;
	private Integer type;
	private Double quantity;
	private Double price;
	private Double odometer;
	private Boolean fullTank;
	private Boolean newMark;
	private Date date;
	
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
	public Integer getIdVehicle() {
		return idVehicle;
	}
	public void setIdVehicle(Integer idVehicle) {
		this.idVehicle = idVehicle;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getOdometer() {
		return odometer;
	}
	public void setOdometer(Double odometer) {
		this.odometer = odometer;
	}
	public Boolean getFullTank() {
		return fullTank;
	}
	public void setFullTank(Boolean fullTank) {
		this.fullTank = fullTank;
	}
	public Boolean getNewMark() {
		return newMark;
	}
	public void setNewMark(Boolean newMark) {
		this.newMark = newMark;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		
		values.put(FuelFill.COLUMN_ID_VEHICLE, idVehicle);
		values.put(FuelFill.COLUMN_FUEL_TYPE, type);
		values.put(FuelFill.COLUMN_FUEL_QTD, quantity);
		values.put(FuelFill.COLUMN_FUEL_PRICE, price);
		values.put(FuelFill.COLUMN_ODOMETER, odometer);
		values.put(FuelFill.COLUMN_FULLTANk, fullTank);
		values.put(FuelFill.COLUMN_NEW_MARK, newMark);
		values.put(FuelFill.COLUMN_DATE, date.getTime());

		return values;
	}

}
