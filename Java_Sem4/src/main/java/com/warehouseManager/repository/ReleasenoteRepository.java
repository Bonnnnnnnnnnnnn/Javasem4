package com.warehouseManager.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mapper.Order_mapper;
import com.mapper.Request_mapper;
import com.mapper.Warehouse_releasenote_mapper;
import com.models.Order;
import com.models.Order_detail;
import com.models.PageView;
import com.models.Request;
import com.models.Request_detail;
import com.models.Warehouse_releasenote;
import com.models.Warehouse_rn_detail;
import com.utils.Views;

@Repository
public class ReleasenoteRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	//update employee_id bang request
	public void updateEmployeeId(int requestId, int employeeId) {
		String sql = "UPDATE Request SET Employee_Id = ? WHERE Id = ?";
		jdbcTemplate.update(sql, employeeId, requestId);
	}
	
	//update employee_id bang order
	public void updateEmployeeIdInOrder(int orderId, int employeeId) {
		String sql = "UPDATE [Order] SET Employee_Id = ? WHERE Id = ?";
		jdbcTemplate.update(sql, employeeId, orderId);
	}
	
	// update status order
	public void updateStatusInOrder(int orderId) {
	    String sql = "UPDATE [Order] SET Status = 'Processing' WHERE Id = ?";
	    jdbcTemplate.update(sql, orderId);

	    String sql1 = "UPDATE [Order_detail] SET Status = 'Processing' WHERE Order_Id = ?";
	    jdbcTemplate.update(sql1, orderId); 
	}


	// update status request
	public void updateStatusToProcessing(int requestId) {
		String sql = "UPDATE Request SET Status = 'Processing' WHERE Id = ?";
		jdbcTemplate.update(sql, requestId);
	}

	//hien thi bang request theo employee_id and order_id = null
	public List<Request> findAllByEmployeeId(PageView itemPage, int employeeId) {
	    try {
	        String sql = String.format(
	                "SELECT * FROM %s WHERE %s = ? AND %s IS NULL AND %s IS NULL ORDER BY %s DESC",
	                Views.TBL_REQUEST, Views.COL_REQUEST_EMPLOYEE_ID, Views.COL_REQUEST_ORDERID, Views.COL_REQUEST_TYPE, Views.COL_REQUEST_ID);

	        if (itemPage != null && itemPage.isPaginationEnabled()) {
	            int count = jdbcTemplate.queryForObject(
	                    String.format("SELECT COUNT(*) FROM %s WHERE %s = ? AND %s IS NULL AND %s IS NULL",
	                            Views.TBL_REQUEST, Views.COL_REQUEST_EMPLOYEE_ID, Views.COL_REQUEST_ORDERID, Views.COL_REQUEST_TYPE),
	                    Integer.class, employeeId);
	            int totalPage = (int) Math.ceil((double) count / itemPage.getPage_size());
	            itemPage.setTotal_page(totalPage);

	            return jdbcTemplate.query(sql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
	                    new Request_mapper(),
	                    employeeId,
	                    (itemPage.getPage_current() - 1) * itemPage.getPage_size(),
	                    itemPage.getPage_size());
	        } else {
	            return jdbcTemplate.query(sql, new Request_mapper(), employeeId);
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching requests: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}
	//hien thi bang request theo employee_id and Type =?
	public List<Request> findAllByEmployeeIdAndType(PageView itemPage, int employeeId) {
	    try {
	        String sql = String.format(
	                "SELECT * FROM %s WHERE %s = ? AND (%s = 'Request' OR %s = 'Order') ORDER BY %s DESC",
	                Views.TBL_REQUEST, Views.COL_REQUEST_EMPLOYEE_ID, Views.COL_REQUEST_TYPE, Views.COL_REQUEST_TYPE, Views.COL_REQUEST_ID);

	        if (itemPage != null && itemPage.isPaginationEnabled()) {
	            int count = jdbcTemplate.queryForObject(
	                    String.format("SELECT COUNT(*) FROM %s WHERE %s = ? AND (%s = 'Request' OR %s = 'Order')",
	                            Views.TBL_REQUEST, Views.COL_REQUEST_EMPLOYEE_ID, Views.COL_REQUEST_TYPE, Views.COL_REQUEST_TYPE),
	                    Integer.class, employeeId);
	            int totalPage = (int) Math.ceil((double) count / itemPage.getPage_size());
	            itemPage.setTotal_page(totalPage);

	            return jdbcTemplate.query(sql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
	                    new Request_mapper(),
	                    employeeId,
	                    (itemPage.getPage_current() - 1) * itemPage.getPage_size(),
	                    itemPage.getPage_size());
	        } else {
	            return jdbcTemplate.query(sql, new Request_mapper(), employeeId);
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching requests: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}

	
	//hien thi bang order theo employee_id
	public List<Order> findOrderByEmployeeId(PageView itemPage, int employeeId) {
	    try {
	        String sql = String.format("SELECT * FROM [%s] WHERE %s = ? AND %s = 'Processing' ORDER BY %s ASC",
	                Views.TBL_ORDER, Views.COL_ORDER_EMPLOYEE,Views.COL_ORDER_STATUS, Views.COL_ORDER_ID);

	        if (itemPage != null && itemPage.isPaginationEnabled()) {

	            int count = jdbcTemplate.queryForObject(
	                    String.format("SELECT COUNT(*) FROM [%s] WHERE %s = ?", Views.TBL_ORDER, Views.COL_ORDER_EMPLOYEE),
	                    Integer.class, employeeId);
	            int totalPage = (int) Math.ceil((double) count / itemPage.getPage_size());
	            itemPage.setTotal_page(totalPage);


	            return jdbcTemplate.query(sql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
	                    new Order_mapper(),
	                    employeeId,
	                    (itemPage.getPage_current() - 1) * itemPage.getPage_size(),
	                    itemPage.getPage_size());
	        } else {
	            return jdbcTemplate.query(sql, new Order_mapper(), employeeId);
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching requests: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}

	//hien thi bang warehouse_releasenote theo employee_id
	public List<Warehouse_releasenote> findWareAllByEmployeeId(PageView itemPage, int employeeId) {
	    try {
	    	String sql = String.format ("SELECT * FROM %s WHERE %s = ? ORDER BY %s DESC",
					Views.TBL_WAREHOUSE_RELEASENOTE, Views.COL_WAREHOUSE_RELEASENOTE_EMPLOYEEID, Views.COL_WAREHOUSE_RELEASENOTE_ID);

	        if (itemPage != null && itemPage.isPaginationEnabled()) {

	            int count = jdbcTemplate.queryForObject(
	                    String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", Views.TBL_WAREHOUSE_RELEASENOTE, Views.COL_WAREHOUSE_RELEASENOTE_EMPLOYEEID),
	                    Integer.class, employeeId);
	            int totalPage = (int) Math.ceil((double) count / itemPage.getPage_size());
	            itemPage.setTotal_page(totalPage);


	            return jdbcTemplate.query(sql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
	                    new Warehouse_releasenote_mapper(),
	                    employeeId,
	                    (itemPage.getPage_current() - 1) * itemPage.getPage_size(),
	                    itemPage.getPage_size());
	        } else {
	        	return jdbcTemplate.query(sql, new Warehouse_releasenote_mapper(), employeeId);
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching requests: " + e.getMessage());
	        return Collections.emptyList();
	    }
		
		
		
	}
	
	// hien thi bang request khi employee_id = null
	public List<Request> findAllByEmployeeIdIsNull(PageView itemPage) {
	    try {
			String sql =String.format( "SELECT * FROM %s WHERE %s IS NULL ORDER BY %s DESC",
					Views.TBL_REQUEST, Views.COL_REQUEST_EMPLOYEE_ID, Views.COL_REQUEST_ID);

	        if (itemPage != null && itemPage.isPaginationEnabled()) {

	            int count = jdbcTemplate.queryForObject(
	                    String.format("SELECT COUNT(*) FROM %s WHERE %s IS NULL", Views.TBL_REQUEST, Views.COL_REQUEST_EMPLOYEE_ID),
	                    Integer.class);
	            int totalPage = (int) Math.ceil((double) count / itemPage.getPage_size());
	            itemPage.setTotal_page(totalPage);


	            return jdbcTemplate.query(sql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
	                    new Request_mapper(),
	                    (itemPage.getPage_current() - 1) * itemPage.getPage_size(),
	                    itemPage.getPage_size());
	        } else {
	    		return jdbcTemplate.query(sql, new Request_mapper());
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching requests: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}
	
	// hien thi bang order khi employee_id = null
	public List<Order> findAllOrderByEmployeeIdIsNull(PageView itemPage) {
	    try {
	        String sql = String.format(
	                "SELECT * FROM [%s] WHERE %s IS NULL AND %s = 'Waiting for confirmation' ORDER BY %s DESC",
	                Views.TBL_ORDER, Views.COL_ORDER_EMPLOYEE, Views.COL_ORDER_STATUS, Views.COL_ORDER_ID);

	        if (itemPage != null && itemPage.isPaginationEnabled()) {
	            int count = jdbcTemplate.queryForObject(
	                    String.format("SELECT COUNT(*) FROM [%s] WHERE %s IS NULL AND %s = 'Waiting for confirmation'",
	                            Views.TBL_ORDER, Views.COL_ORDER_EMPLOYEE, Views.COL_ORDER_STATUS),
	                    Integer.class);
	            int totalPage = (int) Math.ceil((double) count / itemPage.getPage_size());
	            itemPage.setTotal_page(totalPage);

	            return jdbcTemplate.query(sql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
	                    new Order_mapper(),
	                    (itemPage.getPage_current() - 1) * itemPage.getPage_size(),
	                    itemPage.getPage_size());
	        } else {
	            return jdbcTemplate.query(sql, new Order_mapper());
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching requests: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}

	//xoa employee_id bang request
	public void deleteEmployeeIdByRequestId(int id) {
	    String sql = "UPDATE Request SET Employee_Id = NULL, status = 'Pending Approval' WHERE Id = ?";
	    jdbcTemplate.update(sql, id);
	}
	
	//xoa employee_id bang order
	public void deleteEmployeeIdByOrderId(int id) {
	    String sql = "UPDATE [Order] SET Employee_Id = NULL, status = 'canceled' WHERE Id = ?";
	    jdbcTemplate.update(sql, id);
	}
	
	//hien thi bang order theo id
	public Order findOrderById(int id) {
        String sql = "SELECT * FROM [Order] WHERE Id = ?";
        List<Order> orders = jdbcTemplate.query(sql, (rs, rowNum) -> {
        	Order order = new Order();
        	order.setId(rs.getInt(Views.COL_ORDER_ID));
        	order.setCus_Name(rs.getString(Views.COL_ORDER_CUSNAME));
        	order.setDate(rs.getTimestamp(Views.COL_REQUEST_DATE).toLocalDateTime().toLocalDate());
        	order.setStatus(rs.getString(Views.COL_ORDER_STATUS));
        	order.setOrderID(rs.getString(Views.COL_ORDER_ORDERID));
            return order;
        }, id);
        
        return orders.stream().findFirst().orElse(null);
	}
	
	// hiển thị bảng Order_detail
	public List<Order_detail> findOrderDetail(int OrderId) {
	    String sql = "SELECT od.*, p.Product_name AS Product_name " +
	                 "FROM Order_detail od " +
	                 "JOIN Product p ON od.Product_Id = p.id " +
	                 "WHERE od.Order_Id = ?";
	    return jdbcTemplate.query(sql, (rs, rowNum) -> {
	    	Order_detail detail = new Order_detail();
	        detail.setOrder_id(rs.getInt(Views.COL_ORDER_DETAIL_ORDER_ID));
	        detail.setProduct_Id(rs.getInt(Views.COL_ORDER_DETAIL_PRODUCT_ID));
	        detail.setQuantity(rs.getInt(Views.COL_ORDER_DETAIL_QUANTITY));
	        detail.setStatus(rs.getString(Views.COL_ORDER_DETAIL_STATUS));
	        detail.setProduct_name(rs.getString(Views.COL_PRODUCT_NAME)); 
	        return detail;
	    }, OrderId);
	}

	//hien thi bang warehouse_releate theo id
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
    
	// hiển thị bảng Warehouse_rn_detail

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
	        detail.setProductName(rs.getString(Views.COL_PRODUCT_NAME));  
	        return detail;
	    }, wgrnId);
	}
	
	// hiển thị bảng Request_detail
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
	        detail.setQuantity_exported(rs.getInt(Views.COL_REQUEST_DETAIL_QUANTITY_EXPORTED));
	        detail.setStatus(rs.getString(Views.COL_REQUEST_DETAIL_STATUS));
	        detail.setProductName(rs.getString(Views.COL_PRODUCT_NAME)); 
	        return detail;
	    }, requesId);
	}

	// hiển thị request theo employee và id
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
	// hiển thị order theo employee và id
	public Order findOrderByIdEmp(int id, int employeeId) {
	    String sql = "SELECT * FROM [Order] WHERE Id = ? AND Employee_Id = ?";

	    List<Order> orders = jdbcTemplate.query(sql, (rs, rowNum) -> {
	    	Order order = new Order();
	    	order.setId(rs.getInt(Views.COL_ORDER_ID));
	    	order.setEmployee_id(rs.getInt(Views.COL_ORDER_EMPLOYEE));
	    	order.setCus_Name(rs.getString(Views.COL_ORDER_CUSNAME));
	    	order.setDate(rs.getTimestamp(Views.COL_REQUEST_DATE).toLocalDateTime().toLocalDate());
	    	order.setStatus(rs.getString(Views.COL_ORDER_STATUS));
	    	order.setOrderID(rs.getString(Views.COL_ORDER_ORDERID));
	        return order;
	    }, id, employeeId);


	    if (orders.isEmpty()) {
	        return null;
	    }

	    return orders.get(0);  
	}


	//add warehouse_releasenote theo RequestId
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
    
	//add warehouse_releasenote theo OrderId

    @Transactional
    public boolean addWarehouseReleasenoteByOrder(Warehouse_releasenote releasenote, List<Warehouse_rn_detail> details) {
        try {
        	
        	
            String sql1 = "INSERT INTO Warehouse_releasenote (Name, Date, Status, Order_Id, Employee_Id) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int result1 = jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql1, new String[] { "Id" });
                ps.setString(1, releasenote.getName());
                ps.setDate(2, java.sql.Date.valueOf(releasenote.getDate().toLocalDate()));
                ps.setString(3, releasenote.getStatusWr());
                ps.setInt(4, releasenote.getOrder_id());
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
    
    //hiển thị bảng request theo id
    public Request findRequestById(int requestId) {
        String sql = "SELECT * FROM " + Views.TBL_REQUEST + " WHERE " + Views.COL_REQUEST_ID + " = ?";
        
        return jdbcTemplate.queryForObject(sql, new Request_mapper(), requestId);
    }

        
	// số lượng warehouse_rn_detail
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
	  	  
		// số lượng order_detail
	  public int QuantityOrder(int orderId) { 
		  String sql = "SELECT SUM(" +
		  Views.COL_ORDER_DETAIL_QUANTITY + ") AS totalQuantity " + "FROM "
		  + Views.TBL_ORDER_DETAIL + " " + "WHERE " +
		  Views.COL_ORDER_DETAIL_ORDER_ID + " = ?";
		  
		  Integer totalQuantity = jdbcTemplate.queryForObject(sql, Integer.class,
				  orderId);
		  
		  return (totalQuantity != null) ? totalQuantity : 0; 
	  }
	  
	// số lượng request_detail QuantityRequested
	  public int QuantityRequested(int requestId) { 
		  String sql = "SELECT SUM(" +
		  Views.COL_REQUEST_DETAIL_QUANTITY_REQUESTED + ") AS totalQuantity " + "FROM "
		  + Views.TBL_REQUEST_DETAIL + " " + "WHERE " +
		  Views.COL_REQUEST_DETAIL_REQUEST_ID + " = ?";
		  
		  Integer totalQuantity = jdbcTemplate.queryForObject(sql, Integer.class,
		  requestId);
		  
		  return (totalQuantity != null) ? totalQuantity : 0; 
	  }
	  
		// số lượng request_detail QuantityExported
	  public int QuantityExported(int requestId) { 
		  String sql = "SELECT SUM(" +
		  Views.COL_REQUEST_DETAIL_QUANTITY_EXPORTED + ") AS totalQuantity " + "FROM "
		  + Views.TBL_REQUEST_DETAIL + " " + "WHERE " +
		  Views.COL_REQUEST_DETAIL_REQUEST_ID + " = ?";
		  
		  Integer totalQuantity = jdbcTemplate.queryForObject(sql, Integer.class,
		  requestId);
		  
		  return (totalQuantity != null) ? totalQuantity : 0; 
	  }
	  
	  // so sánh quantity vs request Id đổi status
	  public boolean isRequestComplete( int requestId) {
		    int totalQuantityExported = QuantityExported(requestId);
		    int totalRequestedQuantity = QuantityRequested(requestId);
		    
		    
		    if(totalQuantityExported >= totalRequestedQuantity) {

		    	String sql = "UPDATE Request SET Status = 'Shipping' WHERE Id = ?";
		    	jdbcTemplate.update(sql, requestId);
		    	return true;
		    } else {
		    	
		    	String sql = "UPDATE Request Set Type = 'Request' WHERE Id = ?";
		    	jdbcTemplate.update(sql, requestId);
		    }
		    return false;
		}
	  // so sánh quantity vs Order Id đổi status

	  public boolean isOrderComplete(int ReleaseDetailId, int orderId) {
		    int totalReleasedQuantity = QuantityByReleaseNoteId(ReleaseDetailId);
		    int totalOrderQuantity = QuantityOrder(orderId);
		    

		    if(totalReleasedQuantity >= totalOrderQuantity) {

		    	String sql = "UPDATE [Order] SET Status = 'Shipping' WHERE Id = ?";
		    	jdbcTemplate.update(sql, orderId);
		    	return true;
		    }
		    return false;
		}
	  
	  // update quantity_exported
	  public int updateQuantityExported(int wgrnId, int requestId, int idProduct) {
		    String sql = "SELECT SUM(" + Views.COL_WAREHOUSE_RN_DETAIL_QUANTITY + ") " +
		                 "FROM " + Views.TBL_WAREHOUSE_RN_DETAIL + " " +
		                 "WHERE " + Views.COL_WAREHOUSE_RN_DETAIL_PRODUCTID + " = ? " +
		                 "AND " + Views.COL_WAREHOUSE_RNOTE_ID + " = ?"; 

		    Integer totalQuantity = jdbcTemplate.queryForObject(sql, Integer.class, idProduct, wgrnId);
		    if (totalQuantity == null) {
		        totalQuantity = 0;
		    }



		    String updateSql = "UPDATE " + Views.TBL_REQUEST_DETAIL + " SET " +
                  Views.COL_REQUEST_DETAIL_QUANTITY_EXPORTED + " = COALESCE(" +
                  Views.COL_REQUEST_DETAIL_QUANTITY_EXPORTED + ", 0) + ? " +
                  "WHERE " + Views.COL_REQUEST_DETAIL_ID_PRODUCT + " = ? " +
                  "AND " + Views.COL_REQUEST_DETAIL_REQUEST_ID + " = ?";


		    
		    jdbcTemplate.update(updateSql, totalQuantity, idProduct, requestId);  
		    
		    return totalQuantity;
		}

	  
	  //total quantity_requested
	  public int getQuantityRequested(int requestId, int idProduct) {
		    String sql = "SELECT " + Views.COL_REQUEST_DETAIL_QUANTITY_REQUESTED + " " +
		                 "FROM " + Views.TBL_REQUEST_DETAIL + " " +
		                 "WHERE " + Views.COL_REQUEST_DETAIL_REQUEST_ID + " = ? " +
		                 "AND " + Views.COL_REQUEST_DETAIL_ID_PRODUCT + " = ?";

		    Integer quantityRequested = jdbcTemplate.queryForObject(sql, Integer.class, requestId, idProduct);

		    return (quantityRequested != null) ? quantityRequested : 0;
		}

	  //total quantity_exported
	  public int getQuantityExported(int requestId, int idProduct) {
		    String sql = "SELECT " + Views.COL_REQUEST_DETAIL_QUANTITY_EXPORTED + " " +
		                 "FROM " + Views.TBL_REQUEST_DETAIL + " " +
		                 "WHERE " + Views.COL_REQUEST_DETAIL_REQUEST_ID + " = ? " +
		                 "AND " + Views.COL_REQUEST_DETAIL_ID_PRODUCT + " = ?";

		    Integer quantityExported = jdbcTemplate.queryForObject(sql, Integer.class, requestId, idProduct);

		    return (quantityExported != null) ? quantityExported : 0;
		}

	  //update status request_detail
	  public int updateStatusRequestDetail(int requestId,int idProduct) {
		    int totalQuantityRequested = getQuantityRequested(requestId,idProduct);
		    
		    int totalQuantityExported = getQuantityExported(requestId,idProduct);


		    if (totalQuantityExported >= totalQuantityRequested) {
		        String updateStatusSql = "UPDATE " + Views.TBL_REQUEST_DETAIL + " SET " +
		                                 Views.COL_REQUEST_DETAIL_STATUS + " = 'Completed' " + 
		                                 "WHERE " + Views.COL_REQUEST_DETAIL_REQUEST_ID + " = ? AND "
		                                 + Views.COL_REQUEST_DETAIL_ID_PRODUCT + " = ?";
		        jdbcTemplate.update(updateStatusSql, requestId,idProduct);
		    } else {
		    }
		    
		    return totalQuantityRequested;
		}

}
