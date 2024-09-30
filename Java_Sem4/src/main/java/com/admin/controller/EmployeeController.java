package com.admin.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.admin.repository.EmployeeRepository;
import com.models.Employee;
import com.utils.SecurityUtility;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("admin/employee")
public class EmployeeController {
	@Autowired
    private EmployeeRepository emprep;
//	@GetMapping("/login")
//	public String login() {
//		return "admin/employee/login";
//	}
//	@PostMapping("/chklogins")
//	public String chklogins(@RequestParam("phone") String phone,
//	                        @RequestParam("password") String password,
//	                        HttpServletRequest request) {
//	    Logger log = Logger.getGlobal();
//	    log.info("Attempted login by user: " + phone);
//
//	    String encryptedPassword = emprep.findPassword(phone);
//	    Integer roleid = emprep.findPhone(phone);
//
//	    if (encryptedPassword != null && SecurityUtility.compareBcrypt(encryptedPassword, password)) {
//	        request.getSession().setAttribute("myacc", phone);
//	        request.getSession().setAttribute("roleid", roleid);
//
//	        if (roleid == 0) {
//	            return "redirect:admin/employee/showEmp"; 
//	        } else if (roleid == 1) {
//	            return "redirect:warehouseManage/warehouse/index"; 
//	        } else if (roleid == 2) {
//	            return "redirect:businessManage/order/index";
//	        } else {
//	            return "redirect:admin/employee/login";
//	        }
//	    } else {
//	        request.setAttribute("error", "Invalid username or password");
//	        return "/admin/employee/login";
//	    }
//	}
	@GetMapping("/showEmp")
	public String showEmp(Model model ) {
		model.addAttribute("employees", emprep.findAll());
		return "admin/employee/showEmp";
	}
	@GetMapping("/showRegister")
	public String showRegister(Model model) {
		model.addAttribute("roles",emprep.findAllRole());
		return "admin/employee/showRegister";
	}
	@PostMapping("/registerEmp")
    public String registerEmp(@RequestParam String finame,
					    		@RequestParam String laname,
					    		@RequestParam String pwd,
					    		@RequestParam String phone,
					    		@RequestParam int roleId) {
        Employee emp = new Employee();
        emp.setFirst_name(finame);
        emp.setLast_name(laname);
        emp.setPassword(pwd); 
        emp.setPhone(phone);
        emp.setRole_id(roleId);
        emprep.saveEmp(emp);
        return "redirect:/admin/employee/showEmp";
    }

}
