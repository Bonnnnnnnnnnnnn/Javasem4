package com.warehouseManager.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Employee_mapper;
import com.models.Employee;
import com.utils.Views;


@Repository
public class inforEmpWarehouse {
	
    @Autowired 
    private JdbcTemplate jdbcTemplate;
     
    public List<Employee> showEmpWarehouse(int employeeId) {
        try {
            // Viết trực tiếp câu truy vấn SQL
            String str_query = "SELECT e.First_name, e.Last_name, e.Phone, " +
                               "w.Id AS WarehouseId, w.Name AS WarehouseName " +
                               "FROM Employee e " +
                               "JOIN employee_warehouse ew ON e.Id = ew.Employee_Id " +
                               "JOIN Warehouse w ON ew.Warehouse_Id = w.Id " +
                               "WHERE ew.Employee_Id = ?";

            System.out.println("Generated Query: " + str_query);

            return jdbcTemplate.query(str_query, new Employee_mapper(), employeeId);
        } catch (Exception e) {
            System.err.println("Error fetching employee warehouse data: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public Employee findId(int employeeId) {
        try {
            String sql = """
            		SELECT e.First_name, e.Last_name, e.Phone
							
					FROM Employee e
					JOIN employee_warehouse ew ON e.Id = ew.Employee_Id
					JOIN Warehouse w ON ew.Warehouse_Id = w.Id
					WHERE ew.Employee_Id = ?;
            		""";

            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Employee emp = new Employee();
                emp.setId(rs.getInt(Views.COL_EMPLOYEE_ID)); 
                emp.setFirst_name(rs.getString(Views.COL_EMPLOYEE_FIRST_NAME));
                emp.setLast_name(rs.getString(Views.COL_EMPLOYEE_LAST_NAME));
                emp.setPhone(rs.getString(Views.COL_EMPLOYEE_PHONE));
                System.out.println("setId" + emp.getId());
                System.out.println("setFirst_name" + emp.getFirst_name());
                System.out.println("setLast_name" + emp.getLast_name());
                System.out.println("setPhone" + emp.getPhone());
                return emp;
            }, employeeId); 
            
        } catch (DataAccessException e) {
            System.err.println("Error fetching employee with ID: " + employeeId + " - " + e.getMessage());
            return null;
        }
    }
    


}
