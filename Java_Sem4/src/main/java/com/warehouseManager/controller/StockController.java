package com.warehouseManager.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.models.Employee;
import com.models.Stock;
import com.models.StockSumByWarehouseId;
import com.utils.Views;
import com.warehouseManager.repository.StockRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("warehouseManager")
public class StockController {
	@Autowired
	private StockRepository repst;
	
	@GetMapping("/showStock")
	public String showStock(HttpSession session, Model model,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		
		System.out.println("startDate: " + startDate);
		System.out.println("endDate: " + endDate);
		Integer warehouseId = (Integer) session.getAttribute("warehouseId");
			
	    if (startDate == null) {
	        startDate = LocalDate.of(2020, 1, 1); 
	    }
	    if (endDate == null) {
	        endDate = LocalDate.now(); 
	    }

	    List<StockSumByWarehouseId> stock = repst.findAllStock(warehouseId, startDate, endDate);
	    List<Stock> stockdetail = repst.findStockDetail(warehouseId, startDate, endDate);			   

	    model.addAttribute("stocks", stock);	
	    model.addAttribute("stockdetail", stockdetail);	
	    model.addAttribute("startDate",startDate); 
	    model.addAttribute("endDate",endDate);
	    
		return Views.SHOW_STOCK;
	}
	
	@GetMapping("/showStockAllJson")
	@ResponseBody
	public List<StockSumByWarehouseId> showStockAllJson(HttpSession session,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		System.out.println("Start Date1: " + startDate);
	    System.out.println("End Date1: " + endDate);

	    Integer warehouseId = (Integer) session.getAttribute("warehouseId");
	    if (warehouseId == null) {
	        throw new IllegalStateException("Session expired or warehouseId not found.");
	    }
	    if (startDate == null) startDate = LocalDate.of(2020, 1, 1);
	    if (endDate == null) endDate = LocalDate.now();

	    return repst.findAllStock(warehouseId, startDate, endDate); 
	}

	
	@GetMapping("/showStockDetailJson")
	@ResponseBody
    public List<Stock> showStockDetailJson(HttpSession session,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		System.out.println("Start Date2: " + startDate);
	    System.out.println("End Date2: " + endDate);
        
        Integer warehouseId = (Integer) session.getAttribute("warehouseId");
        if (warehouseId == null) {
            throw new IllegalStateException("Session expired or warehouseId not found.");
        }
        
        return repst.findStockDetail(warehouseId, startDate, endDate); 
    }
	
	@GetMapping("/showStockDetail")
	public String showStockDetail(HttpSession session, Model model,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		System.out.println("Start Date4: " + startDate);
	    System.out.println("End Date4: " + endDate);
		
		Integer warehouseId = (Integer) session.getAttribute("warehouseId");

		    List<Stock> stock = repst.findStockDetail(warehouseId,startDate,endDate);
		    if (startDate == null) {
		        startDate = LocalDate.of(2020, 1, 1); 
		    }
		    if (endDate == null) {
		        endDate = LocalDate.now(); 
		    }
		    model.addAttribute("stocks", stock);
		    model.addAttribute("startDate", startDate); 
		    model.addAttribute("endDate", endDate);
		    
		return Views.SHOW_STOCK_DETAIL;
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
