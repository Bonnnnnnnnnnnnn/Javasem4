package com.models;

public class Product_img {
	private int Id;
	private String Img_url;
	private int Product_id;
	private String Product_name;
	private int Id_main_img;
	public Product_img() {
	}
	public Product_img(int id, String img_url, int product_id, int id_main_img) {
		Id = id;
		Img_url = img_url;
		Product_id = product_id;
		Id_main_img = id_main_img;
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getImg_url() {
		return Img_url;
	}
	public void setImg_url(String img_url) {
		Img_url = img_url;
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
	public int getId_main_img() {
		return Id_main_img;
	}
	public void setId_main_img(int id_main_img) {
		Id_main_img = id_main_img;
	}
	
	
}
