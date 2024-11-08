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
import org.springframework.web.bind.annotation.ResponseBody;

import com.businessManager.repository.RequestOrderRepository;
import com.models.Employee;
import com.models.Product;
import com.models.Warehouse_releasenote;
import com.models.Warehouse_rn_detail;
import com.models.Request;
import com.models.Request_detail;
import com.utils.Views;
import com.warehouseManager.repository.ReleasenoteRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("warehouseManager")
public class ReleasenoteController {

	@Autowired
	private RequestOrderRepository repoder;

	@Autowired
	private ReleasenoteRepository rele;
	
	@GetMapping("/showWareReleasenote")
	public String showWareReleasenote(Model model, HttpSession session) {
		Employee loggedInEmployee = (Employee) session.getAttribute("loggedInEmployee");

		if (loggedInEmployee != null) {
			int employeeId = loggedInEmployee.getId();

			List<Warehouse_releasenote> releasenotes = rele.findWareAllByEmployeeId(employeeId);

			model.addAttribute("releasenotes", releasenotes);
		} else {
			model.addAttribute("error", "login");
		}

		return Views.SHOW_WAREHOUSE_RELEASENOTE;
	}
	
	  @GetMapping("/warehouseReleasenoteDetail") 
	  public String warehouseReleasenoteDetail(@RequestParam("id") int id, Model model) {
		  
	  Warehouse_releasenote releasenote = rele.findWarehouseReleasenoteById(id); 
	  List<Warehouse_rn_detail> details = rele.findWarehouseRnDetail(id);

	  model.addAttribute("releasenote", releasenote); 
	  model.addAttribute("details", details);	 	  
	  return Views.SHOW_WAREHOUSE_RELEASENOTE_DETAIL; 
	  }

	@GetMapping("/ShowOrderRequest")
	public String showRequest(Model model) {
		List<Request> releasenotes = rele.findAllByEmployeeIdIsNull();
		model.addAttribute("requests", releasenotes);
		return Views.SHOW_REQUEST_WAREHOUSE_RELEASENOTE;
	}

	
	  @GetMapping("/orderRequestdetails") public String
	  showUpdateOrderDetailForm(@RequestParam("id") int id, Model model) {
		  
	  Request request = repoder.findRequestById(id); 
	  List<Request_detail> details = rele.findDetailsByRequestId(id);
	  model.addAttribute("request", request); 
	  model.addAttribute("details", details);	 	  
	  return Views.SHOW_REQUEST_WAREHOUSE_RELEASENOTE_DETAIL; 
	  }
	 

	@PostMapping("/receiveNote")
	public String receiveNote(@RequestParam("requestId") int releasenoteId, HttpSession session) {

		Employee loggedInEmployee = (Employee) session.getAttribute("loggedInEmployee");

		if (loggedInEmployee != null) {
			int employeeId = loggedInEmployee.getId();
			rele.updateEmployeeId(releasenoteId, employeeId);
			rele.updateStatusToPending(releasenoteId);
		} else {
			System.out.println("login");
		}
		return "redirect:/warehouseManager/ShowOrderRequest";
	}

	@GetMapping("/showOrderRequestinWarehouse")
	public String showOrderRequestinWarehouse(Model model, HttpSession session) {
		Employee loggedInEmployee = (Employee) session.getAttribute("loggedInEmployee");

		if (loggedInEmployee != null) {
			int employeeId = loggedInEmployee.getId();

			List<Request> requests = rele.findAllByEmployeeId(employeeId);

			model.addAttribute("requests", requests);
		} else {
			model.addAttribute("error", "login");
		}

		return Views.SHOW_ORDER_REQUEST_IN_WAREHOUSE;
	}

	@GetMapping("/orderInWarehouseDetail")
	public String orderInWarehouseDetail(
			@RequestParam("id") int id,
			HttpSession session, Model model) {
		
		Employee loggedInEmployee = (Employee) session.getAttribute("loggedInEmployee");
		int employeeId = loggedInEmployee.getId();
			
		Request request = rele.findRequestByIdEmp(id, employeeId);
		    
		List<Request_detail> details = rele.findDetailsByRequestId(id);
		    
		model.addAttribute("request", request);
		model.addAttribute("details", details);
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("requestId", id);
	    return Views.SHOW_ORDER_REQUEST_IN_WAREHOUSE_DETAIL;
	}

	
	@GetMapping("/showAddAllWarehouseRelesenote")
	public String showAddAllWarehouseRelesenote(
	        @RequestParam("employeeId") int employeeId,
	        @RequestParam("requestId") int requestId,
	        @RequestParam("name") String name, 
	        
	        Model model) {

	    Request request = rele.findRequestById(requestId); 
	    List<Request_detail> details = rele.findDetailsByRequestId(requestId);


	    model.addAttribute("request", request);
	    model.addAttribute("details", details);
	    model.addAttribute("employeeId", employeeId);
	    model.addAttribute("requestId", requestId);
	    model.addAttribute("name", name);
	    

	    return Views.ADD_ALL_WAREHOUSE_RELEASENOTE;
	}


	 
    @GetMapping("/deleteRequest")
    public String deleteRequest(@RequestParam("id") int requestId) {
    	rele.deleteEmployeeIdByRequestId(requestId);
        return "redirect:showOrderRequestinWarehouse";
    }
	
	@GetMapping("/showAddWarehouseRelesenote")
	public String showAddWarehouseReleasenote(
			@RequestParam("employeeId") int employeeId,
			@RequestParam("requestId") int requestId, 
			Model model) {

		model.addAttribute("employeeId", employeeId);
		model.addAttribute("requestId", requestId);
		model.addAttribute("releasenotes", new Warehouse_releasenote());
		return Views.ADD_WAREHOUSE_RELEASENOTE;
	}

	@PostMapping("/addWarehouseRelesenote")
	public String addWarehouseReleasenote(
			@RequestParam("name") String name, 
			@RequestParam("statusWr") String statusWr,
			@RequestParam("requestId") int requestId, 
			@RequestParam("employeeId") int employeeId,
			@RequestParam(required = false) List<Integer> id_product,
			@RequestParam(required = false) List<Integer> quantity, 
			@RequestParam(required = false) List<String> status,
			Model model) {
		

		Warehouse_releasenote releasenote = new Warehouse_releasenote();
		releasenote.setName(name);
		releasenote.setDate(LocalDateTime.now());
		releasenote.setStatusWr(statusWr);
		releasenote.setRequest_id(requestId);
		releasenote.setEmployee_Id(employeeId);

		List<Warehouse_rn_detail> details = new ArrayList<>();
		if (id_product != null && !id_product.isEmpty()) {
			for (int i = 0; i < id_product.size(); i++) {

				Warehouse_rn_detail detail = new Warehouse_rn_detail();
				detail.setId_product(id_product.get(i));
				detail.setQuantity(quantity.get(i));
				detail.setStatus(status.get(i));

				details.add(detail);
			}
		}

		boolean isSaved = rele.addWarehouseReleasenote(releasenote, details);
		

	    if (isSaved) {
	        int releaseNoteId = releasenote.getId(); 

	        rele.isRequestComplete(releaseNoteId, requestId);
	    }
		
		return "redirect:showOrderRequestinWarehouse";
	}

	@GetMapping("/getProducts")
	@ResponseBody
	public List<Product> getProducts() {
		return repoder.findAllProduct();
	}

}
