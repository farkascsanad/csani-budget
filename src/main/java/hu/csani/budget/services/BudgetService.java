package hu.csani.budget.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import hu.csani.budget.data.Budget;
import hu.csani.budget.data.Category;
import hu.csani.budget.repositories.BudgetRepository;

@Service
public class BudgetService {

	private final BudgetRepository budgetRepository;
	private final JdbcTemplate jdbcTemplate;
	private final TransactionTemplate transactionTemplate;

	public BudgetService(BudgetRepository budgetRepository, JdbcTemplate jdbcTemplate,
			TransactionTemplate transactionTemplate) {
		this.budgetRepository = budgetRepository;
		this.jdbcTemplate = jdbcTemplate;
		this.transactionTemplate = transactionTemplate;
	}
	
	public List<Budget> findTop10ByCategoryIsNullOrderByAmountDesc() {
		return budgetRepository.findTop10ByCategoryIsNullOrderByAmountDesc();
	}

	/**
	 * Execute dynamic SELECT query and return List<Budget>
	 */
	public List<Budget> executeSelectQuery(String sql) {
		try {
			return jdbcTemplate.query(sql, new BudgetRowMapperWithCategory());
		} catch (Exception e) {
			throw new RuntimeException("Error executing SELECT query: " + e.getMessage(), e);
		}
	}

	/**
	 * Execute UPDATE query with test mode (rollback after execution)
	 */
	public int executeUpdateQueryTest(String sql) {
		return transactionTemplate.execute(status -> {
			try {
				int result = jdbcTemplate.update(sql);
				status.setRollbackOnly(); // Force rollback for test mode
				return result;
			} catch (Exception e) {
				throw new RuntimeException("Error executing UPDATE query in test mode: " + e.getMessage(), e);
			}
		});
	}

	/**
	 * Execute dynamic UPDATE/INSERT/DELETE query
	 */
	public int executeUpdateQuery(String sql) {
		try {
			return jdbcTemplate.update(sql);
		} catch (Exception e) {
			throw new RuntimeException("Error executing UPDATE query: " + e.getMessage(), e);
		}
	}

	public List<Budget> findAll() {
		return budgetRepository.findAll();
	}

	public Budget findById(Integer id) {
		return budgetRepository.findById(id).orElse(null);
	}

	public Budget save(Budget budget) {
		return budgetRepository.save(budget);
	}

	public void deleteById(Integer id) {
		budgetRepository.deleteById(id);
	}

	public void saveList(List<Budget> listOfBudget) {
		budgetRepository.saveAll(listOfBudget);

	}

	public List<Budget> findByAccountId(Integer accountId) {
		return budgetRepository.findByAccountId(accountId);
	}

	public List<Budget> findByAccountIdAndBookingDateBetween(Integer accountId, LocalDate startDate,
			LocalDate endDate) {
		return budgetRepository.findByAccountIdAndBookingDateBetween(accountId, startDate, endDate);
	}

	public List<Budget> findByAccountIdAndTransactionDateBetween(Integer accountId, LocalDate startDate,
			LocalDate endDate) {
		return budgetRepository.findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate);
	}

	/**
	 * Enhanced RowMapper for Budget entity with Category support
	 * Use this when your SQL includes a JOIN with the category table
	 */
	private static class BudgetRowMapperWithCategory implements RowMapper<Budget> {
	    @Override
	    public Budget mapRow(ResultSet rs, int rowNum) throws SQLException {
	        Budget budget = new Budget();
	        
	        // Map all Budget fields
	        budget.setBudgetId(rs.getObject("budget_id", Integer.class));
	        budget.setAccountId(rs.getObject("account_id", Integer.class));
	        
	        // Handle LocalDate fields
	        if (rs.getDate("booking_date") != null) {
	            budget.setBookingDate(rs.getDate("booking_date").toLocalDate());
	        }
	        if (rs.getDate("transaction_date") != null) {
	            budget.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
	        }
	        
	        // Handle BigDecimal fields
	        budget.setAmount(rs.getBigDecimal("amount"));
	        budget.setAmountIn(rs.getBigDecimal("amount_in"));
	        budget.setAmountOut(rs.getBigDecimal("amount_out"));
	        
	        // Handle String fields
	        budget.setCurrency(rs.getString("currency"));
	        budget.setDirection(rs.getString("direction"));
	        budget.setOriginalId(rs.getString("original_id"));
	        budget.setOtherPartyName(rs.getString("other_party_name"));
	        budget.setOtherPartyAccountNumber(rs.getString("other_party_account_number"));
	        budget.setTransactionType(rs.getString("transaction_type"));
	        budget.setNote(rs.getString("note"));
	        
	        // Handle Integer fields
	        budget.setCategoryRuleId(rs.getObject("category_rule_id", Integer.class));
	        budget.setManualCategoryId(rs.getObject("manual_category_id", Integer.class));
	        budget.setTransferId(rs.getObject("transfer_id", Integer.class));
	        
	        // Handle Category relationship (if joined in SQL)
	        try {
	            Integer categoryId = rs.getObject("category_id", Integer.class);
	            if (categoryId != null) {
	                Category category = new Category();
	                category.setCategoryName("category_name");
	                budget.setCategory(category);
	            }
	        } catch (SQLException e) {
	            // Category columns not present in result set, skip
	        }
	        
	        return budget;
	    }
	}

}
