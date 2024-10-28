package com.models;

public class Product {
	private int Id;
	private String Product_name;
	private int Cate_id;
	private String Category_name;
    private int Brand_id;
    private String Brand_name;    
    private int Conversion_id;
	private double Price;
	private String Img;
	private String Status;
	private String Description;
	private int Warranty_period;
	public Product(int id, String product_name, int cate_id, int brand_id, int conversion_id, double price, String img,
			String description, int warranty_period,String status) {
		super();
		Id = id;
		Product_name = product_name;
		Cate_id = cate_id;
		Brand_id = brand_id;
		Conversion_id = conversion_id;
		Price = price;
		Img = img;
		Status = status;
		Description = description;
		Warranty_period = warranty_period;
	}
	public Product() {
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getProduct_name() {
		return Product_name;
	}
	public void setProduct_name(String product_name) {
		Product_name = product_name;
	}
	public int getCate_id() {
		return Cate_id;
	}
	public void setCate_id(int cate_id) {
		Cate_id = cate_id;
	}
	public int getBrand_id() {
		return Brand_id;
	}
	public void setBrand_id(int brand_id) {
		Brand_id = brand_id;
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
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public int getWarranty_period() {
		return Warranty_period;
	}
	public void setWarranty_period(int warranty_period) {
		Warranty_period = warranty_period;
	}
	public String getCategoryName() {
		return Category_name;
	}
	public void setCategoryName(String category_name) {
		Category_name = category_name;
	}
	public String getBrandName() {
		return Brand_name;
	}
	public void setBrandName(String brand_name) {
		Brand_name = brand_name;
	}
	public int getConversion_id() {
		return Conversion_id;
	}
	public void setConversion_id(int conversion_id) {
		Conversion_id = conversion_id;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	
}
