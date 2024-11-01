package com.admin.controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.admin.repository.ProductRepository;
import com.models.PageView;
import com.models.Product;
import com.utils.FileUtils;
import com.utils.Views;

@Controller
@RequestMapping("admin/product")
public class ProductController {
	@Autowired
	private ProductRepository reppro;
	@GetMapping("/showProduct")
	public String showProduct(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
	    PageView pv = new PageView();
	    pv.setPage_current(cp);
	    pv.setPage_size(10);
	    List<Product> products = reppro.findAll(pv);
	    model.addAttribute("products", products);
	    model.addAttribute("pv", pv);
	    return Views.PRODUCT_SHOWPRODUCT;
	}
	@GetMapping("/showProductDetail")
	public String showProductDetail(@RequestParam("id") String productId, Model model) {
	    int idpro = Integer.parseInt(productId);
	    Product pro = reppro.findId(idpro);
	    NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
	    String formattedPrice = formatter.format(pro.getPrice());
	    model.addAttribute("product", pro);
	    model.addAttribute("formattedPrice", formattedPrice);
	    return Views.PRODUCT_SHOWPRODUCTDETAIL;
	}


	@GetMapping("showAddProduct")
	public String showAddProduct(Model model) {
	    Product prod = new Product();
		model.addAttribute("conversions",reppro.findAllConv());
		model.addAttribute("brands",reppro.findAllBrand());
		model.addAttribute("categorys",reppro.findAllCategory());
	    model.addAttribute("new_item", prod);
	    return Views.PRODUCT_SHOWADDPRODUCT;
	}
	@PostMapping("addProduct")
	public String addProduct(@RequestParam String proName,
								@RequestParam int cateId,
								@RequestParam int conversionId,
								@RequestParam int brandId,
								@RequestParam double price,
								@RequestParam String description,
								@RequestParam int wpe,
								@RequestParam String status,
								@RequestParam("images") MultipartFile images) {
		Product prod = new Product();
		prod.setProduct_name(proName);
		prod.setCate_id(cateId);
		prod.setConversion_id(conversionId);
		prod.setBrand_id(brandId);
		prod.setPrice(price);
		prod.setDescription(description);
		prod.setWarranty_period(wpe);
		prod.setStatus(status);
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
	@PostMapping("/deleteSelected")
	@ResponseBody
	public ResponseEntity<String> deleteSelectedProducts(@RequestBody List<Integer> ids) {
	    if (ids == null || ids.isEmpty()) {
	        return ResponseEntity.badRequest().body("No product IDs provided.");
	    }
	    ids.removeIf(Objects::isNull);

	    try {
	        for (Integer id : ids) {
	            String fileName = reppro.getProductImageById(id);
	            String result = reppro.deleteProduct(id, "uploads", fileName);
	            System.out.println(result);
	        }
	        return ResponseEntity.ok("Products and corresponding images deleted successfully.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete products: " + e.getMessage());
	    }
	}
	@GetMapping("/showUpdateProduct")
	public String showUpdateProduct(Model model,@RequestParam String id) {
		int idPro = Integer.parseInt(id);
		model.addAttribute("up_item", reppro.findId(idPro));
		model.addAttribute("conversions",reppro.findAllConv());
		model.addAttribute("brands",reppro.findAllBrand());
		model.addAttribute("categorys",reppro.findAllCategory());
		return Views.PRODUCT_SHOWUPDATEPRODUCT;
	}
	@PostMapping("updateProduct")
	public String updateProduct(@RequestParam("product_name") String proName,
								@RequestParam("cate_id") int cateId,
								@RequestParam("conversion_id") int conversionId,
								@RequestParam("brand_id") int brandId,
								@RequestParam("price") double price,
								@RequestParam("description") String description,
								@RequestParam("warranty_period") int wpe,
								@RequestParam("status") String status,
								@RequestParam("img") MultipartFile img,
								@RequestParam("id") int id) {
		Product prod = new Product();
		prod.setProduct_name(proName);
		prod.setCate_id(cateId);
		prod.setConversion_id(conversionId);
		prod.setBrand_id(brandId);
		prod.setPrice(price);
		prod.setDescription(description);
		prod.setWarranty_period(wpe);
		prod.setStatus(status);
		prod.setImg(FileUtils.uploadFileImage(img , "uploads"));
		prod.setId(id);
		reppro.updateProduct(prod);	
		return "redirect:showProduct";
	}
}
