package com.warehouseManager.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.businessManager.repository.RequestOrderRepository;
import com.models.Employee;
import com.models.Warehouse_releasenote;
import com.models.Warehouse_rn_detail;
import com.utils.Views;
import com.warehouseManager.repository.ReleasenoteRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("warehouseManager")
public class ReleasenoteController {
	
    @Autowired
    private RequestOrderRepository repoder;
    
    @Autowired
    private ReleasenoteRepository  rele;
    
    @GetMapping("/ShowOrderRequest")
    public String showWarehouseReleasenotes(Model model) {
        List<Warehouse_releasenote> releasenotes = repoder.findAll();
        model.addAttribute("releasenotes", releasenotes);
        return Views.SHOW_WAREHOUSE_RELEASENOTE; 
    }
   
    @GetMapping("/orderRequestdetails")
    public String showUpdateOrderDetailForm(
            @RequestParam("id") int id,
            Model model) {

        Warehouse_releasenote releasenote = repoder.findReleasenoteById(id);
        List<Warehouse_rn_detail> details = repoder.findDetailsByReleasenoteId(id);

        model.addAttribute("releasenote", releasenote);
        model.addAttribute("details", details);

        return Views.SHOW_WAREHOUSE_RELEASENOTE_DETAIL; 
    }
    
    @PostMapping("/receiveNote")
    public String receiveNote(@RequestParam("releasenoteId") int releasenoteId, HttpSession session) {

        Employee loggedInEmployee = (Employee) session.getAttribute("loggedInEmployee");

        if (loggedInEmployee != null) {
            int employeeId = loggedInEmployee.getId();  
            rele.updateEmployeeId(releasenoteId, employeeId); 
            rele.updateStatusToPending(releasenoteId);
        } else {
            System.out.println("login");
        }
        return "redirect:/warehouse/ShowOrderRequest";
    }
    
    @GetMapping("/showOrderRequestinWarehouse")
    public String showOrderRequestinWarehouse(Model model, HttpSession session) {
        Employee loggedInEmployee = (Employee) session.getAttribute("loggedInEmployee");

        if (loggedInEmployee != null) {
            int employeeId = loggedInEmployee.getId();
            
            List<Warehouse_releasenote> releasenotes = rele.findAllByEmployeeId(employeeId);            
            
            model.addAttribute("releasenotes", releasenotes);
        } else {
            model.addAttribute("error", "login");
        }

        return Views.SHOW_ORDER_REQUEST_IN_WAREHOUSE; 
    }

    @GetMapping("/orderInWarehouseDetail")
    public String orderInWarehouseDetail(
            @RequestParam("id") int id,
            Model model) {

        Warehouse_releasenote releasenote = repoder.findReleasenoteById(id);
        List<Warehouse_rn_detail> details = repoder.findDetailsByReleasenoteId(id);

        model.addAttribute("releasenote", releasenote);
        model.addAttribute("details", details);

        return Views.SHOW_ORDER_REQUEST_IN_WAREHOUSE_DETAIL; 
    }
    
    @GetMapping("/addReleasenote")
    public String showAddRequestOrderForm(Model model) {
        model.addAttribute("warehouseReleasenote", new Warehouse_releasenote());
        return Views.ADD_WAREHOUSE_RELEASENOTE; 
    }


    @PostMapping("/addReleasenote")
    public String addRequestOrder(
            @RequestParam("name") String name,
            @RequestParam("statusWr") String statusWr, 
            @RequestParam("orderId") int orderId, 
            @RequestParam(required = false) List<Integer> id_product, 
            @RequestParam(required = false) List<Integer> quantity,
            @RequestParam(required = false) List<String> status, 
            @RequestParam(required = false) List<Integer> stock_id, 
            Model model) {

        Warehouse_releasenote releasenote = new Warehouse_releasenote();
        releasenote.setName(name);
        releasenote.setDate(LocalDateTime.now());
        releasenote.setStatusWr(statusWr); 
        releasenote.setOrder_id(orderId); 

        List<Warehouse_rn_detail> details = new ArrayList<>();
        if (id_product != null && !id_product.isEmpty()) {
            for (int i = 0; i < id_product.size(); i++) {
                Warehouse_rn_detail detail = new Warehouse_rn_detail();
                detail.setId_product(id_product.get(i));
                detail.setQuantity(quantity != null && i < quantity.size() ? quantity.get(i) : 0); 
                detail.setStatus(status != null && i < status.size() ? status.get(i) : ""); 
                detail.setStock_id(stock_id != null && i < stock_id.size() ? stock_id.get(i) : 0); 

                details.add(detail);
            }
        }

        boolean isAdded = repoder.addRequestOrderWithDetails(releasenote, details);
        
        if (isAdded) {
            model.addAttribute("successMessage");
        } else {
            model.addAttribute("errorMessage");
        }
        
        return "redirect:showDeleasenote"; 
    }

}
