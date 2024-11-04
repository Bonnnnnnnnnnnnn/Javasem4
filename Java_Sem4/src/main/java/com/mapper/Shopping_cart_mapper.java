package com.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.models.Shopping_cart;
import com.utils.Views;

public class Shopping_cart_mapper implements RowMapper<Shopping_cart> {
    public Shopping_cart mapRow(ResultSet rs, int rowNum) throws SQLException {
        Shopping_cart item = new Shopping_cart();
        
        item.setId(rs.getInt(Views.COL_SHOPING_CART_ID));
        item.setCustomer_id(rs.getInt(Views.COL_SHOPING_CART_CUSTOMER_ID));
        item.setProduct_id(rs.getInt(Views.COL_SHOPING_CART_PRODUCT_ID));
        item.setQuantity(rs.getInt(Views.COL_SHOPING_CART_QUANTITY));
        
        // Ánh xạ thêm các trường thông tin sản phẩm
        item.setProduct_name(rs.getString(Views.COL_PRODUCT_NAME)); 
        item.setPrice(rs.getDouble(Views.COL_PRODUCT_PRICE)); 
        item.setImg(rs.getString(Views.COL_PRODUCT_IMG)); 

        return item;
    }
}
