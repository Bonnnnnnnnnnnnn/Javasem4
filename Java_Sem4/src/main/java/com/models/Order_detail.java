package com.models;

public class Order_detail {
	private int Id;
	private int Stock_id;
	private int Order_id;
	private String Status;
	private double Price;
	private int Product_Id;
	
	
	public Order_detail(int id, int stock_id, int order_id, String status, double price, int product_Id) {
		super();
		Id = id;
		Stock_id = stock_id;
		Order_id = order_id;
		Status = status;
		Price = price;
		Product_Id = product_Id;
	}
	public Order_detail() {}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getStock_id() {
		return Stock_id;
	}
	public void setStock_id(int stock_id) {
		Stock_id = stock_id;
	}
	public int getOrder_id() {
		return Order_id;
	}
	public void setOrder_id(int order_id) {
		Order_id = order_id;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public double getPrice() {
		return Price;
	}
	public void setPrice(double price) {
		Price = price;
	}
	public int getProduct_Id() {
		return Product_Id;
	}
	public void setProduct_Id(int product_Id) {
		Product_Id = product_Id;
	}
	
}
