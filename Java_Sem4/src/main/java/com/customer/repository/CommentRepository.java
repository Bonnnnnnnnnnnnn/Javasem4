package com.customer.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mapper.*;
import com.models.Comment;
import com.utils.Views;



@Repository
public class CommentRepository {
	@Autowired
	JdbcTemplate db;
	 
	public List<Comment> getCommentsByProductId(int productId) {
	    try {
	    	String sql = String.format(
	                "SELECT c.*, " +
	                "cus.%s as customer_firstname, " +
	                "cus.%s as customer_lastname, " +
	                "emp.%s as employee_firstname, " +
	                "emp.%s as employee_lastname " +
	                "FROM %s c " +
	                "LEFT JOIN %s cus ON c.%s = cus.%s " +
	                "LEFT JOIN %s emp ON c.%s = emp.%s " +
	                "WHERE c.%s = ? " +
	                "ORDER BY c.%s DESC, c.%s DESC",
	                Views.COL_CUSTOMER_FIRST_NAME,
	                Views.COL_CUSTOMER_LAST_NAME,
	                Views.COL_EMPLOYEE_FIRST_NAME,
	                Views.COL_EMPLOYEE_LAST_NAME,
	                Views.TBL_COMMENTS,
	                Views.TBL_CUSTOMER, Views.COL_COMMENT_CUSTOMER_ID, Views.COL_CUSTOMER_ID,
	                Views.TBL_EMPLOYEE, Views.COL_COMMENT_STAFF_ID, Views.COL_EMPLOYEE_ID,
	                Views.COL_COMMENT_PRODUCT_ID,
	                Views.COL_COMMENT_CREATED_AT,
	                Views.COL_COMMENT_ID
	            );
	    	List<Comment> allComments = db.query(sql, new Object[]{productId}, new Comment_mapper());
	        
	    	// Sắp xếp tất cả comments theo thời gian tạo (mới nhất lên đầu)
	    	allComments.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

	    	List<Comment> parentComments = new ArrayList<>();
	    	Map<Integer, Comment> commentMap = new HashMap<>();

	    	for (Comment comment : allComments) {
	    	    commentMap.put(comment.getId(), comment);
	    	}

	    	for (Comment comment : allComments) {
	    	    if (comment.getParentId() == 0) {
	    	        parentComments.add(comment);
	    	    } else {
	    	        Comment parentComment = commentMap.get(comment.getParentId());
	    	        if (parentComment != null) {
	    	            parentComment.getReplies().add(0, comment);
	    	        }
	    	    }
	    	}

	    	// Sắp xếp parent comments theo thời gian (mới nhất lên đầu)
	    	parentComments.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

	    	return parentComments;
	    } catch (DataAccessException e) {
	        System.err.println("Error getting comments: " + e.getMessage());
	        return new ArrayList<>();
	    }
	}
	
	public Comment insertComment(Comment comment) {
	    try {
	        // Kiểm tra từ cấm
	        String checkBannedSql = String.format(
	            "SELECT COUNT(*) FROM %s " +
	            "WHERE ? LIKE CONCAT('%%', %s, '%%') " +
	            "AND %s = 1",
	            Views.TBL_BANNED_KEYWORDS,
	            Views.COL_BANNED_KEYWORD,
	            Views.COL_BANNED_KEYWORD_IS_ACTIVE
	        );
	        
	        int bannedWordCount = db.queryForObject(checkBannedSql, Integer.class, comment.getContent().toLowerCase());
	        if (bannedWordCount > 0) {
	            return null;
	        }

	        // Insert comment
	        String insertSql = String.format(
	            "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) " +
	            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
	            Views.TBL_COMMENTS,
	            Views.COL_COMMENT_CONTENT,
	            Views.COL_COMMENT_PRODUCT_ID,
	            Views.COL_COMMENT_CUSTOMER_ID,
	            Views.COL_COMMENT_PARENT_ID,
	            Views.COL_COMMENT_STAFF_ID,
	            Views.COL_COMMENT_STATUS,
	            Views.COL_COMMENT_CREATED_AT,
	            Views.COL_COMMENT_UPDATED_AT
	        );

	        LocalDateTime now = LocalDateTime.now();
	        
	        Integer parentId = comment.getParentId() > 0 ? comment.getParentId() : null;
	        Integer employeeId = comment.getEmployeeId() > 0 ? comment.getEmployeeId() : null;
	        String status = (comment.getStatus() == null || comment.getStatus().isEmpty()) ? "PENDING" : comment.getStatus();

	        KeyHolder keyHolder = new GeneratedKeyHolder();
	        
	        int rowsAffected = db.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
	            ps.setString(1, comment.getContent());
	            ps.setInt(2, comment.getProductId());
	            ps.setInt(3, comment.getCustomerId());
	            ps.setObject(4, parentId);
	            ps.setObject(5, employeeId);
	            ps.setString(6, status);
	            ps.setTimestamp(7, Timestamp.valueOf(now));
	            ps.setTimestamp(8, Timestamp.valueOf(now));
	            return ps;
	        }, keyHolder);

	        if (rowsAffected > 0) {
	            // Lấy comment vừa insert với đầy đủ thông tin
	            String selectSql = String.format(
	                "SELECT c.*, " +
	                "cus.%s as customer_firstname, " +
	                "cus.%s as customer_lastname, " +
	                "emp.%s as employee_firstname, " +
	                "emp.%s as employee_lastname " +
	                "FROM %s c " +
	                "LEFT JOIN %s cus ON c.%s = cus.%s " +
	                "LEFT JOIN %s emp ON c.%s = emp.%s " +
	                "WHERE c.%s = ?",
	                Views.COL_CUSTOMER_FIRST_NAME,
	                Views.COL_CUSTOMER_LAST_NAME,
	                Views.COL_EMPLOYEE_FIRST_NAME,
	                Views.COL_EMPLOYEE_LAST_NAME,
	                Views.TBL_COMMENTS,
	                Views.TBL_CUSTOMER, Views.COL_COMMENT_CUSTOMER_ID, Views.COL_CUSTOMER_ID,
	                Views.TBL_EMPLOYEE, Views.COL_COMMENT_STAFF_ID, Views.COL_EMPLOYEE_ID,
	                Views.COL_COMMENT_ID
	            );
	            
	            return db.queryForObject(selectSql, new Comment_mapper(), keyHolder.getKey().intValue());
	        }

	        return null;

	    } catch (DataAccessException e) {
	        System.err.println("Error inserting comment: " + e.getMessage());
	        e.printStackTrace();
	        return null;
	    }
	}

	public Comment insertComment(String content, int productId, int customerId) {
	    Comment comment = new Comment();
	    comment.setContent(content);
	    comment.setProductId(productId);
	    comment.setCustomerId(customerId);
	    return insertComment(comment);
	}

	public Comment insertReplyComment(String content, int productId, int customerId, int parentId) {
	    Comment comment = new Comment();
	    comment.setContent(content);
	    comment.setProductId(productId);
	    comment.setCustomerId(customerId);
	    comment.setParentId(parentId);
	    return insertComment(comment);
	}

	public Comment insertStaffReply(String content, int productId, int employeeId, int parentId) {
	    Comment comment = new Comment();
	    comment.setContent(content);
	    comment.setProductId(productId);
	    comment.setEmployeeId(employeeId);
	    comment.setParentId(parentId);
	    comment.setStatus("APPROVED");
	    return insertComment(comment);
	}
	
	
}

	
