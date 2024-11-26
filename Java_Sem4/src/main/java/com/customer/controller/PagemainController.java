package com.customer.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;

import com.customer.repository.PagemainRepository;
import com.customer.repository.ShoppingpageRepository;
import com.models.PageView;
import com.utils.Views;

@Controller
@RequestMapping("")
public class PagemainController {
	
	@Autowired
	private ShoppingpageRepository rep;
	@Autowired
	PagemainRepository reppage;

	@GetMapping("")
	public String showpage(Model model) {
	    int[] idCategories = {}; 
	    int[] idBrands = {}; 
	    String[] statusesnew = {"NewRelease"};
	    PageView pv = new PageView();
	    pv.setPage_current(1);
	    reppage.getProvinces();
	    pv.setPage_size(8);
	    String[] statuses = {"NewRelease","Active","OutOfStock"}; 
	    // Call the findAllpaging method with the defined arrays
	    model.addAttribute("pronewar", rep.findAllnopaging(new PageView(), "", idCategories, idBrands, statusesnew));
	    model.addAttribute("pro_eightnew", rep.findAllpaging(pv, "", idCategories, idBrands, statuses));
		return Views.CUS_SHOWPAGEMAIN;
	}
}
