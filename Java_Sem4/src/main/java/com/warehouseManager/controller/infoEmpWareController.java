package com.warehouseManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.models.Employee;
import com.utils.Views;
import com.warehouseManager.repository.inforEmpWarehouse;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("warehouseManager")
public class infoEmpWareController {

    @Autowired
    private inforEmpWarehouse emp;

    @GetMapping("showEmployee")
    public String showEmployee(Model model, HttpSession session) {
        try {
            // Lấy đối tượng Employee từ session
            Employee loggedInEmployee = (Employee) session.getAttribute("loggedInEmployee");

            // Kiểm tra nếu nhân viên chưa đăng nhập
            if (loggedInEmployee == null) {
                return "redirect:/employee/login";  // Chuyển hướng về trang login nếu chưa đăng nhập
            }

            Integer employeeId = loggedInEmployee.getId();
            System.out.println("Employee ID: " + employeeId);

            // Lấy thông tin nhân viên từ cơ sở dữ liệu
            Employee employee = emp.findId(employeeId);

            // Kiểm tra nếu không tìm thấy nhân viên
            if (employee == null) {
                return "redirect:/employee/login";  // Nếu không tìm thấy nhân viên, chuyển hướng về trang login
            }

            // Đưa thông tin nhân viên vào model để truyền cho view
            model.addAttribute("employee", employee);

            // Trả về view để hiển thị thông tin nhân viên
            return Views.EMPLOYEE_WAREHOUSE_MANAGER;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/employee/login";  // Xử lý lỗi và chuyển hướng về trang login
        }
    }



}
