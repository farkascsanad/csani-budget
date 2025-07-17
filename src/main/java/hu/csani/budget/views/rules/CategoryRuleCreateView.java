package hu.csani.budget.views.rules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import hu.csani.budget.data.Budget;
import hu.csani.budget.data.BudgetRuleEntity;
import hu.csani.budget.data.BudgetSqlClauseEntity;
import hu.csani.budget.repositories.BudgetRuleRepository;
import hu.csani.budget.services.BudgetService;
import hu.csani.budget.services.CategoryService;

@PageTitle("Create rule")
@Route("category-rule-create")
@Menu(order = 15, icon = LineAwesomeIconUrl.RULER_COMBINED_SOLID)
@Uses(Icon.class)
public class CategoryRuleCreateView extends Div implements BeforeEnterObserver {

	private final String HEADER_EMPTY_EXAMPLE = "Uncategorized example rows. HINT: Double click cell create condition!";
	private final String HEADER_CATEGORY_EXAMPLE = "Budget history filtered by rule";
	private NativeLabel gridHeader = new NativeLabel();

	private ComboBox<String> matchType;

	private BudgetService budgetService;
	private CategoryService categoryService;

	private BudgetRuleEntity budgetRuleEntity;

	// Conditions Section
	private VerticalLayout conditionsLayout = new VerticalLayout();
	// Set Section
	private VerticalLayout setsLayout = new VerticalLayout();

	// Holds the mapping of field name to type for Budget
	private final Map<String, Class<?>> budgetFields;
	private List<BudgetSqlClauseRow> conditionRows = new ArrayList<>();


	Grid<Budget> budgetGrid = new Grid<>(Budget.class);

	private BudgetRuleRepository budgetRuleRepository;

	public CategoryRuleCreateView(BudgetService budgetService, CategoryService categoryService,
			BudgetRuleRepository budgetRuleRepository) {

		this.budgetService = budgetService;
		this.budgetRuleRepository = budgetRuleRepository;
		this.categoryService = categoryService;

		if (budgetRuleEntity == null) {
			resetBudgetEntity();
			gridHeader.setText(HEADER_EMPTY_EXAMPLE);
		} else {

			gridHeader.setText(HEADER_CATEGORY_EXAMPLE);
		}

		// Use reflection to get fields/types
		this.budgetFields = getFieldsAndTypes(Budget.class);

		add(new H2("Create Rule"));

		// Top: "If all/any of these conditions match:"
		HorizontalLayout matchTypeLayout = new HorizontalLayout();
		matchTypeLayout.setAlignItems(Alignment.BASELINE);
		matchTypeLayout.add(new NativeLabel("If"));

		matchType = new ComboBox<>();
		matchType.setItems("all", "any");
		matchType.setValue("all");
		matchTypeLayout.add(matchType, new NativeLabel("of these conditions match:"));

		add(matchTypeLayout);
		conditionsLayout.setPadding(false);
		conditionsLayout.setSpacing(false);
		conditionsLayout.setWidthFull();
		add(conditionsLayout);

		// Reset call-ed in the end of constuctor so no more needede
//		addConditionRow(conditionsLayout);

		Button addCondition = new Button("+ Add Condition", new Icon("lumo", "plus"));
		addCondition.addClickListener(e -> addConditionRow(conditionsLayout));
		add(addCondition);

		// SET Section
		add(new NativeLabel("SET the following:"));
		setsLayout.setPadding(false);
		setsLayout.setSpacing(false);
		add(setsLayout);

//		addActionRow(setsLayout);

		Button addSet = new Button("+ Add SET", new Icon("lumo", "plus"));
		addSet.addClickListener(e -> addActionRow(setsLayout));
		add(addSet);

		// Budgets Preview Grid
		add(new H2("Matching Budgets Preview"));

//		budgetGrid.addColumn(Budget::getBudgetId).setHeader("ID");
//		budgetGrid.addColumn(Budget::getAmount).setHeader("Amount");
//		budgetGrid.addColumn(Budget::getCurrency).setHeader("Currency");

		// Option 2: Remove auto-generated column and add custom one

		setupGrid();
		budgetGrid.setItems(Collections.emptyList()); // TODO: Bind to filtered results
		budgetGrid.setHeight("250px");

		// Add double-click listener to the grid
		budgetGrid.addItemDoubleClickListener(event -> {
			Budget clickedBudget = event.getItem();

			// Get the column that was clicked
			Grid.Column<Budget> clickedColumn = event.getColumn();

			if (clickedColumn != null) {
				// Get the column name (key)
				String columnName = clickedColumn.getKey();

				// Get the cell value using reflection or property access
				Object cellValue = getCellValue(clickedBudget, columnName);

				// Handle the double-click event
//				System.out.println("Double-clicked on column: " + columnName);
//				System.out.println("Cell value: " + cellValue);

				// You can also show a notification or perform other actions
//				Notification.show("Clicked column: " + columnName + ", Value: " + cellValue);

				handleBudgetGridDoubleClick(columnName, cellValue);
			}
		});

		add(gridHeader);
		add(budgetGrid);

		// Save/Cancel Buttons
		HorizontalLayout buttons = new HorizontalLayout();
		Button save = new Button("Save", new Icon("lumo", "checkmark"), e -> save());
		Button test = new Button("Test", new Icon("lumo", "checkmark"), e -> test());
		Button reset = new Button("Reset", new Icon("lumo", "cross"), e -> reset());
		buttons.add(save, test, reset);
		add(buttons);

		reset();
	}

