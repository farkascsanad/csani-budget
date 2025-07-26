//package hu.csani.budget.repositories;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import hu.csani.budget.data.BudgetActionEntity;
//import hu.csani.budget.data.BudgetRuleEntity;
//
///**
// * Repository for BudgetActionEntity
// */
//@Repository
//public interface BudgetActionRepository extends JpaRepository<BudgetActionEntity, Integer> {
//
////	/**
////	 * Find actions by budget rule ID
////	 */
////	List<BudgetActionEntity> findByBudgetRuleIdOrderByActionOrderAsc(Integer budgetRuleId);
////
////	/**
////	 * Find actions by budget rule ID and field name
////	 */
////	List<BudgetActionEntity> findByBudgetRuleIdAndFieldName(Integer budgetRuleId, String fieldName);
////
////	/**
////	 * Find actions by action type
////	 */
////	List<BudgetActionEntity> findByActionType(String actionType);
////
////	/**
////	 * Find actions by type
////	 */
////	List<BudgetActionEntity> findByType(String type);
////
////	/**
////	 * Find actions by budget rule
////	 */
////	List<BudgetActionEntity> findByBudgetRuleOrderByActionOrderAsc(BudgetRuleEntity budgetRule);
////
////	/**
////	 * Delete all actions for a budget rule
////	 */
////	void deleteByBudgetRuleId(Integer budgetRuleId);
//}