package com.customer.repository;

import java.util.logging.Logger;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.Customer;
import com.utils.SecurityUtility;

@Repository
public class CustomerRepository {
	private final JdbcTemplate cusdb;
	public CustomerRepository(JdbcTemplate jdbc) {
		this.cusdb = jdbc;
	}
	public void saveCus(Customer cus) {
		try {
			String encodedPassword = SecurityUtility.encryptBcrypt(cus.getPassword());
			String sql = "INSERT INTO Customer (First_name, Last_name, Phone, Uid, Image_cus,Address ,birthofdate,Creation_time ,Password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			cusdb.update(sql,cus.getFirst_name(),cus.getLast_name(),cus.getPhone(),cus.getUid(),cus.getImg_cus(),
					cus.getAddress(),cus.getBirthDay(),cus.getCreation_time(),encodedPassword);
            Logger.getGlobal().info("employee saved successfully: " + cus.getUid());
		} catch (DataAccessException e) {
			 Logger.getGlobal().severe("Error saving customer: " + e.getMessage());
	            throw new RuntimeException("Database error while saving customer.", e);
		}catch (Exception e) {
			 Logger.getGlobal().severe("Unexpected error: " + e.getMessage());
	            throw new RuntimeException("Unexpected error occurred.", e);
		}
	}
}
