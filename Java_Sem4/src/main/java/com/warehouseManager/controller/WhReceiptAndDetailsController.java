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

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("warehouseManager/warehouseReceipt")
public class WhReceiptAndDetailsController {
		@Autowired
		private WhReceiptAndDetailsRepository repwd;

		//show phiếu nhập kho
		@GetMapping("showWhReceipt")
		public String showWhReceipt(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
		    try {
		        PageView pv = new PageView();
		        pv.setPage_current(cp);
		        pv.setPage_size(10);
		        List<Warehouse_receipt> whr = repwd.findAll(pv);
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
		        
		        Warehouse_receipt whr = new Warehouse_receipt();
		        model.addAttribute("new_item", whr);
		        model.addAttribute("warehouses", repwd.findAllWh());
		        model.addAttribute("products", repwd.findAllPro());
		        model.addAttribute("conversions",repwd.findAllcon());
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
		        @RequestParam("otherFee") double otherFee, 
		        @RequestParam("totalFee") double totalFee,
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
		    receipt.setTotal_fee(totalFee);
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

		 //sửa phiếu nhập kho
		 @GetMapping("showUpdateWhReceipt")
		 public String showUpdateWhReceipt(@RequestParam String id, Model model) {
		     try {
		         int idwhr = Integer.parseInt(id);
		         Warehouse_receipt whr = repwd.findId(idwhr);    
		         model.addAttribute("up_item", whr);
		         model.addAttribute("warehouses", repwd.findAllWh());
		         return Views.UPDATE_WAREHOUSE_RECEIPT;
		     } catch (Exception e) {
		         e.printStackTrace();
		         return "error";
		     }
		 }

		@PostMapping("updateWhReceipt")
		public String updateWhReceipt( @RequestParam("name") String name,
								        @RequestParam("wh_id") int wh_id, 
								        @RequestParam("status") String status,
								        @RequestParam("shippingFee") double ShippingFee,
								        @RequestParam("otherFee") double otherFee, 
								        @RequestParam("totalFee") double totalFee,
								        @RequestParam("employeeId") int employeeId,
								        @RequestParam("id") int id,Model model) {
			Warehouse_receipt receipt = new Warehouse_receipt();
		    receipt.setName(name);
		    receipt.setWh_id(wh_id);
		    receipt.setStatus(status);
		    receipt.setShipping_fee(ShippingFee);
		    receipt.setOther_fee(otherFee);
		    receipt.setTotal_fee(totalFee);
		    receipt.setEmployee_id(employeeId);
		    receipt.setDate(LocalDateTime.now()); 
		    receipt.setId(id);
		    
		    repwd.updateWhRe(receipt);
			return "redirect:showWhReceipt";
		}
		//view chung với chi tiết phiếu
		@PostMapping("addWhReceiptDetail")
		public String addWhReceiptDetail(@RequestParam("wh_receipt_id") int wh_receipt_id,
									        @RequestParam("wh_price") double wh_price, 
									        @RequestParam("quantity") int quantity,
									        @RequestParam("conversionId") int conversionId,
									        @RequestParam("status") String status,
									        @RequestParam("product_id") int product_id) {
			Warehouse_receipt_detail wrd = new Warehouse_receipt_detail();
			wrd.setWh_receipt_id(wh_receipt_id);
			wrd.setWh_price(wh_price);
			wrd.setQuantity(quantity);
			wrd.setConversion_id(conversionId);
			wrd.setStatus(status);
			wrd.setProduct_id(product_id);
			
			repwd.addWhDetail(wrd);
			return "redirect:showWhReceiptDetail?id=" + wh_receipt_id;
		}
		
		//sửa chi tiết phiếu nhập 
		@GetMapping("showUpdateWhDetail")
		public String showUpdateWhDetail(@RequestParam String id, Model model) {
		    try {
		        int idwhr = Integer.parseInt(id);
		        Warehouse_receipt_detail whrd = repwd.findIdDetail(idwhr);    
		        model.addAttribute("detail", whrd);
		        model.addAttribute("products", repwd.findAllPro());
		        model.addAttribute("wh_receipt_id", whrd.getWh_receipt_id());
		        return Views.UPDATE_WAREHOUSE_RECEIPT_DETAIL;
		    } catch (Exception e) {
		        e.printStackTrace();
		        return "error";
		    }
		}

		@PostMapping("updateWhDetail")
		public String updateWhDetail(@RequestParam("wh_price") String wh_priceStr, 
		                              @RequestParam("quantity") String quantityStr, 
		                              @RequestParam("product_id") int product_id,
		                              @RequestParam("conversionId") int conversionId,
								      @RequestParam("status") String status,
		                              @RequestParam("wh_receipt_id") int wh_receipt_id,
		                              @RequestParam("id") int id) {
		    double wh_price = Double.parseDouble(wh_priceStr.replace(",", "."));
		    int quantity = Integer.parseInt(quantityStr);
		    
		    Warehouse_receipt_detail wrd = new Warehouse_receipt_detail();
		    wrd.setWh_receipt_id(wh_receipt_id);
		    wrd.setWh_price(wh_price);
		    wrd.setQuantity(quantity);
			wrd.setConversion_id(conversionId);
			wrd.setStatus(status);
		    wrd.setProduct_id(product_id);
		    wrd.setId(id);
		    
		    repwd.updateWhDetails(wrd);
		    
		    return "redirect:showWhReceiptDetail?id=" + wh_receipt_id ;
		}



}