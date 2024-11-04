package com.customer.repository;

import java.sql.Types;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Product_mapper;
import com.mapper.Unit_mapper;
import com.mapper.Brand_mapper;
import com.mapper.Category_mapper;
import com.models.Brand;
import com.models.Category_Product;
import com.models.PageView;
import com.models.Product;
import com.models.Unit;
import com.utils.FileUtils;
import com.utils.Views;


@Repository
public class DetailproductRepository {
	@Autowired
	JdbcTemplate db;

	public Product findId(int id) {
	    try {
	        
	        String str_query = String.format(
	            "SELECT p.*, b.%s as brand_name, u.%s as unit_name, c.%s as category_name " +
	            "FROM %s p " +
	            "INNER JOIN %s b ON p.%s = b.%s " +
	            "INNER JOIN %s u ON p.%s = u.%s " +
	            "INNER JOIN %s c ON p.%s = c.%s " +
	            "WHERE p.Id = ?",
	            Views.COL_BRAND_NAME, Views.COL_UNIT_NAME, Views.COL_CATEGORY_NAME,
	            Views.TBL_PRODUCT,
	            Views.TBL_BRAND, Views.COL_PRODUCT_BRAND_ID, Views.COL_BRAND_ID,
	            Views.TBL_UNIT, Views.COL_PRODUCT_UNIT_ID, Views.COL_UNIT_ID,
	            Views.TBL_CATEGORY, Views.COL_PRODUCT_CATE_ID, Views.COL_CATEGORY_ID
	        );

	     
	        return db.queryForObject(str_query, new Product_mapper(), id);
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching product with ID: " + id + " - " + e.getMessage());
	        return null;
	    }
	}



}
