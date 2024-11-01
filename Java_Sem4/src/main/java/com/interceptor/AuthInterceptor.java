package com.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.interceptor.AuthInterceptor;
import com.models.Employee;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	    HttpSession session = request.getSession();
	    
	    Employee emp = (Employee) session.getAttribute("loggedInEmployee");

	    if (emp == null) {
	        response.sendRedirect("/employee/login");
	        return false;
	    }

	    String requestURI = request.getRequestURI();
	    int roleId = emp.getRole_id();  

	    if (requestURI.startsWith("/admin") && roleId != 1 ) {
	        response.sendRedirect("/employee/login");
	        return false;
	    } else if (requestURI.startsWith("/businessManager") && roleId != 2) {
	        response.sendRedirect("/employee/login");
	        return false;
	    } else if (requestURI.startsWith("/warehouse") && roleId != 3) {
	        response.sendRedirect("/employee/login");
	        return false;
	    }	    
	    return true;
	}

    
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
}
