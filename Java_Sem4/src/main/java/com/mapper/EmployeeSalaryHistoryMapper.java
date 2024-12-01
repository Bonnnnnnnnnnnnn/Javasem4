package com.mapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.models.EmployeeSalaryHistory;
import com.utils.Views;

public class EmployeeSalaryHistoryMapper implements RowMapper<EmployeeSalaryHistory> {
    @Override
    public EmployeeSalaryHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
        EmployeeSalaryHistory history = new EmployeeSalaryHistory();
        history.setId(rs.getInt(Views.COL_SALARY_HISTORY_ID));
        history.setEmployeeId(rs.getInt(Views.COL_SALARY_HISTORY_EMPLOYEE_ID));
        history.setSalaryTypeId(rs.getInt(Views.COL_SALARY_HISTORY_TYPE_ID));
        history.setAmount(rs.getDouble(Views.COL_SALARY_HISTORY_AMOUNT));
        history.setStartDate(rs.getDate(Views.COL_SALARY_HISTORY_START_DATE).toLocalDate());
        
        Date endDate = rs.getDate(Views.COL_SALARY_HISTORY_END_DATE);
        if (endDate != null) {
            history.setEndDate(endDate.toLocalDate());
        }
        
        history.setNote(rs.getString(Views.COL_SALARY_HISTORY_NOTE));
        history.setCreatedBy(rs.getInt(Views.COL_SALARY_HISTORY_CREATED_BY));
        history.setCreatedAt(rs.getTimestamp(Views.COL_SALARY_HISTORY_CREATED_AT).toLocalDateTime());
        
        return history;
    }
}