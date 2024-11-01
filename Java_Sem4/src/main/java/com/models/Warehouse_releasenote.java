package com.models;

import java.time.LocalDateTime;


public class Warehouse_releasenote {
	private int Id;
	private String Name;
	private LocalDateTime Date;
	private String Status;
	private int Order_id;
	private int EmployeeId;
	private int Warehoise_Id;
	
	public Warehouse_releasenote(int id, int warehoise_id,  int employeeId, String name, String status, LocalDateTime date, int order_id) {
		super();
		Id = id;
		Name = name;
		EmployeeId = employeeId;
		Warehoise_Id = warehoise_id;
		Date = date;
		Status = status;
		Order_id = order_id;
	}
	public Warehouse_releasenote() {
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public LocalDateTime getDate() {
		return Date;
	}
	public void setDate(LocalDateTime date) {
		Date = date;
	}
	public String getStatusWr() {
		return Status;
	}
	public void setStatusWr(String status) {
		this.Status = status;
	}

	public int getOrder_id() {
		return Order_id;
	}
	public void setOrder_id(int order_id) {
		Order_id = order_id;
	}
	
	public int getEmployee_Id() {
		return EmployeeId;
	}
	public void setEmployee_Id(int employeeid) {
		Order_id = employeeid;
	}
	
	public int getWarehoise_id() {
		return Warehoise_Id;
	}
	
	public void setWarehoise_id(int warehoise_id) {
		this.Warehoise_Id = warehoise_id;
	}
	
    
}
