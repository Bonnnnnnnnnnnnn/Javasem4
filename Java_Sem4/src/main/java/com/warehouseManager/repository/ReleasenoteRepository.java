package com.warehouseManager.repository;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mapper.Warehouse_releasenote_mapper;
import com.models.Warehouse_releasenote;
import com.models.Warehouse_rn_detail;
import com.utils.Views;

@Repository
public class ReleasenoteRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public Integer findIdByPhone(String phone) {
        String sql = "SELECT " + Views.COL_EMPLOYEE_ID + " FROM " + Views.TBL_EMPLOYEE + " WHERE " + Views.COL_EMPLOYEE_PHONE + " = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, phone);
        } catch (Exception e) {
            return null; 
        }
    }

    public void updateEmployeeId(int releasenoteId, int employeeId) {
        String sql = "UPDATE warehouse_releasenote SET Employee_Id = ? WHERE Id = ?";
        jdbcTemplate.update(sql, employeeId, releasenoteId);
    }

    public void updateStatusToPending(int releasenoteId) {
        String sql = "UPDATE warehouse_releasenote SET Status = 'pending' WHERE Id = ?";
        jdbcTemplate.update(sql, releasenoteId);
    }

    public List<Warehouse_releasenote> findAllByEmployeeId(int employeeId) {
        String sql = "SELECT * FROM warehouse_releasenote WHERE Employee_Id = ?";
        return jdbcTemplate.query(sql, new Warehouse_releasenote_mapper(), employeeId);
    }
 
    @Transactional
    public boolean addReleasenote(Warehouse_releasenote releasenote, List<Warehouse_rn_detail> details) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            String sql1 = "INSERT INTO Warehouse_releasenote (Name, Date, Status, Order_Id) VALUES (?, ?, ?, ?)";
            int result1 = jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql1, new String[] { "Id" });
                ps.setString(1, releasenote.getName());
                ps.setDate(2, java.sql.Date.valueOf(releasenote.getDate().toLocalDate()));
                ps.setString(3, releasenote.getStatusWr());
                ps.setInt(4, releasenote.getOrder_id());
                return ps;
            }, keyHolder);

            if (result1 > 0) {
                int generatedId = keyHolder.getKey().intValue();

                String sql2 = "INSERT INTO Warehouse_rn_detail (Wgrn_id, Status, Id_product, Quantity, Stock_Id) VALUES (?, ?, ?, ?, ?)";
                for (Warehouse_rn_detail detail : details) {
                    detail.setWgrn_id(generatedId);
                    jdbcTemplate.update(sql2,
                        detail.getWgrn_id(),
                        detail.getStatus(),
                        detail.getId_product(),
                        detail.getQuantity(),
                        detail.getStock_id()
                    );
                }
                return true;  
            } else {
                return false; 
            }
        } catch (DataAccessException e) {
            e.printStackTrace(); 
            return false; 
        }
    }

}
