package hu.csani.budget.views.rules;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import hu.csani.budget.data.Account;
import hu.csani.budget.data.Budget;
import hu.csani.budget.data.BudgetSqlClauseEntity;
import hu.csani.budget.data.Category;
import hu.csani.budget.services.AccountService;
import hu.csani.budget.services.CategoryService;
import hu.csani.budget.views.category.CategoryCreateDialog;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;

/**
 * Represents a single condition row for building SQL WHERE clauses for Budget
 * fields.
 */
public class BudgetSqlClauseRow extends HorizontalLayout {

	private BudgetSqlClauseEntity budgetSQLClause;

	private final ComboBox<String> fieldBox;
	private final ComboBox<String> opBox;
	private HorizontalLayout valueFieldHolder;
	private Component valueField;
	private final NativeLabel typeLabel;
	private final Button removeBtn;

	private final Map<String, Class<?>> budgetFields;

	private CategoryService categoryService;
	private AccountService accountService;

	public BudgetSqlClauseRow(Map<String, Class<?>> budgetFields, VerticalLayout container, String clauseType,
			List<BudgetSqlClauseRow> contidionRows, BudgetSqlClauseEntity bc, List<Category> categoryList,
			CategoryService categoryService, AccountService accountService) {

		this.budgetFields = budgetFields;
		this.budgetSQLClause = bc;
		this.categoryService = categoryService;
		this.accountService = accountService;

		if (budgetSQLClause == null) {
			budgetSQLClause = new BudgetSqlClauseEntity();
			budgetSQLClause.setClauseType(clauseType);
		}

		setWidthFull();

		contidionRows.add(this);

		fieldBox = new ComboBox<>();
		Set<String> keySet = budgetFields.keySet();
		if (clauseType.equals("SET")) {
			keySet = keySet.stream().filter(f -> f.contains("account") || f.contains("category"))
					.collect(Collectors.toSet());
		}

		fieldBox.setItems(keySet);
		fieldBox.setPlaceholder("Field");

		opBox = new ComboBox<>();
		opBox.setPlaceholder("Operator");

		valueField = new TextField();// Vaminek kell lennie
		((TextField) valueField).setEnabled(false);
		((TextField) valueField).setPlaceholder("Choose first!");
		valueFieldHolder = new HorizontalLayout(valueField);

		typeLabel = new NativeLabel();
		typeLabel.getStyle().set("font-size", "smaller").set("color", "var(--lumo-secondary-text-color)");

		removeBtn = new Button(new Icon("lumo", "cross"));
		removeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
		removeBtn.addClickListener(e -> {
			container.remove(this);
			contidionRows.remove(this);
		});

		setFlexGrow(10, fieldBox);
		setFlexGrow(5, opBox);
		setFlexGrow(20, valueFieldHolder);
		setFlexGrow(1, typeLabel);
		setFlexGrow(1, removeBtn);

		fieldBox.addValueChangeListener(e -> {
			fieldChanged(budgetFields, categoryList);

		});

		opBox.addValueChangeListener(e -> {
			if (e.getValue() != null && e.getValue().equals("isNull")) {
				valueFieldHolder.removeAll();
			}
			if (e.getOldValue() != null && e.getOldValue().equals("isNull")) {
				fieldChanged(budgetFields, categoryList);
			}
		});

		if (bc != null) {
			// TODO load rule
		}

		add(fieldBox, opBox, valueFieldHolder, typeLabel, removeBtn);
		setAlignItems(Alignment.BASELINE);
	}

	private void fieldChanged(Map<String, Class<?>> budgetFields, List<Category> categoryList) {
		String field = fieldBox.getValue();

		if (field == null) {
			opBox.clear();
			opBox.setItems();
			typeLabel.setText("");
			return;
		}
		Class<?> type = budgetFields.get(field);
		typeLabel.setText(type == null ? "" : type.getSimpleName());

		if (getField().equals("accountId")) {
			opBox.setItems("isNull", "=");

			ComboBox<Account> cb = new ComboBox<Account>();
			cb.setItems(accountService.findAll());
			cb.setWidthFull();

			valueField = cb;

		} else if (getField().equals("category")) {
			opBox.setItems("isNull", "=");

			ComboBox<Category> cb = new ComboBox<Category>();
			cb.setItems(categoryList);
			cb.setWidthFull();

			Button createNewCategory = new Button("+", e -> {
				CategoryCreateDialog categoryCreateDialog = new CategoryCreateDialog(cb, categoryService);
				categoryCreateDialog.open();
			});
			HorizontalLayout categoryLayout = new HorizontalLayout(cb, createNewCategory);

			categoryLayout.setWidthFull();

//			valueField = cb;
			valueField = categoryLayout;

		} else if (Number.class.isAssignableFrom(type) || type == int.class || type == long.class
				|| type == double.class) {
			opBox.setItems("isNull", "=", "!=", "<", "<=", ">", ">=");
			NumberField numberField = new NumberField();
			numberField.setWidthFull();
			valueField = numberField;
		} else if (type == LocalDate.class || type == Date.class) {
			opBox.setItems("isNull", "=", "before", "after");
			DatePicker dateField = new DatePicker();
			dateField.setWidthFull();
			valueField = dateField;
		} else if (type == String.class) {
			opBox.setItems("isNull", "=", "contains", "not contains", "regex", "!=");
			TextField textField = new TextField();
			textField.setWidthFull();
			valueField = textField;
		} else {
			opBox.setItems("isNull", "=");
			TextField textField = new TextField();
			textField.setWidthFull();
			valueField = textField;
		}
		valueFieldHolder.removeAll();
		valueFieldHolder.add(valueField);

		if (budgetSQLClause.getClauseType().equals("SET")) {
			opBox.setItems("=");
			opBox.setValue("=");
			opBox.setEnabled(false);
		}
	}

