package com.customer.repository;



import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mapper.Customer_mapper;
import com.mapper.Payment_mapper;
import com.mapper.Shopping_cart_mapper;
import com.models.Customer;
import com.models.Order;
import com.models.Order_detail;
import com.models.Payment;
import com.models.Shopping_cart;
import com.utils.Views;

@Repository
public class CheckoutRepository {
	@Autowired
	JdbcTemplate db;
	
	@Autowired
	Order_detailRepository repod;
	public List<Payment> takeall() {
	    try {
	        // Define the SQL query to retrieve the customer by email
	        String str_query = String.format("SELECT * FROM %s ",
	                Views.TBL_PAYMENT);

	        // Fetch the customer details
	       
	        return db.query(str_query, new Payment_mapper());

	       
	    } catch (DataAccessException e) {
	        
	    }
	    return null; // Return null if login fails
	}
	public boolean Checkout(Order order, List<Shopping_cart> listc) {
	    try {
	        
	        String insertSql = String.format("INSERT INTO [%s] (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
	        		Views.TBL_ORDER,
	                Views.COL_ORDER_CUSTOMER_ID,
	                Views.COL_ORDER_CUSNAME,
	                Views.COL_ORDER_PHONE,
	                Views.COL_ORDER_STATUS,
	                Views.COL_ORDER_ADDRESS,
	                Views.COL_ORDER_PAYSTATUS,
	                Views.COL_ORDER_EMPLOYEE,
	                Views.COL_ORDER_PAYMENT_ID,
	                Views.COL_ORDER_DATE,
	                Views.COL_ORDER_COUPONID,     
	                Views.COL_ORDER_DISCOUNT,       
	                Views.COL_ORDER_TOTALAMOUNT,   
	                Views.COL_ORDER_SHIPPINGFEE,
	                Views.COL_ORDER_ORDERID,
	                Views.COL_ORDER_NOTES);   
	        order.setOrderID(generateOrderId());
	        KeyHolder keyHolder = new GeneratedKeyHolder();
	        int rowsAffected = db.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
	            ps.setInt(1, order.getCustomer_id());
	            ps.setString(2, order.getCus_Name());
	            ps.setString(3, order.getPhone());
	            ps.setString(4, order.getStatus());
	            ps.setString(5, order.getAddress());
	            ps.setString(6, order.getPay_status());
	            ps.setObject(7, order.getEmployee_id() != 0 ? order.getEmployee_id() : null); 
	            ps.setInt(8, order.getPayment_id());
	            ps.setDate(9, Date.valueOf(order.getDate()));
	            ps.setObject(10, null);  
	            ps.setBigDecimal(11, order.getDiscount());
	            ps.setBigDecimal(12, order.getTotalAmount());
	            ps.setBigDecimal(13, order.getShippingFee());
	            ps.setString(14, order.getOrderID());
	            ps.setString(15, order.getNotes());
	            return ps;
	        }, keyHolder);

	        if (rowsAffected > 0) {
	            // Fetch the auto-generated Id from the KeyHolder
	            order.setId(keyHolder.getKey().intValue());
	            
	            for(Shopping_cart cart : listc) {
	            	Order_detail od = new Order_detail();
	                
	                
	                od.setStock_id(0); 
	                od.setPrice(cart.getPrice());
	                od.setStatus(null); 
	                od.setOrder_id(order.getId()); 
	                od.setProduct_Id(cart.getProduct_id());
	                
	                boolean check = repod.insertOrderDetail(od,cart.getId());
	                
	                if (!check) {
	                    System.err.println("Failed to insert order detail for product: " + cart.getProduct_id());
	                    return false; 
	                }
	            }
	            return true;
	        } else {
	            return false;
	        }
 
	    } catch (DataAccessException e) {
	        System.err.println("Error inserting order: " + e.getMessage());
	        return false; 
	    }
	}


	
	private String generateOrderId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder orderId = new StringBuilder(16);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(characters.length());
            orderId.append(characters.charAt(index));
        }
        return orderId.toString();
    }


}
