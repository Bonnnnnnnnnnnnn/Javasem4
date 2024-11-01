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
import com.admin.repository.ProductRepository;
import com.models.Conversion;
import com.models.PageView;
import com.utils.Views;



@Controller
@RequestMapping("admin")
public class ConversionController {
	
	@Autowired
	private ConversionRepository con;
	@Autowired
	private ProductRepository pro;
	
    @GetMapping("/conversions")
    public String findAllConversions(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
	    PageView pv = new PageView();
	    pv.setPage_current(cp);
	    pv.setPage_size(10);
        List<Conversion> conversions = con.findAllConversions(pv);
        model.addAttribute("conversions", conversions);
	    model.addAttribute("pv", pv);
        return Views.SHOW_CONVERSION; 
    }
    
    @GetMapping("/addConversion")
    public String addConversionForm(Model model) {
		model.addAttribute("units",pro.findAllUnit());
        model.addAttribute("conversion", new Conversion());
        return Views.ADD_CONVERSION; 
    }

    @PostMapping("/addConversion")
    public String addConversion(@RequestParam int from_unit_id,
                                 @RequestParam int to_unit_id,
                                 @RequestParam int conversion_rate) {
        Conversion conver = new Conversion();
        conver.setFrom_unit_id(from_unit_id);
        conver.setTo_unit_id(to_unit_id);
        conver.setConversion_rate(conversion_rate);
        con.addConversion(conver);
        return "redirect:/admin/conversions";
    }


    @GetMapping("/deleteConversion")
    public String deleteConversion(@RequestParam("id") int conversionId) {
    	con.deleteConversion(conversionId);
        return "redirect:/admin/conversions";
    }
    
    @GetMapping("/editConversion")
    public String editConversion(@RequestParam("id") int conversionId, Model model) {
		model.addAttribute("units",pro.findAllUnit());
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
