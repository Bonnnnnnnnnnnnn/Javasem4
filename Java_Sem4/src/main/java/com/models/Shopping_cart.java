package com.models;

public class Shopping_cart {
	private int Id;
	private int Customer_id;
	private int Product_id;
	private int Quantity;
	
	private String Product_name;
	private double Price;
	private String Img;

	
	public Shopping_cart(int id, int customer_id, int product_id, int quantity, String product_name, double price,
			String img) {
		super();
		Id = id;
		Customer_id = customer_id;
		Product_id = product_id;
		Quantity = quantity;
		Product_name = product_name;
		Price = price;
		Img = img;
	}
	public Shopping_cart() {
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getCustomer_id() {
		return Customer_id;
	}
	public void setCustomer_id(int customer_id) {
		Customer_id = customer_id;
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
	public String getProduct_name() {
		return Product_name;
	}
	public void setProduct_name(String product_name) {
		Product_name = product_name;
	}
	public double getPrice() {
		return Price;
	}
	public void setPrice(double price) {
		Price = price;
	}
	public String getImg() {
		return Img;
	}
	public void setImg(String img) {
		Img = img;
	}
	
}
