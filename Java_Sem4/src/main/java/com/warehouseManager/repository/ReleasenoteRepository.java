package com.warehouseManager.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mapper.Request_detail_mapper;
import com.mapper.Request_mapper;
import com.mapper.Warehouse_releasenote_mapper;
import com.mapper.Warehouse_rn_detail_mapper;
import com.models.Request;
import com.models.Request_detail;
import com.models.Warehouse_releasenote;
import com.models.Warehouse_rn_detail;
import com.utils.Views;

@Repository
public class ReleasenoteRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void updateEmployeeId(int requestId, int employeeId) {
		String sql = "UPDATE Request SET Employee_Id = ? WHERE Id = ?";
		jdbcTemplate.update(sql, employeeId, requestId);
	}

	public void updateStatusToPending(int requestId) {
		String sql = "UPDATE Request SET Status = 'Processing' WHERE Id = ?";
		jdbcTemplate.update(sql, requestId);
	}


	public List<Request> findAllByEmployeeId(int employeeId) {
		String sql = "SELECT * FROM Request WHERE Employee_Id = ?";
		return jdbcTemplate.query(sql, new Request_mapper(), employeeId);
	}
	
	public List<Warehouse_releasenote> findWareAllByEmployeeId(int employeeId) {
		String sql = "SELECT * FROM Warehouse_releasenote WHERE Employee_Id = ?";
		return jdbcTemplate.query(sql, new Warehouse_releasenote_mapper(), employeeId);
	}

	public List<Request> findAllByEmployeeIdIsNull() {
		String sql = "SELECT * FROM Request WHERE Employee_Id IS NULL";
		return jdbcTemplate.query(sql, new Request_mapper());
	}
	
	public void deleteEmployeeIdByRequestId(int id) {
	    String sql = "UPDATE Request SET Employee_Id = NULL, status = 'canceled' WHERE Id = ?";
	    jdbcTemplate.update(sql, id);
	}

    public Warehouse_releasenote findWarehouseReleasenoteById(int id) {
        String sql = "SELECT * FROM Warehouse_releasenote WHERE Id = ?";
        List<Warehouse_releasenote> releasenotes = jdbcTemplate.query(sql, (rs, rowNum) -> {
        	Warehouse_releasenote releasenote = new Warehouse_releasenote();
        	releasenote.setId(rs.getInt(Views.COL_WAREHOUSE_RELEASENOTE_ID));
        	releasenote.setName(rs.getString(Views.COL_WAREHOUSE_RELEASENOTE_NAME));
        	releasenote.setDate(rs.getTimestamp(Views.COL_WAREHOUSE_RELEASENOTE_DATE).toLocalDateTime());
        	releasenote.setStatusWr(rs.getString(Views.COL_WAREHOUSE_RELEASENOTE_STATUS));
            return releasenote;
        }, id);
        
        return releasenotes.stream().findFirst().orElse(null);
    }
    
	
	public List<Warehouse_rn_detail> findWarehouseRnDetail(int wgrnId) {
	    String sql = "SELECT wd.*, p.Product_name AS Product_name " +
	                 "FROM Warehouse_rn_detail wd " +
	                 "JOIN Product p ON wd.id_product = p.id " +
	                 "WHERE wd.Wgrn_Id = ?";
	    return jdbcTemplate.query(sql, (rs, rowNum) -> {
	    	Warehouse_rn_detail detail = new Warehouse_rn_detail();
	        detail.setWgrn_id(rs.getInt(Views.COL_WAREHOUSE_RNOTE_ID));
	        detail.setId_product(rs.getInt(Views.COL_WAREHOUSE_RN_DETAIL_PRODUCTID));
	        detail.setQuantity(rs.getInt(Views.COL_WAREHOUSE_RN_DETAIL_QUANTITY));
	        detail.setStatus(rs.getString(Views.COL_WAREHOUSE_RN_DETAIL_STATUS));
	        detail.setProductName(rs.getString("Product_name")); 
	        return detail;
	    }, wgrnId);
	}
	
	public List<Request_detail> findDetailsByRequestId(int requesId) {
	    String sql = "SELECT rd.*, p.Product_name AS Product_name " +
	                 "FROM Request_detail rd " +
	                 "JOIN Product p ON rd.id_product = p.id " +
	                 "WHERE rd.Request_Id = ?";
	    return jdbcTemplate.query(sql, (rs, rowNum) -> {
	        Request_detail detail = new Request_detail();
	        detail.setRequest_id(rs.getInt(Views.COL_REQUEST_DETAIL_ID));
	        detail.setId_product(rs.getInt(Views.COL_REQUEST_DETAIL_ID_PRODUCT));
	        detail.setQuantity_requested(rs.getInt(Views.COL_REQUEST_DETAIL_QUANTITY_REQUESTED));
	        detail.setStatus(rs.getString(Views.COL_REQUEST_DETAIL_STATUS));
	        detail.setProductName(rs.getString("Product_name")); 
	        return detail;
	    }, requesId);
	}

	
	public Request findRequestByIdEmp(int id, int employeeId) {
	    String sql = "SELECT * FROM Request WHERE Id = ? AND Employee_Id = ?";

	    List<Request> requests = jdbcTemplate.query(sql, (rs, rowNum) -> {
	        Request request = new Request();
	        request.setId(rs.getInt(Views.COL_REQUEST_ID));
	        request.setEmployee_Id(rs.getInt(Views.COL_REQUEST_EMPLOYEE_ID));
	        request.setName(rs.getString(Views.COL_REQUEST_NAME));
	        request.setDate(rs.getTimestamp(Views.COL_REQUEST_DATE).toLocalDateTime());
	        request.setStatusRequest(rs.getString(Views.COL_REQUEST_STATUS));
	        return request;
	    }, id, employeeId);


	    if (requests.isEmpty()) {
	        return null;
	    }

	    return requests.get(0);  
	}



    @Transactional
    public boolean addWarehouseReleasenote(Warehouse_releasenote releasenote, List<Warehouse_rn_detail> details) {
        try {
            String sql1 = "INSERT INTO Warehouse_releasenote (Name, Date, Status, Request_Id, Employee_Id) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int result1 = jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql1, new String[] { "Id" });
                ps.setString(1, releasenote.getName());
                ps.setDate(2, java.sql.Date.valueOf(releasenote.getDate().toLocalDate()));
                ps.setString(3, releasenote.getStatusWr());
                ps.setInt(4, releasenote.getRequest_id());
                ps.setInt(5, releasenote.getEmployee_Id());
                return ps;
            }, keyHolder);

            int generatedId = keyHolder.getKey().intValue();
            
            releasenote.setId(generatedId);

            String sql2 = "INSERT INTO Warehouse_rn_detail (Wgrn_Id, Status, Id_product, Quantity) VALUES ( ?, ?, ?, ?)";
            for (Warehouse_rn_detail detail : details) {
                detail.setWgrn_id(generatedId);
                jdbcTemplate.update(sql2,
                    detail.getWgrn_id(),
                    detail.getStatus(),
                    detail.getId_product(),
                    detail.getQuantity()
                );
                
                String sql3 = "UPDATE Stock SET Quantity = Quantity - ? WHERE Id_product = ?";
                jdbcTemplate.update(sql3, detail.getQuantity(), detail.getId_product());
            }
            
            return result1 > 0;  
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Request findRequestById(int requestId) {
        String sql = "SELECT * FROM " + Views.TBL_REQUEST + " WHERE " + Views.COL_REQUEST_ID + " = ?";
        
        return jdbcTemplate.queryForObject(sql, new Request_mapper(), requestId);
    }

        
	
	  public int QuantityByReleaseNoteId(int warehouseReleaseNoteId) { 
		  String sql = "SELECT SUM(wd." + Views.COL_WAREHOUSE_RN_DETAIL_QUANTITY +
		  ") AS totalQuantity " + "FROM " + Views.TBL_WAREHOUSE_RN_DETAIL + " wd " +
		  "JOIN " + Views.TBL_WAREHOUSE_RELEASENOTE + " wr ON wd." +
		  Views.COL_WAREHOUSE_RNOTE_ID + " = wr." + Views.COL_WAREHOUSE_RELEASENOTE_ID
		  + " " + "WHERE wr." + Views.COL_WAREHOUSE_RELEASENOTE_ID + " = ?";
		  
		  Integer totalQuantity = jdbcTemplate.queryForObject(sql, Integer.class,
		  warehouseReleaseNoteId);
		  
		  return (totalQuantity != null) ? totalQuantity : 0; 
	  
	  }
	  	  
	  
	  
	  public int QuantityRequested(int requestId) { 
		  String sql = "SELECT SUM(" +
		  Views.COL_REQUEST_DETAIL_QUANTITY_REQUESTED + ") AS totalQuantity " + "FROM "
		  + Views.TBL_REQUEST_DETAIL + " " + "WHERE " +
		  Views.COL_REQUEST_DETAIL_REQUEST_ID + " = ?";
		  
		  Integer totalQuantity = jdbcTemplate.queryForObject(sql, Integer.class,
		  requestId);
		  
		  return (totalQuantity != null) ? totalQuantity : 0; 
	  }
	  
	  public boolean isRequestComplete(int ReleaseDetailId, int requestId) {
		    int totalReleasedQuantity = QuantityByReleaseNoteId(ReleaseDetailId);
		    int totalRequestedQuantity = QuantityRequested(requestId);
		    

		    if(totalReleasedQuantity >= totalRequestedQuantity) {

		    	String sql = "UPDATE Request SET Status = 'complete' WHERE Id = ?";
		    	jdbcTemplate.update(sql, requestId);
		    	return true;
		    }
		    return false;
		}

}
