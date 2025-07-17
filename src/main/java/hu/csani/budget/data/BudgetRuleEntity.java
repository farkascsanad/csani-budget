package hu.csani.budget.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * Entity representing a budget rule
 */
@Entity
@Table(name = "budget_rule", schema = "app")
public class BudgetRuleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "budget_rule_id")
	private Integer budgetRuleId;

	@Column(name = "budget_rule_name", nullable = false, length = 255)
	private String budgetRuleName;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "priority", nullable = false)
	private Integer priority = 100;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "is_overwrite_enabled", nullable = false)
	private Boolean isOverwriteEnabled = false;

	@Column(name = "where_clause", columnDefinition = "TEXT")
	private String whereClause;

	@Column(name = "set_clause", columnDefinition = "TEXT")
	private String setClause;
	

	@Column(name = "budget_rule_sql", columnDefinition = "TEXT")
	private String budgetRuleSQL;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "budget_rule_id")
	private List<BudgetSqlClauseEntity> conditions;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "budget_rule_id")
	private List<BudgetActionEntity> actions;

	// Constructors
	public BudgetRuleEntity() {
	}

	public BudgetRuleEntity(String budgetRuleName, String description) {
		this.budgetRuleName = budgetRuleName;
		this.description = description;
	}

	// Getters and Setters
	public Integer getBudgetRuleId() {
		return budgetRuleId;
	}

	public void setBudgetRuleId(Integer budgetRuleId) {
		this.budgetRuleId = budgetRuleId;
	}

	public String getBudgetRuleName() {
		return budgetRuleName;
	}

	public void setBudgetRuleName(String budgetRuleName) {
		this.budgetRuleName = budgetRuleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsOverwriteEnabled() {
		return isOverwriteEnabled;
	}

	public void setIsOverwriteEnabled(Boolean isOverwriteEnabled) {
		this.isOverwriteEnabled = isOverwriteEnabled;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getSetClause() {
		return setClause;
	}

	public void setSetClause(String setClause) {
		this.setClause = setClause;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<BudgetSqlClauseEntity> getConditions() {
		return conditions;
	}

	public void setConditions(List<BudgetSqlClauseEntity> conditions) {
		this.conditions = conditions;
	}

	public List<BudgetActionEntity> getActions() {
		return actions;
	}

	public void setActions(List<BudgetActionEntity> actions) {
		this.actions = actions;
	}

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	
	public String getBudgetRuleSQL() {
		return budgetRuleSQL;
	}

	public void setBudgetRuleSQL(String budgetRuleSQL) {
		this.budgetRuleSQL = budgetRuleSQL;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		BudgetRuleEntity that = (BudgetRuleEntity) o;
		return Objects.equals(budgetRuleId, that.budgetRuleId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(budgetRuleId);
	}

	
	
	@Override
	public String toString() {
		return "BudgetRuleEntity{" + "budgetRuleId=" + budgetRuleId + ", budgetRuleName='" + budgetRuleName + '\''
				+ ", description='" + description + '\'' + ", priority=" + priority + ", isActive=" + isActive
				+ ", isOverwriteEnabled=" + isOverwriteEnabled + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
				+ '}';
	}
}