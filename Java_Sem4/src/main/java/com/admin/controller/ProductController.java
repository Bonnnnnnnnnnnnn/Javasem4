package com.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.admin.repository.ProductRepository;
import com.models.Brand;
import com.models.Category_Product;
import com.models.PageView;
import com.models.Product;
import com.models.Unit;
import com.utils.FileUtils;

@Controller
@RequestMapping("admin/product")
public class ProductController {
	@Autowired
	private ProductRepository reppro;
	@GetMapping("/showProduct")
	public String showProduct(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
	    PageView pv = new PageView();
	    pv.setPage_current(cp);
	    pv.setPage_size(5);
	    List<Product> products = reppro.findAll(pv);
	    model.addAttribute("products", products);
	    model.addAttribute("pv", pv);

	    return "admin/product/showProduct";
	}
	@GetMapping("showAddProduct")
	public String showAddProduct(Model model) {
	    Product prod = new Product();
		model.addAttribute("units",reppro.findAllUnit());
		model.addAttribute("brands",reppro.findAllBrand());
		model.addAttribute("categorys",reppro.findAllCategory());
	    model.addAttribute("new_item", prod);
	    return "admin/product/showAddProduct";
	}
	@PostMapping("addProduct")
	public String addProduct(@RequestParam String proName,
								@RequestParam int cateId,
								@RequestParam int unitId,
								@RequestParam int brandId,
								@RequestParam double price,
								@RequestParam String description,
								@RequestParam int wpe,
								@RequestParam("images") MultipartFile images) {
		Product prod = new Product();
		prod.setProduct_name(proName);
		prod.setCate_id(cateId);
		prod.setUnit_id(unitId);
		prod.setBrand_id(brandId);
		prod.setPrice(price);
		prod.setDescription(description);
		prod.setWarranty_period(wpe);
		prod.setImg(FileUtils.uploadFileImage(images,"uploads"));
		
		reppro.saveProduct(prod);
		
		return "redirect:showProduct";
	}
	@GetMapping("deleteProduct")
	public String deleteProduct(@RequestParam("id") String id, 
	                            @RequestParam("fileName") String fileName) {
	    int idp = Integer.parseInt(id);
	    String folderName = "uploads";
	    String result = reppro.deleteProduct(idp, folderName, fileName);
	    System.out.println(result);
	    return "redirect:showProduct";
	}

	@GetMapping("/showUpdateProduct")
	public String showUpdateProduct(Model model,@RequestParam String id) {
		int idPro = Integer.parseInt(id);
		model.addAttribute("up_item", reppro.findId(idPro));
		model.addAttribute("units",reppro.findAllUnit());
		model.addAttribute("brands",reppro.findAllBrand());
		model.addAttribute("categorys",reppro.findAllCategory());
		return"admin/product/showUpdateProduct";
	}
	@PostMapping("updateProduct")
	public String updateProduct(@RequestParam("product_name") String proName,
								@RequestParam("cate_id") int cateId,
								@RequestParam("unit_id") int unitId,
								@RequestParam("brand_id") int brandId,
								@RequestParam("price") double price,
								@RequestParam("description") String description,
								@RequestParam("warranty_period") int wpe,
								@RequestParam("img") MultipartFile images,
								@RequestParam("id") int id) {
		Product prod = new Product();
		prod.setProduct_name(proName);
		prod.setCate_id(cateId);
		prod.setUnit_id(unitId);
		prod.setBrand_id(brandId);
		prod.setPrice(price);
		prod.setDescription(description);
		prod.setWarranty_period(wpe);
		prod.setImg(FileUtils.uploadFileImage(images , "uploads"));
		prod.setId(id);
		
		reppro.updateProduct(prod);	
		return "redirect:showProduct";
	}
	
	
	
}
