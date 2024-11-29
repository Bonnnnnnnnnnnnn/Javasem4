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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.admin.repository.WarehouseRepository;
import com.customer.repository.GHNService;
import com.models.Employee_warehouse;
import com.models.PageView;
import com.models.Warehouse;
import com.models.ghn.District;
import com.models.ghn.Province;
import com.models.ghn.Ward;
import com.utils.Views;

import ch.qos.logback.core.joran.spi.NoAutoStart;

@Controller
@RequestMapping("admin/warehouse")
public class WarehouseController {

	@Autowired
	private WarehouseRepository repwh;
	@Autowired
	private GHNService ghn;
	@GetMapping("showWarehouse")
	public String showWarehouse(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
	    PageView pv = new PageView();
	    pv.setPage_current(cp);
	    pv.setPage_size(5);
	    List<Warehouse> warehouses = repwh.findAll(pv);
	    model.addAttribute("warehouses", warehouses);
	    model.addAttribute("pv", pv);

	    return Views.WAREHOUSE_SHOWWAREHOUSE;
	}
	@GetMapping("showWarehouseDetails")
	public String showWarehouseDetails(Model model, @RequestParam("id") String id) {
	    try {
	        int idwh = Integer.parseInt(id); 
	        Warehouse wh = repwh.findId(idwh);
	        if (wh != null) {
	            model.addAttribute("warehouse", wh);
	        } else {
	            model.addAttribute("error", "Warehouse not found");
	        }
	    } catch (NumberFormatException e) {
	        model.addAttribute("error", "Invalid Warehouse ID");
	    }
	    return Views.WAREHOUSE_SHOWWAREHOUSEDETAILS;
	}

	@GetMapping("showAddWarehouse")
	public String showAddWarehouse(Model model) {
		Warehouse wh = new Warehouse();
		List<Province> province = ghn.getProvinces();
		model.addAttribute("provinces",province);
		model.addAttribute("new_item",wh);
		model.addAttribute("types",repwh.findAllType());
		return Views.WAREHOUSE_SHOWADDWAREHOUSE;
	}
	@GetMapping("districts")
    public ResponseEntity<List<District>> getDistricts(@RequestParam("provinceId") int provinceId) {
        try {
            List<District> districts = ghn.getDistricts(provinceId);
            if (districts.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(districts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
	@GetMapping("wards")
	@ResponseBody
	public ResponseEntity<List<Ward>> getWards(@RequestParam("districtId") int districtId) {
	    try {
	        List<Ward> wards = ghn.getWards(districtId);
	        if (wards.isEmpty()) {
	            return ResponseEntity.noContent().build();
	        }
	        return ResponseEntity.ok(wards);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
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
	public String showUpdateWarehouse(Model model,@RequestParam("id") String id) {
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
	//		//Employee Warehouse
	@GetMapping("showEmpWare")
	public String showEmpWare(Model model) {	
		model.addAttribute("ews",repwh.findAllEw());
		return Views.EMPLOYEE_WAREHOUSE_SHOWEMPWARE;
	}

	@GetMapping("showWarehouseManagementLevel")
	public String showWarehouseManagementLevel(Model model, @RequestParam String iddtwh) {
	    int idwh = Integer.parseInt(iddtwh);
	    Warehouse wh = repwh.findId(idwh);
	    model.addAttribute("employees", repwh.showEmpAll());
	    model.addAttribute("warehouse", wh);
	    return Views.EMPLOYEE_WAREHOUSE_SHOWWAREHOUSEMANAGEMENTLEVEL;
	}

	@PostMapping("warehouseManagementLevel")
	public String warehouseManagementLevel(@RequestParam int warehouse_id,
	                                       @RequestParam int employee_id,
	                                       RedirectAttributes redirectAttributes) {
	    Employee_warehouse ew = new Employee_warehouse();
	    ew.setWarehouse_Id(warehouse_id);
	    ew.setEmployee_Id(employee_id);
	    
	    repwh.addEw(ew);
	    
	    redirectAttributes.addFlashAttribute("successMessage", "Employee assigned successfully!");
	    
	    return "redirect:/admin/warehouse/showWarehouseDetails?id=" + warehouse_id;
	}
	@GetMapping("showUpdateWarehouseManagementLevel")
	public String showUpdateWarehouseManagementLevel(Model model, @RequestParam String iddtwh, @RequestParam(required = false) String idup) {
	    int idwh = Integer.parseInt(iddtwh);
	    Warehouse wh = repwh.findId(idwh);
	    Employee_warehouse ew = null;
	    if (idup != null && !idup.isEmpty()) {
	        try {
	            int idupp = Integer.parseInt(idup);
	            ew = repwh.findByEmpwhId(idupp);
	        } catch (NumberFormatException e) {
	            e.printStackTrace();
	        }
	    }

	    model.addAttribute("employees", repwh.showEmpAll());
	    model.addAttribute("warehouse", wh);
	    model.addAttribute("empwh", ew); 

	    return Views.EMPLOYEE_WAREHOUSE_SHOWUPDATEWAREHOUSEMANAGEMENTLEVEL;
	}

}
