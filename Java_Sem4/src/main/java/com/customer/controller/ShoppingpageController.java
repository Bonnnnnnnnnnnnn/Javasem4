package com.customer.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.admin.repository.BrandRepository;
import com.admin.repository.CategoryRepository;
import com.customer.repository.PagemainRepository;
import com.customer.repository.ShoppingpageRepository;
import com.models.Customer;
import com.models.PageView;
import com.utils.FileUtils;
import com.utils.Views;

@Controller
@RequestMapping("shoppingpage")
public class ShoppingpageController {
	
	@Autowired
	private ShoppingpageRepository reppro;
	
	@Autowired
	private BrandRepository repbr;
	
	
	@GetMapping("")
	public String showpage(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {	
		PageView pv = new PageView();
	    pv.setPage_current(cp);
	    pv.setPage_size(12);
		model.addAttribute("pronewar", reppro.findAllpaging(pv,"",0,""));
		model.addAttribute("brands", repbr.findAll());
		return Views.CUS_SHOPPINGPAGE;
	}
}
