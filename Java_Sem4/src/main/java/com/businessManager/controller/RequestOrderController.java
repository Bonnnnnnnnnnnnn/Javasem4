package com.businessManager.controller;


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

import com.models.Warehouse_releasenote;
import com.models.Warehouse_rn_detail;
import com.utils.Views;

@Controller
@RequestMapping("businessManager")
public class RequestOrderController {
    
    @Autowired
    private RequestOrderRepository repoder;
    

    @GetMapping("/warehouseReleasenotes")
    public String showWarehouseReleasenotes(Model model) {
        List<Warehouse_releasenote> releasenotes = repoder.findAll();
        model.addAttribute("releasenotes", releasenotes);
        return Views.SHOW_ORDER_REQUEST; 
    }
    
    @GetMapping("/addRequestOrder")
    public String showAddRequestOrderForm(Model model) {
        model.addAttribute("warehouseReleasenote", new Warehouse_releasenote());
        return Views.ADD_ORDER_REQUEST; 
    }


    @PostMapping("/addRequestOrder")
    public String addRequestOrder(
            @RequestParam("name") String name,
            @RequestParam("statusWr") String statusWr, 
            @RequestParam(required = false) List<Integer> id_product, 
            @RequestParam(required = false) List<Integer> quantity,
            @RequestParam(required = false) List<String> status, 
            Model model) {

        Warehouse_releasenote releasenote = new Warehouse_releasenote();
        releasenote.setName(name);
        releasenote.setDate(LocalDateTime.now());
        releasenote.setStatusWr(statusWr); 

        List<Warehouse_rn_detail> details = new ArrayList<>();
        if (id_product != null && !id_product.isEmpty()) {
            for (int i = 0; i < id_product.size(); i++) {
            	
                Warehouse_rn_detail detail = new Warehouse_rn_detail();
                detail.setId_product(id_product.get(i));
                detail.setQuantity(quantity != null && i < quantity.size() ? quantity.get(i) : null);
                detail.setStatus(status != null && i < status.size() ? status.get(i) : null); 

                details.add(detail);
            }
        }
        repoder.addRequestOrderWithDetails(releasenote, details);
        
        return "redirect:warehouseReleasenotes"; 
    }
    
    
    
    @GetMapping("/deleteOrderRequest")
    public String deleteOrderRequest(@RequestParam("id") int releasenoteId) {
    	repoder.deleteOrderRequest(releasenoteId);
        return "redirect:warehouseReleasenotes";
    }


    @GetMapping("/updateOrderDetail")
    public String showUpdateOrderDetailForm(
            @RequestParam("id") int id,
            Model model) {

        Warehouse_releasenote releasenote = repoder.findReleasenoteById(id);
        List<Warehouse_rn_detail> details = repoder.findDetailsByReleasenoteId(id);

        model.addAttribute("releasenote", releasenote);
        model.addAttribute("details", details);

        return Views.UPDATE_ORDER_REQUEST; 
    }

    @PostMapping("/updateOrderDetail")
    public String updateOrderDetail(
            @RequestParam("name") String name,
            @RequestParam("statusWr") String statusWr, 
            @RequestParam(required = false) List<Integer> id_product, 
            @RequestParam(required = false) List<Integer> quantity,
            @RequestParam(required = false) List<String> status, 
            Model model) {

        Warehouse_releasenote releasenote = new Warehouse_releasenote();
        releasenote.setName(name);
        releasenote.setDate(LocalDateTime.now());
        releasenote.setStatusWr(statusWr);


        List<Warehouse_rn_detail> details = new ArrayList<>();
        if (id_product != null && !id_product.isEmpty()) {
            for (int i = 0; i < id_product.size(); i++) {
                Warehouse_rn_detail detail = new Warehouse_rn_detail();
                detail.setId_product(id_product.get(i));
                detail.setQuantity(quantity != null && i < quantity.size() ? quantity.get(i) : null);
                detail.setStatus(status != null && i < status.size() ? status.get(i) : null); 

                details.add(detail);
            }
        }

        repoder.updateRequestOrderDetail(releasenote, details);

        return "redirect:warehouseReleasenotes"; 
    }


}
