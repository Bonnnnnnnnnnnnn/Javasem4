package com.customer.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Coupon_mapper;
import com.models.Coupon;
import com.utils.Views;


@Repository
public class CouponRepository {
	@Autowired
	JdbcTemplate db;

	public Coupon findCouponByCode(String couponCode) {
	    try {
	        // Câu lệnh SQL để tìm coupon dựa trên code
	        String sql = String.format("SELECT * FROM %s WHERE %s = ?",
	                Views.TBL_DISCOUNT, Views.COL_DISCOUNT_CODE);

	        return db.queryForObject(sql, new Object[]{couponCode}, new Coupon_mapper());
	    } catch (DataAccessException e) {
	       
	        return null;
	    }
	}

	public Coupon findCouponByid(int id) {
	    try {

	        String sql = String.format("SELECT * FROM %s WHERE %s = ?",
	                Views.TBL_DISCOUNT, Views.COL_DISCOUNT_ID);

	        return db.queryForObject(sql, new Object[]{id}, new Coupon_mapper());
	    } catch (DataAccessException e) {
	       
	        return null;
	    }
	}
	public double calculateTotalCartValueWithCoupon(double totalCartValue, Coupon cp) {

	    double discount = 0.0;

	        if (cp != null) {
	            // Kiểm tra loại discount của coupon
	            if (cp.getDiscountPercentage() != null) {

	                discount = (cp.getDiscountPercentage() / 100.0) * totalCartValue;
	            } else if (cp.getDiscountAmount() != null) {

	                discount = cp.getDiscountAmount();
	            }

	            if (cp.getMaxDiscountAmount() != null && discount > cp.getMaxDiscountAmount()) {
	                discount = cp.getMaxDiscountAmount();
	            }
	        }

	    return discount;
	}
}
