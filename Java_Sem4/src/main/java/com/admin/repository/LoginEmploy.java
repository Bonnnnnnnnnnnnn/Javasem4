package com.admin.repository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Employee_mapper;
import com.models.Employee;
import com.utils.Views;

@Repository
public class LoginEmploy {
	@Autowired
    private JdbcTemplate jdbcTemplate;
	

    public Employee login(String uid, String pwd) {
    	 String sql = "SELECT * FROM " + Views.TBL_EMPLOYEE + " WHERE " + Views.COL_EMPLOYEE_PHONE + " = ?";
        
        try {
        	Employee emp = jdbcTemplate.queryForObject(sql, new Employee_mapper(), uid);
        	
        	if (emp != null &&  BCrypt.checkpw(pwd, emp.getPassword())) {
                return emp;
            } else {
                return null;
            }
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
    }
    
    public void updatePassword(Employee emp) {
        String sql = "UPDATE " + Views.TBL_EMPLOYEE + " SET " + Views.COL_EMPLOYEE_PASSWORD + " = ? WHERE " + Views.COL_EMPLOYEE_ID + " = ?";
        jdbcTemplate.update(sql, emp.getPassword(), emp.getId());
    }
}
