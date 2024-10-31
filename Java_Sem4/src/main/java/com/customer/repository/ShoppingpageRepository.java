package com.customer.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Product_mapper;
import com.models.Customer;
import com.models.PageView;
import com.models.Product;
import com.utils.SecurityUtility;
import com.utils.Views;

@Repository
public class ShoppingpageRepository {
	
	@Autowired
	JdbcTemplate dbpro;
	
	public List<Product> findAllnopaging(PageView ItemPage, String search, int idcate, String status) {
	    try {
	        // Bắt đầu với câu truy vấn cơ bản
	        StringBuilder str_query = new StringBuilder(
	            String.format("SELECT p.*, b.%s as brand_name, u.%s as unit_name, c.%s as category_name " +
	                    "FROM %s p " +
	                    "INNER JOIN %s b ON p.%s = b.%s " +
	                    "INNER JOIN %s u ON p.%s = u.%s " +
	                    "INNER JOIN %s c ON p.%s = c.%s " +
	                    "WHERE 1=1 ",
	                    Views.COL_BRAND_NAME,Views.COL_UNIT_NAME,Views.COL_CATEGORY_NAME,
	                    Views.TBL_PRODUCT,
	                    Views.TBL_BRAND, Views.COL_PRODUCT_BRAND_ID, Views.COL_BRAND_ID,
	                    Views.TBL_UNIT, Views.COL_PRODUCT_UNIT_ID, Views.COL_UNIT_ID,
	                    Views.TBL_CATEGORY, Views.COL_PRODUCT_CATE_ID, Views.COL_CATEGORY_ID));
	       
	        List<Object> params = new ArrayList<>();  // Để chứa các tham số query

	       //  Thêm điều kiện tìm kiếm theo chuỗi nếu có
	        if (search != null && !search.isEmpty()) {
	            str_query.append(" AND (p.Product_name LIKE ? OR p.Description LIKE ?) ");
	            params.add("%" + search + "%");
	            params.add("%" + search + "%");
	        }
//
	        // Thêm điều kiện theo idcate nếu idcate > 0
	        if (idcate > 0) {
	            str_query.append(" AND p.").append(Views.COL_PRODUCT_CATE_ID).append(" = ? ");
	            params.add(idcate);
	        }
//
	        // Thêm điều kiện theo status nếu có
	        if (status != null && !status.isEmpty()) {
	            str_query.append(" AND p.Status = ? ");
	            params.add(status);
	        }

	        // Thêm phần ORDER BY
	        str_query.append(" ORDER BY p.").append(Views.COL_PRODUCT_ID).append(" DESC");

	       
	     
	        return dbpro.query(str_query.toString(), new Product_mapper(), params.toArray());

	    } catch (DataAccessException e) {
	        System.err.println("Error fetching products: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}

	public List<Product> findAllpaging(PageView ItemPage, String search, int idcate, String status) {
	    try {
	        // Bắt đầu với câu truy vấn cơ bản
	        StringBuilder str_query = new StringBuilder(
	            String.format("SELECT p.*, b.%s as brand_name, u.%s as unit_name, c.%s as category_name " +
	                    "FROM %s p " +
	                    "INNER JOIN %s b ON p.%s = b.%s " +
	                    "INNER JOIN %s u ON p.%s = u.%s " +
	                    "INNER JOIN %s c ON p.%s = c.%s " +
	                    "WHERE 1=1 ",
	                    Views.COL_BRAND_NAME,Views.COL_UNIT_NAME,Views.COL_CATEGORY_NAME,
	                    Views.TBL_PRODUCT,
	                    Views.TBL_BRAND, Views.COL_PRODUCT_BRAND_ID, Views.COL_BRAND_ID,
	                    Views.TBL_UNIT, Views.COL_PRODUCT_UNIT_ID, Views.COL_UNIT_ID,
	                    Views.TBL_CATEGORY, Views.COL_PRODUCT_CATE_ID, Views.COL_CATEGORY_ID));
	       
	        List<Object> params = new ArrayList<>();  // Để chứa các tham số query

	       //  Thêm điều kiện tìm kiếm theo chuỗi nếu có
	        if (search != null && !search.isEmpty()) {
	            str_query.append(" AND (p.Product_name LIKE ? OR p.Description LIKE ?) ");
	            params.add("%" + search + "%");
	            params.add("%" + search + "%");
	        }
//
	        // Thêm điều kiện theo idcate nếu idcate > 0
	        if (idcate > 0) {
	            str_query.append(" AND p.").append(Views.COL_PRODUCT_CATE_ID).append(" = ? ");
	            params.add(idcate);
	        }
//
	        // Thêm điều kiện theo status nếu có
	        if (status != null && !status.isEmpty()) {
	            str_query.append(" AND p.Status = ? ");
	            params.add(status);
	        }

	        // Thêm phần ORDER BY
	        str_query.append(" ORDER BY p.").append(Views.COL_PRODUCT_ID).append(" DESC");

	        // Xử lý phân trang
	        if (ItemPage != null && ItemPage.isPaginationEnabled()) {
	            int count = dbpro.queryForObject("SELECT COUNT(*) FROM " + Views.TBL_PRODUCT, Integer.class);
	            int total_page = (int) Math.ceil((double) count / ItemPage.getPage_size());
	            ItemPage.setTotal_page(total_page);

	            // Thêm OFFSET và FETCH NEXT cho phân trang
	            str_query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
	            params.add((ItemPage.getPage_current() - 1) * ItemPage.getPage_size());
	            params.add(ItemPage.getPage_size());
	        }
	     
	        return dbpro.query(str_query.toString(), new Product_mapper(), params.toArray());

	    } catch (DataAccessException e) {
	        System.err.println("Error fetching products: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}
}
