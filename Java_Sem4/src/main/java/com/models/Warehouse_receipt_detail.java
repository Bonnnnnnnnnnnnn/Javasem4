package com.models;

public class Warehouse_receipt_detail {
	private int Id;
	private int Wh_receipt_id;
	private double Wh_price;
	private int Product_id;
	private int Quantity;
	public Warehouse_receipt_detail(int id, int wh_receipt_id, double wh_price, int quantity,int roduct_id) {
		super();
		Id = id;
		Wh_receipt_id = wh_receipt_id;
		Wh_price = wh_price;
		Product_id = roduct_id;
		Quantity = quantity;
	}
	public Warehouse_receipt_detail() {
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getWh_receipt_id() {
		return Wh_receipt_id;
	}
	public void setWh_receipt_id(int wh_receipt_id) {
		Wh_receipt_id = wh_receipt_id;
	}
	public double getWh_price() {
		return Wh_price;
	}
	public void setWh_price(double wh_price) {
		Wh_price = wh_price;
	}
	public int getQuantity() {
		return Quantity;
	}
	public void setQuantity(int quantity) {
		Quantity = quantity;
	}
	public int getProduct_id() {
		return Product_id;
	}
	public void setProduct_id(int product_id) {
		Product_id = product_id;
	}
	
}