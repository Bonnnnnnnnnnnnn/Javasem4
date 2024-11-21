package com.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.models.ReturnOrder;
import com.utils.Views;


public class ReturnOrderMapper implements RowMapper<ReturnOrder> {
    @Override
    public ReturnOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setId(rs.getInt(Views.COL_RETURN_ORDER_ID));
        returnOrder.setOrderId(rs.getInt(Views.COL_RETURN_ORDER_ORDER_ID));
        returnOrder.setReturnDate(rs.getTimestamp(Views.COL_RETURN_ORDER_DATE).toLocalDateTime().toLocalDate());
        returnOrder.setStatus(rs.getString(Views.COL_RETURN_ORDER_STATUS));
        returnOrder.setNote(rs.getString(Views.COL_RETURN_ORDER_NOTE));
        returnOrder.setTotalAmount(rs.getDouble(Views.COL_RETURN_ORDER_TOTAL_AMOUNT));
        returnOrder.setDiscountAmount(rs.getDouble(Views.COL_RETURN_ORDER_DISCOUNT_AMOUNT));
        returnOrder.setFinalAmount(rs.getDouble(Views.COL_RETURN_ORDER_FINAL_AMOUNT));
        
        // Nếu join với Customer
        try {
            returnOrder.setCustomerName(rs.getString("CustomerName"));
        } catch (SQLException e) {
            // Ignore if not joined
        }
        
        return returnOrder;
    }
}
