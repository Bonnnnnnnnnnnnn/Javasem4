package com.admin.repository;

import java.sql.Types;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.Brand;
import com.models.PageView;
import com.utils.Views;
import com.mapper.Brand_mapper;

@Repository
public class BrandRepository {

	@Autowired
	private final JdbcTemplate dbbr;
	
	public BrandRepository(JdbcTemplate jdbc) {
		this.dbbr = jdbc;
	}
	public List<Brand> findAll(PageView itemPage) {
	    try {
	        String sql = "SELECT * FROM Brand ORDER BY Id DESC";
	        if (itemPage != null && itemPage.isPaginationEnabled()) {
	            int count = dbbr.queryForObject("SELECT COUNT(*) FROM Brand", Integer.class);
	            int totalPage = (int) Math.ceil((double) count / itemPage.getPage_size());
	            itemPage.setTotal_page(totalPage);

	            return dbbr.query(sql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
	                    new Brand_mapper(),
	                    (itemPage.getPage_current() - 1) * itemPage.getPage_size(),
	                    itemPage.getPage_size());
	        } else {
	            return dbbr.query(sql, new Brand_mapper());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}
	public int countByBrandId(int brandId) {
		try {
			String sql = "SELECT COUNT(*) FROM Product WHERE brand_id = ?";
		    return dbbr.queryForObject(sql, Integer.class, brandId);
		} catch (Exception e) {
			return 0;
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
	public int countBrand() {
	    String sql = "SELECT COUNT(*) FROM Brand";
	    return dbbr.queryForObject(sql, Integer.class);
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
