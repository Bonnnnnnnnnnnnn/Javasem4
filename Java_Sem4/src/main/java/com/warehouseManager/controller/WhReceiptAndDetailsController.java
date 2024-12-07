package com.warehouseManager.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.models.Conversion;
import com.models.PageView;
import com.models.Warehouse_receipt;
import com.models.Warehouse_receipt_detail;
import com.utils.Views;
import com.warehouseManager.repository.WhReceiptAndDetailsRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("warehouseManager/warehouseReceipt")
public class WhReceiptAndDetailsController {
		@Autowired
		private WhReceiptAndDetailsRepository repwd;

		//show phiếu nhập kho
		@GetMapping("showWhReceipt")
		public String showWhReceipt(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp, HttpSession session) {
		    try {
		        PageView pv = new PageView();
		        pv.setPage_current(cp);
		        pv.setPage_size(10);
		        int employeeId = repwd.getEmployeeIdFromSession(session);
		        List<Warehouse_receipt> whr = repwd.findAll(pv, employeeId);
		        model.addAttribute("whrs", whr);
		        model.addAttribute("pv", pv);
		        return Views.SHOW_WAREHOUSE_RECEIPT;
		    } catch (Exception e) {
		        e.printStackTrace();
		        return "error";
		    }
		}

		//show chi tiết phiếu nhập kho
		@GetMapping("/showWhReceiptDetail")
		public String showWhReceiptDetail(@RequestParam("id") String WhrId, @RequestParam("id") String WhrdId, Model model) {
		    try {
		        int idwhr = Integer.parseInt(WhrId);
		        Warehouse_receipt whr = repwd.findId(idwhr);
		        int idwhrs = Integer.parseInt(WhrdId);
		        List<Warehouse_receipt_detail> details = repwd.findDetailsByReceiptId(idwhrs);
		        model.addAttribute("details", details);
		        model.addAttribute("whr", whr);
		        model.addAttribute("products", repwd.findAllPro());
		        return Views.SHOW_WAREHOUSE_RECEIPT_DETAILS;
		    } catch (Exception e) {
		        e.printStackTrace();
		        return "error";
		    }
		}

		// thêm phiếu nhập kho và chi tiết
		@GetMapping("showAddWhReceipt")
		public String showAddWhReceipt(Model model, HttpSession session) {
		    try {
		        int employeeId = repwd.getEmployeeIdFromSession(session);
		        Integer warehouseId = repwd.findWarehouseIdByEmployeeId(employeeId);
		        if (warehouseId == null) {
		            model.addAttribute("error", "Employees have not been assigned to manage any warehouses.");
		            return "error";
		        }
		        model.addAttribute("whId", warehouseId);
		        model.addAttribute("products", repwd.findAllPro());
		        model.addAttribute("employeeId", employeeId);

		        return Views.ADD_WAREHOUSE_RECEIPT;
		    } catch (Exception e) {
		        e.printStackTrace();
		        return "error";
		    }
		}

		@PostMapping("/addWhReceipt")
		public String addWhReceipt(
		        @RequestParam("wh_id") int wh_id,
		        @RequestParam("status") String status,
		        @RequestParam("shippingFee") double shippingFee,
		        @RequestParam(value = "otherFee", defaultValue = "0") double otherFee, 
		        @RequestParam("employeeId") int employeeId,
		        @RequestParam List<Integer> quantity,
		        @RequestParam List<Double> wh_price,
		        @RequestParam List<Integer> product_id,
		        @RequestParam List<Integer> conversionId,
		        @RequestParam List<String> statusDetails,
		        Model model) {
		    
		    Warehouse_receipt receipt = new Warehouse_receipt();
		    
		    receipt.setName(repwd.generateReceiptName());
		    receipt.setWh_id(wh_id);
		    receipt.setStatus(status);
		    receipt.setShipping_fee(shippingFee);
		    receipt.setOther_fee(otherFee);
		    receipt.setEmployee_id(employeeId);
		    receipt.setDate(LocalDateTime.now());
	
		    List<Warehouse_receipt_detail> details = new ArrayList<>();
		    for (int i = 0; i < quantity.size(); i++) {
		        Warehouse_receipt_detail detail = new Warehouse_receipt_detail();
		        detail.setQuantity(quantity.get(i));
		        detail.setWh_price(wh_price.get(i));
		        detail.setProduct_id(product_id.get(i));
		        detail.setConversion_id(conversionId.get(i));
		        detail.setStatus(statusDetails.get(i));
		        details.add(detail);
		    }
		    boolean isAdded = repwd.addRequestOrderWithDetails(receipt, details);
	
		    if (isAdded) {
		        model.addAttribute("message", "Input repository has been added successfully!");
		    } else {
		        model.addAttribute("message", "Adding warehouse receipt failed.");
		    }
		    
		    return "redirect:showWhReceipt";
		}
		
		//randum tên phiếu
		 @GetMapping("/generateReceiptName")
	    @ResponseBody
	    public String generateReceiptName() {
	        String receiptName = repwd.generateReceiptName();
	        return receiptName;
	    }
		
		 // lấy đơn vị theo product
		@GetMapping("/getConversions")
		@ResponseBody
		public List<Conversion> getConversions(@RequestParam("id") int productId) {
		    try {
		        return repwd.getAllConversions(productId);
		    } catch (Exception e) {
		        e.printStackTrace();
		        return Collections.emptyList();
		    }
		}

}
