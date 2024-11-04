package com.customer.controller;



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
@RequestMapping("detailproduct")
public class DetailproductController {
	@Autowired
	private DetailproductRepository rep;
	
	@GetMapping("/product")
	public String showpage(Model model,@RequestParam String id) {	
		int idpro = Integer.parseInt(id);
		Product pro = rep.findId(idpro);
	    model.addAttribute("product", pro);
		return Views.CUS_DETAILPROPAGE;
	}
}
