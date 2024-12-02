package com.models;

import java.util.List;

public class StockSumByWarehouseId {

    private int productId;
    private String productName;
    private String unitName;
    private int quantity;
    private int price;
    private List<ConversionShow> conversions;

    // Constructor
    public StockSumByWarehouseId(int productId, String productName, String unitName, int quantity, int price) {
        this.productId = productId;
        this.productName = productName;
        this.unitName = unitName;
        this.quantity = quantity;
        this.price = price;
    }
    
    

    public StockSumByWarehouseId() {
		super();
	}



	public List<ConversionShow> getConversions() {
		return conversions;
	}



	public void setConversions(List<ConversionShow> conversions) {
		this.conversions = conversions;
	}


	// Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "StockSumByWarehouseId{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", unitName='" + unitName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
