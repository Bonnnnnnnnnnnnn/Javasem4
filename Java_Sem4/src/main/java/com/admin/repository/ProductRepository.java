package com.admin.repository;

import java.sql.Types;
import java.util.Collections;
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
import com.models.Product_specifications;
import com.models.Unit;
import com.utils.FileUtils;
import com.utils.Views;



@Repository
public class ProductRepository {

	private final JdbcTemplate dbpro;
	
	public ProductRepository(JdbcTemplate jdbc) {
		this.dbpro = jdbc;
	}
	public List<Product> findAll(PageView itemPage) {
	    try {
	        String str_query = String.format(
	                "SELECT p.*, " +
	                "b.%s AS brand_name, " +
	                "c.%s AS category_name, " +
	                "u.%s AS unit_name, " +
	                "p.%s AS status " +
	                "FROM %s p " +
	                "INNER JOIN %s b ON p.%s = b.%s " +
	                "INNER JOIN %s c ON p.%s = c.%s " +
	                "LEFT JOIN %s u ON p.%s = u.%s " +
	                "ORDER BY p.%s DESC",
	                Views.COL_BRAND_NAME, Views.COL_CATEGORY_NAME, Views.COL_UNIT_NAME, Views.COL_PRODUCT_STATUS,
	                Views.TBL_PRODUCT,
	                Views.TBL_BRAND, Views.COL_PRODUCT_BRAND_ID, Views.COL_BRAND_ID,
	                Views.TBL_CATEGORY, Views.COL_PRODUCT_CATE_ID, Views.COL_CATEGORY_ID,
	                Views.TBL_UNIT, Views.COL_PRODUCT_UNIT_ID, Views.COL_UNIT_ID,
	                Views.COL_PRODUCT_ID
	        );

	        System.out.println("Generated Query: " + str_query);

	        if (itemPage != null && itemPage.isPaginationEnabled()) {
	            int count = dbpro.queryForObject("SELECT COUNT(*) FROM " + Views.TBL_PRODUCT, Integer.class);
	            int totalPage = (int) Math.ceil((double) count / itemPage.getPage_size());
	            itemPage.setTotal_page(totalPage);

	            return dbpro.query(
	                str_query + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
	                new Product_mapper(),
	                (itemPage.getPage_current() - 1) * itemPage.getPage_size(),
	                itemPage.getPage_size()
	            );
	        } else {
	            return dbpro.query(str_query, new Product_mapper());
	        }
	    } catch (DataAccessException e) {
	        System.err.println("Error fetching products: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}

	public List<Unit> findAllUnit() {
    	try {
    		String sql = "SELECT * FROM Unit";
            return dbpro.query(sql, new Unit_mapper());
		} catch (Exception e) {
			System.err.println("error:" + e.getMessage());
			return Collections.emptyList();
		}      
    }
    public List<Brand> findAllBrand() {
    	try {
    		String sql = "SELECT * FROM Brand";
            return dbpro.query(sql, new Brand_mapper());
		} catch (Exception e) {
			System.err.println("error:" + e.getMessage());
			return Collections.emptyList();
		}      
    }
    public List<Category_Product> findAllCategory(){
    	try {
        	String sql = "SELECT * FROM Product_category";
        	return dbpro.query(sql, new Category_mapper());
		} catch (Exception e) {
			System.err.println("error:" + e.getMessage());
			return Collections.emptyList();
		}
    }
    
    public Product findId(int id) {
        try {
            String sql = "SELECT p.*, b.Name AS brand_name, c.Cate_name AS category_name, "
                       + "u.Name AS unit_name, p.Status AS status "
                       + "FROM Product p "
                       + "LEFT JOIN Brand b ON p.Brand_id = b.Id "
                       + "LEFT JOIN Product_category c ON p.Cate_id = c.Id "
                       + "LEFT JOIN Unit u ON p.Unit_Id = u.Id "
                       + "WHERE p.Id = ?";

            return dbpro.queryForObject(sql, (rs, rowNum) -> {
                Product pro = new Product();
                pro.setId(rs.getInt("Id"));
                pro.setBrand_id(rs.getInt("Brand_id"));
                pro.setCate_id(rs.getInt("Cate_id"));
                pro.setUnit_id(rs.getInt("Unit_Id"));
                pro.setProduct_name(rs.getString("Product_name"));
                pro.setDescription(rs.getString("Description"));
                pro.setImg(rs.getString("Img"));
                pro.setPrice(rs.getDouble("Price"));
                pro.setWarranty_period(rs.getInt("Warranty_period"));
                pro.setBrandName(rs.getString("brand_name"));
                pro.setCategoryName(rs.getString("category_name"));
                pro.setStatus(rs.getString("Status"));
                pro.setUnit_name(rs.getString("unit_name"));
                return pro;
            }, id);
        } catch (DataAccessException e) {
            System.err.println("Lỗi khi lấy sản phẩm với ID: " + id + " - " + e.getMessage());
            return null;
        }
    }
    public boolean saveProduct(Product pro) {
        try {
            String sql = "INSERT INTO Product (Product_name, Cate_Id, Brand_Id, Unit_Id, Price, Img, Status, Description, Warranty_period) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int rowsAffected = dbpro.update(sql, pro.getProduct_name(), pro.getCate_id(),
            		pro.getBrand_id(), pro.getUnit_id(), pro.getPrice(), pro.getImg(),pro.getStatus(),
            		pro.getDescription(), pro.getWarranty_period());
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @SuppressWarnings("deprecation")
	public String getProductImageById(int idpro) {
    	try {
    		String sql = "SELECT img FROM Product WHERE Id = ?";
            Object[] params = {idpro};
            return dbpro.queryForObject(sql, params, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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
    public String deleteById(int idpro) {
        String fileName = null;
        try {
            fileName = getProductImageById(idpro);
            String sql = "DELETE FROM Product WHERE Id = ?";
            Object[] params = {idpro};
            int[] types = {Types.INTEGER};
            int rowsAffected = dbpro.update(sql, params, types);

            if (rowsAffected > 0) {
                return fileName;
            } else {
                System.out.println("No rows affected, deletion failed for product ID: " + idpro);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; 
        }
    }

    public boolean updateProduct(Product pro) 
    {   
        try {
        	String sql = "UPDATE Product SET Product_name = ?, Cate_Id = ?, Brand_Id = ?, Unit_Id = ?, Price = ?, Img = ?, Status = ?, Description = ?, Warranty_period = ? WHERE Id = ?";
            Object[] params = {
                pro.getProduct_name(),
                pro.getCate_id(),
                pro.getBrand_id(),
                pro.getUnit_id(),
                pro.getPrice(),
                pro.getImg(),
                pro.getStatus(),
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
    
    //product_specifications
    
    public boolean addProSpe(Product_specifications ps) {
    	try {
			String sql = "INSERT INTO product_specifications (Name_spe, Des_spe, Product_id) VALUES (?, ?, ?)";
			int row = dbpro.update(sql,ps.getName_spe(),ps.getDes_spe(),ps.getProduct_id());
			return row > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    public boolean updateProSpe(Product_specifications ps) {
    	try {
    		String sql = "UPDATE product_specifications SET Name_spe = ?, Des_spe = ?, Product_id = ? WHERE Id = ?";
			int row = dbpro.update(sql,ps.getName_spe(),ps.getDes_spe(),ps.getProduct_id(),ps.getId());
			return row > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
}
