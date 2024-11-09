package com.mapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.models.Order;
import com.utils.Views;

public class Order_mapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order item = new Order();

        item.setId(rs.getInt(Views.COL_ORDER_ID));
        item.setCustomer_id(rs.getInt(Views.COL_ORDER_CUSTOMER_ID));
        item.setEmployee_id(rs.getInt(Views.COL_ORDER_EMPLOYEE));
        item.setPayment_id(rs.getInt(Views.COL_ORDER_PAYMENT_ID));
        item.setStatus(rs.getString(Views.COL_ORDER_STATUS));
        item.setCus_Name(rs.getString(Views.COL_ORDER_CUSNAME));
        item.setPhone(rs.getString(Views.COL_ORDER_PHONE));
        item.setAddress(rs.getString(Views.COL_ORDER_ADDRESS));
        item.setPay_status(rs.getString(Views.COL_ORDER_PAYSTATUS));
        
        // Map Date
        Timestamp timestampCreationTime = rs.getTimestamp(Views.COL_ORDER_DATE);
        if (timestampCreationTime != null) {
            item.setDate(timestampCreationTime.toLocalDateTime().toLocalDate());
        }

        
        item.setCoupon_id(rs.getInt(Views.COL_ORDER_COUPONID));
        item.setDiscount(rs.getBigDecimal(Views.COL_ORDER_DISCOUNT));
        item.setTotalAmount(rs.getBigDecimal(Views.COL_ORDER_TOTALAMOUNT));
        item.setShippingFee(rs.getBigDecimal(Views.COL_ORDER_SHIPPINGFEE));
        item.setNotes(rs.getString(Views.COL_ORDER_NOTES));
        item.setOrderID(rs.getString(Views.COL_ORDER_ORDERID));
        
        return item;
    }
}
