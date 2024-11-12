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
import org.springframework.web.bind.annotation.ResponseBody;

import com.businessManager.repository.RequestOrderRepository;
import com.models.PageView;
import com.models.Product;
import com.models.Request;
import com.models.Request_detail;
import com.utils.Views;
import com.warehouseManager.repository.ReleasenoteRepository;

@Controller
@RequestMapping("businessManager")
public class RequestOrderController {
    
    @Autowired
    private RequestOrderRepository repoder;
	@Autowired
	private ReleasenoteRepository rele;

    @GetMapping("/showOrderRequest")
    public String showshowOrderRequest(Model model,
    		@RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
	    PageView pv = new PageView();
	    pv.setPage_current(cp);
	    pv.setPage_size(5);
        List<Request> request = rele.findAllByEmployeeIdIsNull(pv);
        model.addAttribute("requests", request);
		model.addAttribute("pv",pv);
        return Views.SHOW_ORDER_REQUEST; 
    }
    
    @GetMapping("/getProducts")
    @ResponseBody
    public List<Product> getProducts() {    	
        return repoder.findAllProduct();
    }
    
    @GetMapping("/addRequestOrder")
    public String showAddRequestOrderForm(Model model) {
        model.addAttribute("requests", new Request());
        return Views.ADD_ORDER_REQUEST; 
    }
    

    @PostMapping("/addRequestOrder")
    public String addRequestOrder(
            @RequestParam("name") String name,
            @RequestParam("statusRequest") String statusRequest,
            @RequestParam(required = false) List<Integer> id_product, 
            @RequestParam(required = false) List<Integer> quantity_requested,
            @RequestParam(required = false) List<String> status,            
            Model model) {

        Request request = new Request();
        request.setName(name);
        request.setDate(LocalDateTime.now());
        request.setStatusRequest(statusRequest);

        List<Request_detail> details = new ArrayList<>();
        if (id_product != null && !id_product.isEmpty()) {
            for (int i = 0; i < id_product.size(); i++) {
            	
            	Request_detail detail = new Request_detail();
                detail.setId_product(id_product.get(i));
                detail.setQuantity_requested(quantity_requested != null && i < quantity_requested.size() ? quantity_requested.get(i) : null);
                detail.setStatus(status != null && i < status.size() ? status.get(i) : null); 

                details.add(detail);
            }
        }
        repoder.addRequestOrderWithDetails(request, details);
        
        return "redirect:showOrderRequest"; 
    }
    
    
    
    @GetMapping("/deleteOrderRequest")
    public String deleteOrderRequest(@RequestParam("id") int requestId) {
    	repoder.deleteOrderRequest(requestId);
        return "redirect:showOrderRequest";
    }


    @GetMapping("/updateOrderDetail")
    public String showUpdateOrderDetailForm(
            @RequestParam("id") int id,
            Model model) {

        Request request = repoder.findRequestById(id);
        List<Request_detail> details = repoder.findDetailsByRequestId(id);

        model.addAttribute("requests", request);
        model.addAttribute("details", details);

        return Views.UPDATE_ORDER_REQUEST; 
    }
    

    @GetMapping("/searchOrderRequest")
    @ResponseBody
    public List<Request> searchOrderRequest(@RequestParam String query) {
        return repoder.searchReleaseNotes(query);
    }

}
