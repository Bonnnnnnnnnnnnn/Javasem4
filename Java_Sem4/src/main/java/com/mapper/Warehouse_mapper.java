package com.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.models.Warehouse;
import com.utils.Views;

public class Warehouse_mapper implements RowMapper<Warehouse>{
	public Warehouse mapRow(ResultSet r , int rowNum) throws SQLException{
		Warehouse item = new Warehouse();
		item.setId(r.getInt(Views.COL_WAREHOUSE_ID));
		item.setName(r.getString(Views.COL_WAREHOUSE_NAME));
		item.setAddress(r.getString(Views.COL_WAREHOUSE_ADDRESS));
		item.setWh_type_id(r.getInt(Views.COL_WAREHOUSE_TYPE_WAREHOUSE_ID));
        item.setTypeName(r.getString("type_name"));
		return item;
	}

}
