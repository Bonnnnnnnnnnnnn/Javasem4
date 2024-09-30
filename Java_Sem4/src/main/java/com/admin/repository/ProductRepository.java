package com.admin.repository;

import java.sql.Types;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Product_mapper;
import com.mapper.Unit_mapper;
import com.mapper.Brand_mapper;
import com.mapper.Category_mapper;
import com.models.Brand;
import com.models.Category_Product;
import com.models.PageView;
import com.models.Product;
import com.models.Unit;
import com.utils.FileUtils;
import com.utils.Views;



@Repository
public class ProductRepository {

	private final JdbcTemplate dbpro;
	
	public ProductRepository(JdbcTemplate jdbc) {
		this.dbpro = jdbc;
	}
	public List<Unit> findAllUnit(){
		String sql = "SELECT * FROM Unit";
		return dbpro.query(sql, new Unit_mapper());
	}
    public List<Brand> findAllBrand() {
        String sql = "SELECT * FROM Brand";
        return dbpro.query(sql, new Brand_mapper());
    }
    public List<Category_Product> findAllCategory(){
    	String sql = "SELECT * FROM Product_category";
    	return dbpro.query(sql, new Category_mapper());
    }
    public boolean saveProduct(Product pro) {
        try {
            String sql = "INSERT INTO Product (Product_name, Cate_Id, Brand_Id, Unit_Id, Price, Img, Description, Warranty_period) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            int rowsAffected = dbpro.update(sql, pro.getProduct_name(), pro.getCate_id(),
            		pro.getBrand_id(), pro.getUnit_id(), pro.getPrice(), pro.getImg(),
            		pro.getDescription(), pro.getWarranty_period());
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String deleteProduct(int idpro, String folderName, String fileName) {   
        try {
        	String sql = "DELETE FROM Product WHERE Id = ?";
            Object[] params = {idpro};
            int[] types = {Types.INTEGER};
            int rowsAffected = dbpro.update(sql, params, types);
            if (rowsAffected > 0) {
                String deleteFileResult = FileUtils.deleteFile(folderName, fileName);
                if (deleteFileResult.equals("file deleted")) {
                    return "Product and image file deleted successfully!";
                } else {
                    return "Product deleted, but failed to delete image file.";
                }
            } else {
                return "Failed to delete product: No rows affected";
            }
        } catch (Exception e) {
            return "Failed to delete product: " + e.getMessage();
        }
    }
    public Product findId(int id) {
    	String sql = "SELECT * FROM Product where Id=?";
    	 return dbpro.queryForObject(sql, (rs, rowNum) -> {
    		 Product pro = new Product();
    		 pro.setId(rs.getInt(Views.COL_PRODUCT_ID));
    		 pro.setBrand_id(rs.getInt(Views.COL_PRODUCT_BRAND_ID));
    		 pro.setCate_id(rs.getInt(Views.COL_PRODUCT_CATE_ID));
    		 pro.setUnit_id(rs.getInt(Views.COL_PRODUCT_UNIT_ID));
    		 pro.setProduct_name(rs.getString(Views.COL_PRODUCT_NAME));
    		 pro.setDescription(rs.getString(Views.COL_PRODUCT_DESCIPTION));
    		 pro.setImg(rs.getString(Views.COL_PRODUCT_IMG));
    		 pro.setPrice(rs.getDouble(Views.COL_PRODUCT_PRICE));
    		 pro.setWarranty_period(rs.getInt(Views.COL_PRODUCT_WARRANTY_PERIOD));
             return pro;
         }, id);
    }
    public boolean updateProduct(Product pro) 
    {   
        try {
        	String sql = "UPDATE Product SET Product_name = ?, Cate_Id = ?, Brand_Id = ?, Unit_Id = ?, Price = ?, Img = ?, Description = ?, Warranty_period = ? WHERE Id = ?";
            Object[] params = {
                pro.getProduct_name(),
                pro.getCate_id(),
                pro.getBrand_id(),
                pro.getUnit_id(),
                pro.getPrice(),
                pro.getImg(),
                pro.getDescription(),
                pro.getWarranty_period(),
                pro.getId()
            };
            int rowsAffected = dbpro.update(sql, params);
            return rowsAffected > 0;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Product> findAll(PageView ItemPage) {
        String str_query = String.format("SELECT p.*, b.%s as brand_name, u.%s as unit_name, c.%s as category_name " +
                "FROM %s p " +
                "INNER JOIN %s b ON p.%s = b.%s " +
                "INNER JOIN %s u ON p.%s = u.%s " +
                "INNER JOIN %s c ON p.%s = c.%s " +
                "ORDER BY p.%s DESC",
                Views.COL_BRAND_NAME, Views.COL_UNIT_NAME, Views.COL_CATEGORY_NAME, Views.TBL_PRODUCT,
                Views.TBL_BRAND, Views.COL_PRODUCT_BRAND_ID, Views.COL_BRAND_ID,
                Views.TBL_UNIT, Views.COL_PRODUCT_UNIT_ID, Views.COL_UNIT_ID,
                Views.TBL_CATEGORY, Views.COL_PRODUCT_CATE_ID, Views.COL_CATEGORY_ID,
                Views.COL_PRODUCT_ID);
        if (ItemPage != null && ItemPage.isPaginationEnabled()) {
            int count = dbpro.queryForObject("SELECT COUNT(*) FROM " + Views.TBL_PRODUCT, Integer.class);
            int total_page = (int) Math.ceil((double) count / ItemPage.getPage_size());
            ItemPage.setTotal_page(total_page);

            return dbpro.query(str_query + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                    new Product_mapper(),
                    (ItemPage.getPage_current() - 1) * (ItemPage.getPage_size()),
                    ItemPage.getPage_size());
        } else {
            return dbpro.query(str_query, new Product_mapper());
        }
    }


}
