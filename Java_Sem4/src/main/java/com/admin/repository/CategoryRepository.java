package com.admin.repository;

import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Category_mapper;
import com.models.Category_Product;
import com.utils.Views;

@Repository
public class CategoryRepository {
	@Autowired
	private final JdbcTemplate dbcate;
	
	public CategoryRepository(JdbcTemplate jdbc) {
		this.dbcate = jdbc;
	}
	public List<Category_Product> findAll(){
		try {
			String sql = "SELECT * FROM Product_category";
			return dbcate.query(sql, new Category_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public Category_Product findId(int id) {
		try {
			String sql = "SELECT * FROM Product_category WHERE Id=?";
			return dbcate.queryForObject(sql ,(rs,rowNum) -> {
				Category_Product ca = new Category_Product();
				ca.setId(rs.getInt(Views.COL_CATEGORY_ID));
				ca.setName(rs.getString(Views.COL_CATEGORY_NAME));
				return ca;
			},id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public boolean saveCate(Category_Product ca) {
		try {
			String sql = "INSERT INTO Product_category (Cate_name) VALUES (?)";
			int row = dbcate.update(sql,ca.getName());
			return row > 0 ;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean deleteCa(int id) {
		try {
			String sql = "DELETE FROM Product_category WHERE Id=?";
			Object[] params = {id};
			int[] types = {Types.INTEGER};
			int row = dbcate.update(sql,params,types);
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
	public boolean updateCa(Category_Product ca) {
		try {
			String sql = "UPDATE Product_category SET Cate_name = ? WHERE Id = ?";
			int row = dbcate.update(sql, ca.getName(),ca.getId());
			return row > 0 ;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
