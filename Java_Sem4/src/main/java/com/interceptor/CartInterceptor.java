package com.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.admin.repository.CategoryRepository;
import com.customer.repository.AccountRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // Thêm annotation này
public class CartInterceptor implements HandlerInterceptor {
    
    @Autowired
    private CategoryRepository repca;
    
  
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession().getAttribute("logined") != null) {
            // User is logged in
            request.setAttribute("logined", request.getSession().getAttribute("logined"));
            return true; // Continue processing the request
        } else {
            // User is not logged in, redirect to the sign-in page
            response.sendRedirect(request.getContextPath() + "/account/signin");
            return false; // Prevent further handling of the request
        }
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
       
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
