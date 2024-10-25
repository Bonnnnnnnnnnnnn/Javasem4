package com.admin.repository;

import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.Brand;
import com.utils.Views;
import com.mapper.Brand_mapper;

@Repository
public class BrandRepository {

	@Autowired
	private final JdbcTemplate dbbr;
	
	public BrandRepository(JdbcTemplate jdbc) {
		this.dbbr = jdbc;
	}
	public List<Brand> findAll(){
		try {
			String sql = "SELECT * FROM Brand";
			return dbbr.query(sql, new Brand_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public Brand findId(int id) {
		try {
			String sql = "SELECT * FROM Brand WHERE Id=?";
			return dbbr.queryForObject(sql ,(rs,rowNum) -> {
				Brand br = new Brand();
				br.setId(rs.getInt(Views.COL_BRAND_ID));
				br.setName(rs.getString(Views.COL_BRAND_NAME));
				return br;
			},id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public boolean saveBr(Brand br) {
		try {
			String sql = "INSERT INTO Brand (Name) VALUES (?)";
			int row = dbbr.update(sql,br.getName());
			return row > 0 ;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean deleteBr(int id) {
		try {
			String sql = "DELETE FROM Brand WHERE Id=?";
			Object[] params = {id};
			int[] types = {Types.INTEGER};
			int row = dbbr.update(sql,params,types);
			if(row > 0) {
				System.out.println("delete suess");
				return true;
			}else {
				System.out.println("delete faller");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean updateBr(Brand br) {
		try {
			String sql = "UPDATE Brand SET Name = ? WHERE Id = ?";
			int row = dbbr.update(sql, br.getName(),br.getId());
			return row > 0 ;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
