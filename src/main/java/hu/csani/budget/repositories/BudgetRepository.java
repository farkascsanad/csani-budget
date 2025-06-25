package hu.csani.budget.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.csani.budget.data.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

	// 1. All budgets by accountId
	List<Budget> findByAccountId(Integer accountId);

	// 2. Budgets by accountId and booking date range
	List<Budget> findByAccountIdAndBookingDateBetween(Integer accountId, LocalDate startDate, LocalDate endDate);

	// 3. Budgets by accountId and transaction date range
	List<Budget> findByAccountIdAndTransactionDateBetween(Integer accountId, LocalDate startDate, LocalDate endDate);

}
