package com.customer.controller;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.customer.repository.AccountRepository;
import com.customer.repository.CartRepository;
import com.customer.repository.CheckoutRepository;
import com.models.Customer;
import com.models.Order;
import com.models.Shopping_cart;
import com.utils.Views;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("checkout")
public class CheckoutController {
	@Autowired
	CartRepository repcart;
	
	@Autowired
	AccountRepository repacc;
	
	@Autowired
	CheckoutRepository repco;
	
	@GetMapping("/showcheckout")
	public String showpage(Model model, HttpServletRequest request) {	
		List<Shopping_cart> listc = repcart.findAllCartsByCustomerId((int) request.getSession().getAttribute("logined"));
		double totalCartValue = 0.0;
	    for (Shopping_cart cartItem : listc) {
	        totalCartValue += cartItem.getPrice() * cartItem.getQuantity();
	    }
	    model.addAttribute("totalCartValue", totalCartValue);
		model.addAttribute("carts", listc);
		Customer cus = repacc.finbyid((int) request.getSession().getAttribute("logined"));
		model.addAttribute("cusinfo", cus);
		model.addAttribute("listco", repco.takeall());
		
			return Views.CUS_CUSCHECKOUTPAGE;
	}

	@GetMapping("/gocheckout")
	public String gocheckout(@ModelAttribute Customer cusinfo,
							@RequestParam String payment,
							@RequestParam String phoneco,
							@RequestParam(required = false) String notes,
							Model model, HttpServletRequest request) {
		
			Order or = new Order();
			or.setDiscount(BigDecimal.ZERO);
			or.setCustomer_id(cusinfo.getId());
			List<Shopping_cart> listc = repcart.findAllCartsByCustomerId(or.getCustomer_id());
			BigDecimal totalCartValue = calculateTotalCartValue(listc);
			BigDecimal totalAmount = totalCartValue
                    .add(or.getShippingFee() != null ? or.getShippingFee() : BigDecimal.ZERO)
                    .subtract(or.getDiscount() != null ? or.getDiscount() : BigDecimal.ZERO); 
			or.setCus_Name(cusinfo.getFirst_name() + " " + cusinfo.getLast_name());
			or.setStatus("Waiting for cofirmation");
			or.setPhone(phoneco);
			or.setAddress(cusinfo.getAddress());
			or.setEmployee_id(0);
			or.setPayment_id(Integer.parseInt(payment));
			or.setNotes(notes);
			or.setDate(LocalDate.now());
			or.setTotalAmount(totalAmount);
			or.setShippingFee(BigDecimal.ZERO);
			or.setCoupon_id(0);
			request.getSession().setAttribute("checkout", or);
			
			if(or.getPayment_id() == 1) {
				or.setPay_status("Not pay yet");
				repco.Checkout(or,listc);
			}
		return "redirect:/checkout/showcheckout";
	}
	public BigDecimal calculateTotalCartValue(List<Shopping_cart> listc) {
        BigDecimal totalCartValue = BigDecimal.ZERO; 
        for (Shopping_cart cartItem : listc) {
            
            BigDecimal price = BigDecimal.valueOf(cartItem.getPrice());
            BigDecimal quantity = BigDecimal.valueOf(cartItem.getQuantity());

            
            totalCartValue = totalCartValue.add(price.multiply(quantity));
        }

        return totalCartValue; 
    }
}	
