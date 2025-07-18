package hu.csani.budget.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.csani.budget.data.Account;
import hu.csani.budget.data.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

	// 1. All budgets by accountId
	List<Budget> findByAccount(Account account);

	// 2. Budgets by accountId and booking date range
	List<Budget> findByAccountAndBookingDateBetween(Account account, LocalDate startDate, LocalDate endDate);

	// 3. Budgets by accountId and transaction date range
	List<Budget> findByAccountAndTransactionDateBetween(Account account, LocalDate startDate, LocalDate endDate);

	// Budgets with no category
	List<Budget> findTop10ByCategoryIsNullOrderByAmountDesc();
	

	// Budgets with no category
//	List<Budget> findTop10ByCategoryIsNullOrderByAmountAsc();

	// Budgets with a specific category id
	List<Budget> findTop10ByCategoryCategoryIdOrderByAmountDesc(Long categoryId);
}
