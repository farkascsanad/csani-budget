package hu.csani.budget.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "budget_sql_clause", schema = "app")
public class BudgetSqlClauseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "budget_sql_clause_id")
	private Long id;

	@Column(name = "budget_rule_id")
	private Integer budgetRuleId;

	@Column(name = "clause_type", length = 10)
	private String clauseType;

	@Column(name = "field_name", nullable = false, length = 255)
	private String fieldName;

	@Column(name = "operation", nullable = false, length = 50)
	private String operation;

	@Column(name = "value", columnDefinition = "TEXT")
	private String value;

	@Column(name = "data_type", nullable = false, length = 100)
	private String dataType;

	@Column(name = "budget_condition_order")
	private Integer budgetConditionOrder;

	@Column(name = "sql_snippet", columnDefinition = "TEXT")
	private String sqlSnippet;

	@Column(name = "created_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime updatedAt;

	// Default constructor
	public BudgetSqlClauseEntity() {
	}

	// Constructor with required fields
	public BudgetSqlClauseEntity(String clauseType, String fieldName, String operation, String value, String dataType) {
		this.clauseType = clauseType;
		this.fieldName = fieldName;
		this.operation = operation;
		this.value = value;
		this.dataType = dataType;
	}

	// Utility methods for type conversion
	public Object getValueAsObject() {
		if (value == null) {
			return null;
		}

		return switch (dataType) {
		case "Integer" -> Integer.parseInt(value);
		case "BigDecimal" -> Double.parseDouble(value);
//	            case "boolean" -> Boolean.parseBoolean(value);
		case "LocalDate" -> LocalDateTime.parse(value);
		default -> value; // Return as string for other types
		};
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getBudgetRuleId() {
		return budgetRuleId;
	}

	public void setBudgetRuleId(Integer budgetRuleId) {
		this.budgetRuleId = budgetRuleId;
	}

	public String getClauseType() {
		return clauseType;
	}

	public void setClauseType(String clauseType) {
		this.clauseType = clauseType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getBudgetConditionOrder() {
		return budgetConditionOrder;
	}

	public void setBudgetConditionOrder(Integer budgetConditionOrder) {
		this.budgetConditionOrder = budgetConditionOrder;
	}

	public String getSqlSnippet() {
		return sqlSnippet;
	}

	public void setSqlSnippet(String sqlSnippet) {
		this.sqlSnippet = sqlSnippet;
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

	@Override
	public String toString() {
		return "BudgetConditionEntity [id=" + id + ", budgetRuleId=" + budgetRuleId + ", clauseType=" + clauseType
				+ ", fieldName=" + fieldName + ", operation=" + operation + ", value=" + value + ", dataType="
				+ dataType + ", budgetConditionOrder=" + budgetConditionOrder + ", sqlSnippet=" + sqlSnippet
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
	
	
	

}
