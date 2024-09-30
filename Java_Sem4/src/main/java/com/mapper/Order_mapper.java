package com.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.models.Order;
import com.utils.Views;

public class Order_mapper implements RowMapper<Order>{
	public Order mapRow(ResultSet rs , int rowNum) throws SQLException{
		Order item = new Order();
		item.setId(rs.getInt(Views.COL_ORDER_ID));
		item.setCustomer_id(rs.getInt(Views.COL_ORDER_CUSTOMER_ID));
		item.setEmployee_id(rs.getInt(Views.COL_ORDER_EMPLOYEE));
		item.setPayment_id(rs.getInt(Views.COL_ORDER_PAYMENT_ID));
		item.setStatus(rs.getString(Views.COL_ORDER_STATUS));
		return item;
	}

}
