package com.warehouseManager.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Conversion_mapper;
import com.mapper.Product_mapper;
import com.mapper.Stock_mapper;
import com.models.Conversion;
import com.models.Product;
import com.models.Stock;
import com.utils.Views;
import java.util.Map;


@Repository
public class StockRepository {
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
	

    public List<Stock> findAllStock(int employeeId, int warehouseId) {
        String sql = """
            SELECT st.*, 
                   p.Product_name, 
                   wrd.Wh_price, 
                   u.Name, 
                   whr.Shipping_fee, 
                   whr.Date 
            FROM stock st
            JOIN Product p ON st.Id_product = p.Id
            JOIN Unit u ON p.Unit_id = u.Id
            JOIN Conversion c ON p.Id = c.Product_Id
            JOIN Warehouse_receipt_detail wrd ON st.Wh_rc_dt_Id = wrd.Id
            JOIN Warehouse_receipt whr ON wrd.Wh_receiptId = whr.Id
            JOIN Warehouse wh ON whr.Wh_Id = wh.Id
            JOIN employee_warehouse ew ON wh.Id = ew.Warehouse_Id
            WHERE ew.Employee_Id = ? 
              AND ew.Warehouse_Id = ?;
        """;

        return jdbcTemplate.query(sql, new Stock_mapper(), employeeId, warehouseId);
    }
	
    public List<Map<String, Object>> getInventoryStats() {
        String sql = """
            SELECT 
                p.Product_name AS ProductName,
                wr.Date AS ImportDate,
                st.Quantity AS StockQuantity,
                CASE 
                    WHEN st.Quantity > 0 THEN 'Còn'
                    ELSE 'Hết'
                END AS Status
            FROM stock st
            JOIN Product p ON st.Id_product = p.Id
            JOIN Warehouse_receipt_detail wrd ON st.Wh_rc_dt_Id = wrd.Id
            JOIN Warehouse_receipt wr ON wrd.Wh_receiptId = wr.Id
            ORDER BY p.Product_name, wr.Date
        """;

        return jdbcTemplate.queryForList(sql);  
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
