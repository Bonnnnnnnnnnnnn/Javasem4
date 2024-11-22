package com.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.models.Coupon;
import com.utils.Views;

public class Coupon_mapper implements RowMapper<Coupon> {
    @Override
    public Coupon mapRow(ResultSet rs, int rowNum) throws SQLException {
        Coupon coupon = new Coupon();
        coupon.setId(rs.getInt(Views.COL_COUPON_ID));
        coupon.setCode(rs.getString(Views.COL_COUPON_CODE));
        coupon.setDiscountPercentage(rs.getDouble(Views.COL_COUPON_DISCOUNT_PERCENTAGE));
        coupon.setDiscountAmount(rs.getDouble(Views.COL_COUPON_DISCOUNT_AMOUNT));
        coupon.setExpiryDate(rs.getDate(Views.COL_COUPON_EXPIRY_DATE).toLocalDate());
        coupon.setMinOrderValue(rs.getDouble(Views.COL_COUPON_MIN_ORDER_VALUE));
        coupon.setMaxDiscountAmount(rs.getDouble(Views.COL_COUPON_MAX_DISCOUNT_AMOUNT));
        coupon.setStatus(rs.getString(Views.COL_COUPON_STATUS));
        return coupon;
    }
}