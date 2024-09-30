package com.models;

import java.time.LocalDateTime;

public class Customer {
	private int Id;
	private String Uid;
	private LocalDateTime Creation_time;
	private String Phone;
	private LocalDateTime BirthDay;
	private String Password;
	private String Address;
	private String First_name;
	private String Last_name;
	private String Img_cus;
	public Customer(int id, String uid, LocalDateTime creation_time, String phone, LocalDateTime birthDay,
			String password, String address, String first_name, String last_name, String img_cus) {
		super();
		Id = id;
		Uid = uid;
		Creation_time = creation_time;
		Phone = phone;
		BirthDay = birthDay;
		Password = password;
		Address = address;
		First_name = first_name;
		Last_name = last_name;
		Img_cus = img_cus;
	}
	public Customer() {

	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getUid() {
		return Uid;
	}
	public void setUid(String uid) {
		Uid = uid;
	}
	public LocalDateTime getCreation_time() {
		return Creation_time;
	}
	public void setCreation_time(LocalDateTime creation_time) {
		Creation_time = creation_time;
	}
	public String getPhone() {
		return Phone;
	}
	public void setPhone(String phone) {
		Phone = phone;
	}
	public LocalDateTime getBirthDay() {
		return BirthDay;
	}
	public void setBirthDay(LocalDateTime birthDay) {
		BirthDay = birthDay;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
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
	public String getImg_cus() {
		return Img_cus;
	}
	public void setImg_cus(String img_cus) {
		Img_cus = img_cus;
	}
	
}
