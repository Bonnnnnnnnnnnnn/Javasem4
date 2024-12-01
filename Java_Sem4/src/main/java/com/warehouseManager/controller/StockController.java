package com.warehouseManager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.models.Employee;
import com.models.Stock;
import com.utils.Views;
import com.warehouseManager.repository.StockRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("warehouseManager")
public class StockController {
	@Autowired
	private StockRepository repst;
	
	@GetMapping("/showStock")
	public String showStock(HttpSession session, Model model) {
		
		Employee loggedInEmployee = (Employee) session.getAttribute("loggedInEmployee");
		Integer warehouseId = (Integer) session.getAttribute("warehouseId");

		if(loggedInEmployee != null) {
			int employeeId = loggedInEmployee.getId();
		    List<Stock> stock = repst.findAllStock(employeeId,warehouseId);
		    System.out.println("employeeId: " + employeeId);
		    System.out.println("warehouseId: " + warehouseId);

		    model.addAttribute("stocks", stock);
		}	
	    
		return Views.SHOW_STOCK;
	}
	
	@GetMapping("/inventory-stats")
	@ResponseBody
	public List<Map<String, Object>> getInventoryStats(HttpSession session) {
	    Employee loggedInEmployee = (Employee) session.getAttribute("loggedInEmployee");

	    if (loggedInEmployee != null) {
	        return repst.getInventoryStats();
	    }

	    return List.of();
	}
	
	@GetMapping("/chartStock")
	public String chart(HttpSession session) {
	    Employee loggedInEmployee = (Employee) session.getAttribute("loggedInEmployee");

	    if (loggedInEmployee == null) {
	        return "redirect:/employee/login"; 
	    }

	    return Views.SHOW_STOCK_CHAR; 
	}


}
