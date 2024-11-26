package com.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.admin.repository.ConversionRepository;
import com.models.Conversion;
import com.models.PageView;
import com.utils.Views;



@Controller
@RequestMapping("admin")
public class ConversionController {
	
	@Autowired
	private ConversionRepository con;

	
    
    @GetMapping("/addConversion")
    public String addConversionForm(Model model) {
		model.addAttribute("units",con.findAllUnit());
        model.addAttribute("conversion", new Conversion());
        return Views.ADD_CONVERSION; 
    }



    @PostMapping("/deleteConversion")
    public String deleteConversion(@RequestParam("id") int conversionId,
    		@RequestParam int product_id) {
    	con.deleteConversion(conversionId);
        return "redirect:/admin/product/showProductDetail?id=" + product_id;
    }
    
    @GetMapping("/editConversion")
    public String editConversion(@RequestParam("id") int conversionId, Model model) {
		model.addAttribute("units",con.findAllUnit());
        Conversion conversion = con.findById(conversionId);
        model.addAttribute("conversion", conversion);
        return Views.UPDATE_CONVERSION; 
    }

    @PostMapping("/updateConversion")
    public String updateConversion(Conversion conversion) {
    	con.updateConversion(conversion);
        return "redirect:/admin/conversions";
    }
}