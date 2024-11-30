  package com.admin.controller;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.admin.repository.ProductRepository;
import com.models.PageView;
import com.models.Product;
import com.models.Product_img;
import com.models.Product_price_change;
import com.models.Product_specifications;
import com.utils.FileUtils;
import com.utils.Views;

@Controller
@RequestMapping("admin/product")
public class ProductController {
	@Autowired
	private ProductRepository reppro;
	
	// show sản phẩm
	@GetMapping("/showProduct")
	public String showProduct(Model model, @RequestParam(name = "cp", required = false, defaultValue = "1") int cp) {
	    try {
	        PageView pv = new PageView();
	        pv.setPage_current(cp);
	        pv.setPage_size(10);
	        List<Product> products = reppro.findAll(pv);
	        model.addAttribute("products", products);
	        model.addAttribute("pv", pv);
	        return Views.PRODUCT_SHOWPRODUCT;
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("errorMessage", "An error occurred while loading products. Please try again.");
	        return "admin/product/errorPage";
	    }
	}
	
	// show deatils sản phẩm
	@GetMapping("/showProductDetail")
	public String showProductDetail(@RequestParam("id") String productId, @RequestParam("id") String psId, Model model) {
	    try {
	        int idpro = Integer.parseInt(productId);
	        int idps = Integer.parseInt(psId);
	        Product pro = reppro.findId(idpro);
	        List<Product_specifications> ps = reppro.findListPs(idps);
	        List<Product_img> images = reppro.findListImages(idpro);
	        List<Product_price_change> priceChanges = reppro.findListProductPriceChanges(idpro);
	        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
	        String formattedPrice = formatter.format(pro.getPrice());
	        model.addAttribute("product", pro);
	        model.addAttribute("formattedPrice", formattedPrice);
	        model.addAttribute("ps", ps);
	        model.addAttribute("images", images);
	        model.addAttribute("priceChanges", priceChanges);

	        return Views.PRODUCT_SHOWPRODUCTDETAIL;
	    } catch (NumberFormatException e) {
	        System.err.println("Product ID or Specification ID not valid: " + productId + ", " + psId);
	        return "admin/product/errorPage";
	    }
	}

	// add sản phẩm,images chi tiết,change price, và thuộc tính sản phẩm
	@GetMapping("showAddProduct")
	public String showAddProduct(Model model) {
	    try {
	        Product prod = new Product();
	        model.addAttribute("units", reppro.findAllUnit());
	        model.addAttribute("brands", reppro.findAllBrand());
	        model.addAttribute("categorys", reppro.findAllCategory());
	        model.addAttribute("new_item", prod);
	        return Views.PRODUCT_SHOWADDPRODUCT;
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("error", "An error occurred while loading the page.");
	        return "admin/product/errorPage";
	    }
	}
	@PostMapping("addProductWithDetails")
	public String addProductWithDetails(
	        @RequestParam String proName,
	        @RequestParam int cateId,
	        @RequestParam int unitId,
	        @RequestParam int brandId,
	        @RequestParam double price,
	        @RequestParam String description,
	        @RequestParam int wpe,
	        @RequestParam String status,
	        @RequestParam int weight,
	        @RequestParam int width,
	        @RequestParam int height,
	        @RequestParam int length,
	        @RequestParam("productImage") MultipartFile productImage,
	        @RequestParam("additionalImages") List<MultipartFile> additionalImages,
	        @RequestParam("specNames") List<String> specNames,
	        @RequestParam("specDescriptions") List<String> specDescriptions,
	        @RequestParam double priceChanges,
	        @RequestParam String dateStart,
	        @RequestParam String dateEnd
	) {
	    Product prod = new Product();
	    prod.setProduct_name(proName);
	    prod.setCate_id(cateId);
	    prod.setUnit_id(unitId);
	    prod.setBrand_id(brandId);
	    prod.setPrice(price);
	    prod.setDescription(description);
	    prod.setWarranty_period(wpe);
	    prod.setStatus(status);
	    prod.setWeight(weight);
	    prod.setWidth(width);
	    prod.setHeight(height);
	    prod.setLength(length);

	    String productImgUrl = FileUtils.uploadFileImage(productImage, "uploads", "product");
	    prod.setImg(productImgUrl);
	    //lưu nhiều images
	    List<Product_img> productImages = new ArrayList<>();
	    for (MultipartFile image : additionalImages) {
	        if (!image.isEmpty()) {
	            String imgUrl = FileUtils.uploadFileImage(image, "uploads", "imgDetail");
	            Product_img productImageDetail = new Product_img();
	            productImageDetail.setImg_url(imgUrl);
	            productImages.add(productImageDetail);
	        }
	    }

	    // Lưu nhiều Product_specifications
	    List<Product_specifications> specifications = new ArrayList<>();
	    for (int i = 0; i < specNames.size(); i++) {
	        Product_specifications spec = new Product_specifications();
	        spec.setName_spe(specNames.get(i));
	        spec.setDes_spe(specDescriptions.get(i));
	        specifications.add(spec);
	    }
	    // Lưu thông tin thay đổi giá
	    Product_price_change priceChangeDetail = new Product_price_change();
	    priceChangeDetail.setPrice(priceChanges);
	    priceChangeDetail.setDate_start(LocalDateTime.now());
	    priceChangeDetail.setDate_end(null);
	    
	    reppro.saveProductWithDetails(prod, specifications, productImages, priceChangeDetail);

	    return "redirect:showProduct";
	}

