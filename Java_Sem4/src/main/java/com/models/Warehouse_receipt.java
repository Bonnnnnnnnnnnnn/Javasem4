package com.models;

import java.time.LocalDateTime;
import java.util.List;

public class Warehouse_receipt {
	private int Id;
	private String Name;
	private int Wh_id;
	private String Wh_name;
    private LocalDateTime Date;
	public Warehouse_receipt(int id, String name, int wh_id, LocalDateTime date) {
		super();
		Id = id;
		Name = name;
		Wh_id = wh_id;
		Date = date;
	}
	public Warehouse_receipt() {
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getWh_id() {
		return Wh_id;
	}
	public void setWh_id(int wh_id) {
		Wh_id = wh_id;
	}
	public LocalDateTime getDate() {
		return Date;
	}
	public void setDate(LocalDateTime date) {
		Date = date;
	}
	public String getWh_name() {
		return Wh_name;
	}
	public void setWh_name(String wh_name) {
		Wh_name = wh_name;
	}
	
}
