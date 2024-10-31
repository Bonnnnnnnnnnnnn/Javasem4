package com.customer.controller;


import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.customer.repository.DetailproductRepository;
import com.models.Product;
import com.utils.Views;

@Controller
@RequestMapping("cart")
public class CartController {
	@Autowired
	private DetailproductRepository reppro;
	
	@GetMapping("/showcart")
	public String showpage(Model model,@RequestParam String id) {	
		
		return Views.CUS_CARTPAGE;
	}
}