	// xóa sản phẩm,hình sản phẩm
	@GetMapping("deleteProduct")
	public String deleteProduct(@RequestParam("id") String id, 
	                            @RequestParam("fileName") String fileName) {
	    try {
	        int idp = Integer.parseInt(id);
	        String folderName = "uploads";
	        String result = reppro.deleteProduct(idp, folderName, fileName);
	        System.out.println(result);
	        return "redirect:showProduct";

	    } catch (NumberFormatException e) {
	        e.printStackTrace();
	        return "admin/product/errorPage";

	    } 
	}
	
	// sửa lại sản phẩm , change price lại
	@GetMapping("/showUpdateProduct")
	public String showUpdateProduct(Model model, @RequestParam String id) {
	    try {
	        int idPro = Integer.parseInt(id);
	        Product product = reppro.findId(idPro);
	        if (product == null) {
	            model.addAttribute("error", "Product not found.");
	            return "redirect:showProduct";
	        }
	        model.addAttribute("product", product);
	        model.addAttribute("units", reppro.findAllUnit());
	        model.addAttribute("brands", reppro.findAllBrand());
	        model.addAttribute("categorys", reppro.findAllCategory());

	        return Views.PRODUCT_SHOWUPDATEPRODUCT;

	    } catch (NumberFormatException e) {
	        e.printStackTrace();
	        model.addAttribute("error", "Invalid Product ID format.");
	        return "redirect:showProduct";
	    } 
	}
	@PostMapping("/updateProductAndPrice")
	public String updateProductAndPrice(
	        @RequestParam int id,
	        @RequestParam String productName,
	        @RequestParam int categoryId,
	        @RequestParam int brandId,
	        @RequestParam int unitId,
	        @RequestParam double price,
	        @RequestParam String description,
	        @RequestParam String status,
	        @RequestParam("warranty_period") int warrantyPeriod,
	        @RequestParam int weight,
	        @RequestParam int width,
	        @RequestParam int height,
	        @RequestParam int length,
	        @RequestParam("img") MultipartFile productImage,
	        @RequestParam double newPrice) {
	    Product product = new Product();
	    product.setId(id);
	    product.setProduct_name(productName);
	    product.setCate_id(categoryId);
	    product.setBrand_id(brandId);
	    product.setUnit_id(unitId);
	    product.setPrice(price);
	    product.setDescription(description);
	    product.setStatus(status);
	    product.setWarranty_period(warrantyPeriod);
	    product.setWeight(weight);
	    product.setWidth(width);
	    product.setHeight(height);
	    product.setLength(length);

	    if (!productImage.isEmpty()) {
	        String imageUrl = FileUtils.uploadFileImage(productImage, "uploads", "product");
	        product.setImg(imageUrl);
	    }
	     reppro.updateProductAndPrice(product, newPrice);

	     return "redirect:showProduct";
	}
	
	
	
	//product_specifications ============================================================
	
	// add thêm thuộc tính nếu cần
	@GetMapping("showAddPs")
	public String showAddPs(Model model, @RequestParam("id") int id) {
	    try {
	        Product_specifications ps = new Product_specifications();
	        Product product = reppro.findIdProT(id);
	        if (product != null) {
	            model.addAttribute("new_item", ps);
	            model.addAttribute("product", product);
	        } else {
	            model.addAttribute("errorMessage", "Product not found!");
	        }

	        return Views.PRODUCT_SPE_SHOWADDPS;

	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("errorMessage", "An error occurred while loading the product.");
	        return "redirect:/showProduct";
	    }
	}
	@PostMapping("addPs")
	public String addPs(@RequestParam("name_spe") String nameSpe,
	                    @RequestParam("des_spe") String desSpe,
	                    @RequestParam("product_id") int productId) {
	    Product_specifications ps = new Product_specifications();
	    ps.setName_spe(nameSpe);
	    ps.setDes_spe(desSpe);
	    ps.setProduct_id(productId);

	    reppro.addProPs(ps);

	    return "redirect:/admin/product/showProductDetail?id=" + productId + "&activeTab=productSpecifications";
	}


	@GetMapping("/errorPage")
	public String errorPage() {
		
		return "admin/product/errorPage";
	}
}
