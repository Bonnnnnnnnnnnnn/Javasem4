package com.businessManager.repository;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;


import com.mapper.Warehouse_releasenote_mapper;

import com.models.Warehouse_releasenote;
import com.models.Warehouse_rn_detail;

@Repository
public class RequestOrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    
    @Transactional
    public boolean addRequestOrderWithDetails(Warehouse_releasenote releasenote, List<Warehouse_rn_detail> details) {
        try {
            String sql1 = "INSERT INTO Warehouse_releasenote (Name, Date, Status) VALUES (?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int result1 = jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql1, new String[] { "Id" });
                ps.setString(1, releasenote.getName());
                ps.setDate(2, java.sql.Date.valueOf(releasenote.getDate().toLocalDate()));
                ps.setString(3, releasenote.getStatusWr());
                return ps;
            }, keyHolder);

            int generatedId = keyHolder.getKey().intValue();

            String sql2 = "INSERT INTO Warehouse_rn_detail (Wgrn_id, Status, Id_product, Quantity) VALUES ( ?, ?, ?, ?)";
            for (Warehouse_rn_detail detail : details) {
                detail.setWgrn_id(generatedId);
                jdbcTemplate.update(sql2,
                    detail.getWgrn_id(),
                    detail.getStatus(),
                    detail.getId_product(),
                    detail.getQuantity()
                );
            }

            return result1 > 0;  
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public List<Warehouse_releasenote> findAll() {
        String sql = "SELECT * FROM Warehouse_releasenote";
        return jdbcTemplate.query(sql, new Warehouse_releasenote_mapper());
    }
    
    
    public void deleteOrderRequest(int releasenoteId) {    	
        String deleteOrderDetailsSql = "DELETE FROM Warehouse_rn_detail WHERE Wgrn_Id = ?";
        jdbcTemplate.update(deleteOrderDetailsSql, releasenoteId);
        String deleteWarehouseReleaseNoteSql = "DELETE FROM Warehouse_releasenote WHERE id = ?";
        jdbcTemplate.update(deleteWarehouseReleaseNoteSql, releasenoteId);
    }
    


    @Transactional
    public boolean updateRequestOrderDetail(Warehouse_releasenote releasenote, List<Warehouse_rn_detail> details) {
        try {

            String sqlUpdateReleasenote = "UPDATE Warehouse_releasenote SET Name = ?, Date = ?, Status = ? WHERE Id = ?";
            int resultUpdateReleasenote = jdbcTemplate.update(
                sqlUpdateReleasenote,
                releasenote.getName(),
                java.sql.Date.valueOf(releasenote.getDate().toLocalDate()),
                releasenote.getStatusWr(),
                releasenote.getId()
            );

            String sqlUpdateDetail = "UPDATE Warehouse_rn_detail SET Status = ?, Id_product = ?, Quantity = ? WHERE id = ? AND Wgrn_Id = ?";
            for (Warehouse_rn_detail detail : details) {
                jdbcTemplate.update(
                    sqlUpdateDetail,
                    detail.getStatus(),
                    detail.getId_product(),
                    detail.getQuantity(),
                    detail.getId(), 
                    detail.getWgrn_id()
                );
            }

            return resultUpdateReleasenote > 0;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Warehouse_releasenote findReleasenoteById(int id) {
        String sql = "SELECT * FROM Warehouse_releasenote WHERE Id = ?";
        List<Warehouse_releasenote> releasenotes = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Warehouse_releasenote releasenote = new Warehouse_releasenote();
            releasenote.setId(rs.getInt("Id"));
            releasenote.setName(rs.getString("Name"));
            releasenote.setDate(rs.getTimestamp("Date").toLocalDateTime());
            releasenote.setStatusWr(rs.getString("Status"));
            return releasenote;
        }, id);
        
        return releasenotes.stream().findFirst().orElse(null);
    }

    public List<Warehouse_rn_detail> findDetailsByReleasenoteId(int wgrnId) {
        String sql = "SELECT * FROM Warehouse_rn_detail WHERE Wgrn_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Warehouse_rn_detail detail = new Warehouse_rn_detail();
            detail.setId(rs.getInt("Id"));
            detail.setWgrn_id(rs.getInt("Wgrn_id"));
            detail.setId_product(rs.getInt("Id_product"));
            detail.setQuantity(rs.getInt("Quantity"));
            detail.setStatus(rs.getString("Status"));
            return detail;
        }, wgrnId);
    }


}
