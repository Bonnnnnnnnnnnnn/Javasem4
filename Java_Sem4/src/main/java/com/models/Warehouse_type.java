package com.models;

public class Warehouse_type {
	private int Id;
	private String Name;
	public Warehouse_type(int id, String name) {
		super();
		Id = id;
		Name = name;
	}
	public Warehouse_type() {
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
}
