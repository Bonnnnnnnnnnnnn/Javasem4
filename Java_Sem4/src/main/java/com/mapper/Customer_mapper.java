package com.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.models.Customer;
import com.utils.Views;

public class Customer_mapper implements RowMapper<Customer> {
	public Customer mapRow(ResultSet rs,int rowNum) throws SQLException{
		Customer item = new Customer();
		item.setAddress(rs.getString(Views.COL_CUSTOMER_ADDRESS));
		Timestamp timestamp = rs.getTimestamp(Views.COL_CUSTOMER_BIRTHOFDATE);
        if (timestamp != null) {
            item.setBirthDay(timestamp.toLocalDateTime());
        }
        Timestamp timestamps = rs.getTimestamp(Views.COL_CUSTOMER_CREATION_TIME);
        if (timestamps != null) {
            item.setCreation_time(timestamps.toLocalDateTime());
        }
        item.setId(rs.getInt(Views.COL_CUSTOMER_ID));
        item.setFirst_name(rs.getString(Views.COL_CUSTOMER_FIRST_NAME));
        item.setLast_name(rs.getString(Views.COL_CUSTOMER_LAST_NAME));
        item.setPassword(rs.getString(Views.COL_CUSTOMER_PASSWORD));
        item.setPhone(rs.getString(Views.COL_CUSTOMER_PHONE));
        item.setUid(rs.getString(Views.COL_CUSTOMER_UID));
		return item;
	}

}
