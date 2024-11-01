package com.admin.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.admin.repository.WarehouseRepository;
import com.models.Warehouse;
import com.utils.Views;

@Controller
@RequestMapping("warehouse")
public class WarehouseController {

	@Autowired
	private WarehouseRepository repwh;
	@GetMapping("showWarehouse")
	public String showWarehouse(Model model) {	
		model.addAttribute("warehouses",repwh.findAll());
		return Views.WAREHOUSE_SHOWWAREHOUSE;
	}
	@GetMapping("showAddWarehouse")
	public String showAddWarehouse(Model model) {
		Warehouse wh = new Warehouse();
		model.addAttribute("new_item",wh);
		model.addAttribute("types",repwh.findAllType());
		return Views.WAREHOUSE_SHOWADDWAREHOUSE;
	}
	@PostMapping("addWh")
	public String addWh(@RequestParam String name,
							@RequestParam String address,
							@RequestParam int wh_type_id) {
		Warehouse wh = new Warehouse();
		wh.setName(name);
		wh.setAddress(address);
		wh.setWh_type_id(wh_type_id);
		
		repwh.saveWh(wh);
		return "redirect:showWarehouse";
	}
	@GetMapping("deleteWh")
	public String deleteWh(@RequestParam String id) {
		int idwh = Integer.parseInt(id);
		repwh.deleteWh(idwh);
		return "redirect:showWarehouse";
	}
	@PostMapping("/deleteSelectedWarehouses")
	@ResponseBody
	public ResponseEntity<String> deleteSelectedWarehouses(@RequestBody List<Integer> ids) {
	    if (ids == null || ids.isEmpty()) {
	        return ResponseEntity.badRequest().body("No warehouse IDs provided.");
	    }
	    ids.removeIf(Objects::isNull);

	    try {
	        for (Integer id : ids) {
	            repwh.deleteWh(id);
	        }
	        return ResponseEntity.ok("Warehouses deleted successfully.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete warehouses: " + e.getMessage());
	    }
	}

	@GetMapping("showUpdateWarehouse")
	public String showUpdateWarehouse(Model model,@RequestParam String id) {
		int idwh = Integer.parseInt(id);
		model.addAttribute("up_item",repwh.findId(idwh));
		model.addAttribute("types",repwh.findAllType());
		return Views.WAREHOUSE_SHOWUPDATEWAREHOUSE;
	}
	@PostMapping("updateWh")
	public String updateWh(@RequestParam("name") String name,
							@RequestParam("address") String address,
							@RequestParam("wh_type_id") int wh_type_id,
							@RequestParam("id") int id) {
		Warehouse wh = new Warehouse();
		wh.setName(name);
		wh.setAddress(address);
		wh.setWh_type_id(wh_type_id);
		wh.setId(id);
		repwh.updatewh(wh);
		return "redirect:showWarehouse";
	}
}
