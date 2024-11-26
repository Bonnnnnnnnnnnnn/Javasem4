package com.models;

import java.time.LocalDate;
import java.util.List;

public class ReturnOrder {
	private int id;
    private int orderId;
    private LocalDate returnDate;
    private String status;
    private String note;
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    
    // Thêm fields để hiển thị
    private String customerName;
    private List<ReturnOrderDetail> returnDetails;
	public ReturnOrder(int id, int orderId, LocalDate returnDate, String status, String note, double totalAmount,
			double discountAmount, double finalAmount, String customerName, List<ReturnOrderDetail> returnDetails) {
		this.id = id;
		this.orderId = orderId;
		this.returnDate = returnDate;
		this.status = status;
		this.note = note;
		this.totalAmount = totalAmount;
		this.discountAmount = discountAmount;
		this.finalAmount = finalAmount;
		this.customerName = customerName;
		this.returnDetails = returnDetails;
	}
	
	public ReturnOrder() {
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public LocalDate getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}
	public double getFinalAmount() {
		return finalAmount;
	}
	public void setFinalAmount(double finalAmount) {
		this.finalAmount = finalAmount;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public List<ReturnOrderDetail> getReturnDetails() {
		return returnDetails;
	}
	public void setReturnDetails(List<ReturnOrderDetail> returnDetails) {
		this.returnDetails = returnDetails;
	}
	
    
}
