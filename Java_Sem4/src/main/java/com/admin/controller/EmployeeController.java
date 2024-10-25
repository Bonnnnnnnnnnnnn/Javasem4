package com.admin.controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
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
import com.models.PageView;
import com.models.Product;
import com.utils.SecurityUtility;
import com.utils.Views;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("admin/employee")
public class EmployeeController {
	@Autowired
    private EmployeeRepository emprep;
//	@GetMapping("login")
//	public String login() {
//		return Views.EMPLOYEE_LOGIN;
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
//	        if (roleid == 1) {
//	            return "redirect:admin/employee/showEmp"; 
//	        } else if (roleid == 2) {
//	            return "redirect:warehouseManage/warehouse/index"; 
//	        } else if (roleid == 3) {
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
	public String showEmp(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
	    PageView pv = new PageView();
	    pv.setPage_current(cp);
	    pv.setPage_size(5);
	    List<Employee> emp = emprep.findAll(pv);
		model.addAttribute("employees", emp);
	    model.addAttribute("pv", pv);
		return Views.EMPLOYEE_SHOWEMP;
	}
	@GetMapping("/showEmployeeDetail")
	public String showEmployeeDetail(@RequestParam("id") String employeeId, Model model) {
		int idemp = Integer.parseInt(employeeId);
		Employee emp = emprep.findId(idemp);
	    model.addAttribute("employee", emp);
	    return Views.EMPLOYEE_SHOWEMPLOYEEDETAIL;
	}
	@GetMapping("/showRegister")
	public String showRegister(Model model) {
		model.addAttribute("roles",emprep.findAllRole());
		return Views.EMPLOYEE_SHOWREGISTER;
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
	@GetMapping("showUpdateEmployee")
	public String showUpdateEmployee(Model model,@RequestParam String id) {
		int ide = Integer.parseInt(id);
		model.addAttribute("employee",emprep.findId(ide));
		model.addAttribute("roles", emprep.findAllRole());
		return Views.EMPLOYEE_SHOWUPDATEMPLOYEE;
	}
	@PostMapping("updateEmp")
	public String updateEmp(@RequestParam("id") int id,
	                        @RequestParam("first_name") String finame,
	                        @RequestParam("last_name") String laname,
	                        @RequestParam("password") String pwd,
	                        @RequestParam("phone") String phone,
	                        @RequestParam("role_id") int roleId) {
	    Employee emp = new Employee();
	    emp.setId(id);
	    emp.setFirst_name(finame);
	    emp.setLast_name(laname);
	    emp.setPassword(pwd);
	    emp.setPhone(phone);
	    emp.setRole_id(roleId);
	    emprep.updateEmp(emp);
	    return "redirect:/admin/employee/showEmp";
	}

	@GetMapping("deleteEmployee")
	public String deleteEmployee(@RequestParam("id") String id) {
		int ide = Integer.parseInt(id);
		emprep.deleteEmployee(ide);
		return "redirect:/admin/employee/showEmp";
	}

}