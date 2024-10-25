package com.warehouseManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.warehouseManager.repository.StockRepository;

@Controller
public class StockController {
	@Autowired
	private StockRepository repst;
	
}
