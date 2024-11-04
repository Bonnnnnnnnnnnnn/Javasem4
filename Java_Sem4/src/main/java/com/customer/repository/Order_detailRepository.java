package com.customer.repository;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.Order_detail;
import com.utils.Views;


@Repository
public class Order_detailRepository {
	@Autowired
	JdbcTemplate db;
	
	@Autowired
	CartRepository repca;
	public boolean insertOrderDetail(Order_detail od,int cart_id) {
	    try {
	    	String insertSql = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
	    		    Views.TBL_ORDER_DETAIL,  // Bảng order detail
	    		    Views.COL_ORDER_DETAIL_STOCK_ID,   // Cột stock_id
	    		    Views.COL_ORDER_DETAIL_ORDER_ID,   // Cột order_id
	    		    Views.COL_ORDER_DETAIL_PRICE,      // Cột price
	    		    Views.COL_ORDER_DETAIL_STATUS      // Cột status
	    		);

	        
	        int rowsAffected = db.update(insertSql, 
				        				od.getStock_id() != 0 ? od.getStock_id() : null,
				        				od.getOrder_id(),  
				        				od.getPrice(), 
				        				od.getStatus());
	        if(rowsAffected > 0) {
	        	repca.deleteCartById(cart_id);
	        }

	        return rowsAffected > 0;
	    } catch (DataAccessException e) {
	    		System.out.println(e.getMessage());
	        return false;
	    }
	}

	


}
