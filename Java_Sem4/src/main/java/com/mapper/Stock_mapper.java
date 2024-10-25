package com.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.models.Stock;
import com.utils.Views;

public class Stock_mapper implements RowMapper<Stock> {
	public Stock mapRow(ResultSet rs ,int rowNum) throws SQLException {
		Stock item = new Stock();
		item.setId(rs.getInt(Views.COL_STOCK_ID));
		item.setProduct_id(rs.getInt(Views.COL_STOCK_PRODUCT_ID));
		item.setQuantity(rs.getInt(Views.COL_STOCK_QUANTITY));
		item.setWh_rc_dt_id(rs.getInt(Views.COL_STOCK_WARERCDT_ID));
		item.setProduct_name(rs.getString(Views.COL_PRODUCT_NAME));
		item.setWh_rc_dt_id_quantity(rs.getInt(Views.COL_WAREHOUSE_RECEIPT_DETAIL_QUANTITY));
		return item;
	}

}
