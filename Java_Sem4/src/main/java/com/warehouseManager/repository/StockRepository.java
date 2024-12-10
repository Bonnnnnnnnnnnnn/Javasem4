package com.warehouseManager.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Conversion_mapper;
import com.mapper.Product_mapper;
import com.mapper.StockSum_mapper;
import com.mapper.Stock_mapper;
import com.models.Conversion;
import com.models.ConversionShow;
import com.models.Product;
import com.models.Stock;
import com.models.StockSumByWarehouseId;
import com.utils.Views;
import java.util.Map;


@Repository
public class StockRepository {
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
	

    public List<StockSumByWarehouseId> findAllStock(int warehouseId, LocalDate startDate, LocalDate endDate) {
        String sql = String.format("""
                SELECT 
                    p.Id,
                    p.Product_name,
                    u.Name, 
                    wh.Id AS Warehouse_Id,
                    whr.Date,                    
                    SUM(st.Quantity) AS total_Quantity,
                    AVG(wrd.Wh_price) AS Average_Wh_price                   
                FROM 
                    stock st
                    JOIN Product p ON st.Id_product = p.Id
                    JOIN Unit u ON p.Unit_id = u.Id 
                    JOIN Warehouse_receipt_detail wrd ON st.Wh_rc_dt_Id = wrd.Id
                    JOIN Warehouse_receipt whr ON wrd.Wh_receiptId = whr.Id
                    JOIN Warehouse wh ON whr.Wh_Id = wh.Id
                    JOIN employee_warehouse ew ON wh.Id = ew.Warehouse_Id
                WHERE 
                    ew.Warehouse_Id = ?
                    AND whr.Date BETWEEN ?  AND ?
                GROUP BY 
                    p.Id, p.Product_name, u.Name, wh.Id, whr.Date
                ORDER BY 
                      whr.Date DESC, wh.Id, p.Product_name;
                """);

        List<StockSumByWarehouseId> stocks = jdbcTemplate.query(sql, new StockSum_mapper(), warehouseId, startDate, endDate);

        
        for (StockSumByWarehouseId stock : stocks) {

            String sql1 = """
                    SELECT c.*, u.[Name] as fromName, u1.[Name] as toName 
                    FROM Conversion c
                    JOIN Unit u ON c.From_unit_id = u.Id
                    JOIN Unit u1 ON c.To_unit_id = u1.Id
                    WHERE c.Product_Id = ?;
                    """;

            List<Conversion> conversions = jdbcTemplate.query(sql1, (rs, rowNum) -> {
                Conversion conversion = new Conversion();
                conversion.setId(rs.getInt("Id"));
                conversion.setFrom_unit_id(rs.getInt("From_unit_id"));
                conversion.setTo_unit_id(rs.getInt("To_unit_id"));
                conversion.setConversion_rate(rs.getInt("Conversion_rate"));
                conversion.setFromUnitName(rs.getString("fromName"));
                conversion.setToUnitName(rs.getString("toName"));
                return conversion;
            }, stock.getProductId());

            // Now, process conversion and add to the list
            List<ConversionShow> conversionshows = new ArrayList<>();
            for (Conversion cs : conversions) {
                int convertedQuantity = (int) (stock.getQuantity() / cs.getConversion_rate());

                ConversionShow conversionShow = new ConversionShow(cs.getFromUnitName(), convertedQuantity);

                conversionshows.add(conversionShow);
            }
            stock.setConversions(conversionshows);
        }

        return stocks;
    }

    

    public List<Stock> findStockDetail(int warehouseId,  LocalDate startDate, LocalDate endDate) {
        String sql = String.format("""
                SELECT 
                    st.*,
                    p.Product_name,
                    p.id,
                    u.Name,
                    wrd.Wh_price,
                    ew.Employee_Id,
                    ew.Warehouse_Id,
                    wh.Name,
                    whr.Shipping_fee,
                    whr.Date
                FROM 
                    stock st
                    JOIN Product p ON st.Id_product = p.Id
                    JOIN Unit u ON p.Unit_id = u.Id
                    JOIN Warehouse_receipt_detail wrd ON st.Wh_rc_dt_Id = wrd.Id
                    JOIN Warehouse_receipt whr ON wrd.Wh_receiptId = whr.Id
                    JOIN Warehouse wh ON whr.Wh_Id = wh.Id
                    JOIN employee_warehouse ew ON wh.Id = ew.Warehouse_Id
                WHERE 
                    ew.Warehouse_Id = ?
                    AND whr.Date BETWEEN ? AND ? ;
                """);

        List<Stock> stocks = jdbcTemplate.query(sql, new Stock_mapper(), warehouseId, startDate, endDate);

        for (Stock stock : stocks) {

            String sql1 = """
                     SELECT c.*, u.[Name] as fromName, u1.[Name] as toName 
                    FROM Conversion c
                    JOIN Unit u ON c.From_unit_id = u.Id
                    JOIN Unit u1 ON c.To_unit_id = u1.Id
                    WHERE c.Product_Id = ?;
                    """;

            List<Conversion> conversions = jdbcTemplate.query(sql1, (rs, rowNum) -> {
                Conversion conversion = new Conversion();
                conversion.setId(rs.getInt("Id"));
                conversion.setFrom_unit_id(rs.getInt("From_unit_id"));
                conversion.setTo_unit_id(rs.getInt("To_unit_id"));
                conversion.setConversion_rate(rs.getInt("Conversion_rate"));
                conversion.setFromUnitName(rs.getString("fromName"));
                conversion.setToUnitName(rs.getString("toName"));
                return conversion;
            }, stock.getProduct_id());

            List<ConversionShow> conversionshows = new ArrayList<>();
            for (Conversion cs : conversions) {
                int convertedQuantity = (int) (stock.getQuantity() / cs.getConversion_rate());

                ConversionShow conversionShow = new ConversionShow(cs.getFromUnitName(), convertedQuantity);

                conversionshows.add(conversionShow);
            }

            stock.setListconversion(conversionshows);
        }

        return stocks;
    }

    
    
    
    public List<Map<String, Object>> getInventoryStats() {
        String sql = """
            SELECT 
                p.Product_name AS ProductName,
                wr.Date AS ImportDate,
                st.Quantity AS StockQuantity,
                CASE 
                    WHEN st.Quantity > 0 THEN 'Còn'
                    ELSE 'Hết'
                END AS Status
            FROM stock st
            JOIN Product p ON st.Id_product = p.Id
            JOIN Warehouse_receipt_detail wrd ON st.Wh_rc_dt_Id = wrd.Id
            JOIN Warehouse_receipt wr ON wrd.Wh_receiptId = wr.Id
            ORDER BY p.Product_name, wr.Date
        """;

        return jdbcTemplate.queryForList(sql);  
    }

	
	public List<Conversion> conversionByProductId(int product_Id) {
	    try {
	        String str_query = String.format(
	            "SELECT * FROM Conversion WHERE %s = ?",
	            Views.COL_CONVERSION_PRODUCT_ID
	        );
	        return jdbcTemplate.query(str_query, new Conversion_mapper(), product_Id);
	    } catch (Exception e) {
	        System.err.println("Error fetching conversions: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}

	
	public List<Product> findAddPro(){
		try {
			String sql = "SELECT * FROM Product";
			return jdbcTemplate.query(sql, new Product_mapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
