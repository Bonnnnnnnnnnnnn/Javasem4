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

import com.admin.repository.CategoryRepository;
import com.customer.repository.PagemainRepository;
import com.models.Customer;
import com.utils.FileUtils;
import com.utils.Views;

@Controller
@RequestMapping("/Shoppingpage")
public class ShoppingpageController {
	
	@GetMapping("")
	public String showpage(Model model) {	
		
		return Views.CUS_SHOPPINGPAGE;
	}
}
