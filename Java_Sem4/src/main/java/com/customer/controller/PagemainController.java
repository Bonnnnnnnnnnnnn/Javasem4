package com.customer.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;

import com.customer.repository.ShoppingpageRepository;
import com.models.PageView;
import com.utils.Views;

@Controller
@RequestMapping("")
public class PagemainController {
	
	@Autowired
	private ShoppingpageRepository rep;
	

	@GetMapping("")
	public String showpage(Model model) {
	    int[] idCategories = {}; 
	    int[] idBrands = {}; 
	    String[] statuses = {"NewRelease"}; 

	    // Call the findAllpaging method with the defined arrays
	    model.addAttribute("pronewar", rep.findAllpaging(new PageView(), "", idCategories, idBrands, statuses));
		return Views.CUS_SHOWPAGEMAIN;
	}
}
