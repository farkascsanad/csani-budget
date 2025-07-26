//package hu.csani.budget.data;
//
//import java.time.LocalDateTime;
//import java.util.Objects;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.PrePersist;
//import jakarta.persistence.PreUpdate;
//import jakarta.persistence.Table;
//
///**
// * Entity representing a budget action (SET clause)
// */
//@Entity
//@Table(name = "budget_action", schema = "app")
//public class BudgetActionEntity {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "budget_action_id")
//	private Integer budgetActionId;
//
//	@Column(name = "budget_rule_id", insertable = false, updatable = false)
//	private Integer budgetRuleId;
//
//	@Column(name = "field_name", nullable = false, length = 255)
//	private String fieldName;
//
//	@Column(name = "action_type", nullable = false, length = 50)
//	private String actionType;
//
//	@Column(name = "value", columnDefinition = "TEXT")
//	private String value;
//
//	@Column(name = "type", nullable = false, length = 100)
//	private String type;
//
//	@Column(name = "action_order")
//	private Integer actionOrder;
//
//	@Column(name = "created_at")
//	private LocalDateTime createdAt;
//
//	@Column(name = "updated_at")
//	private LocalDateTime updatedAt;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "budget_rule_id", nullable = false)
//	private BudgetRuleEntity budgetRule;
//
//	// Constructors
//	public BudgetActionEntity() {
//	}
//
//	public BudgetActionEntity(String fieldName, String actionType, String value, String type) {
//		this.fieldName = fieldName;
//		this.actionType = actionType;
//		this.value = value;
//		this.type = type;
//	}
//
//	// Getters and Setters
//	public Integer getBudgetActionId() {
//		return budgetActionId;
//	}
//
//	public void setBudgetActionId(Integer budgetActionId) {
//		this.budgetActionId = budgetActionId;
//	}
//
//	public Integer getBudgetRuleId() {
//		return budgetRuleId;
//	}
//
//	public void setBudgetRuleId(Integer budgetRuleId) {
//		this.budgetRuleId = budgetRuleId;
//	}
//
//	public String getFieldName() {
//		return fieldName;
//	}
//
//	public void setFieldName(String fieldName) {
//		this.fieldName = fieldName;
//	}
//
//	public String getActionType() {
//		return actionType;
//	}
//
//	public void setActionType(String actionType) {
//		this.actionType = actionType;
//	}
//
//	public String getValue() {
//		return value;
//	}
//
//	public void setValue(String value) {
//		this.value = value;
//	}
//
//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}
//
//	public Integer getActionOrder() {
//		return actionOrder;
//	}
//
//	public void setActionOrder(Integer actionOrder) {
//		this.actionOrder = actionOrder;
//	}
//
//	public LocalDateTime getCreatedAt() {
//		return createdAt;
//	}
//
//	public void setCreatedAt(LocalDateTime createdAt) {
//		this.createdAt = createdAt;
//	}
//
//	public LocalDateTime getUpdatedAt() {
//		return updatedAt;
//	}
//
//	public void setUpdatedAt(LocalDateTime updatedAt) {
//		this.updatedAt = updatedAt;
//	}
//
//	public BudgetRuleEntity getBudgetRule() {
//		return budgetRule;
//	}
//
//	public void setBudgetRule(BudgetRuleEntity budgetRule) {
//		this.budgetRule = budgetRule;
//	}
//
//	@PrePersist
//	protected void onCreate() {
//		createdAt = LocalDateTime.now();
//		updatedAt = LocalDateTime.now();
//	}
//
//	@PreUpdate
//	protected void onUpdate() {
//		updatedAt = LocalDateTime.now();
//	}
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o)
//			return true;
//		if (o == null || getClass() != o.getClass())
//			return false;
//		BudgetActionEntity that = (BudgetActionEntity) o;
//		return Objects.equals(budgetActionId, that.budgetActionId);
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(budgetActionId);
//	}
//
//	@Override
//	public String toString() {
//		return "BudgetActionEntity{" + "budgetActionId=" + budgetActionId + ", fieldName='" + fieldName + '\''
//				+ ", actionType='" + actionType + '\'' + ", value='" + value + '\'' + ", type='" + type + '\''
//				+ ", actionOrder=" + actionOrder + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
//	}
//}