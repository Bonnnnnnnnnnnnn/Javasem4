package com.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.admin.repository.WarehouseTypeRepository;
import com.models.Warehouse;
import com.models.Warehouse_type;
import com.utils.Views;

@Controller
@RequestMapping("/admin/warehouseType")
public class WarehouseTypeController {
	@Autowired
	private WarehouseTypeRepository reptype;
	
	@GetMapping("showWhType")
	public String showWhType(Model model) {
		model.addAttribute("whtypes", reptype.findAll());
		return Views.WAREHOUSETYPE_SHOWWHTYPE;
	}
	@GetMapping("/getWarehousesByType")
	@ResponseBody
	public List<Warehouse> getWarehousesByType(@RequestParam("typeId") String typeId) {
	    int idl = Integer.parseInt(typeId);
	    List<Warehouse> warehouses = reptype.findByTypeId(idl);
	    return warehouses;
	}
	@GetMapping("showAddWhType")
	public String showAddWhType(Model model) {
		Warehouse_type wt = new Warehouse_type();
		model.addAttribute("new_item",wt);
		return Views.WAREHOUSETYPE_SHOWADDWHTYPE;
	}
	@PostMapping("addWhType")
	public String addWhType(@RequestParam String name) {
		Warehouse_type wt = new Warehouse_type();
		wt.setName(name);
		reptype.saveWhType(wt);
		return"redirect:showWhType";
	}
	@GetMapping("deleteWt")
	public String deleteWt(String id) {
		int idwt = Integer.parseInt(id);
		reptype.deleteWt(idwt);
		return"redirect:showWhType";
	}
	@GetMapping("showUpdateWhType")
	public String showUpdateWhType(Model model,@RequestParam String id) {
		int idw = Integer.parseInt(id);
		model.addAttribute("up_item",reptype.findById(idw));
		return Views.WAREHOUSETYPE_SHOWUPDATEWHTYPE;
	}
	@PostMapping("updateWt")
	public String updateWt(@RequestParam("id") int id,
							@RequestParam("name") String Name) {
		Warehouse_type wt = new Warehouse_type();
		wt.setId(id);
		wt.setName(Name);
		reptype.updateWt(wt);
		return "redirect:showWhType";
	}
}
