package com.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.admin.repository.CategoryRepository;
import com.customer.repository.AccountRepository;
import com.models.Customer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // Thêm annotation này
public class HeaderInterceptor implements HandlerInterceptor {
    
    @Autowired
    private CategoryRepository repca;
    @Autowired
    private AccountRepository repacc;
  
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        request.setAttribute("categorys", repca.findAll());
        if(request.getSession().getAttribute("logined") != null) {
        	int idlogined = (int)request.getSession().getAttribute("logined");
        	 request.setAttribute("logined", idlogined);
        	 Customer cus =  repacc.finbyid(idlogined);
        	 var name = cus.getFirst_name()+ " " + cus.getLast_name();
        	 request.setAttribute("loginedname",name);
        }else {
        	 request.setAttribute("logined", null);
        }
       
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            modelAndView.addObject("categorys", request.getAttribute("categorys"));
            modelAndView.addObject("logined", request.getAttribute("logined"));
            modelAndView.addObject("loginedname", request.getAttribute("loginedname"));
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
