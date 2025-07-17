package hu.csani.budget.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.csani.budget.data.BudgetSqlClauseEntity;
import hu.csani.budget.data.BudgetRuleEntity;

/**
 * Repository for BudgetConditionEntity
 */
@Repository
public interface BudgetConditionRepository extends JpaRepository<BudgetSqlClauseEntity, Integer> {

//	/**
//	 * Find conditions by budget rule ID
//	 */
//	List<BudgetConditionEntity> findByBudgetRuleIdOrderByBudgetConditionOrderAsc(Integer budgetRuleId);
//
//	/**
//	 * Find conditions by budget rule ID and field name
//	 */
//	List<BudgetConditionEntity> findByBudgetRuleIdAndFieldName(Integer budgetRuleId, String fieldName);
//
//	/**
//	 * Find conditions by operation type
//	 */
//	List<BudgetConditionEntity> findByOperation(String operation);
//
//	/**
//	 * Find conditions by type
//	 */
//	List<BudgetConditionEntity> findByType(String type);
//
//	/**
//	 * Find conditions by budget rule
//	 */
//	List<BudgetConditionEntity> findByBudgetRuleOrderByBudgetConditionOrderAsc(BudgetRuleEntity budgetRule);
//
//	/**
//	 * Delete all conditions for a budget rule
//	 */
//	void deleteByBudgetRuleId(Integer budgetRuleId);
}
