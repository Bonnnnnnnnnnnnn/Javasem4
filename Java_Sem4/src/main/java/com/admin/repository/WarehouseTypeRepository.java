package com.admin.repository;

import java.sql.Types;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.Warehouse;
import com.models.Warehouse_type;
import com.utils.Views;

import com.mapper.Warehouse_type_mapper;

@Repository
public class WarehouseTypeRepository {

	@Autowired
	private final JdbcTemplate dbtype;
	
	public WarehouseTypeRepository( JdbcTemplate jdbc) {
		this.dbtype = jdbc;
	}
	public Warehouse_type findById(int id) {
        String sql = "SELECT * FROM Warehouse_type WHERE Id = ?";
        return dbtype.queryForObject(sql, (rs, rowNum) -> {
            Warehouse_type wt = new Warehouse_type();
            wt.setId(rs.getInt(Views.COL_WAREHOUSE_TYPE_ID));
            wt.setName(rs.getString(Views.COL_WAREHOUSE_TYPE_NAME));
            
            return wt;
        }, id);
    }
	public List<Warehouse_type> findAll() {
		try {
			String sql ="SELECT * FROM Warehouse_type";
			return dbtype.query(sql, new Warehouse_type_mapper());
		} catch (Exception e) {
			System.err.print("erro:" + e.getMessage());
			return Collections.emptyList();
		}
	}
	public boolean saveWhType(Warehouse_type whtp) {
		try {
			String sql ="INSERT INTO Warehouse_type (Name) VALUES (?)";
			int row = dbtype.update(sql, whtp.getName());
			return row >0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean updateWt(Warehouse_type wt) {
		try {
			String sql = "UPDATE Warehouse_type SET Name = ? WHERE Id = ?";
			int row = dbtype.update(sql,wt.getName(),wt.getId());
			return row > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean deleteWt(int id) {
	    try {
	    	String sql = "DELETE FROM Warehouse_type WHERE Id = ?";
	 	    Object[] params = {id};
	 	    int[] types = {Types.INTEGER};
	        int rowsAffected = dbtype.update(sql, params, types);
	        if (rowsAffected > 0) {
	            System.out.println("WarehouseType deleted successfully!");
	            return true;
	        } else {
	            System.err.println("Failed to delete WarehouseType: No rows affected");
	            return false;
	        }
	    } catch (Exception ex) {
	        System.err.println("Failed to delete category: " + ex.getMessage());
	        return false;
	    }
	}
	public List<Warehouse> findByTypeId(int typeId) {
	    try {
	        String sql = "SELECT w.*, t.name AS type_name " +
	                     "FROM " + Views.TBL_WAREHOUSE + " w " +
	                     "LEFT JOIN " + Views.TBL_WAREHOUSE_TYPE + " t ON w.id = t.Id " +
	                     "WHERE w.id = ?";

	        return dbtype.query(sql, (rs, rowNum) -> {
	            Warehouse warehouse = new Warehouse();
	            warehouse.setId(rs.getInt(Views.COL_WAREHOUSE_ID));
	            warehouse.setName(rs.getString(Views.COL_WAREHOUSE_NAME));
	            warehouse.setAddress(rs.getString(Views.COL_WAREHOUSE_ADDRESS));
	            warehouse.setWh_type_id(rs.getInt(Views.COL_WAREHOUSE_TYPE_WAREHOUSE_ID));
	            warehouse.setTypeName(rs.getString("type_name"));
	            return warehouse;
	        }, typeId);
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching warehouses for type ID: " + typeId + " - " + e.getMessage());
	        return Collections.emptyList();
	    }
	}


}