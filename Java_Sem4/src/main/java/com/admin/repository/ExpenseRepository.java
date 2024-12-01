package com.admin.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.ExpenseHistory;
import com.models.ExpenseType;
import com.mapper.*;
import com.utils.Views;

@Repository
public class ExpenseRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // Expense Types
    private static final String SELECT_ALL_TYPES = 
        String.format("SELECT * FROM %s", Views.TBL_EXPENSE_TYPES);
        
    private static final String SELECT_TYPE_BY_ID = 
        String.format("SELECT * FROM %s WHERE %s = ?", 
            Views.TBL_EXPENSE_TYPES, 
            Views.COL_EXPENSE_TYPE_ID);
            
    private static final String INSERT_TYPE = 
        String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
            Views.TBL_EXPENSE_TYPES,
            Views.COL_EXPENSE_TYPE_NAME,
            Views.COL_EXPENSE_TYPE_DESC,
            Views.COL_EXPENSE_TYPE_IS_FIXED,
            Views.COL_EXPENSE_TYPE_IS_ACTIVE);
            
    private static final String UPDATE_TYPE = 
        String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = GETDATE() WHERE %s = ?",
            Views.TBL_EXPENSE_TYPES,
            Views.COL_EXPENSE_TYPE_NAME,
            Views.COL_EXPENSE_TYPE_DESC,
            Views.COL_EXPENSE_TYPE_IS_FIXED,
            Views.COL_EXPENSE_TYPE_IS_ACTIVE,
            Views.COL_EXPENSE_TYPE_UPDATED_AT,
            Views.COL_EXPENSE_TYPE_ID);
            
    // Expense History
    private static final String SELECT_ALL_HISTORY = 
        String.format("SELECT * FROM %s", Views.TBL_EXPENSE_HISTORY);
        
    private static final String SELECT_HISTORY_BY_TYPE = 
        String.format("SELECT * FROM %s WHERE %s = ?", 
            Views.TBL_EXPENSE_HISTORY, 
            Views.COL_EXPENSE_HISTORY_TYPE_ID);
            
    private static final String INSERT_HISTORY = 
        String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?)",
            Views.TBL_EXPENSE_HISTORY,
            Views.COL_EXPENSE_HISTORY_TYPE_ID,
            Views.COL_EXPENSE_HISTORY_AMOUNT,
            Views.COL_EXPENSE_HISTORY_START_DATE,
            Views.COL_EXPENSE_HISTORY_END_DATE,
            Views.COL_EXPENSE_HISTORY_NOTE,
            Views.COL_EXPENSE_HISTORY_CREATED_BY);
    
    // Expense Types Methods
    public List<ExpenseType> findAllTypes() {
        return jdbcTemplate.query(SELECT_ALL_TYPES, new ExpenseTypeMapper());
    }
    
    public ExpenseType findTypeById(int id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_TYPE_BY_ID, new ExpenseTypeMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    public void saveType(ExpenseType type) {
        jdbcTemplate.update(INSERT_TYPE,
            type.getName(),
            type.getDescription(),
            type.getIsFixed(),
            type.getIsActive()
        );
    }
    
    public void updateType(ExpenseType type) {
        jdbcTemplate.update(UPDATE_TYPE,
            type.getName(),
            type.getDescription(),
            type.getIsFixed(),
            type.getIsActive(),
            type.getId()
        );
    }
    
    // Expense History Methods
    public List<ExpenseHistory> findAllHistory() {
        return jdbcTemplate.query(SELECT_ALL_HISTORY, new ExpenseHistoryMapper());
    }
    
    public List<ExpenseHistory> findHistoryByType(int typeId) {
        return jdbcTemplate.query(SELECT_HISTORY_BY_TYPE, new ExpenseHistoryMapper(), typeId);
    }
    
    public void saveHistory(ExpenseHistory history) {
        jdbcTemplate.update(INSERT_HISTORY,
            history.getExpenseTypeId(),
            history.getAmount(),
            history.getStartDate(),
            history.getEndDate(),
            history.getNote(),
            history.getCreatedBy()
        );
    }
}