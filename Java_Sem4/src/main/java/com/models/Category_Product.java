package com.models;

public class Category_Product {
	private int Id;
	private String Name;
	public Category_Product(int id, String name) {
		super();
		Id = id;
		Name = name;
	}
	public Category_Product() {
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