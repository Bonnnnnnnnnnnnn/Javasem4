package com.admin.repository;

import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Employee_mapper;
import com.mapper.Role_mapper;
import com.models.Employee;
import com.models.PageView;
import com.models.Role;
import com.utils.SecurityUtility;
import com.utils.Views;

@Repository
public class EmployeeRepository {
	private final JdbcTemplate empdb;
	
	public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.empdb = jdbcTemplate;
    }
	public List<Employee> findAll(PageView itemPage) {
	    try {
	    	String str_query = String.format(
	    		    "SELECT e.Id, e.First_name, e.Last_name, e.Phone, e.Role_Id, e.Password, r.Name AS role_name " +
	    		    "FROM %s e " +
	    		    "INNER JOIN %s r ON e.%s = r.%s " +
	    		    "WHERE e.%s <> 'admin' " +
	    		    "ORDER BY e.%s DESC",
	    		    Views.TBL_EMPLOYEE, Views.TBL_ROLE,
	    		    Views.COL_EMPLOYEE_ROLE_ID, Views.COL_ROLE_ID,
	    		    Views.COL_EMPLOYEE_PHONE,
	    		    Views.COL_EMPLOYEE_ID
	    		);
	        if (itemPage != null && itemPage.isPaginationEnabled()) {
	            int count = empdb.queryForObject("SELECT COUNT(*) FROM " + Views.TBL_EMPLOYEE, Integer.class);
	            int total_page = (int) Math.ceil((double) count / itemPage.getPage_size());
	            itemPage.setTotal_page(total_page);

	            return empdb.query(str_query + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
	                    new Employee_mapper(),
	                    (itemPage.getPage_current() - 1) * itemPage.getPage_size(),
	                    itemPage.getPage_size());
	        } else {
	            return empdb.query(str_query, new Employee_mapper());
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching employees: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}
    public List<Role> findAllRole() {
    	try {
    		String sql = "SELECT * FROM Role WHERE id != 1";
            return empdb.query(sql, new Role_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
        
    }
    public Employee findId(int id) {
        try {
            String sql = "SELECT e.*, r.Name AS role_name " +
                         "FROM Employee e " +
                         "LEFT JOIN Role r ON e.Role_Id = r.Id " +
                         "WHERE e.Id = ?";
            return empdb.queryForObject(sql, (rs, rowNum) -> {
                Employee emp = new Employee();
                emp.setId(rs.getInt(Views.COL_EMPLOYEE_ID));
                emp.setFirst_name(rs.getString(Views.COL_EMPLOYEE_FIRST_NAME));
                emp.setLast_name(rs.getString(Views.COL_EMPLOYEE_LAST_NAME));
                emp.setRole_id(rs.getInt(Views.COL_EMPLOYEE_ROLE_ID));
                emp.setPassword(rs.getString(Views.COL_EMPLOYEE_PASSWORD));
                emp.setPhone(rs.getString(Views.COL_EMPLOYEE_PHONE));
                emp.setRole_name(rs.getString("role_name"));
                return emp;
            }, id);
        } catch (DataAccessException e) {
            System.err.println("Error fetching employee with ID: " + id + " - " + e.getMessage());
            return null;
        }
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
    public Boolean deleteEmployee(int id) {
    	try {
			String sql = "DELETE FROM Employee where Id=?";
			Object[] params = {id};
			int[] types = {Types.INTEGER};
			int row = empdb.update(sql,params,types);
			return row >0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
	public int countEmp() {
		try {
			String sql = "SELECT COUNT(*) FROM Employee";
		    return empdb.queryForObject(sql, Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	    
	}
    public void updateEmp(Employee emp) {
        try {
            String currentPassword = empdb.queryForObject(
                "SELECT Password FROM Employee WHERE Id = ?", 
                String.class, 
                emp.getId()
            );
            String encodedPassword = emp.getPassword().isEmpty() ? currentPassword : SecurityUtility.encryptBcrypt(emp.getPassword());
            String sql = "UPDATE Employee SET First_name = ?, Last_name = ?, Phone = ?, Role_Id = ?, Password = ? WHERE Id = ?";
            empdb.update(sql, emp.getFirst_name(), emp.getLast_name(), emp.getPhone(), emp.getRole_id(), encodedPassword, emp.getId());

            Logger.getGlobal().info("Employee updated successfully: " + emp.getPhone());

        } catch (DataAccessException e) {
            Logger.getGlobal().severe("Error updating employee: " + e.getMessage());
            throw new RuntimeException("Database error while updating employee.", e);
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
	    try {
	    	 String sql = "SELECT Role_Id FROM Employee WHERE Phone = ?";
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
