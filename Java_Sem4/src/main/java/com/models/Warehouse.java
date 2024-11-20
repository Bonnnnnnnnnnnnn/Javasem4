package com.models;

import java.util.List;

public class Warehouse {
	private int Id;
	private String Name;
	private String Address;
	private int Wh_type_id;
	private String Type_name;
	private String ManagerFirstName;
	private String ManagerLastName;
	private List<Employee> managers;
	private int relatedCount;
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
	public String getManagerFirstName() {
		return ManagerFirstName;
	}
	public void setManagerFirstName(String managerFirstName) {
		ManagerFirstName = managerFirstName;
	}
	public String getManagerLastName() {
		return ManagerLastName;
	}
	public void setManagerLastName(String managerLastName) {
		ManagerLastName = managerLastName;
	}
	public List<Employee> getManagers() {
		return managers;
	}
	public void setManagers(List<Employee> managers) {
		this.managers = managers;
	}
	public int getRelatedCount() {
		return relatedCount;
	}
	public void setRelatedCount(int relatedCount) {
		this.relatedCount = relatedCount;
	}
	
}
