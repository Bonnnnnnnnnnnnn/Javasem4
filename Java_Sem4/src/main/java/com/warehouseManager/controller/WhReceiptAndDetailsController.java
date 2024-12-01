package com.warehouseManager.controller;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		
		//Phần Warehouse_receipt 
		
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
		@GetMapping("/showWhReceiptDetail")
		public String showWhReceiptDetail(@RequestParam("id") String WhrId,@RequestParam("id") String WhrdId, Model model) {
				int idwhr = Integer.parseInt(WhrId);
				Warehouse_receipt whr = repwd.findId(idwhr);
				int idwhrs = Integer.parseInt(WhrdId);
		        List<Warehouse_receipt_detail> details = repwd.findDetailsByReceiptId(idwhrs); 
		        model.addAttribute("details", details);
			    model.addAttribute("whr", whr);
			    model.addAttribute("products",repwd.findAllPro());
			return Views.SHOW_WAREHOUSE_RECEIPT_DETAILS;
		}
		@GetMapping("showAddWhReceipt")
		public String showAddWhReceipt(Model model) {
			Warehouse_receipt whr = new Warehouse_receipt();
			model.addAttribute("new_item",whr);
			model.addAttribute("warehouses",repwd.findAllWh());
			model.addAttribute("products",repwd.findAllPro());
			return Views.ADD_WAREHOUSE_RECEIPT;
		}
		@PostMapping("/addWhReceipt")
		public String addWhReceipt(
		        @RequestParam("wh_id") int wh_id,
		        @RequestParam("status") String status,
		        @RequestParam List<Integer> quantity,
		        @RequestParam List<Double> wh_price,
		        @RequestParam List<Integer> product_id,
		        Model model) {
		    
		    Warehouse_receipt receipt = new Warehouse_receipt();
		    
		    receipt.setName(repwd.generateReceiptName());
		    receipt.setWh_id(wh_id);
		    receipt.setStatus(status);
		    receipt.setDate(LocalDateTime.now());
	
		    List<Warehouse_receipt_detail> details = new ArrayList<>();
		    for (int i = 0; i < quantity.size(); i++) {
		        Warehouse_receipt_detail detail = new Warehouse_receipt_detail();
		        detail.setQuantity(quantity.get(i));
		        detail.setWh_price(wh_price.get(i));
		        detail.setProduct_id(product_id.get(i));
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
		 @GetMapping("/generateReceiptName")
	    @ResponseBody
	    public String generateReceiptName() {
	        String receiptName = repwd.generateReceiptName();
	        return receiptName;
	    }

		@GetMapping("showUpdateWhReceipt")
		public String showUpdateWhReceipt(@RequestParam String id ,Model model) {
			int idwhr = Integer.parseInt(id);
			Warehouse_receipt whr = repwd.findId(idwhr);	
			model.addAttribute("up_item",whr);
			model.addAttribute("warehouses",repwd.findAllWh());
			return Views.UPDATE_WAREHOUSE_RECEIPT;
		}
		@PostMapping("updateWhReceipt")
		public String updateWhReceipt( @RequestParam("name") String name,
								        @RequestParam("wh_id") int wh_id, 
								        @RequestParam("status") String status,
								        @RequestParam("id") int id,Model model) {
			Warehouse_receipt receipt = new Warehouse_receipt();
		    receipt.setName(name);
		    receipt.setWh_id(wh_id);
		    receipt.setStatus(status);
		    receipt.setDate(LocalDateTime.now()); 
		    receipt.setId(id);
		    
		    repwd.updateWhRe(receipt);
			return "redirect:showWhReceipt";
		}
		@PostMapping("addWhReceiptDetail")
		public String addWhReceiptDetail(@RequestParam("wh_receipt_id") int wh_receipt_id,
									        @RequestParam("wh_price") double wh_price, 
									        @RequestParam("quantity") int quantity,
									        @RequestParam("shipping_fee") double shipping_fee,
									        @RequestParam("product_id") int product_id) {
			Warehouse_receipt_detail wrd = new Warehouse_receipt_detail();
			wrd.setWh_receipt_id(wh_receipt_id);
			wrd.setWh_price(wh_price);
			wrd.setQuantity(quantity);
			wrd.setShipping_fee(shipping_fee);
			wrd.setProduct_id(product_id);
			
			repwd.addWhDetail(wrd);
			return "redirect:showWhReceiptDetail?id=" + wh_receipt_id;
		}
		@GetMapping("showUpdateWhDetail")
		public String showUpdateWhDetail(@RequestParam String id ,Model model) {
			int idwhr = Integer.parseInt(id);
			Warehouse_receipt_detail whrd = repwd.findIdDetail(idwhr);	
			model.addAttribute("detail",whrd);
			model.addAttribute("products",repwd.findAllPro());
			 model.addAttribute("wh_receipt_id", whrd.getWh_receipt_id());
			return Views.UPDATE_WAREHOUSE_RECEIPT_DETAIL;
		}
		@PostMapping("updateWhDetail")
		public String updateWhDetail(@RequestParam("wh_price") String wh_priceStr, 
		                              @RequestParam("quantity") String quantityStr, 
		                              @RequestParam("product_id") int product_id,
		                              @RequestParam("shipping_fee") int shipping_fee,
		                              @RequestParam("wh_receipt_id") int wh_receipt_id,
		                              @RequestParam("id") int id) {
		    double wh_price = Double.parseDouble(wh_priceStr.replace(",", "."));
		    int quantity = Integer.parseInt(quantityStr);
		    
		    Warehouse_receipt_detail wrd = new Warehouse_receipt_detail();
		    wrd.setWh_receipt_id(wh_receipt_id);
		    wrd.setWh_price(wh_price);
		    wrd.setQuantity(quantity);
		    wrd.setShipping_fee(shipping_fee);
		    wrd.setProduct_id(product_id);
		    wrd.setId(id);
		    
		    repwd.updateWhDetails(wrd);
		    
		    return "redirect:showWhReceiptDetail?id=" + wh_receipt_id ;
		}



}
