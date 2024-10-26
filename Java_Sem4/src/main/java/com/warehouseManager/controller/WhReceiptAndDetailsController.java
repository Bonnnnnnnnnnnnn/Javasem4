package com.warehouseManager.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.models.PageView;
import com.models.Warehouse_receipt;
import com.models.Warehouse_receipt_detail;
import com.utils.Views;
import com.warehouseManager.repository.WhReceiptAndDetailsRepository;

@Controller
@RequestMapping("warehouseManager/warehouseReceipt")
public class WhReceiptAndDetailsController {
	@Autowired
	private WhReceiptAndDetailsRepository repwd;
	@GetMapping("showWhReceipt")
	public String showWhReceipt( Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		 PageView pv = new PageView();
		    pv.setPage_current(cp);
		    pv.setPage_size(10);
		    List<Warehouse_receipt> whr = repwd.findAll(pv);
		    model.addAttribute("whrs", whr);
		    model.addAttribute("pv", pv);
		return Views.SHOW_WAREHOUSE_RECEIPT;
	}
	@GetMapping("showAddWhAndWhDetails")
	public String showAddWhAndWhDetails(Model model) {
		Warehouse_receipt_detail whdt = new Warehouse_receipt_detail();
		Warehouse_receipt whr = new Warehouse_receipt();
		model.addAttribute("new_details",whdt);
		model.addAttribute("new_item",whr);
		model.addAttribute("warehouses",repwd.findAllWh());
		return Views.ADD_WAREHOUSE_RECEIPT;
	}
	@PostMapping("addWhReDetail")
	public String addWhReDetail(@RequestParam String name,
	                             @RequestParam int wh_id,
	                             @RequestParam("date") String dateStr,
	                             @RequestParam int wh_receiptId,
	                             @RequestParam double wh_price,
	                             @RequestParam int quantity) {
	    
	    LocalDateTime date = LocalDateTime.parse(dateStr);

	    Warehouse_receipt whr = new Warehouse_receipt();
	    whr.setName(name);
	    whr.setWh_id(wh_id);
	    whr.setDate(date);
	    
	    boolean isReceiptSaved = repwd.saveWhRe(whr);
	    if (isReceiptSaved) {
	        Warehouse_receipt_detail whdt = new Warehouse_receipt_detail();
	        whdt.setWh_receipt_id(wh_receiptId);
	        whdt.setWh_price(wh_price);

 	        boolean isDetailSaved = repwd.saveWhDetail(whdt);
	        if (!isDetailSaved) {
	        }
		    } else {
		    }
	    return "redirect:showWhReceipt";
	}
	@GetMapping("deleteWhr")
	public String deleteWhr(@RequestParam("id") String id) {
	    int idwhr = Integer.parseInt(id);
	    boolean success = repwd.deleteWarehouseAndDetails(idwhr);
	    if (success) {
	        return "redirect:showWhReceipt";
	    } else {
	        return "redirect:showWhReceipt?error=true";
	    }
	}

}
