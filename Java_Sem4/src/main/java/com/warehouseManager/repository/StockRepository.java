package com.warehouseManager.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.PageView;
import com.models.Product;
import com.models.Stock;
import com.mapper.Stock_mapper;
import com.mapper.Product_mapper;
import com.utils.Views;

@Repository
public class StockRepository {
	@Autowired
	private final JdbcTemplate dbSt;
	
	public StockRepository(JdbcTemplate jdbc) {
		this.dbSt = jdbc;
	}
	public List<Stock> findAllStock(PageView ItemPage) {
	    try {
	    	String str_query = String.format("SELECT s.*, p.%s as product_name, wrd.%s as wh_rc_dt_quantity " +
	    	        "FROM %s s " +
	    	        "INNER JOIN %s p ON s.%s = p.%s " +
	    	        "INNER JOIN %s wrd ON s.%s = wrd.%s " +
	    	        "ORDER BY s.%s DESC",
	    	        Views.COL_PRODUCT_NAME, Views.COL_WAREHOUSE_RECEIPT_DETAIL_QUANTITY,
	    	        Views.TBL_STOCK, 
	    	        Views.TBL_PRODUCT, Views.COL_STOCK_PRODUCT_ID, Views.COL_PRODUCT_ID,
	    	        Views.TBL_WAREHOUSE_RECEIPT_DETAIL, Views.COL_STOCK_WARERCDT_ID, Views.COL_WAREHOUSE_RECEIPT_DETAIL_ID,
	    	        Views.COL_STOCK_ID);

	        
	        if (ItemPage != null && ItemPage.isPaginationEnabled()) {
	            int count = dbSt.queryForObject("SELECT COUNT(*) FROM " + Views.TBL_STOCK, Integer.class);
	            int total_page = (int) Math.ceil((double) count / ItemPage.getPage_size());
	            ItemPage.setTotal_page(total_page);

	            return dbSt.query(str_query + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
	                    new Stock_mapper(),
	                    (ItemPage.getPage_current() - 1) * (ItemPage.getPage_size()),
	                    ItemPage.getPage_size());
	        } else {
	            return dbSt.query(str_query, new Stock_mapper());
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching stock: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}
	public List<Product> findAddPro(){
		try {
			String sql = "SELECT * FROM Product";
			return dbSt.query(sql, new Product_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
