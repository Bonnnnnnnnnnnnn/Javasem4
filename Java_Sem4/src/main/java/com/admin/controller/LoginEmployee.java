package com.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.admin.repository.LoginEmploy;
import com.models.Employee;
import com.utils.Views;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("employee")
public class LoginEmployee {
	@Autowired
	private LoginEmploy emprep;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLoginPage() {
	    return Views.EMPLOYEE_LOGIN; 
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam("uid") String uid, 
	                    @RequestParam("pwd") String pwd, 
	                    HttpSession session, 
	                    Model model) {
	    Employee emp = emprep.login(uid, pwd);
	    System.out.println(emp.getId());
	    if (emp != null) {
	        session.setAttribute("loggedInEmployee", emp);


	        switch (emp.getRole_id()) {
	            case 1: return "redirect:/admin/employee/showEmp";            
	            case 2: return "redirect:/warehouseManager/warehouseReceipt/showWhReceipt";
	            case 3: return "redirect:/businessManager/warehouseReleasenotes";  
	            default: return "redirect:/login?error=role";      
	        }
	    } else {
	        model.addAttribute("error", "Wrong login information");
	        return Views.EMPLOYEE_LOGIN; 
	    }
	}	
}
