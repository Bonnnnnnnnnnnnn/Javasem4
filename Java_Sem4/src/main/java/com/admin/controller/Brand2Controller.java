package com.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.admin.repository.BrandRepository;
import com.models.Brand;
import com.utils.Views;

@Controller
@RequestMapping("admin/brand")
public class Brand2Controller {
	@Autowired
	private BrandRepository repbr;
	
	@GetMapping("showBrand")
	public String showBrand(Model model) {	
		model.addAttribute("brands", repbr.findAll());
		return Views.BRAND_SHOWBRAND;
	}
	@GetMapping("showAddBrand")
	public String showAddBrand(Model model) {
		Brand br  = new Brand();
		model.addAttribute("new_item",br);
		return Views.BRAND_SHOWADDBRAND;
	}
	@PostMapping("addBrand")
	public String addBrand(@RequestParam String name) {
		Brand br = new Brand();
		br.setName(name);
		repbr.saveBr(br);
		return "redirect:showBrand";
	}
	@GetMapping("deleteBr")
	public String deleteBr(@RequestParam String id) {
		int idb = Integer.parseInt(id);
		repbr.deleteBr(idb);
		return "redirect:showBrand";
	}
	@GetMapping("showUpdateBrand")
	public String showUpdateBrand(Model model , @RequestParam String id) {
		int idp = Integer.parseInt(id);
		model.addAttribute("up_item",repbr.findId(idp));
		return Views.BRAND_SHOWUPDATEBRAND;
	}
	@PostMapping("updateBr")
	public String updateBr(@RequestParam("id") int id,
							@RequestParam("name") String name) {
		Brand br = new Brand();
		br.setName(name);
		br.setId(id);
		
		repbr.updateBr(br);
		return "redirect:showBrand";
	}
}
