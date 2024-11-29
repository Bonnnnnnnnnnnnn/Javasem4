package com.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Comment {
    private int id;
    private String content;
    private int productId;
    private int customerId;
    private int employeeId;
    private int parentId;    
    private boolean isReply;  
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String customerFirstName;
    private String customerLastName;
    private String employeeFirstName;
    private String employeeLastName;
    private List<Comment> replies = new ArrayList<>();
    public Comment() {
    	
    }

    public Comment(int id, String content, int productId, int customerId, int employeeId, int parentId,
			boolean isReply, String status, LocalDate createdAt, LocalDate updatedAt) {
		super();
		this.id = id;
		this.content = content;
		this.productId = productId;
		this.customerId = customerId;
		this.employeeId = employeeId;
		this.parentId = parentId;
		this.isReply = isReply;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
    public List<Comment> getReplies() {
        return replies;
    }
    
    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }
    
   
    public void addReply(Comment reply) {
        if (this.replies == null) {
            this.replies = new ArrayList<>();
        }
        this.replies.add(reply);
    }
    
  
    public int getReplyCount() {
        return replies.size();
    }
    public String getCustomerFirstName() {
		return customerFirstName;
	}

	public void setCustomerFirstName(String customerFirstName) {
		this.customerFirstName = customerFirstName;
	}

	public String getCustomerLastName() {
		return customerLastName;
	}

	public void setCustomerLastName(String customerLastName) {
		this.customerLastName = customerLastName;
	}

	public String getEmployeeFirstName() {
		return employeeFirstName;
	}

	public void setEmployeeFirstName(String employeeFirstName) {
		this.employeeFirstName = employeeFirstName;
	}

	public String getEmployeeLastName() {
		return employeeLastName;
	}

	public void setEmployeeLastName(String employeeLastName) {
		this.employeeLastName = employeeLastName;
	}

	public boolean isReply() {
        return isReply;
    }

    public void setReply(boolean reply) {
        isReply = reply;
    }

	// Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}