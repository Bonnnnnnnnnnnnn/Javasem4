package com.models;

public class Brand {
	private int Id;
	private String Name;
	public Brand(int id, String name) {
		Id = id;
		Name = name;
	}
	public Brand() {}
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
