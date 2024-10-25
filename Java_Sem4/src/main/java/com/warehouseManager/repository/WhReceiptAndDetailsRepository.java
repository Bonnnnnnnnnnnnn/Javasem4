package com.warehouseManager.repository;

import java.sql.Types;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
	public List<Warehouse_receipt> findAll(PageView ItemPage) {
	    try {
	        String str_query = String.format("SELECT wr.%s as id, wr.%s as name, wr.%s as Wh_Id, wr.%s as date, w.%s as wh_name " +
	                "FROM %s wr " +
	                "INNER JOIN %s w ON wr.%s = w.%s " +
	                "ORDER BY wr.%s DESC",
	                Views.COL_WAREHOUSE_RECEIPT_ID, Views.COL_WAREHOUSE_RECEIPT_NAME, 
	                Views.COL_WAREHOUSE_RECEIPT_IDWH, Views.COL_WAREHOUSE_RECEIPT_DATE,
	                Views.COL_WAREHOUSE_NAME,
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

	public boolean saveWhRe(Warehouse_receipt wre) {
		try {
			String sql = "INSERT INTO Warehouse_receipt ( Name, Wh_Id, Date) VALUES (?,?,?)";
			int row = dbwhd.update(sql, wre.getName(),wre.getWh_id(),wre.getDate());
			return row > 0 ;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean saveWhDetail(Warehouse_receipt_detail wrd) {
		try {
			String sql = "INSERT INTO Warehouse_receipt_detail ( Wh_receiptId, Wh_price, Quantity) VALUES (? , ? , ?)";
			int row = dbwhd.update(sql, wrd.getWh_receipt_id(),wrd.getWh_price(),wrd.getQuantity());
			return row > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean updateWhRe(Warehouse_receipt whr) {
		try {
			String sql = "UPDATE Warehouse_receipt SET Name = ? ,Wh_Id = ? ,Date = ? WHERE Id= ?";
			Object[] params = {whr.getName() , whr.getWh_id(),whr.getDate() , whr.getId()};
			int row = dbwhd.update(sql,params);
			return row > 0 ;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
	@Transactional
    public boolean deleteWarehouseAndDetails(int idwhr) {
        try {
            String sqlDetail = "DELETE FROM Warehouse_receipt_detail WHERE Id = ?";
            Object[] paramsDetail = {idwhr};
            int[] typesDetail = {Types.INTEGER};
            dbwhd.update(sqlDetail, paramsDetail, typesDetail);
            //
            String sqlReceipt = "DELETE FROM Warehouse_receipt WHERE Id = ?";
            Object[] paramsReceipt = {idwhr};
            int[] typesReceipt = {Types.INTEGER};
            dbwhd.update(sqlReceipt, paramsReceipt, typesReceipt);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
