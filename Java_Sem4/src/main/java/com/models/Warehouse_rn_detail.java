package com.models;

public class Warehouse_rn_detail {
	private int Id;
	private int Wgrn_id;
	private String status;
	private int Stock_id;
	private int Quantity;
	public Warehouse_rn_detail(int id, int wgrn_id, String status, int stock_id, int quantity) {
		super();
		Id = id;
		Wgrn_id = wgrn_id;
		this.status = status;
		Stock_id = stock_id;
		Quantity = quantity;
	}
	public Warehouse_rn_detail() {
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getWgrn_id() {
		return Wgrn_id;
	}
	public void setWgrn_id(int wgrn_id) {
		Wgrn_id = wgrn_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getStock_id() {
		return Stock_id;
	}
	public void setStock_id(int stock_id) {
		Stock_id = stock_id;
	}
	public int getQuantity() {
		return Quantity;
	}
	public void setQuantity(int quantity) {
		Quantity = quantity;
	}
	
}
