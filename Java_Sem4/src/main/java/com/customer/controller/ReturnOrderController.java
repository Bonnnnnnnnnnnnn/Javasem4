package com.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.Order;
import com.models.ReturnOrder;
import com.models.ReturnOrderDetail;

import lombok.Data;

import com.customer.repository.OrderRepository;
import com.customer.repository.ReturnOrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("returnorders")
public class ReturnOrderController {
    
    @Autowired
    private ReturnOrderRepository returnOrderRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createReturnOrder(@RequestBody ReturnOrderRequest request) {
        try {

            
            ReturnOrder returnOrder = new ReturnOrder();
            returnOrder.setOrderId(request.getOrderId());
            returnOrder.setReturnDate(LocalDate.now());
            returnOrder.setStatus("Processing");
            returnOrder.setNote(request.getReason());
            
            double totalAmount = 0;
            for (ReturnItemRequest item : request.getItems()) {
                totalAmount += item.getAmount();
            }
            
            Order originalOrder = orderRepository.getOrderById(request.getOrderId());
            if (originalOrder == null) {
                return ResponseEntity.badRequest().body("Order not found");
            }
            
            double totalDiscount = originalOrder.getDiscount();
            returnOrder.setTotalAmount(totalAmount);
            returnOrder.setDiscountAmount(totalDiscount);
            returnOrder.setFinalAmount(totalAmount - totalDiscount);
            
          
            int returnOrderId = returnOrderRepository.insertReturnOrder(returnOrder);
            if (returnOrderId == 0) {
                return ResponseEntity.badRequest().body("Failed to create return order");
            }
            returnOrder.setId(returnOrderId);
            
         // Insert ReturnOrderDetails
            boolean allDetailsInserted = true;
            for (ReturnItemRequest item : request.getItems()) {
                ReturnOrderDetail detail = new ReturnOrderDetail();
                detail.setReturnOrderId(returnOrder.getId());
                detail.setOrderDetailId(item.getOrderDetailId());
                detail.setQuantity(item.getQuantity());
                detail.setReason(item.getReason());
                detail.setAmount(item.getAmount());
                
                boolean insertDetailSuccess = returnOrderRepository.insertReturnOrderDetail(detail);
                if (!insertDetailSuccess) {
                    allDetailsInserted = false;
                    break;
                }
            }
            
            if (!allDetailsInserted) {
                return ResponseEntity.badRequest().body("Failed to create return order details");
            }
            
            
            
            return ResponseEntity.ok("Return order created successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

class ReturnOrderRequest {
    private int orderId;
    private String reason;
    private List<ReturnItemRequest> items;

    // Getters
    public int getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
    }

    public List<ReturnItemRequest> getItems() {
        return items;
    }

    // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setItems(List<ReturnItemRequest> items) {
        this.items = items;
    }
}

class ReturnItemRequest {
    private int orderDetailId;
    private int quantity;
    private Double amount;
    private String reason;

    // Getters
    public int getOrderDetailId() {
        return orderDetailId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    // Setters
    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}