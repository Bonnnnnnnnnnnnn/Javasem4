package com.admin.repository;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Employee_mapper;
import com.mapper.Role_mapper;
import com.models.Employee;
import com.models.Role;
import com.utils.SecurityUtility;

@Repository
public class EmployeeRepository {
	private final JdbcTemplate empdb;
	
	public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.empdb = jdbcTemplate;
    }
    public List<Employee> findAll() {
        String sql = "SELECT * FROM Employee";
        return empdb.query(sql, new Employee_mapper());
    }
    public List<Role> findAllRole() {
        String sql = "SELECT * FROM Role WHERE id != 0";
        return empdb.query(sql, new Role_mapper());
    }

    public void saveEmp(Employee emp) {
        try {
            String encodedPassword = SecurityUtility.encryptBcrypt(emp.getPassword());
            String sql = "INSERT INTO Employee (First_name, Last_name, Phone, Role_Id, Password) VALUES (?, ?, ?, ?, ?)";
            empdb.update(sql, emp.getFirst_name(), emp.getLast_name(), emp.getPhone(), emp.getRole_id(), encodedPassword);
            Logger.getGlobal().info("employee saved successfully: " + emp.getPhone());

        } catch (DataAccessException e) {
            Logger.getGlobal().severe("Error saving employee: " + e.getMessage());
            throw new RuntimeException("Database error while saving employee.", e);
        } catch (Exception e) {
            Logger.getGlobal().severe("Unexpected error: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred.", e);
        }
    }

	@SuppressWarnings("deprecation")
	public String findPassword(String phone) {
	    String sql = "SELECT Password FROM Employee WHERE Phone = ?";
	    try {
	        List<String> passwords = empdb.queryForList(sql, new Object[]{phone}, String.class);
	        if (passwords.size() == 1) {
	            return passwords.get(0);
	        } else {
	            return null;
	        }
	    } catch (Exception e) {
	        System.err.println("Error while fetching password: " + e.getMessage());
	        return null;
	    }
	}
	@SuppressWarnings("deprecation")
	public Integer findPhone(String phone) {
	    String sql = "SELECT Role_Id FROM Employee WHERE Phone = ?";
	    try {
	        List<Integer> userTypes = empdb.queryForList(sql, new Object[]{phone}, Integer.class);
	        if (userTypes.size() == 1) {
	            return userTypes.get(0);
	        } else {
	            return null;
	        }
	    } catch (Exception e) {
	        System.err.println("Error while fetching user type: " + e.getMessage());
	        return null;
	    }
	}

}
