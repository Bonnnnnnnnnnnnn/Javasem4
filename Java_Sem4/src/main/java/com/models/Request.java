package com.models;

import java.time.LocalDateTime;

public class Request {
	private int Id;
	private String Name;
	private LocalDateTime Date;
	private String StatusRequest;
	private int Employee_Id;
	private int Warehouse_Id;
	
	public Request(int id, int warehouse_id,  int employee_Id, String name, String status, LocalDateTime date) {
		super();
		Id = id;
		Name = name;
		Employee_Id = employee_Id;
		Warehouse_Id = warehouse_id;
		Date = date;
		StatusRequest = status;
	}

	public Request() {}
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

	public String getStatusRequest() {
		return StatusRequest;
	}

	public void setStatusRequest(String status) {
		StatusRequest = status;
	}

	public int getEmployee_Id() {
		return Employee_Id;
	}

	public void setEmployee_Id(int employee_Id) {
		Employee_Id = employee_Id;
	}

	public int getWarehouse_Id() {
		return Warehouse_Id;
	}

	public void setWarehouse_Id(int warehoise_Id) {
		Warehouse_Id = warehoise_Id;
	}
	
	
}
