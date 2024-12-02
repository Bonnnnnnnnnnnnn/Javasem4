package com.admin.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.models.ExpenseHistory;
import com.models.ExpenseType;
import com.models.PageView;
import com.mapper.*;
import com.utils.Views;

@Repository
public class ExpenseRepository {
	@Autowired
	private JdbcTemplate db;

	public List<ExpenseType> findAllTypes() {
		try {
			String sql = String.format("SELECT * FROM %s WHERE %s = 'true' OR %s = '1' ORDER BY %s DESC",
					Views.TBL_EXPENSE_TYPES, Views.COL_EXPENSE_TYPE_IS_ACTIVE, Views.COL_EXPENSE_TYPE_IS_ACTIVE,
					Views.COL_EXPENSE_TYPE_UPDATED_AT);
			return db.query(sql, new ExpenseTypeMapper());
		} catch (DataAccessException e) {
			System.err.println("Error in findAllTypes: " + e.getMessage());
			return Collections.emptyList();
		}
	}

	public ExpenseHistory saveHistory(ExpenseHistory history) {
		try {
			String insertSql = String.format(
					"INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?); "
							+ "SELECT h.*, t.%s as TypeName FROM %s h " + "INNER JOIN %s t ON h.%s = t.%s "
							+ "WHERE h.%s = SCOPE_IDENTITY()",
					Views.TBL_EXPENSE_HISTORY, Views.COL_EXPENSE_HISTORY_TYPE_ID, Views.COL_EXPENSE_HISTORY_AMOUNT,
					Views.COL_EXPENSE_HISTORY_START_DATE, Views.COL_EXPENSE_HISTORY_END_DATE,
					Views.COL_EXPENSE_HISTORY_NOTE, Views.COL_EXPENSE_HISTORY_CREATED_BY, Views.COL_EXPENSE_TYPE_NAME,
					Views.TBL_EXPENSE_HISTORY, Views.TBL_EXPENSE_TYPES, Views.COL_EXPENSE_HISTORY_TYPE_ID,
					Views.COL_EXPENSE_TYPE_ID, Views.COL_EXPENSE_HISTORY_ID);

			return db.queryForObject(insertSql, new ExpenseHistoryMapper(), history.getExpenseTypeId(),
					history.getAmount(), history.getStartDate(), history.getEndDate(), history.getNote(),
					history.getCreatedBy());
		} catch (DataAccessException e) {
			System.err.println("Error saving expense history: " + e.getMessage());
			throw e;
		}
	}

	public ExpenseType saveType(ExpenseType type) {
		try {
			String insertSql = String.format(
					"INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?); "
							+ "SELECT * FROM %s WHERE %s = SCOPE_IDENTITY()",
					Views.TBL_EXPENSE_TYPES, Views.COL_EXPENSE_TYPE_NAME, Views.COL_EXPENSE_TYPE_DESC,
					Views.COL_EXPENSE_TYPE_IS_FIXED, Views.COL_EXPENSE_TYPE_IS_ACTIVE, Views.TBL_EXPENSE_TYPES,
					Views.COL_EXPENSE_TYPE_ID);

			return db.queryForObject(insertSql, new ExpenseTypeMapper(), type.getName(), type.getDescription(),
					type.getIsFixed(), type.getIsActive());
		} catch (DataAccessException e) {
			System.err.println("Error saving expense type: " + e.getMessage());
			throw e;
		}
	}

	public ExpenseType updateType(ExpenseType type) {
		try {
			String updateSql = String.format(
					"UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = GETDATE() WHERE %s = ?",
					Views.TBL_EXPENSE_TYPES, Views.COL_EXPENSE_TYPE_NAME, Views.COL_EXPENSE_TYPE_DESC,
					Views.COL_EXPENSE_TYPE_IS_FIXED, Views.COL_EXPENSE_TYPE_IS_ACTIVE,
					Views.COL_EXPENSE_TYPE_UPDATED_AT, Views.COL_EXPENSE_TYPE_ID);

			db.update(updateSql, type.getName(), type.getDescription(), type.getIsFixed(), type.getIsActive(),
					type.getId());

			return findTypeById(type.getId());
		} catch (DataAccessException e) {
			System.err.println("Error updating expense type: " + e.getMessage());
			throw e;
		}
	}

	public List<ExpenseHistory> findAll(PageView pageView) {
		try {
			// Query chính chỉ cần lấy history
			StringBuilder str_query = new StringBuilder(String.format("SELECT h.* FROM %s h ORDER BY h.%s DESC",
					Views.TBL_EXPENSE_HISTORY, Views.COL_EXPENSE_HISTORY_CREATED_AT));

			// Query đếm tổng số records
			String countQuery = String.format("SELECT COUNT(*) FROM %s", Views.TBL_EXPENSE_HISTORY);

			// Tính toán phân trang
			int count = db.queryForObject(countQuery, Integer.class);
			int total_page = (int) Math.ceil((double) count / pageView.getPage_size());
			pageView.setTotal_page(total_page);

			List<ExpenseHistory> histories;

			// Thực hiện query với phân trang
			if (pageView.isPaginationEnabled()) {
				str_query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
				histories = db.query(str_query.toString(), new ExpenseHistoryMapper(),
						(pageView.getPage_current() - 1) * pageView.getPage_size(), pageView.getPage_size());
			} else {
				histories = db.query(str_query.toString(), new ExpenseHistoryMapper());
			}

			// Gán type cho mỗi history
			for (ExpenseHistory history : histories) {
				ExpenseType type = findTypeById(history.getExpenseTypeId());
				history.setExpenseType(type);
			}

			return histories;

		} catch (DataAccessException e) {
			System.err.println("Error in findAll: " + e.getMessage());
			return Collections.emptyList();
		}
	}

	public List<ExpenseHistory> findHistoryByType(int typeId) {
		try {
			String sql = String.format("SELECT * FROM %s WHERE %s = ?", Views.TBL_EXPENSE_HISTORY,
					Views.COL_EXPENSE_HISTORY_TYPE_ID);
			return db.query(sql, new ExpenseHistoryMapper(), typeId);
		} catch (DataAccessException e) {
			System.err.println("Error in findHistoryByType: " + e.getMessage());
			return Collections.emptyList();
		}
	}

	public ExpenseType findTypeById(int id) {
		try {
			String sql = String.format("SELECT * FROM %s WHERE %s = ?", Views.TBL_EXPENSE_TYPES,
					Views.COL_EXPENSE_TYPE_ID);
			return db.queryForObject(sql, new ExpenseTypeMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (DataAccessException e) {
			System.err.println("Error finding expense type by id: " + e.getMessage());
			throw e;
		}
	}

}