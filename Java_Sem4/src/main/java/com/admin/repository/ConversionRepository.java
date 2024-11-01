package com.admin.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mapper.Conversion_mapper;
import com.models.Conversion;
import com.models.PageView;
import com.utils.Views;

@Repository
public class ConversionRepository {
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<Conversion> findAllConversions(PageView ItemPage) {
        try {
            String str_query = String.format("SELECT c.*, u1.%s as from_unit_name, u2.%s as to_unit_name " +
                    "FROM %s c " +
                    "INNER JOIN %s u1 ON c.%s = u1.%s " +
                    "INNER JOIN %s u2 ON c.%s = u2.%s " +
                    "ORDER BY c.%s DESC",
                    Views.COL_UNIT_NAME, Views.COL_UNIT_NAME, Views.TBL_CONVERSION,
                    Views.TBL_UNIT, Views.COL_CONVERSION_FROM_UNIT_ID, Views.COL_UNIT_ID,
                    Views.TBL_UNIT, Views.COL_CONVERSION_TO_UNIT_ID, Views.COL_UNIT_ID,
                    Views.COL_CONVERSION_ID);

            if (ItemPage != null && ItemPage.isPaginationEnabled()) {
                int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Views.TBL_CONVERSION, Integer.class);
                int total_page = (int) Math.ceil((double) count / ItemPage.getPage_size());
                ItemPage.setTotal_page(total_page);

                return jdbcTemplate.query(str_query + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                        new Conversion_mapper(),
                        (ItemPage.getPage_current() - 1) * (ItemPage.getPage_size()),
                        ItemPage.getPage_size());
            } else {
                return jdbcTemplate.query(str_query, new Conversion_mapper());
            }
        } catch (DataAccessException e) {
            System.err.println("Error fetching conversions: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    
    public int addConversion(Conversion conversion) {
        String sql = "INSERT INTO conversion (from_unit_id, to_unit_id, conversion_rate) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, 
            conversion.getFrom_unit_id(), 
            conversion.getTo_unit_id(), 
            conversion.getConversion_rate()
        );
    }
    
    public int deleteConversion(int conversionId) {
        String sql = "DELETE FROM Conversion WHERE id = ?";
        return jdbcTemplate.update(sql, conversionId);
    }

    public int updateConversion(Conversion conversion) {
        String sql = "UPDATE Conversion SET from_unit_id = ?, to_unit_id = ?, conversion_rate = ? WHERE id = ?";
        return jdbcTemplate.update(sql, 
            conversion.getFrom_unit_id(),
            conversion.getTo_unit_id(),
            conversion.getConversion_rate(),
            conversion.getId()
        );
    }
    
    public Conversion findById(int id) {
        String sql = "SELECT c.*, u1.name AS from_unit_name, u2.name AS to_unit_name " +
                     "FROM Conversion c " +
                     "JOIN Unit u1 ON c.from_unit_id = u1.id " +
                     "JOIN Unit u2 ON c.to_unit_id = u2.id " +
                     "WHERE c.id = ?";
        return jdbcTemplate.queryForObject(sql, new Conversion_mapper(), id);
    }


}
