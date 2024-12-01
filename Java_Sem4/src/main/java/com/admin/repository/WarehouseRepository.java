package com.admin.repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.Employee;
import com.models.Employee_warehouse;
import com.models.PageView;
import com.models.Warehouse;
import com.models.Warehouse_type;
import com.utils.Views;
import com.mapper.Employee_warehouse_mapper;
import com.mapper.Warehouse_mapper;
import com.mapper.Warehouse_type_mapper;

@Repository
public class WarehouseRepository {

	private final JdbcTemplate dbwh;
	
	public WarehouseRepository(JdbcTemplate jdbc) {
		this.dbwh = jdbc;
	}
	public List<Warehouse> findAll(PageView itemPage) {
	    try {
	        String sql = "SELECT w.*, wt.name AS type_name, " +
	                     "e.first_name AS managerFirstName, " +
	                     "e.last_name AS managerLastName " +
	                     "FROM Warehouse w " +
	                     "JOIN Warehouse_type wt ON w.wh_type_id = wt.id " +
	                     "LEFT JOIN Employee e ON w.id = e.id " +
	                     "ORDER BY w.id DESC";

	        if (itemPage != null && itemPage.isPaginationEnabled()) {
	            int totalRecords = dbwh.queryForObject("SELECT COUNT(*) FROM Warehouse", Integer.class);
	            itemPage.setTotal_page((int) Math.ceil((double) totalRecords / itemPage.getPage_size()));

	            sql += " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

	            List<Warehouse> warehouses = dbwh.query(sql, new Warehouse_mapper(),
	                    (itemPage.getPage_current() - 1) * itemPage.getPage_size(),
	                    itemPage.getPage_size());

	            for (Warehouse warehouse : warehouses) {
	                warehouse.setRelatedCount(countRelatedData(warehouse.getId()));
	            }
	            return warehouses;
	        }

	        List<Warehouse> warehouses = dbwh.query(sql, new Warehouse_mapper());
	        for (Warehouse warehouse : warehouses) {
	            warehouse.setRelatedCount(countRelatedData(warehouse.getId()));
	        }
	        return warehouses;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}

	public int countRelatedData(int warehouseId) {
	    try {
	        String sql = "SELECT COUNT(*) FROM Warehouse_receipt WHERE Wh_Id = ?";
	        return dbwh.queryForObject(sql, Integer.class, warehouseId);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return 0;
	    }
	}

	public List<Warehouse_type> findAllType(){
		try {
			String sql = "SELECT * FROM Warehouse_type";
			return dbwh.query(sql, new Warehouse_type_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	public boolean saveWh(Warehouse wh) {
		try {
			String sql = "INSERT INTO Warehouse (Name,Address,Wh_type_Id) VALUES (?,?,?)";
			int row = dbwh.update(sql,wh.getName(),wh.getAddress(),wh.getWh_type_id());
			return row >0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean deleteWh(int id) {
		try {
			String sql = "DELETE FROM Warehouse WHERE Id = ?";
			Object[] params = {id};
			int[] types = {Types.INTEGER};
			int row = dbwh.update(sql,params,types);
			if(row > 0) {
				System.out.println("delete success:");
				return false;
			}else {
				System.out.println("Failed to delete Warehouse: No rows affected ");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
//	public Warehouse findId(int id) {
//	    try {
//	        String sql = "SELECT w.*, wt.name AS type_name, e.first_name AS manager_first_name, e.last_name AS manager_last_name " +
//	                     "FROM Warehouse w " +
//	                     "JOIN Warehouse_type wt ON w.wh_type_id = wt.id " +
//	                     "LEFT JOIN Employee_warehouse ew ON ew.Warehouse_Id = w.id " +
//	                     "LEFT JOIN Employee e ON ew.Employee_Id = e.Id " +
//	                     "WHERE w.id = ?";
//	        
//	        return dbwh.queryForObject(sql, (rs, rowNum) -> {
//	            Warehouse wh = new Warehouse();
//	            wh.setId(rs.getInt(Views.COL_WAREHOUSE_ID));
//	            wh.setName(rs.getString(Views.COL_WAREHOUSE_NAME));
//	            wh.setAddress(rs.getString(Views.COL_WAREHOUSE_ADDRESS));
//	            wh.setWh_type_id(rs.getInt(Views.COL_WAREHOUSE_TYPE_WAREHOUSE_ID));
//	            wh.setTypeName(rs.getString("type_name"));
//	            wh.setManagerFirstName(rs.getString("manager_first_name"));
//	            wh.setManagerLastName(rs.getString("manager_last_name"));
//	            return wh;
//	        }, id);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return null;
//	    }
//	}
	public Warehouse findId(int id) {
	    try {
	        String sql = "SELECT w.*, wt.name AS type_name, " +
	                     "e.first_name AS manager_first_name, e.last_name AS manager_last_name " +
	                     "FROM Warehouse w " +
	                     "JOIN Warehouse_type wt ON w.wh_type_id = wt.id " +
	                     "LEFT JOIN Employee_warehouse ew ON ew.Warehouse_Id = w.id " +
	                     "LEFT JOIN Employee e ON ew.Employee_Id = e.Id " +
	                     "WHERE w.id = ?";

	        return dbwh.query(sql, (rs) -> {
	            if (rs.next()) {  // Kiểm tra nếu có dòng dữ liệu
	                Warehouse warehouse = new Warehouse();
	                warehouse.setId(rs.getInt("id"));
	                warehouse.setName(rs.getString("name"));
	                warehouse.setAddress(rs.getString("address"));
	                warehouse.setWh_type_id(rs.getInt("wh_type_id"));
	                warehouse.setTypeName(rs.getString("type_name"));

	                List<Employee> managers = new ArrayList<>();
	                do {
	                    String firstName = rs.getString("manager_first_name");
	                    String lastName = rs.getString("manager_last_name");
	                    if (firstName != null && lastName != null) {
	                        Employee manager = new Employee();
	                        manager.setFirst_name(firstName);
	                        manager.setLast_name(lastName);
	                        managers.add(manager);
	                    }
	                } while (rs.next());

	                warehouse.setManagers(managers);

	                return warehouse;
	            }
	            return null;
	        }, id);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	public boolean updatewh(Warehouse wh) {
		try {
			String sql = "UPDATE Warehouse SET Name = ?,Address = ?,Wh_type_Id = ? WHERE Id = ?";
			int row = dbwh.update(sql,wh.getName(),wh.getAddress(),wh.getWh_type_id(),wh.getId());
			return row > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//Employee Warehouse 
	public List<Employee_warehouse> findAllEw() {
	    try {
	        String sql = "SELECT ew.Id, ew.Employee_Id, " +
	                     "CONCAT(e.First_name, ' ', e.Last_name) AS Employee_name, " +
	                     "ew.Warehouse_Id, w.Name AS Warehouse_name " +
	                     "FROM employee_warehouse ew " +
	                     "JOIN employee e ON ew.Employee_Id = e.Id " +
	                     "JOIN warehouse w ON ew.Warehouse_Id = w.Id";
	        
	        List<Employee_warehouse> result = dbwh.query(sql, new Employee_warehouse_mapper());
	        return result;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	public List<Employee> showEmpAll() {
	    try {
	        String sql = "SELECT e.*, r.Name AS role_name " +
	                     "FROM Employee e " +
	                     "LEFT JOIN Role r ON e.Role_Id = r.Id " +
	                     "WHERE r.Name NOT IN ('admin', 'businessManager')";

	        return dbwh.query(sql, (rs, rowNum) -> {
	            Employee emp = new Employee();
	            emp.setId(rs.getInt(Views.COL_EMPLOYEE_ID));
	            emp.setFirst_name(rs.getString(Views.COL_EMPLOYEE_FIRST_NAME));
	            emp.setLast_name(rs.getString(Views.COL_EMPLOYEE_LAST_NAME));
	            emp.setRole_id(rs.getInt(Views.COL_EMPLOYEE_ROLE_ID));
	            emp.setPassword(rs.getString(Views.COL_EMPLOYEE_PASSWORD));
	            emp.setPhone(rs.getString(Views.COL_EMPLOYEE_PHONE));
	            emp.setRole_name(rs.getString("role_name"));
	            return emp;
	        });
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching employees: " + e.getMessage());
	        return new ArrayList<>();
	    }
	}
	public Employee_warehouse findByEmpwhId(int id) {
	    try {
	        String sql = "SELECT ew.Id, ew.Employee_Id, " +
	                     "CONCAT(e.First_name, ' ', e.Last_name) AS Employee_name, " +
	                     "ew.Warehouse_Id, w.Name AS Warehouse_name " +
	                     "FROM employee_warehouse ew " +
	                     "JOIN employee e ON ew.Employee_Id = e.Id " +
	                     "JOIN warehouse w ON ew.Warehouse_Id = w.Id " +
	                     "WHERE ew.Id = ?";
	        return dbwh.queryForObject(sql, new Employee_warehouse_mapper(), id);
	    } catch (EmptyResultDataAccessException e) {
	        return null;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	public boolean addEw(Employee_warehouse ew) {
		try {
			String sql = "INSERT INTO employee_warehouse (Employee_Id,Warehouse_Id) VALUES (?,?)";
			int row = dbwh.update(sql,ew.getEmployee_id(),ew.getWarehouse_id());
			return row >0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean updateEw(Employee_warehouse ew) {
		try {
			String sql = "UPDATE employee_warehouse SET Employee_Id=?,Warehouse_Id=? WHERE Id =?";
			int row = dbwh.update(sql,ew.getEmployee_id(),ew.getWarehouse_id(),ew.getId());
			return row >0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
