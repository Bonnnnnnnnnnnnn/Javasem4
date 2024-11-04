package com.warehouseManager.repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mapper.Warehouse_mapper;
import com.mapper.Warehouse_receipt_detail_mapper;
import com.mapper.Warehouse_recript_mapper;
import com.models.PageView;
import com.models.Warehouse;
import com.models.Warehouse_receipt;
import com.models.Warehouse_receipt_detail;
import com.utils.Views;

@Repository
public class WhReceiptAndDetailsRepository {
	@Autowired
	private final JdbcTemplate dbwhd;
	public WhReceiptAndDetailsRepository(JdbcTemplate jdbc) {
		this.dbwhd = jdbc;
	}
	
	//Phần Warehouse_receipt 
	
	public List<Warehouse_receipt> findAll(PageView ItemPage) {
	    try {
	        String str_query = String.format("SELECT wr.%s as id, wr.%s as name, wr.%s as Wh_Id, wr.%s as date, wr.%s as status, w.%s as wh_name " +
	                "FROM %s wr " +
	                "INNER JOIN %s w ON wr.%s = w.%s " +
	                "ORDER BY wr.%s DESC",
	                Views.COL_WAREHOUSE_RECEIPT_ID, Views.COL_WAREHOUSE_RECEIPT_NAME, 
	                Views.COL_WAREHOUSE_RECEIPT_IDWH, Views.COL_WAREHOUSE_RECEIPT_DATE,
	                Views.COL_WAREHOUSE_RECEIPT_STATUS, Views.COL_WAREHOUSE_NAME,
	                Views.TBL_WAREHOUSE_RECEIPT,
	                Views.TBL_WAREHOUSE,
	                Views.COL_WAREHOUSE_RECEIPT_IDWH, Views.COL_WAREHOUSE_ID,
	                Views.COL_WAREHOUSE_RECEIPT_ID);

	        if (ItemPage != null && ItemPage.isPaginationEnabled()) {
	            int count = dbwhd.queryForObject("SELECT COUNT(*) FROM " + Views.TBL_WAREHOUSE_RECEIPT, Integer.class);
	            int total_page = (int) Math.ceil((double) count / ItemPage.getPage_size());
	            ItemPage.setTotal_page(total_page);
	            return dbwhd.query(str_query + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
	                    new Warehouse_recript_mapper(),
	                    (ItemPage.getPage_current() - 1) * (ItemPage.getPage_size()),
	                    ItemPage.getPage_size());
	        } else {
	            return dbwhd.query(str_query, new Warehouse_recript_mapper());
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching warehouse receipts: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}
	
	public List<Warehouse> findAllWh() {
	    try {
	        String sql = "SELECT * FROM Warehouse";
	        return dbwhd.query(sql, (rs, rowNum) -> {
	            Warehouse item = new Warehouse();
	            item.setId(rs.getInt(Views.COL_WAREHOUSE_ID));
	            item.setName(rs.getString(Views.COL_WAREHOUSE_NAME));
	            item.setAddress(rs.getString(Views.COL_WAREHOUSE_ADDRESS));
	            item.setWh_type_id(rs.getInt(Views.COL_WAREHOUSE_TYPE_WAREHOUSE_ID));
	            return item;
	        });
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	public Warehouse_receipt findId(int id) {
	    try {
	        String sql = "SELECT wr.*, w.Name AS warehouse_name "
	                   + "FROM " + Views.TBL_WAREHOUSE_RECEIPT + " wr "
	                   + "LEFT JOIN Warehouse w ON wr." + Views.COL_WAREHOUSE_RECEIPT_IDWH + " = w.Id "
	                   + "WHERE wr." + Views.COL_WAREHOUSE_RECEIPT_ID + " = ?";

	        return dbwhd.queryForObject(sql, (rs, rowNum) -> {
	            Warehouse_receipt wr = new Warehouse_receipt();
	            wr.setId(rs.getInt(Views.COL_WAREHOUSE_RECEIPT_ID));
	            wr.setName(rs.getString(Views.COL_WAREHOUSE_RECEIPT_NAME));
	            wr.setWh_id(rs.getInt(Views.COL_WAREHOUSE_RECEIPT_IDWH));
	            wr.setStatus(rs.getString(Views.COL_WAREHOUSE_RECEIPT_STATUS));
	            Timestamp timestamp = rs.getTimestamp(Views.COL_WAREHOUSE_RECEIPT_DATE);
	            if (timestamp != null) {
	                wr.setDate(timestamp.toLocalDateTime());
	            }
	            wr.setWh_name(rs.getString("warehouse_name"));
	            return wr;
	        }, id);
	    } catch (EmptyResultDataAccessException e) {
	        System.err.println("No warehouse receipt found with ID: " + id);
	        return null;
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching warehouse receipt with ID: " + id + " - " + e.getMessage());
	        return null;
	    }
	}

	
	public boolean updateWhRe(Warehouse_receipt whr) {
		try {
			String sql = "UPDATE Warehouse_receipt SET Name = ? ,Wh_Id = ? ,Date = ? ,Status = ? WHERE Id= ?";
			Object[] params = {whr.getName() , whr.getWh_id(),whr.getDate() , whr.getStatus() , whr.getId()};
			int row = dbwhd.update(sql,params);
			return row > 0 ;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional
	public boolean addRequestOrderWithDetails(Warehouse_receipt receipt, List<Warehouse_receipt_detail> details) {
	    try {
	        String sql1 = "INSERT INTO " + Views.TBL_WAREHOUSE_RECEIPT + " (" +
	                Views.COL_WAREHOUSE_RECEIPT_NAME + ", " +
	                Views.COL_WAREHOUSE_RECEIPT_IDWH + ", " +
	                Views.COL_WAREHOUSE_RECEIPT_DATE + ", " + 
	                Views.COL_WAREHOUSE_RECEIPT_STATUS + ") VALUES (?, ?, ? ,? )";
	        
	        KeyHolder keyHolder = new GeneratedKeyHolder();
	        int result1 = dbwhd.update(connection -> {
	            var ps = connection.prepareStatement(sql1, new String[] { Views.COL_WAREHOUSE_RECEIPT_ID });
	            ps.setString(1, receipt.getName());
	            ps.setInt(2, receipt.getWh_id());
	            ps.setDate(3, java.sql.Date.valueOf(receipt.getDate().toLocalDate()));
	            ps.setString(4, receipt.getStatus());
	            return ps;
	        }, keyHolder);

	        int generatedId = keyHolder.getKey().intValue();

	        String sql2 = "INSERT INTO " + Views.TBL_WAREHOUSE_RECEIPT_DETAIL + " (" +
	                Views.COL_DETAIL_WAREHOUSE_RECEIPT_ID + ", " +
	                Views.COL_WAREHOUSE_RECEIPT_DETAIL_WH_PRICE + ", " +
	                Views.COL_WAREHOUSE_RECEIPT_DETAIL_QUANTITY + ") VALUES (?, ?, ?)";
	        
	        for (Warehouse_receipt_detail detail : details) {
	            detail.setWh_receipt_id(generatedId);
	            dbwhd.update(sql2,
	                detail.getWh_receipt_id(),
	                detail.getWh_price(),
	                detail.getQuantity()
	            );
	        }
	        return result1 > 0;  
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	// Phần Warehouse_receipt_detail
	
	public List<Warehouse_receipt_detail> findDetailsByReceiptId(int whReceiptId) {
	    String sql = "SELECT wrd.* "
	               + "FROM " + Views.TBL_WAREHOUSE_RECEIPT_DETAIL + " wrd "
	               + "WHERE wrd." + Views.COL_DETAIL_WAREHOUSE_RECEIPT_ID + " = ?";

	    return dbwhd.query(sql, (rs, rowNum) -> {
	        Warehouse_receipt_detail wrd = new Warehouse_receipt_detail();
	        wrd.setId(rs.getInt(Views.COL_WAREHOUSE_RECEIPT_DETAIL_ID));
	        wrd.setWh_receipt_id(rs.getInt(Views.COL_DETAIL_WAREHOUSE_RECEIPT_ID));
	        wrd.setQuantity(rs.getInt(Views.COL_WAREHOUSE_RECEIPT_DETAIL_QUANTITY));
	        wrd.setWh_price(rs.getDouble(Views.COL_WAREHOUSE_RECEIPT_DETAIL_WH_PRICE));
	        return wrd;
	    }, whReceiptId);
	}

	public boolean updateWhDetails(Warehouse_receipt_detail wrd) {
		try {
			String sql = "UPDATE Warehouse_receipt_detail SET Wh_receiptId = ? ,Wh_price = ? ,Quantity = ? WHERE Id= ?";
			Object[] params = {wrd.getWh_receipt_id() , wrd.getWh_price(),wrd.getQuantity() , wrd.getId()};
			int row = dbwhd.update(sql,params);
			return row > 0 ;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean addWhDetail(Warehouse_receipt_detail wrd) {
	    try {
	        String sql = "INSERT INTO " + Views.TBL_WAREHOUSE_RECEIPT_DETAIL + " (" +
	                     Views.COL_DETAIL_WAREHOUSE_RECEIPT_ID + ", " +
	                     Views.COL_WAREHOUSE_RECEIPT_DETAIL_WH_PRICE + ", " +
	                     Views.COL_WAREHOUSE_RECEIPT_DETAIL_QUANTITY + 
	                     ") VALUES (?, ?, ?)";
	        Object[] params = {wrd.getWh_receipt_id() , wrd.getWh_price(),wrd.getQuantity()};
			int row = dbwhd.update(sql,params);
			return row > 0 ;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

}
