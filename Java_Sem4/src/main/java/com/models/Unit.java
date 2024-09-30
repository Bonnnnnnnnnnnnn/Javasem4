package com.models;

public class Unit {
	private int Id;
	private String Name;
	public Unit(int id, String name) {
		super();
		Id = id;
		Name = name;
	}
	public Unit() {
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
