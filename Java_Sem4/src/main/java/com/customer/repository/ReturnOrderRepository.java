package com.customer.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.models.ReturnOrder;
import com.models.ReturnOrderDetail;
import com.utils.Views;
import com.mapper.ReturnOrderDetailMapper;
@Repository
public class ReturnOrderRepository {
    
    @Autowired
    private JdbcTemplate db;
    
    public int insertReturnOrder(ReturnOrder ro) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            String insertSql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?)",
                Views.TBL_RETURN_ORDER,
                Views.COL_RETURN_ORDER_ORDER_ID,
                Views.COL_RETURN_ORDER_DATE,
                Views.COL_RETURN_ORDER_STATUS,
                Views.COL_RETURN_ORDER_NOTE,
                Views.COL_RETURN_ORDER_TOTAL_AMOUNT,
                Views.COL_RETURN_ORDER_DISCOUNT_AMOUNT,
                Views.COL_RETURN_ORDER_FINAL_AMOUNT
            );

            int rowsAffected = db.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, ro.getOrderId());
                ps.setDate(2, java.sql.Date.valueOf(ro.getReturnDate()));
                ps.setString(3, ro.getStatus());
                ps.setString(4, ro.getNote());
                ps.setDouble(5, ro.getTotalAmount());
                ps.setDouble(6, ro.getDiscountAmount());
                ps.setDouble(7, ro.getFinalAmount());
                return ps;
            }, keyHolder);

            if(rowsAffected > 0) {
            	return keyHolder.getKey().intValue();
            }
            return keyHolder.getKey().intValue();
        } catch (DataAccessException e) {
            System.out.println("Error in insertReturnOrder: " + e.getMessage());
            return 0;
        }
    }

    public boolean insertReturnOrderDetail(ReturnOrderDetail rod) {
        try {
            String insertSql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
                Views.TBL_RETURN_ORDER_DETAIL,
                Views.COL_RETURN_DETAIL_RETURN_ID,
                Views.COL_RETURN_DETAIL_ORDER_DETAIL_ID,
                Views.COL_RETURN_DETAIL_QUANTITY,
                Views.COL_RETURN_DETAIL_REASON,
                Views.COL_RETURN_DETAIL_AMOUNT
            );

            int rowsAffected = db.update(insertSql, 
                rod.getReturnOrderId(),
                rod.getOrderDetailId(),
                rod.getQuantity(),
                rod.getReason(),
                rod.getAmount()
            );

            return rowsAffected > 0;
        } catch (DataAccessException e) {
            System.out.println("Error in insertReturnOrderDetail: " + e.getMessage());
            return false;
        }
    }
    public ReturnOrder findReturnOrderByOrderId(int orderId) {
        try {
            String sql = String.format("SELECT * FROM %s WHERE %s = ?",
                Views.TBL_RETURN_ORDER,
                Views.COL_RETURN_ORDER_ORDER_ID
            );
            
            @SuppressWarnings("deprecation")
			List<ReturnOrder> returnOrders = db.query(sql, new Object[]{orderId}, (rs, rowNum) -> {
                ReturnOrder ro = new ReturnOrder();
                ro.setId(rs.getInt(Views.COL_RETURN_ORDER_ID));
                ro.setOrderId(rs.getInt(Views.COL_RETURN_ORDER_ORDER_ID));
                ro.setReturnDate(rs.getDate(Views.COL_RETURN_ORDER_DATE).toLocalDate());
                ro.setStatus(rs.getString(Views.COL_RETURN_ORDER_STATUS));
                ro.setNote(rs.getString(Views.COL_RETURN_ORDER_NOTE));
                ro.setTotalAmount(rs.getDouble(Views.COL_RETURN_ORDER_TOTAL_AMOUNT));
                ro.setDiscountAmount(rs.getDouble(Views.COL_RETURN_ORDER_DISCOUNT_AMOUNT));
                ro.setFinalAmount(rs.getDouble(Views.COL_RETURN_ORDER_FINAL_AMOUNT));
                return ro;
            });
            
            return returnOrders.isEmpty() ? null : returnOrders.get(0);
        } catch (DataAccessException e) {
            System.out.println("Error in findReturnOrderByOrderId: " + e.getMessage());
            return null;
        }
    }
    @SuppressWarnings("deprecation")
    public List<ReturnOrderDetail> findReturnOrderDetailsByReturnOrderId(int returnOrderId) {
        try {
            String sql = String.format("""
                SELECT rod.*, 
                       od.%s as OriginalQuantity, 
                       od.%s as ProductPrice,
                       p.%s as ProductName, 
                       p.%s as ProductImage
                FROM %s rod
                JOIN %s od ON rod.%s = od.%s
                JOIN %s p ON od.%s = p.%s
                WHERE rod.%s = ?
                """,
                Views.COL_ORDER_DETAIL_QUANTITY,
                Views.COL_ORDER_DETAIL_PRICE,
                Views.COL_PRODUCT_NAME,
                Views.COL_PRODUCT_IMG,
                Views.TBL_RETURN_ORDER_DETAIL,
                Views.TBL_ORDER_DETAIL,
                Views.COL_RETURN_DETAIL_ORDER_DETAIL_ID,
                Views.COL_ORDER_DETAIL_ID,
                Views.TBL_PRODUCT,
                Views.COL_ORDER_DETAIL_PRODUCT_ID,
                Views.COL_PRODUCT_ID,
                Views.COL_RETURN_DETAIL_RETURN_ID
            );
            
            return db.query(sql, new Object[]{returnOrderId}, new ReturnOrderDetailMapper());
            
        } catch (DataAccessException e) {
            System.out.println("Error in findReturnOrderDetailsByReturnOrderId: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
}