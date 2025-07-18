package hu.csani.budget.views.rules;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Locale;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;

import hu.csani.budget.data.Budget;

public class BudgetGrid extends Grid<Budget> {

    private CategoryRuleCreateView categoryRuleCreateView;

	public BudgetGrid(CategoryRuleCreateView categoryRuleCreateView) {
        super(Budget.class);
        this.categoryRuleCreateView = categoryRuleCreateView;
        setupGrid();
        
        addDoubleClickListener();
    }

    private void setupGrid() {
        // Remove default columns if any
        removeAllColumns();

        // Budget ID column
        addColumn(Budget::getBudgetId).setHeader("Budget ID").setKey("budgetId").setResizable(true)
                .setSortable(true).setWidth("120px").setFlexGrow(0);

        // Account ID column
        addColumn(budget -> budget.getAccount().getAccountName()).setHeader("Account ID").setKey("accountId").setResizable(true)
                .setSortable(true).setWidth("120px").setFlexGrow(0);

        // Booking Date column
        addColumn(budget -> budget.getBookingDate() != null ? budget.getBookingDate().toString() : "")
                .setHeader("Booking Date").setKey("bookingDate").setResizable(true).setSortable(true).setWidth("40px")
                .setFlexGrow(0);

        // Transaction Date column
        addColumn(budget -> budget.getTransactionDate() != null ? budget.getTransactionDate().toString() : "")
                .setHeader("Transaction Date").setKey("transactionDate").setResizable(true).setSortable(true)
                .setWidth("150px").setFlexGrow(0);

//        // Amount column
//        addColumn(budget -> budget.getAmount() != null ? budget.getAmount().toString() : "")
//                .setHeader("Amount").setKey("amount").setResizable(true).setSortable(true).setWidth("120px")
//                .setFlexGrow(0);

     // Amount column
        addColumn(budget -> {
            if (budget.getAmount() != null) {
                NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
                formatter.setMaximumFractionDigits(0); // No decimal places
                return formatter.format(budget.getAmount());
            }
            return "";
        })
        .setHeader("Amount").setKey("amount").setResizable(true).setSortable(true).setWidth("120px")
        .setFlexGrow(0);
        
        // Currency column
        addColumn(Budget::getCurrency).setHeader("Currency").setKey("currency").setResizable(true)
                .setSortable(true).setWidth("100px").setFlexGrow(0);

        // Original ID column
        addColumn(Budget::getOriginalId).setHeader("Original ID").setKey("originalId").setResizable(true)
                .setSortable(true).setWidth("150px").setFlexGrow(0);

        // Other Party Name column
        addColumn(Budget::getOtherPartyName).setHeader("Other Party Name").setKey("otherPartyName")
                .setResizable(true).setSortable(true).setWidth("200px").setFlexGrow(1);

        // Other Party Account Number column
        addColumn(Budget::getOtherPartyAccountNumber).setHeader("Other Party Account")
                .setKey("otherPartyAccountNumber").setResizable(true).setSortable(true).setWidth("180px")
                .setFlexGrow(0);

        // Transaction Type column
        addColumn(Budget::getTransactionType).setHeader("Transaction Type").setKey("transactionType")
                .setResizable(true).setSortable(true).setWidth("150px").setFlexGrow(0);

        // Note column
        addColumn(Budget::getNote).setHeader("Note").setKey("note").setResizable(true).setSortable(true)
                .setWidth("200px").setFlexGrow(1);

        // Category Rule ID column
        addColumn(Budget::getCategoryRuleId).setHeader("Category Rule ID").setKey("categoryRuleId")
                .setResizable(true).setSortable(true).setWidth("140px").setFlexGrow(0);

        // Category column (assuming Category has a name or description method)
        addColumn(budget -> budget.getCategory() != null ? budget.getCategory().getCategoryName() : "")
                .setHeader("Category").setKey("category").setResizable(true).setSortable(true).setWidth("150px")
                .setFlexGrow(0);

        // Manual Category ID column
        addColumn(Budget::getManualCategoryId).setHeader("Manual Category ID").setKey("manualCategoryId")
                .setResizable(true).setSortable(true).setWidth("150px").setFlexGrow(0);

        // Transfer ID column
        addColumn(Budget::getTransferId).setHeader("Transfer ID").setKey("transferId").setResizable(true)
                .setSortable(true).setWidth("120px").setFlexGrow(0);

        // Grid configuration
        setHeight("250px");
        setMultiSort(true);
    }

    private void addDoubleClickListener() {
        addItemDoubleClickListener(event -> {
            Budget clickedBudget = event.getItem();
            Grid.Column<Budget> clickedColumn = event.getColumn();

            if (clickedColumn != null) {
                String columnName = clickedColumn.getKey();
                Object cellValue = getCellValue(clickedBudget, columnName);
                categoryRuleCreateView.handleBudgetGridDoubleClick(columnName, cellValue);
            }
        });
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

//    // Override this method in your view class or use a listener interface
//    protected void handleBudgetGridDoubleClick(String columnName, Object cellValue) {
////        Notification.show("Double-clicked on column: " + columnName + ", Value: " + cellValue);
//    	  
//    }
}