	private void resetBudgetEntity() {
		budgetRuleEntity = new BudgetRuleEntity();
		budgetRuleEntity.setConditions(new ArrayList<>());
	}

	private void reset() {

		resetBudgetEntity();

		conditionRows.clear();
		conditionsLayout.removeAll();
		setsLayout.removeAll();

		addConditionRow(conditionsLayout);
		addActionRow(setsLayout);

		List<Budget> top10ByCategoryIsNullOrderByAmountDesc = budgetService
				.findTop10ByCategoryIsNullOrderByAmountDesc();

		budgetGrid.setItems(top10ByCategoryIsNullOrderByAmountDesc);
		gridHeader.setText(HEADER_EMPTY_EXAMPLE);
	}

	private Object test() {
		StringBuilder fullSQL = new StringBuilder("Select * from app.budget b");
		try {
			String matchtypeValue = matchType.getValue().equals("all") ? " AND " : " OR ";

//			StringBuilder fullSQL = new StringBuilder("Select * from app.budget b");

			generateWhereClause(matchtypeValue, fullSQL, true);

//		System.out.println(fullSQL);

			List<Budget> executeSelectQuery = budgetService.executeSelectQuery(fullSQL.toString());

			budgetGrid.setItems(executeSelectQuery);

			gridHeader.setText(HEADER_CATEGORY_EXAMPLE);

			String updateSQL = generateUpdateSQL();

			int executeUpdateQueryTest = budgetService.executeUpdateQueryTest(updateSQL);
			if (executeUpdateQueryTest > 0) {
				Notification notification = Notification.show("Update SQL test success!");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			} else {
				Notification notification = Notification.show("Update SQL test failed!");
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			}

			System.out.println(updateSQL);
			System.out.println(fullSQL);

			List<Budget> budgetRuleData = budgetService.executeSelectQuery(fullSQL.toString());
			budgetGrid.setItems(budgetRuleData);

		} catch (Exception e) {
			Notification notification = Notification.show(
					"Update SQL test failed! Make sure that at least 1 condition and 1 action added, and all of these filled!"
							+ e.getLocalizedMessage());
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
		return fullSQL;
	}

	private Object save() {
		String fullSQL = generateUpdateSQL();

		budgetRuleEntity.setBudgetRuleSQL(fullSQL);
		budgetRuleEntity.setWhereClause(generateUpdateSQL());
		budgetRuleEntity.setSetClause(generateSETClause());

		budgetRuleRepository.save(budgetRuleEntity);

		reset();
		return fullSQL;
	}

	private String generateUpdateSQL() {
		String matchtypeValue = matchType.getValue().equals("all") ? " AND " : " OR ";

		StringBuilder fullSQL = new StringBuilder("UPDATE app.budget b ");
		fullSQL.append("\n");
		fullSQL.append("SET ");

		String finalSET = generateSETClause();

		fullSQL.append(finalSET);

		generateWhereClause(matchtypeValue, fullSQL, false);

//		System.out.println(fullSQL);
		return fullSQL.toString();
	}

	private String generateSETClause() {
		StringBuilder setSQL = new StringBuilder();
		int order = 0;
		for (BudgetSqlClauseRow budgetConditionRow : conditionRows) {
			BudgetSqlClauseEntity budgetCondition = budgetConditionRow.getBudgetCondition();

//			System.out.println(budgetConditionRow.toSQL());
			budgetConditionRow.setBudgetConditionEntityClass(order);

			order++;
			if (budgetCondition.getClauseType().equals("SET")) {
				setSQL.append("\n");
				setSQL.append(budgetConditionRow.toSQL() + ",");
			}

			budgetRuleEntity.getConditions().add(budgetConditionRow.getBudgetCondition());
		}

		// Levágjuk a végéről a ","-t mert oda már nem kell
		String finalSET = setSQL.toString().replaceAll(",$", "");
		return finalSET;
	}

	private void generateWhereClause(String matchtypeValue, StringBuilder fullSQL, boolean test) {
		fullSQL.append("\n");
		fullSQL.append(" WHERE ");
		fullSQL.append("\n");
		fullSQL.append(" 1 = 1");

		int order = 0;

		for (BudgetSqlClauseRow budgetConditionRow : conditionRows) {
			BudgetSqlClauseEntity budgetCondition = budgetConditionRow.getBudgetCondition();

//			System.out.println(budgetConditionRow.toSQL());
			budgetConditionRow.setBudgetConditionEntityClass(order);

			if (budgetCondition.getClauseType().equals("WHERE")) {
				fullSQL.append("\n");
				fullSQL.append(matchtypeValue + budgetConditionRow.toSQL());
			}
			order++;
			if (!test)
				budgetRuleEntity.getConditions().add(budgetConditionRow.getBudgetCondition());
		}
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		// TODOD update budgetRuleEntity
	}

	private Map<String, Class<?>> getFieldsAndTypes(Class<?> clazz) {

		Map<String, Class<?>> map = new LinkedHashMap<>();
		for (Field field : clazz.getDeclaredFields()) {
			map.put(field.getName(), field.getType());
		}
		return map;
	}

	private BudgetSqlClauseRow addConditionRow(VerticalLayout container) {
		BudgetSqlClauseRow rowx = new BudgetSqlClauseRow(budgetFields, container, "WHERE", conditionRows, null,
				categoryService.findAllAndBuildTree(), categoryService);

		container.add(rowx);

		return rowx;
	}

	private void addActionRow(VerticalLayout container) {

		BudgetSqlClauseRow row = new BudgetSqlClauseRow(budgetFields, container, "SET", conditionRows, null,
				categoryService.findAllAndBuildTree(), categoryService);

		container.add(row);
	}

	private void setupGrid() {
		// Remove default columns if any
		budgetGrid.removeAllColumns();

		// Budget ID column
		budgetGrid.addColumn(Budget::getBudgetId).setHeader("Budget ID").setKey("budgetId").setResizable(true)
				.setSortable(true).setWidth("120px").setFlexGrow(0);

		// Account ID column
		budgetGrid.addColumn(Budget::getAccountId).setHeader("Account ID").setKey("accountId").setResizable(true)
				.setSortable(true).setWidth("120px").setFlexGrow(0);

		// Booking Date column
		budgetGrid.addColumn(budget -> budget.getBookingDate() != null ? budget.getBookingDate().toString() : "")
				.setHeader("Booking Date").setKey("bookingDate").setResizable(true).setSortable(true).setWidth("40px")
				.setFlexGrow(0);

		// Transaction Date column
		budgetGrid
				.addColumn(budget -> budget.getTransactionDate() != null ? budget.getTransactionDate().toString() : "")
				.setHeader("Transaction Date").setKey("transactionDate").setResizable(true).setSortable(true)
				.setWidth("150px").setFlexGrow(0);

		// Amount column
		budgetGrid.addColumn(budget -> budget.getAmount() != null ? budget.getAmount().toString() : "")
				.setHeader("Amount").setKey("amount").setResizable(true).setSortable(true).setWidth("120px")
				.setFlexGrow(0);

//	        // Amount In column
//	        addColumn(budget -> budget.getAmountIn() != null ? 
//	                  budget.getAmountIn().toString() : "")
//	            .setHeader("Amount In")
//	            .setKey("amountIn")
//	            .setResizable(true)
//	            .setSortable(true)
//	            .setWidth("120px")
//	            .setFlexGrow(0);
//	        
//	        // Amount Out column
//	        addColumn(budget -> budget.getAmountOut() != null ? 
//	                  budget.getAmountOut().toString() : "")
//	            .setHeader("Amount Out")
//	            .setKey("amountOut")
//	            .setResizable(true)
//	            .setSortable(true)
//	            .setWidth("120px")
//	            .setFlexGrow(0);
//	        
		// Currency column
		budgetGrid.addColumn(Budget::getCurrency).setHeader("Currency").setKey("currency").setResizable(true)
				.setSortable(true).setWidth("100px").setFlexGrow(0);

//	        // Direction column
//	        budgetGrid.addColumn(Budget::getDirection)
//	            .setHeader("Direction")
//	            .setKey("direction")
//	            .setResizable(true)
//	            .setSortable(true)
//	            .setWidth("100px")
//	            .setFlexGrow(0);
//	        
		// Original ID column
		budgetGrid.addColumn(Budget::getOriginalId).setHeader("Original ID").setKey("originalId").setResizable(true)
				.setSortable(true).setWidth("150px").setFlexGrow(0);

		// Other Party Name column
		budgetGrid.addColumn(Budget::getOtherPartyName).setHeader("Other Party Name").setKey("otherPartyName")
				.setResizable(true).setSortable(true).setWidth("200px").setFlexGrow(1);

		// Other Party Account Number column
		budgetGrid.addColumn(Budget::getOtherPartyAccountNumber).setHeader("Other Party Account")
				.setKey("otherPartyAccountNumber").setResizable(true).setSortable(true).setWidth("180px")
				.setFlexGrow(0);

		// Transaction Type column
		budgetGrid.addColumn(Budget::getTransactionType).setHeader("Transaction Type").setKey("transactionType")
				.setResizable(true).setSortable(true).setWidth("150px").setFlexGrow(0);

		// Note column
		budgetGrid.addColumn(Budget::getNote).setHeader("Note").setKey("note").setResizable(true).setSortable(true)
				.setWidth("200px").setFlexGrow(1);

		// Category Rule ID column
		budgetGrid.addColumn(Budget::getCategoryRuleId).setHeader("Category Rule ID").setKey("categoryRuleId")
				.setResizable(true).setSortable(true).setWidth("140px").setFlexGrow(0);

		// Category column (assuming Category has a name or description method)
		budgetGrid.addColumn(budget -> budget.getCategory() != null ? budget.getCategory().getCategoryName() : "")
				.setHeader("Category").setKey("category").setResizable(true).setSortable(true).setWidth("150px")
				.setFlexGrow(0);

		// Manual Category ID column
		budgetGrid.addColumn(Budget::getManualCategoryId).setHeader("Manual Category ID").setKey("manualCategoryId")
				.setResizable(true).setSortable(true).setWidth("150px").setFlexGrow(0);

		// Transfer ID column
		budgetGrid.addColumn(Budget::getTransferId).setHeader("Transfer ID").setKey("transferId").setResizable(true)
				.setSortable(true).setWidth("120px").setFlexGrow(0);

		// Grid configuration
//		budgetGrid.setSelectionMode(SelectionMode.SINGLE);

		budgetGrid.setMultiSort(true);

//		budgetGrid.setColumnReorderingAllowed(true);
	}

	// Helper method to get cell value based on column name
	private Object getCellValue(Budget budget, String columnName) {
		try {
			// Convert column name to getter method name
			String getterName = "get" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);

			// Get the method and invoke it
			Method getter = Budget.class.getMethod(getterName);
			return getter.invoke(budget);

		} catch (Exception e) {
			// Alternative approach: use field access if getter method doesn't exist
			try {
				Field field = Budget.class.getDeclaredField(columnName);
				field.setAccessible(true);
				return field.get(budget);
			} catch (Exception ex) {
				System.err.println("Could not get value for column: " + columnName);
				return null;
			}
		}
	}

	private void handleBudgetGridDoubleClick(String columnName, Object cellValue) {

		BudgetSqlClauseRow conditionRow = addConditionRow(conditionsLayout);
		conditionRow.setDoubleClickedItems(columnName, cellValue);
	}

}