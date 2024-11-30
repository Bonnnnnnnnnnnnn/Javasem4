package com.customer.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.customer.repository.CustomerRepository;
import com.models.Customer;

@Controller
@RequestMapping("/customer")
public class CustomerController {
	@Autowired
	private CustomerRepository cusrep;
	@GetMapping("/showRegisterCustomer")
	public String showRegisterCustomer() {
		
		return "customer/showRegisterCustomer";
	}
	@PostMapping("registerCustomer")
	public String registerCustomer(
								    @RequestParam String finame,
								    @RequestParam String laname,
								    @RequestParam String address,
								    @RequestParam String pwd,
								    @RequestParam String phone,
								    @RequestParam String uid,
								    @RequestParam String birthDay,
								    @RequestParam MultipartFile imgCus
								) {
		    Customer cus = new Customer();
		    cus.setFirst_name(finame);
		    cus.setLast_name(laname);
		    cus.setAddress(address);
		    cus.setPassword(pwd);
		    cus.setPhone(phone);
		    cus.setUid(uid);
		    LocalDateTime currentTime = LocalDateTime.now();
		    cus.setCreation_time(currentTime);
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		    LocalDateTime birthDayParsed = LocalDateTime.parse(birthDay, formatter);
		    cus.setBirthDay(birthDayParsed);
//		    cus.setImg_cus(FileUtils.uploadFileImage(imgCus, "uploads"));
		    cusrep.saveCus(cus);
		    return "redirect:/customer/index";
		}
	@GetMapping("showLoginCustomer")
	public String showLoginCustomer() {
		return "customer/showloginCustomer";
	}
//	@PostMapping("checkLogincus")
//	public String checkLogincus() {
//		
//	}
}