	public String getField() {
		return fieldBox.getValue();
	}

	public String getOperator() {
		return opBox.getValue();
	}

	public String getValue() {

		String result = "";

		if (getOperator().equals("isNull"))
			return result;

		Component valueField = valueFieldHolder.getChildren().findFirst().get();

		if (valueField instanceof TextField) {
			result = ((TextField) valueField).getValue();
		} else if (valueField instanceof NumberField) {
			result = ((NumberField) valueField).getValue().toString();
		} else if (valueField instanceof DatePicker) {
			result = ((DatePicker) valueField).getValue().toString();
		} else if (valueField instanceof HorizontalLayout) {
//			else if (valueField instanceof ComboBox<?>) {
			HorizontalLayout categoryLayout = (HorizontalLayout) valueField;
			Object value = ((ComboBox) categoryLayout.getChildren().findFirst().get()).getValue();

			if (value instanceof Category) {
				result = ((Category) value).getCategoryId().toString();
			} else {
				result = "x";
			}

		} else if (valueField == null) {
			result = " is null";
		} else if (valueField instanceof ComboBox) {
			try {
				ComboBox<Account> cbA = (ComboBox<Account>) valueField;
				result = "" + cbA.getValue().getAccountId();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
//            result = "Unknown component type: " + valueField.getClass().getSimpleName();
		}

		return result;
	}

	public Class<?> getFieldType() {
		if (fieldBox.getValue() == null)
			return null;
		return budgetFields.get(fieldBox.getValue());
	}

	/**
	 * Generates an SQL snippet for this condition based on field, operator, value.
	 * Handles SIMPLE cases only; expand as needed!
	 */
	public String toSQL() {
		String field = getField();
		String op = getOperator();
		String value = "";

		if (!op.equals("isNull"))
			value = getValue();
		
		//Exception - because its an object
		if(field.equals("accountId")) {
			return "account_id = "+value;
		}

		Class<?> type = getFieldType();

		if (field == null || op == null || value == null || type == null) {
			return "";
		}
		Map<String, String> columnNames = getColumnNames(Budget.class);
		String sqlField = columnNames.get(field);

		switch (op) {
		case "isNull":
			return sqlField + " is null";
		case "equals":
			return sqlField + " = '" + escape(value) + "'";
		case "contains":
			return sqlField + " LIKE '%" + escapeLike(value) + "%'";
		case "not contains":
			return sqlField + " NOT LIKE '%" + escapeLike(value) + "%'";
		case "regex":
			return sqlField + " ~ '" + escapeRegex(value) + "'";
		case "!=":
			return sqlField + " <> '" + escape(value) + "'";
		case "on":
			return sqlField + " = '" + escape(value) + "'";
		case "before":
			return sqlField + " < '" + escape(value) + "'";
		case "after":
			return sqlField + " > '" + escape(value) + "'";
		default:
			// For number operators (=, !=, <, <=, >, >=)
			if (Number.class.isAssignableFrom(type) || type == int.class || type == long.class
					|| type == double.class) {
				return sqlField + " " + op + " " + value;
			} else {
				// Fallback for unhandled cases
				return sqlField + " " + op + " '" + escape(value) + "'";
			}
		}
	}

	public static Map<String, String> getColumnNames(Class<?> clazz) {
		Map<String, String> columns = new LinkedHashMap<>();
		for (Field field : clazz.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);// ide kell join culom is

			if (column != null) {
				columns.put(field.getName(), column.name());
			}
			JoinColumn joincolumn = field.getAnnotation(JoinColumn.class);// ide kell join culom is
			if (joincolumn != null) {
				columns.put(field.getName(), joincolumn.name());
			}
		}
		return columns;
	}

	// Basic SQL escape for demonstration (expand as needed)
	private String escape(String v) {
		return v.replace("'", "''");
	}

	private String escapeLike(String v) {
		return escape(v).replace("%", "\\%").replace("_", "\\_");
	}

	private String escapeRegex(String v) {
		return escape(v);
	}

	public BudgetSqlClauseEntity getBudgetCondition() {
		return budgetSQLClause;
	}

	public void setBudgetCondition(BudgetSqlClauseEntity budgetCondition) {
		this.budgetSQLClause = budgetCondition;
	}

	public void setBudgetConditionEntityClass(int order) {

		budgetSQLClause.setBudgetConditionOrder(order);
		budgetSQLClause.setSqlSnippet(toSQL());
		budgetSQLClause.setFieldName(getField());
		budgetSQLClause.setOperation(getOperator());
		budgetSQLClause.setValue(getValue());
		budgetSQLClause.setDataType(typeLabel.getText());
		if (budgetSQLClause.getId() == null)
			budgetSQLClause.setCreatedAt(LocalDateTime.now());
		budgetSQLClause.setUpdatedAt(LocalDateTime.now());

	}

	public void setDoubleClickedItems(String columnName, Object cellValue) {

		fieldBox.setValue(columnName);
		opBox.setValue("=");

		Component valueField = valueFieldHolder.getChildren().findFirst().get();

		if (valueField instanceof TextField) {
			((TextField) valueField).setValue(cellValue.toString());
		} else if (valueField instanceof NumberField) {
			((NumberField) valueField).setValue(Double.parseDouble(cellValue.toString()));
		} else if (valueField instanceof DatePicker) {
			((DatePicker) valueField).setValue(LocalDate.parse(cellValue.toString()));
		} else if (valueField instanceof ComboBox) {
//			if(cellValue instanceof Account) {
//				cellValue = ((Account) cellValue).getAccountId();
//			}
			ComboBox<Account> cb = (ComboBox<Account>) valueField;

			cb.setValue((Account) cellValue);

		}

	}

}