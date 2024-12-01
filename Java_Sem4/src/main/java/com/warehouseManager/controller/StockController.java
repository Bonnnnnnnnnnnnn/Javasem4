package com.warehouseManager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mapper.Conversion_mapper;
import com.models.Conversion;
import com.models.PageView;
import com.models.Stock;
import com.utils.Views;
import com.warehouseManager.repository.StockRepository;

@Controller
@RequestMapping("warehouseManager")
public class StockController {
	@Autowired
	private StockRepository repst;
	
	@GetMapping("/showStock")
	public String showStock(Model model, int stockId) {

	    List<Stock> stock = repst.findAllStock(stockId);
	    
	    model.addAttribute("stocks", stock);
		return Views.SHOW_STOCK;
	}
}
