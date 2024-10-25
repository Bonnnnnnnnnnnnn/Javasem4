package com.admin.repository;

import java.sql.Types;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.Warehouse;
import com.models.Warehouse_type;
import com.utils.Views;
import com.mapper.Warehouse_mapper;
import com.mapper.Warehouse_type_mapper;

@Repository
public class WarehouseRepository {

	private final JdbcTemplate dbwh;
	
	public WarehouseRepository(JdbcTemplate jdbc) {
		this.dbwh = jdbc;
	}
	public List<Warehouse> findAll() {
	    try {
	        String sql = "SELECT w.*, wt.name AS type_name " +
	                     "FROM Warehouse w " +
	                     "JOIN Warehouse_type wt ON w.wh_type_id = wt.id";
	        return dbwh.query(sql, new Warehouse_mapper());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
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
	public Warehouse findId(int id) {
	    try {
	        String sql = "SELECT w.*, wt.name AS type_name " +
	                     "FROM Warehouse w " +
	                     "JOIN Warehouse_type wt ON w.wh_type_id = wt.id " +
	                     "WHERE w.id = ?";
	        return dbwh.queryForObject(sql, (rs, rowNum) -> {
	            Warehouse wh = new Warehouse();
	            wh.setId(rs.getInt(Views.COL_WAREHOUSE_ID));
	            wh.setName(rs.getString(Views.COL_WAREHOUSE_NAME));
	            wh.setAddress(rs.getString(Views.COL_WAREHOUSE_ADDRESS));
	            wh.setWh_type_id(rs.getInt(Views.COL_WAREHOUSE_TYPE_WAREHOUSE_ID));
	            wh.setTypeName(rs.getString("type_name"));
	            return wh;
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
}
