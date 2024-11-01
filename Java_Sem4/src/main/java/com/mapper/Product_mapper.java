package com.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.models.Product;
import com.utils.Views;

public class Product_mapper implements RowMapper<Product> {
    public Product mapRow(ResultSet rs, int RowNum) throws SQLException {
        Product item = new Product();
        item.setId(rs.getInt(Views.COL_PRODUCT_ID));
        item.setBrand_id(rs.getInt(Views.COL_PRODUCT_BRAND_ID));
        item.setCate_id(rs.getInt(Views.COL_PRODUCT_CATE_ID));
        item.setConversion_id(rs.getInt(Views.COL_PRODUCT_CONVERSION_ID));
        item.setProduct_name(rs.getString(Views.COL_PRODUCT_NAME));
        item.setDescription(rs.getString(Views.COL_PRODUCT_DESCIPTION));
        item.setImg(rs.getString(Views.COL_PRODUCT_IMG));
        item.setStatus(rs.getString(Views.COL_PRODUCT_STATUS));
        item.setPrice(rs.getDouble(Views.COL_PRODUCT_PRICE));
        item.setWarranty_period(rs.getInt(Views.COL_PRODUCT_WARRANTY_PERIOD));
        item.setId_Conversion(rs.getInt(Views.COL_PRODUCT_ID_CONVERSION));
        
        item.setBrandName(rs.getString("brand_name"));
        item.setCategoryName(rs.getString("category_name"));
        
        return item;
    }
}

