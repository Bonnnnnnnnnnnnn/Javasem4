package com.models;

public class Stock {
	private int Id;
	private int Product_id;
	private int Quantity;
	private int Wh_rc_dt_id;
	public Stock(int id, int product_id, int quantity, int wh_rc_dt_id) {
		super();
		Id = id;
		Product_id = product_id;
		Quantity = quantity;
		Wh_rc_dt_id = wh_rc_dt_id;
	}
	public Stock() {
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getProduct_id() {
		return Product_id;
	}
	public void setProduct_id(int product_id) {
		Product_id = product_id;
	}
	public int getQuantity() {
		return Quantity;
	}
	public void setQuantity(int quantity) {
		Quantity = quantity;
	}
	public int getWh_rc_dt_id() {
		return Wh_rc_dt_id;
	}
	public void setWh_rc_dt_id(int wh_rc_dt_id) {
		Wh_rc_dt_id = wh_rc_dt_id;
	}
}
