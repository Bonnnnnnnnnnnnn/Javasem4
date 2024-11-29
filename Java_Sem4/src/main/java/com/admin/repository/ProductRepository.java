package com.admin.repository;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mapper.Product_mapper;
import com.mapper.Product_img_mapper;
import com.mapper.Product_price_change_mapper;
import com.mapper.Unit_mapper;
import com.mapper.Brand_mapper;
import com.mapper.Category_mapper;
import com.models.Brand;
import com.models.Category_Product;
import com.models.PageView;
import com.models.Product;
import com.models.Product_img;
import com.models.Product_price_change;
import com.models.Product_specifications;
import com.mapper.Product_specifications_mapper;
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
    public Product findIdProT(int id) {
        try {
            String sql = "SELECT * FROM Product WHERE Id = ?";

            return dbpro.queryForObject(sql, (rs, rowNum) -> {
                Product pro = new Product();
                pro.setId(rs.getInt("Id"));
                return pro;
            }, id); 
        } catch (DataAccessException e) {
            System.err.println("Lỗi khi lấy sản phẩm với ID: " + id + " - " + e.getMessage());
            return null;
        }
    }

    public List<Product> findAllProductIds() {
        try {
            String sql = "SELECT Id FROM Product";
            return dbpro.query(sql, (rs, rowNum) -> {
                Product product = new Product();
                product.setId(rs.getInt(Views.COL_PRODUCT_ID));
                return product;
            });
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
                       + "u.Name AS unit_name, p.Status AS status, "
                       + "p.Length, p.Width, p.Height, p.Weight "
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
                pro.setDescription(rs.getString(Views.COL_PRODUCT_DESCIPTION));  // Sử dụng hằng số
                pro.setImg(rs.getString(Views.COL_PRODUCT_IMG));  // Sử dụng hằng số
                pro.setPrice(rs.getDouble("Price"));
                pro.setWarranty_period(rs.getInt(Views.COL_PRODUCT_WARRANTY_PERIOD));  // Sử dụng hằng số
                pro.setBrandName(rs.getString("brand_name"));
                pro.setCategoryName(rs.getString("category_name"));
                pro.setStatus(rs.getString("Status"));
                pro.setUnit_name(rs.getString("unit_name"));
                pro.setLength(rs.getInt("Length"));
                pro.setWidth(rs.getInt("Width"));
                pro.setHeight(rs.getInt("Height"));
                pro.setWeight(rs.getInt("Weight"));
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
    @Transactional
    public boolean updateProductAndPrice(Product pro, double newPrice) {
        try {
        	String sqlGetCurrentPrice = "SELECT Price FROM Product WHERE Id = ?";
            Double currentPrice = dbpro.queryForObject(sqlGetCurrentPrice, Double.class, pro.getId());
            String sqlUpdateProduct = """
                UPDATE Product 
                SET Product_name = ?, Cate_Id = ?, Brand_Id = ?, Unit_Id = ?, 
                    Price = ?, Img = ?, Status = ?, Description = ?, Warranty_period = ?, 
                    Length = ?, Width = ?, Height = ?, Weight = ? 
                WHERE Id = ?
            """;
            Object[] productParams = {
                pro.getProduct_name(),
                pro.getCate_id(),
                pro.getBrand_id(),
                pro.getUnit_id(),
                pro.getPrice(),
                pro.getImg(),
                pro.getStatus(),
                pro.getDescription(),
                pro.getWarranty_period(),
                pro.getLength(),
                pro.getWidth(),
                pro.getHeight(),
                pro.getWeight(),
                pro.getId()
            };
            int rowsProduct = dbpro.update(sqlUpdateProduct, productParams);

            if (rowsProduct == 0) {
                throw new RuntimeException("Không thể cập nhật thông tin sản phẩm.");
            }

            if (pro.getPrice() != currentPrice) {
                // 2.1 Cập nhật cột Date_end của bản ghi giá trước đó (nếu NULL) với thời gian hiện tại
                String sqlUpdatePreviousPrice = """
                    UPDATE Product_price_change 
                    SET Date_end = ? 
                    WHERE Product_Id = ? AND Date_end IS NULL
                """;
                int rowsUpdatedPrice = dbpro.update(sqlUpdatePreviousPrice, LocalDateTime.now(), pro.getId());

                if (rowsUpdatedPrice == 0) {
                    // Nếu không có bản ghi nào thỏa mãn điều kiện, log chi tiết và thông báo
                    System.err.println("Không có bản ghi giá cũ để cập nhật Date_end.");
                    throw new RuntimeException("Không thể cập nhật thời gian kết thúc cho giá trước đó.");
                }

                // 2.2 Thêm bản ghi mới vào Product_price_change
                String sqlInsertNewPrice = """
                    INSERT INTO Product_price_change (Product_Id, Price, Date_start, Date_end) 
                    VALUES (?, ?, ?, NULL)
                """;
                int rowsPrice = dbpro.update(sqlInsertNewPrice, pro.getId(), newPrice, LocalDateTime.now());

                if (rowsPrice == 0) {
                    // In ra lỗi nếu không thêm được bản ghi
                    System.err.println("Không thể thêm bản ghi thay đổi giá.");
                    throw new RuntimeException("Không thể thêm bản ghi thay đổi giá.");
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cập nhật thất bại, toàn bộ giao dịch bị hủy.");
        }
    }

    
    //product_specifications
    
    public List<Product_specifications> findListPs(int psId) {
        try {
            String sql = "SELECT ps.*, p.Product_name AS product_name " +
                         "FROM product_specifications ps " +
                         "LEFT JOIN Product p ON ps.Product_id = p.Id " +
                         "WHERE ps.Product_id = ?";

            return dbpro.query(sql, new Product_specifications_mapper(), psId);

        } catch (DataAccessException e) {
            System.err.println("erro Product ID: " + psId);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public Product_specifications findPsById(int id) {
        try {
            String sql = "SELECT ps.*, p.Product_name AS product_name " +
                         "FROM product_specifications ps " +
                         "LEFT JOIN Product p ON ps.Product_id = p.Id " +
                         "WHERE ps.Id = ?";
            return dbpro.queryForObject(sql, new Product_specifications_mapper(), id);
        } catch (DataAccessException e) {
            System.err.println("Error when getting product specifications with ID: " + id + " - " + e.getMessage());
            return null;
        }
    }

    @Transactional
    public boolean saveProductWithDetails(Product pro, List<Product_specifications> specifications, List<Product_img> images, Product_price_change priceChange) {
        try {
            // Lưu Product
            String sql1 = "INSERT INTO Product (Product_name, Cate_Id, Brand_Id, Unit_Id, " +
                    "Price, Img, Status, Description, Warranty_period, Weight, Width, Height, Length) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int result1 = dbpro.update(connection -> {
                var ps = connection.prepareStatement(sql1, new String[] { "Id" });
                ps.setString(1, pro.getProduct_name());
                ps.setInt(2, pro.getCate_id());
                ps.setInt(3, pro.getBrand_id());
                ps.setInt(4, pro.getUnit_id());
                ps.setDouble(5, pro.getPrice());
                ps.setString(6, pro.getImg());
                ps.setString(7, pro.getStatus());
                ps.setString(8, pro.getDescription());
                ps.setInt(9, pro.getWarranty_period());
                ps.setInt(10, pro.getWeight());
                ps.setInt(11, pro.getWidth());
                ps.setInt(12, pro.getHeight());
                ps.setInt(13, pro.getLength());
                return ps;
            }, keyHolder);

            int generatedProductId = keyHolder.getKey().intValue();
            pro.setId(generatedProductId);

            // Lưu nhiều Product_specifications
            if (specifications != null && !specifications.isEmpty()) {
                String sql2 = "INSERT INTO product_specifications (Name_spe, Des_spe, Product_id) VALUES (?, ?, ?)";
                for (Product_specifications spec : specifications) {
                    dbpro.update(sql2, spec.getName_spe(), spec.getDes_spe(), generatedProductId);
                }
            }

            // Lưu nhiều Product_img
            if (images != null && !images.isEmpty()) {
                String sql3 = "INSERT INTO product_img (Img_url, Product_id) VALUES (?, ?)";
                for (Product_img img : images) {
                    img.setProduct_id(generatedProductId);
                    dbpro.update(sql3, img.getImg_url(), img.getProduct_id());
                }
            }

            // Lưu Product_price_change
            if (priceChange != null) {
                String sql4 = "INSERT INTO Product_price_change (Product_Id, Price, Date_start, Date_end) VALUES (?, ?, ?, ?)";
                priceChange.setProduct_id(generatedProductId);
                dbpro.update(sql4, priceChange.getProduct_id(), priceChange.getPrice(), priceChange.getDate_start(), null);
            }

            return result1 > 0;
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
    public boolean deletePs(int idps) {
    	try {
    		String sql = "DELETE FROM product_specifications WHERE Id = ?";
            Object[] params = {idps};
            int[] types = {Types.INTEGER};
            int rowsAffected = dbpro.update(sql, params, types);
            return rowsAffected > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    public List<Product_img> findListImages(int productId) {
        try {
            String sql = "SELECT pi.*, p.Product_name AS product_name " +
                         "FROM product_img pi " +
                         "LEFT JOIN Product p ON pi.Product_id = p.Id " +
                         "WHERE pi.Product_id = ?";
            return dbpro.query(sql, new Product_img_mapper(), productId);

        } catch (DataAccessException e) {
            System.err.println("Error fetching images for Product ID: " + productId);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public List<Product_price_change> findListProductPriceChanges(int productId) {
        try {
            String sql = "SELECT ppc.*, p.Product_name AS product_name " +
                         "FROM Product_price_change ppc " +
                         "LEFT JOIN Product p ON ppc.Product_id = p.Id " +
                         "WHERE ppc.Product_id = ?";
            return dbpro.query(sql, new Product_price_change_mapper(), productId);

        } catch (DataAccessException e) {
            System.err.println("Lỗi khi lấy dữ liệu thay đổi giá cho Product ID: " + productId);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public boolean addProPs(Product_specifications ps) {
        try {
            String sql = "INSERT INTO product_specifications (Product_id,Name_spe,Des_spe) VALUES (?, ?, ?)";
            int rowsAffected = dbpro.update(sql, ps.getProduct_id(), ps.getName_spe(),ps.getDes_spe());
            		
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
