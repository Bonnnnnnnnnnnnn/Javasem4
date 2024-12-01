package com.customer.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.models.Order;
import com.models.PageView;
import com.models.ReturnOrder;
import com.models.ReturnOrderDetail;
import com.utils.Views;
import com.mapper.ReturnOrderDetailMapper;
import com.mapper.ReturnOrderMapper;
@Repository
public class ReturnOrderRepository {
    
    @Autowired
    private JdbcTemplate db;
    @Autowired
    private OrderRepository repo;
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
            
            List<ReturnOrder> returnOrders = db.query(sql, new Object[]{orderId}, new ReturnOrderMapper());
            
            if (!returnOrders.isEmpty()) {
                ReturnOrder returnOrder = returnOrders.get(0);
                // Lấy order info
                Order order = repo.getOrderById(returnOrder.getOrderId());
                returnOrder.setOrder(order);
                return returnOrder;
            }
            
            return null;
        } catch (DataAccessException e) {
            System.out.println("Error in findReturnOrderByOrderId: " + e.getMessage());
            return null;
        }
    }
    
    public ReturnOrder findReturnOrderById(int Id) {
        try {
            String sql = String.format("SELECT * FROM %s WHERE %s = ?",
                Views.TBL_RETURN_ORDER,
                Views.COL_RETURN_ORDER_ID
            );
            
            List<ReturnOrder> returnOrders = db.query(sql, new Object[]{Id}, new ReturnOrderMapper());
            
            if (!returnOrders.isEmpty()) {
                ReturnOrder returnOrder = returnOrders.get(0);
                // Lấy order info
                Order order = repo.getOrderById(returnOrder.getOrderId());
                returnOrder.setOrder(order);
                return returnOrder;
            }
            
            return null;
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
    public boolean updateStatusAndMessage(int returnOrderId, String status, String message) {
        try {
            String sql = String.format(
                "UPDATE %s SET %s = ?, message = ? WHERE %s = ?",
                Views.TBL_RETURN_ORDER,
                Views.COL_RETURN_ORDER_STATUS,
                Views.COL_RETURN_ORDER_ID
            );
            
            int rowsAffected = db.update(sql, status, message, returnOrderId);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            System.err.println("Error updating status and message: " + e.getMessage());
            return false;
        }
    }
    public List<ReturnOrder> findAllProcessingReturnOrders(PageView pageView) {
        try {
            // Base query - chỉ để 1 ORDER BY
            StringBuilder str_query = new StringBuilder(
                String.format("SELECT ro.* FROM %s ro " +
                            "WHERE ro.%s = 'Processing' " +
                            "ORDER BY ro.%s ASC",
                            Views.TBL_RETURN_ORDER,
                            Views.COL_RETURN_ORDER_STATUS,
                            Views.COL_RETURN_ORDER_DATE));

            List<Object> params = new ArrayList<>();

            // Query đếm với điều kiện status
            String countQuery = String.format(
                "SELECT COUNT(*) FROM %s WHERE %s = 'Processing'",
                Views.TBL_RETURN_ORDER,
                Views.COL_RETURN_ORDER_STATUS
            );
            
            int count = db.queryForObject(countQuery, Integer.class);
            
            // Tính total page
            int total_page = (int) Math.ceil((double) count / pageView.getPage_size());
            pageView.setTotal_page(total_page);

            // Thêm phân trang (bỏ ORDER BY ở đây)
            if (pageView.isPaginationEnabled()) {
                str_query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
                params.add((pageView.getPage_current() - 1) * pageView.getPage_size());
                params.add(pageView.getPage_size());
            }

            
            List<ReturnOrder> returnOrders = db.query(str_query.toString(), new ReturnOrderMapper(), params.toArray());

          
            for (ReturnOrder ro : returnOrders) {
                Order order = repo.getOrderById(ro.getOrderId());
                ro.setOrder(order);
            }

            return returnOrders;

        } catch (DataAccessException e) {
            System.err.println("Error in findAllProcessingReturnOrders: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    public List<ReturnOrder> findAllProcessedReturnOrders(PageView pageView) {
        try {
          
            StringBuilder str_query = new StringBuilder(
                String.format("SELECT ro.* FROM %s ro " +
                            "WHERE ro.%s IN ('Accepted', 'Completed', 'Rejected') " +
                            "ORDER BY ro.%s DESC",
                            Views.TBL_RETURN_ORDER,
                            Views.COL_RETURN_ORDER_STATUS,
                            Views.COL_RETURN_ORDER_DATE));

            List<Object> params = new ArrayList<>();

            // Query đếm với điều kiện status
            String countQuery = String.format(
                "SELECT COUNT(*) FROM %s WHERE %s IN ('Accepted', 'Completed', 'Rejected')",
                Views.TBL_RETURN_ORDER,
                Views.COL_RETURN_ORDER_STATUS
            );
            
            int count = db.queryForObject(countQuery, Integer.class);
            
            // Tính total page
            int total_page = (int) Math.ceil((double) count / pageView.getPage_size());
            pageView.setTotal_page(total_page);

            // Thêm phân trang
            if (pageView.isPaginationEnabled()) {
                str_query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
                params.add((pageView.getPage_current() - 1) * pageView.getPage_size());
                params.add(pageView.getPage_size());
            }

            // Thực hiện query và map kết quả
            List<ReturnOrder> returnOrders = db.query(str_query.toString(), new ReturnOrderMapper(), params.toArray());

            // Lấy thông tin Order cho mỗi ReturnOrder
            for (ReturnOrder ro : returnOrders) {
                Order order = repo.getOrderById(ro.getOrderId());
                ro.setOrder(order);
            }

            return returnOrders;

        } catch (DataAccessException e) {
            System.err.println("Error in findAllProcessedReturnOrders: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}