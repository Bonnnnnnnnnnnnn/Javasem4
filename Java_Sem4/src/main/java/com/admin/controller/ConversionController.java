package com.admin.controller;



import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.admin.repository.ConversionRepository;
import com.models.Conversion;
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

    @GetMapping("/conversion")
    @ResponseBody
    public List<Map<String, Object>> findAllConversions(@RequestParam("productId") int productId) {
        return con.findAllConversions(productId);
    }

    @PostMapping("/addConversion")
    public String addUnit(
    		@RequestParam("product_id") int product_id,
    		@RequestParam("from_unit_name") int from_unit_name,
    		@RequestParam("to_unit_name") int to_unit_name,
    		@RequestParam("conversion_rate") int conversion_rate) {
        Conversion conversion = new Conversion();
        conversion.setProduct_id(product_id);
        conversion.setFrom_unit_id(from_unit_name);
        conversion.setTo_unit_id(to_unit_name);
        conversion.setConversion_rate(conversion_rate);
        System.out.println("product_id " + conversion.getProduct_id());
        System.out.println("setFrom_unit_id " + conversion.getFrom_unit_id());
        System.out.println("setTo_unit_id " + conversion.getTo_unit_id());
        System.out.println("setConversion_rate " + conversion.getConversion_rate());
        con.addConversion(conversion);
        return "redirect:/admin/product/showProductDetail?id=" + product_id;
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