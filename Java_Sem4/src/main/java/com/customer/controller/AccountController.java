package com.customer.controller;


import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.customer.repository.AccountRepository;
import com.customer.repository.DetailproductRepository;
import com.customer.repository.EmailService;
import com.models.Customer;
import com.models.Product;
import com.utils.Views;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("account")
public class AccountController {
	@Autowired
    EmailService emailService;
	
	@Autowired
	AccountRepository accrepo;
	
	@GetMapping("/signin")
	public String showpagesignin(Model model) {	
		
		return Views.CUS_SIGNINPAGE;
	}
	
	@GetMapping("/signup")
	public String showpagesignup(Model model) {	
		System.out.println(accrepo.isEmailRegistered("bonnguyen11917@gmail.com"));
		return Views.CUS_SIGNUPPAGE;
	}
	@PostMapping("/goregister")
	public String gosent(@RequestParam("email") String email, 
            @RequestParam("password") String password, 
            Model model,HttpServletRequest request) {
			
			String token = EmailService.generateToken();
	        
			request.getSession().setAttribute("emailregister", email);
	        request.getSession().setAttribute("passwordregister", password);
	        request.getSession().setAttribute("tokenregister", token); 
	        model.addAttribute("email", email);
	        // Generate a confirmation link with the token
	        String confirmationLink = "http://localhost:8080/account/confirm?email=" + email + "&token=" + token;
	
	        // Send the confirmation email
	        try {
				emailService.sendConfirmationEmail(email, confirmationLink);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Views.CUS_COFIRMPAGE;
		}
	@GetMapping("/confirm")
    public String confirmRegistration(@RequestParam("email") String email,
                                      @RequestParam("token") String token,
                                      HttpServletRequest request,
                                      Model model) {
        // Retrieve the token stored in the session
        String sessionToken = (String) request.getSession().getAttribute("tokenregister");
        String sessionEmail = (String) request.getSession().getAttribute("emailregister");
        String sessionPw= (String) request.getSession().getAttribute("passwordregister");
        System.out.println(sessionEmail);
        System.out.println(sessionToken);
        // Check if the email and token match
        if (sessionEmail != null && sessionEmail.equals(email) && sessionToken != null && sessionToken.equals(token)) {
        		Customer newcus = new Customer();
        		newcus.setEmail(sessionEmail);
        		newcus.setPassword((String) request.getSession().getAttribute("passwordregister")); 

        		accrepo.createAccount(newcus); 
            System.out.println("Account created successfully for email: " + email);
            Customer cus = accrepo.login(email, sessionPw);
            // Clear session attributes
            request.getSession().removeAttribute("emailregister");
            request.getSession().removeAttribute("passwordregister");
            request.getSession().removeAttribute("tokenregister");
            request.getSession().setAttribute("logined", cus.getId()); 
           
            return "redirect:/"; 
        } else {
          
            System.out.println("Invalid confirmation token or email for email: " + email);
            model.addAttribute("error", "Invalid confirmation token or email.");
            return "redirect:/account/signup"; // Change to your error page
        }
    }
	@GetMapping("/checkemail")
	@ResponseBody
	public boolean checkEmail(@RequestParam("email") String email) {
	    return accrepo.isEmailRegistered(email);
	}
	
	@PostMapping("/checklogin")
	@ResponseBody
	public ResponseEntity<Boolean> checklogin(@RequestParam("email") String email, @RequestParam("password") String password, HttpServletRequest request) {
	    Customer customer = accrepo.login(email, password); 
	    if(customer != null) {
	    	request.getSession().setAttribute("logined", customer.getId()); 
	    }
	    boolean success = customer != null;
	    return ResponseEntity.ok(success);
	}

}
