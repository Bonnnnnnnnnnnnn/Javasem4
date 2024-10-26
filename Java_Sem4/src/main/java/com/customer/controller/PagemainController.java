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
@RequestMapping("/")
public class PagemainController {
	@Autowired
	private CategoryRepository repca;
	
	@GetMapping("")
	public String showpage(Model model) {	
		model.addAttribute("categorys", repca.findAll());
		return Views.CUS_SHOWPAGEMAIN;
	}
}
