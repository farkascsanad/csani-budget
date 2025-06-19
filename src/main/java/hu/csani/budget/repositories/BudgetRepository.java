package hu.csani.budget.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.csani.budget.data.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
}
