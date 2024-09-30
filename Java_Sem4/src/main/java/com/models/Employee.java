package com.models;

public class Employee {
	private int Id;
	private String First_name;
	private String Last_name;
	private String Phone;
	private String Password;
	private int Role_id;
	public Employee(int id, String first_name, String last_name, String phone, String password,
			int role_id) {
		super();
		Id = id;
		First_name = first_name;
		Last_name = last_name;
		Phone = phone;
		Password = password;
		Role_id = role_id;
	}
	public Employee() {
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getFirst_name() {
		return First_name;
	}
	public void setFirst_name(String first_name) {
		First_name = first_name;
	}
	public String getLast_name() {
		return Last_name;
	}
	public void setLast_name(String last_name) {
		Last_name = last_name;
	}
	public String getPhone() {
		return Phone;
	}
	public void setPhone(String phone) {
		Phone = phone;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public int getRole_id() {
		return Role_id;
	}
	public void setRole_id(int role_id) {
		Role_id = role_id;
	}
	
}
