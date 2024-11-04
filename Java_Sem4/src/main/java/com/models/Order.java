package com.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Order {
	private int Id;
	private int Customer_id;
	private String Cus_Name;
	private String Phone;
	private String Status;
	private String Address;
	private String Pay_status;
	private int Employee_id;
	private int Payment_id;
	private LocalDate Date;
	private int Coupon_id;
	private BigDecimal Discount;
	private BigDecimal TotalAmount;
	private BigDecimal ShippingFee;
	private String PaymentMethod;
	private String Notes;
	private String OrderID;
	
	public Order(int id, int customer_id, String cus_Name, String phone, String status, String address,
			String pay_status, int employee_id, int payment_id, LocalDate date, int coupon_id, BigDecimal discount,
			BigDecimal totalAmount, BigDecimal shippingFee, String paymentMethod, String notes, String orderID) {
		super();
		Id = id;
		Customer_id = customer_id;
		Cus_Name = cus_Name;
		Phone = phone;
		Status = status;
		Address = address;
		Pay_status = pay_status;
		Employee_id = employee_id;
		Payment_id = payment_id;
		Date = date;
		Coupon_id = coupon_id;
		Discount = discount;
		TotalAmount = totalAmount;
		ShippingFee = shippingFee;
		PaymentMethod = paymentMethod;
		Notes = notes;
		OrderID = orderID;
	}
	public Order() {
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
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public int getEmployee_id() {
		return Employee_id;
	}
	public void setEmployee_id(int employee_id) {
		Employee_id = employee_id;
	}
	public int getPayment_id() {
		return Payment_id;
	}
	public void setPayment_id(int payment_id) {
		Payment_id = payment_id;
	}
	public String getCus_Name() {
		return Cus_Name;
	}
	public void setCus_Name(String cus_Name) {
		Cus_Name = cus_Name;
	}
	public String getPhone() {
		return Phone;
	}
	public void setPhone(String phone) {
		Phone = phone;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getPay_status() {
		return Pay_status;
	}
	public void setPay_status(String pay_status) {
		Pay_status = pay_status;
	}
	public LocalDate getDate() {
		return Date;
	}
	public void setDate(LocalDate date) {
		Date = date;
	}
	public int getCoupon_id() {
		return Coupon_id;
	}
	public void setCoupon_id(int coupon_id) {
		Coupon_id = coupon_id;
	}
	public BigDecimal getDiscount() {
		return Discount;
	}
	public void setDiscount(BigDecimal discount) {
		Discount = discount;
	}
	public BigDecimal getTotalAmount() {
		return TotalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		TotalAmount = totalAmount;
	}
	public BigDecimal getShippingFee() {
		return ShippingFee;
	}
	public void setShippingFee(BigDecimal shippingFee) {
		ShippingFee = shippingFee;
	}
	public String getPaymentMethod() {
		return PaymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		PaymentMethod = paymentMethod;
	}
	public String getNotes() {
		return Notes;
	}
	public void setNotes(String notes) {
		Notes = notes;
	}
	public String getOrderID() {
		return OrderID;
	}
	public void setOrderID(String orderID) {
		OrderID = orderID;
	}
	
}
