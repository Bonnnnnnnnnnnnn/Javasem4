package com.admin.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.EmployeeSalaryHistory;
import com.models.SalaryType;
import com.mapper.*;
import com.utils.Views;

@Repository
public class SalaryRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	// Salary Types
	private static final String SELECT_ALL_TYPES = String.format("SELECT * FROM %s", Views.TBL_SALARY_TYPES);

	private static final String SELECT_TYPE_BY_ID = String.format("SELECT * FROM %s WHERE %s = ?",
			Views.TBL_SALARY_TYPES, Views.COL_SALARY_TYPE_ID);

	private static final String INSERT_TYPE = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
			Views.TBL_SALARY_TYPES, Views.COL_SALARY_TYPE_NAME, Views.COL_SALARY_TYPE_DESC,
			Views.COL_SALARY_TYPE_IS_ACTIVE);

	// Salary History
	private static final String SELECT_HISTORY_BY_EMPLOYEE = String.format("SELECT * FROM %s WHERE %s = ?",
			Views.TBL_EMPLOYEE_SALARY_HISTORY, Views.COL_SALARY_HISTORY_EMPLOYEE_ID);

	private static final String INSERT_HISTORY = String.format(
			"INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?)",
			Views.TBL_EMPLOYEE_SALARY_HISTORY, Views.COL_SALARY_HISTORY_EMPLOYEE_ID, Views.COL_SALARY_HISTORY_TYPE_ID,
			Views.COL_SALARY_HISTORY_AMOUNT, Views.COL_SALARY_HISTORY_START_DATE, Views.COL_SALARY_HISTORY_END_DATE,
			Views.COL_SALARY_HISTORY_NOTE, Views.COL_SALARY_HISTORY_CREATED_BY);

	// Salary Types Methods
	public List<SalaryType> findAllTypes() {
		return jdbcTemplate.query(SELECT_ALL_TYPES, new SalaryTypeMapper());
	}

	public SalaryType findTypeById(int id) {
		try {
			return jdbcTemplate.queryForObject(SELECT_TYPE_BY_ID, new SalaryTypeMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void saveType(SalaryType type) {
		jdbcTemplate.update(INSERT_TYPE, type.getName(), type.getDescription(), type.getIsActive());
	}

	// Salary History Methods
	public List<EmployeeSalaryHistory> findHistoryByEmployee(int employeeId) {
		return jdbcTemplate.query(SELECT_HISTORY_BY_EMPLOYEE, new EmployeeSalaryHistoryMapper(), employeeId);
	}

	public void saveHistory(EmployeeSalaryHistory history) {
		jdbcTemplate.update(INSERT_HISTORY, history.getEmployeeId(), history.getSalaryTypeId(), history.getAmount(),
				history.getStartDate(), history.getEndDate(), history.getNote(), history.getCreatedBy());
	}
}
