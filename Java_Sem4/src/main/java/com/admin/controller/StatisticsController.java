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

public class StatisticsController {
	

}
