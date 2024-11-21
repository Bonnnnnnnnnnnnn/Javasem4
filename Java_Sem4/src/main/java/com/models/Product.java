package com.models;

import java.util.List;

public class Product {
	private int Id;
	private String Product_name;
	private int Cate_id;
	private String Category_name;
    private int Brand_id;
    private String Brand_name;    
    private int Unit_id;
    private String Unit_name; 
	private double Price;
	private String Img;
	private String Description;
	private int Warranty_period;
	private String Status;
	private Double averageRating;
    private Integer totalFeedbacks;
	public Product(int id, String product_name, int cate_id, String category_name, int brand_id, String brand_name,
			int unit_id, String unit_name, double price, String img, String description, int warranty_period,
			String status) {
		super();
		Id = id;
		Product_name = product_name;
		Cate_id = cate_id;
		Category_name = category_name;
		Brand_id = brand_id;
		Brand_name = brand_name;
		Unit_id = unit_id;
		Unit_name = unit_name;
		Price = price;
		Img = img;
		Description = description;
		Warranty_period = warranty_period;
		Status = status;
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
	public int getUnit_id() {
		return Unit_id;
	}
	public void setUnit_id(int unit_id) {
		Unit_id = unit_id;
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
	public String getUnitName() {
		return Unit_name;
	}
	public void setUnitName(String unit_name) {
		Unit_name = unit_name;
	}
	public String getCategory_name() {
		return Category_name;
	}
	public void setCategory_name(String category_name) {
		Category_name = category_name;
	}
	public String getBrand_name() {
		return Brand_name;
	}
	public void setBrand_name(String brand_name) {
		Brand_name = brand_name;
	}
	public String getUnit_name() {
		return Unit_name;
	}
	public void setUnit_name(String unit_name) {
		Unit_name = unit_name;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public Double getAverageRating() {
		return averageRating;
	}
	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}
	public Integer getTotalFeedbacks() {
		return totalFeedbacks;
	}
	public void setTotalFeedbacks(Integer totalFeedbacks) {
		this.totalFeedbacks = totalFeedbacks;
	}

	
}