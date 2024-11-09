package com.customer.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.customer.repository.CartRepository;
import com.customer.repository.OrderRepository;
import com.customer.repository.Order_detailRepository;
import com.models.Order;
import com.models.Order_detail;
import com.models.PageView;
import com.models.Shopping_cart;
import com.utils.Views;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("order")
public class OrderController {
	@Autowired
	OrderRepository repo;
	
	@Autowired
	Order_detailRepository repod;
	
	@GetMapping("/showorder")
	public String showpage(Model model, 
            @RequestParam(name = "cp", required = false, defaultValue = "1") int cp, 
            HttpServletRequest request) {

			PageView pv = new PageView();
			pv.setPage_current(cp);
			pv.setPage_size(5);
			int idlogined = (int)request.getSession().getAttribute("logined");
			List<Order> listo = repo.getOrdersByCustomerId(pv, idlogined, null, null, null);
			model.addAttribute("listo", listo);
			model.addAttribute("pv", pv);
			return Views.CUS_ORDEREDPAGE;
			}
	@GetMapping("/showdetailor")
	public String showdetailor(Model model, 
			@RequestParam int id,
            HttpServletRequest request) {
			model.addAttribute("order",repo.getOrderByOrderId(id));
			model.addAttribute("orderdetail",repod.findAllOrderDetailsByOrderId(id));
			List<Order_detail> lod = repod.findAllOrderDetailsByOrderId(id);
			for(Order_detail od : lod) {
				System.out.println(od.getId());
			}
			return Views.CUS_ORDEREDDETAILPAGE;
			}
	
}
