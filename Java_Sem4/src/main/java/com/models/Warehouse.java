package com.models;

public class Warehouse {
	private int Id;
	private String Name;
	private String Address;
	private int Wh_type_id;
	private String Type_name;
	public Warehouse(int id, String name, String address, int wh_type_id) {
		super();
		Id = id;
		Name = name;
		Address = address;
		Wh_type_id = wh_type_id;
	}
	public Warehouse() {
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
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public int getWh_type_id() {
		return Wh_type_id;
	}
	public void setWh_type_id(int wh_type_id) {
		Wh_type_id = wh_type_id;
	}
	public String getTypeName() {
		return Type_name;
	}
	public void setTypeName(String type_name) {
		Type_name = type_name;
	}
	
}
