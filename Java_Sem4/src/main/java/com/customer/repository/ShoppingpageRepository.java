package com.customer.repository;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Product_mapper;
import com.models.Customer;
import com.models.PageView;
import com.models.Product;
import com.utils.SecurityUtility;
import com.utils.Views;

@Repository
public class ShoppingpageRepository {
	
	private final JdbcTemplate dbpro;
	
	public List<Product> findAll(PageView ItemPage) {
        try {
        	String str_query = String.format("SELECT p.*, b.%s as brand_name, u.%s as unit_name, c.%s as category_name " +
                    "FROM %s p " +
                    "INNER JOIN %s b ON p.%s = b.%s " +
                    "INNER JOIN %s u ON p.%s = u.%s " +
                    "INNER JOIN %s c ON p.%s = c.%s " +
                    "ORDER BY p.%s DESC",
                    Views.COL_BRAND_NAME, Views.COL_UNIT_NAME, Views.COL_CATEGORY_NAME, Views.TBL_PRODUCT,
                    Views.TBL_BRAND, Views.COL_PRODUCT_BRAND_ID, Views.COL_BRAND_ID,
                    Views.TBL_UNIT, Views.COL_PRODUCT_UNIT_ID, Views.COL_UNIT_ID,
                    Views.TBL_CATEGORY, Views.COL_PRODUCT_CATE_ID, Views.COL_CATEGORY_ID,
                    Views.COL_PRODUCT_ID);
            if (ItemPage != null && ItemPage.isPaginationEnabled()) {
                int count = dbpro.queryForObject("SELECT COUNT(*) FROM " + Views.TBL_PRODUCT, Integer.class);
                int total_page = (int) Math.ceil((double) count / ItemPage.getPage_size());
                ItemPage.setTotal_page(total_page);

                return dbpro.query(str_query + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                        new Product_mapper(),
                        (ItemPage.getPage_current() - 1) * (ItemPage.getPage_size()),
                        ItemPage.getPage_size());
            } else {
                return dbpro.query(str_query, new Product_mapper());
            }
        } catch (DataAccessException e) {
            System.err.println("Error fetching products: " + e.getMessage());
            return Collections.emptyList();
        }
    }
	
}
