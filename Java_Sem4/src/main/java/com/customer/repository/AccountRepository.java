package com.customer.repository;

import java.sql.Types;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Customer_mapper;
import com.models.Customer;
import com.utils.Views;
import org.mindrot.jbcrypt.BCrypt;

@Repository
public class AccountRepository {
	@Autowired
	JdbcTemplate dbpro;

	public boolean createAccount(Customer cus) {
	    try {
	        // Hash the password using BCrypt
	        String hashedPassword = BCrypt.hashpw(cus.getPassword(), BCrypt.gensalt());

	        // Define the SQL insert statement for the Customer table
	        String sql = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)",
	                Views.TBL_CUSTOMER,
	                Views.COL_CUSTOMER_EMAIL,
	                Views.COL_CUSTOMER_PASSWORD);

	        // Execute the insert statement and get the number of rows affected
	        int rowsAffected = dbpro.update(sql, cus.getEmail(), hashedPassword);

	        // Return true if the insert was successful (i.e., at least one row was affected)
	        return rowsAffected > 0;
	    } catch (DataAccessException e) {
	        // Print the error to the console for debugging
	        System.err.println("Error inserting customer: " + e.getMessage());
	        return false; // Return false if there was an error
	    }
	}
	public boolean isEmailRegistered(String email) {
	    try {
	        // Define the SQL select statement to check for existing email
	        String sql = String.format("SELECT %s FROM %s WHERE %s = ?",
	        		Views.COL_CUSTOMER_EMAIL,
	                Views.TBL_CUSTOMER,
	                Views.COL_CUSTOMER_EMAIL);

	        // Execute the query and retrieve the result
	        List<String> existingEmails = dbpro.queryForList(sql, String.class, email);

	        // If the list is not empty, it means the email is already registered
	        return !existingEmails.isEmpty(); // Return true if email exists, false otherwise
	    } catch (DataAccessException e) {
	        // Print the error to the console for debugging
	        System.err.println("Error checking email registration: " + e.getMessage());
	        return false; // Return false in case of an error
	    }
	}

	
	public Customer login(String email, String password) {
	    try {
	        // Define the SQL query to retrieve the customer by email
	        String sql = String.format("SELECT * FROM %s WHERE %s = ?",
	                Views.TBL_CUSTOMER,
	                Views.COL_CUSTOMER_EMAIL);

	        // Fetch the customer details
	        @SuppressWarnings("deprecation")
			Customer customer = dbpro.queryForObject(sql, new Object[]{email}, new Customer_mapper());

	        // Check if the customer exists and verify the password
	        if (customer != null && BCrypt.checkpw(password, customer.getPassword())) {
	            return customer; // Return the customer if the password matches
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Error logging in: " + e.getMessage());
	    }
	    return null; // Return null if login fails
	}

}
