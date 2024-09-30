package com.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class RoleInterceptor implements HandlerInterceptor {

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        HttpSession session = request.getSession();
//        Integer roleid = (Integer) session.getAttribute("roleid");
//        String requestURI = request.getRequestURI();
//        
//        if (requestURI.startsWith("/customer")) {
//            return true;
//        } else if (roleid == null) {
//            response.sendRedirect(request.getContextPath() + "/admin/employee/login");
//            return false;
//        } else if (roleid == 0 && (requestURI.startsWith("/warehouseManage") || requestURI.startsWith("/businessManage"))) {
//            response.sendRedirect(request.getContextPath() + "/admin/employee/login");
//            return false;
//        } else if (roleid == 1 && (requestURI.startsWith("/admin") || requestURI.startsWith("/businessManage"))) {
//            response.sendRedirect(request.getContextPath() + "/admin/employee/login");
//            return false;
//        } else if (roleid == 2 && (requestURI.startsWith("/admin") || requestURI.startsWith("/warehouseManage"))) {
//            response.sendRedirect(request.getContextPath() + "/admin/employee/login");
//            return false;
//        }
//
//        return true;
//
//    }
}


