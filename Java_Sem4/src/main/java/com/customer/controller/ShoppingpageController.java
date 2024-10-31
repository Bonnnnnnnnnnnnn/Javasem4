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

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("shoppingpage")
public class ShoppingpageController {
	
	@Autowired
	private ShoppingpageRepository reppro;
	
	@Autowired
	private BrandRepository repbr;
	
	
	@GetMapping("")
	public String showpage(Model model, 
	                       @RequestParam(name = "cp", required = false, defaultValue = "1") int cp, 
	                       HttpServletRequest request) {
	    PageView pv = new PageView();
	    pv.setPage_current(cp);
	    pv.setPage_size(12);
	    
	    // Retrieve selected filters from session or initialize them
	    int[] idCategories = (int[]) request.getSession().getAttribute("selectedCategories");
	    int[] idBrands = (int[]) request.getSession().getAttribute("selectedBrands");
	    
	    // If there are no selected filters in the session, initialize them as empty arrays
	    if (idCategories == null) {
	        idCategories = new int[0]; // Initialize as empty array
	    }
	    if (idBrands == null) {
	        idBrands = new int[0]; // Initialize as empty array
	    }

	    String[] statuses = {"NewRelease","Active","OutOfStock"}; // Example status array

	    // Call the findAllpaging method with the defined arrays
	    model.addAttribute("pronewar", reppro.findAllpaging(pv, "", idCategories, idBrands, statuses));

	    model.addAttribute("brands", repbr.findAll());
	    model.addAttribute("pv", pv);
	    model.addAttribute("selectedBrands", idBrands);
	    model.addAttribute("selectedCategories", idCategories);
	    return Views.CUS_SHOPPINGPAGE;
	}

	
	@PostMapping("/filter")
	public String applyFilters(@RequestParam(name = "brands", required = false) int[] brandIds,
	                           @RequestParam(name = "categories", required = false) int[] categoryIds,
	                           HttpServletRequest request, // To store filters in the session
	                           Model model) {
	    
	    // Store the selected filters in the session
	    request.getSession().setAttribute("selectedBrands", brandIds);
	    request.getSession().setAttribute("selectedCategories", categoryIds);
	    
	  
	    return "redirect:/shoppingpage"; // Redirect to the shopping page or another page
	}

}
