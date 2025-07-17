package hu.csani.budget.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.csani.budget.data.BudgetRuleEntity;

/**
 * Repository for BudgetRuleEntity
 */
@Repository
public interface BudgetRuleRepository extends JpaRepository<BudgetRuleEntity, Integer> {

//	/**
//	 * Find all active budget rules ordered by priority
//	 */
//	@Query("SELECT br FROM BudgetRuleEntity br WHERE br.isActive = true ORDER BY br.priority ASC")
//	List<BudgetRuleEntity> findAllActiveOrderedByPriority();
//
//	/**
//	 * Find budget rules by name (case-insensitive)
//	 */
//	List<BudgetRuleEntity> findByBudgetRuleNameContainingIgnoreCase(String name);
//
//	/**
//	 * Find budget rules by active status
//	 */
//	List<BudgetRuleEntity> findByIsActive(Boolean isActive);
//
//	/**
//	 * Find budget rules with priority range
//	 */
//	List<BudgetRuleEntity> findByPriorityBetweenOrderByPriorityAsc(Integer minPriority, Integer maxPriority);
//
//	/**
//	 * Find budget rules with overwrite enabled
//	 */
//	List<BudgetRuleEntity> findByIsOverwriteEnabled(Boolean isOverwriteEnabled);
//
//	/**
//	 * Find budget rule with conditions and actions
//	 */
//	@Query("SELECT br FROM BudgetRuleEntity br " + "LEFT JOIN FETCH br.conditions " + "LEFT JOIN FETCH br.actions "
//			+ "WHERE br.budgetRuleId = :id")
//	Optional<BudgetRuleEntity> findByIdWithConditionsAndActions(@Param("id") Integer id);
//
//	/**
//	 * Count active rules
//	 */
//	long countByIsActive(Boolean isActive);
}