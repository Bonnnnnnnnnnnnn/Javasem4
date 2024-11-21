package com.models;

import java.time.LocalDateTime;

public class Warehouse_releasenote {
	private int Id;
	private String Name;
	private LocalDateTime Date;
	private int Order_id;
	public Warehouse_releasenote(int id, String name, LocalDateTime date, int order_id) {
		super();
		Id = id;
		Name = name;
		Date = date;
		Order_id = order_id;
	}
	
	public Warehouse_releasenote() {
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
	public LocalDateTime getDate() {
		return Date;
	}
	public void setDate(LocalDateTime date) {
		Date = date;
	}
	public int getOrder_id() {
		return Order_id;
	}
	public void setOrder_id(int order_id) {
		Order_id = order_id;
	}
	
}
