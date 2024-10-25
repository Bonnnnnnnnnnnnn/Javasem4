package com.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.admin.repository.CategoryRepository;
import com.models.Category_Product;
import com.utils.Views;

@Controller
@RequestMapping("admin/category")
public class CategoryController {

	@Autowired
	private CategoryRepository repca;
	
	@GetMapping("showCategory")
	public String showCategory(Model model) {	
		model.addAttribute("categorys", repca.findAll());
		return Views.CATEGORY_SHOWCATEGORY;
	}
	@GetMapping("showAddCategory")
	public String showAddCategory(Model model) {
		Category_Product ca  = new Category_Product();
		model.addAttribute("new_item",ca);
		return Views.CATEGORY_SHOWADDCATEGORY;
	}
	@PostMapping("addCategory")
	public String addCategory(@RequestParam String name) {
		Category_Product ca = new Category_Product();
		ca.setName(name);
		repca.saveCate(ca);
		return "redirect:showCategory";
	}
	@GetMapping("deleteCa")
	public String deleteCa(@RequestParam String id) {
		int idb = Integer.parseInt(id);
		repca.deleteCa(idb);
		return "redirect:showCategory";
	}
	@GetMapping("showUpdateCategory")
	public String showUpdateCategory(Model model , @RequestParam String id) {
		int idp = Integer.parseInt(id);
		model.addAttribute("up_item",repca.findId(idp));
		return Views.CATEGORY_SHOWUPDATECATEGORY;
	}
	@PostMapping("updateCa")
	public String updateCa(@RequestParam("id") int id,
							@RequestParam("name") String name) {
		Category_Product ca  = new Category_Product();
		ca.setName(name);
		ca.setId(id);
		
		repca.updateCa(ca);
		return "redirect:showCategory";
	}
}
