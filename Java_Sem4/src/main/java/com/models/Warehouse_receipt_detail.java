package com.models;

public class Warehouse_receipt_detail {
	private int Id;
	private int Wh_receipt_id;
	private double Wh_price;
	private int Product_id;
	private int Quantity;
	private double Shipping_fee;
	private String Product_name;
	private String formattedPrice;

	public Warehouse_receipt_detail(int id, int wh_receipt_id, double wh_price, int quantity,double shipping_fee,int product_id) {
		Id = id;
		Wh_receipt_id = wh_receipt_id;
		Product_id = product_id;
		Shipping_fee = shipping_fee;
		Wh_price = wh_price;
		Product_id = product_id;
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
	public String getFormattedPrice() {
		return formattedPrice;
	}
	public void setFormattedPrice(String formattedPrice) {
		this.formattedPrice = formattedPrice;
	}
	public int getProduct_id() { 
		return Product_id;
	}
	public void setProduct_id(int product_id) {
		Product_id = product_id;
	}

	public String getProduct_name() {
		return Product_name;
	}
	public void setProduct_name(String product_name) {
		Product_name = product_name;
	}
	public double getShipping_fee() {
		return Shipping_fee;
	}
	public void setShipping_fee(double shipping_fee) {
		Shipping_fee = shipping_fee;
	}

}