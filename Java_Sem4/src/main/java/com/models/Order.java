package com.models;

public class Order {
	private int Id;
	private int Customer_id;
	private String Status;
	private int Employee_id;
	private int Payment_id;
	public Order(int id, int customer_id, String status, int employee_id, int payment_id) {
		super();
		Id = id;
		Customer_id = customer_id;
		Status = status;
		Employee_id = employee_id;
		Payment_id = payment_id;
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
	
}
