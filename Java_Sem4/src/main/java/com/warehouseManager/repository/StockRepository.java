package com.warehouseManager.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.Conversion;
import com.models.Order_detail;
import com.models.PageView;
import com.models.Product;
import com.models.Stock;
import com.mapper.Stock_mapper;
import com.mapper.Conversion_mapper;
import com.mapper.Product_mapper;
import com.utils.Views;

@Repository
public class StockRepository {
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
	

    public List<Stock> findAllStock(int stockId) {
        String sql = String.format(
            "SELECT s.*, p.%s AS product_name " +
            "FROM Stock s " +
            "JOIN Product p ON s.%s = p.%s " +
            "WHERE s.%s = ?",
            Views.COL_PRODUCT_NAME,       
            Views.COL_STOCK_PRODUCT_ID,   
            Views.COL_PRODUCT_ID,         
            Views.COL_STOCK_ID            
        );

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Stock stock = new Stock();
            stock.setId(rs.getInt(Views.COL_STOCK_ID));                 
            stock.setQuantity(rs.getInt(Views.COL_STOCK_QUANTITY));     
            stock.setStatus(rs.getString(Views.COL_STOCK_STATUS));      
            stock.setProduct_name(rs.getString(Views.COL_PRODUCT_NAME));
            return stock;
        }, stockId);
    }

	
	
	public List<Conversion> conversionByProductId(int product_Id) {
	    try {
	        String str_query = String.format(
	            "SELECT * FROM Conversion WHERE %s = ?",
	            Views.COL_CONVERSION_PRODUCT_ID
	        );
	        return jdbcTemplate.query(str_query, new Conversion_mapper(), product_Id);
	    } catch (Exception e) {
	        System.err.println("Error fetching conversions: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}

	
	public List<Product> findAddPro(){
		try {
			String sql = "SELECT * FROM Product";
			return jdbcTemplate.query(sql, new Product_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
