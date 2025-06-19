package hu.csani.budget.views.rules;

import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import hu.csani.budget.data.CategoryRule;
import hu.csani.budget.services.CategoryRuleService;

@PageTitle("Rules")
@Route("category-rule/:categoryRuleId?/:action?(edit)")
@Menu(order = 2, icon = LineAwesomeIconUrl.COLUMNS_SOLID)
@Uses(Icon.class)
public class RulesView extends Div implements BeforeEnterObserver {

	private final String CATEGORY_RULE_ID = "categoryRuleId";
	private final String CATEGORY_RULE_EDIT_ROUTE_TEMPLATE = "category-rule/%s/edit";

	private final Grid<CategoryRule> grid = new Grid<>(CategoryRule.class, false);

	private TextField ruleName;
	private TextField description;
	private TextField isActive;
	private TextField accountId;
	private TextField minAmount;
	private TextField maxAmount;
	private TextField direction;
	private TextField currency;
	private TextField otherPartyAccountNumber;
	private TextField notePattern;
	private DatePicker bookingDateFrom;
	private DatePicker bookingDateTo;
	private DatePicker transactionDateFrom;
	private DatePicker transactionDateTo;
	
	private TextField categoryId;
	private TextField priority;
	private TextField transactionType;
	private TextField otherPartyName;

	private final Button cancel = new Button("Cancel");
	private final Button save = new Button("Save");

	private final BeanValidationBinder<CategoryRule> binder;

	private CategoryRule categoryRule;

	private final CategoryRuleService categoryRuleService;
	


	public RulesView(CategoryRuleService categoryRuleService) {
		this.categoryRuleService = categoryRuleService;
		addClassNames("category-rule-view");

		// Create UI
		SplitLayout splitLayout = new SplitLayout();
		createGridLayout(splitLayout);
		createEditorLayout(splitLayout);
		splitLayout.setSplitterPosition(70); // 70% for grid, 30% for editor
		add(splitLayout);

		// Configure Grid
		grid.addColumn("categoryRuleId").setHeader("Rule ID").setAutoWidth(true); // was ruleId
		grid.addColumn("categoryId").setHeader("Category ID").setAutoWidth(true);
		grid.addColumn("ruleName").setHeader("Rule Name").setAutoWidth(true); 
		grid.addColumn("description").setHeader("Description").setAutoWidth(true); 
		grid.addColumn("priority").setHeader("Priority").setAutoWidth(true);
		grid.addColumn("isActive").setHeader("Active").setAutoWidth(true); 
		grid.addColumn("accountId").setHeader("Account ID").setAutoWidth(true); 
		grid.addColumn("minAmount").setHeader("Min Amount").setAutoWidth(true); 
		grid.addColumn("maxAmount").setHeader("Max Amount").setAutoWidth(true); 
		grid.addColumn("direction").setHeader("Direction").setAutoWidth(true); 
		grid.addColumn("currency").setHeader("Currency").setAutoWidth(true); 
		grid.addColumn("transactionType").setHeader("Transaction Type").setAutoWidth(true); // no change
		grid.addColumn("otherPartyName").setHeader("Other Party Name").setAutoWidth(true);
		grid.addColumn("otherPartyAccountNumber").setHeader("Other Party Account Number").setAutoWidth(true); 
		grid.addColumn("notePattern").setHeader("Note Pattern").setAutoWidth(true); 
		grid.addColumn("bookingDateFrom").setHeader("Booking Date From").setAutoWidth(true); 
		grid.addColumn("bookingDateTo").setHeader("Booking Date To").setAutoWidth(true); 
		grid.addColumn("transactionDateFrom").setHeader("Transaction Date From").setAutoWidth(true); 
		grid.addColumn("transactionDateTo").setHeader("Transaction Date To").setAutoWidth(true); 

		grid.setItems(
				query -> categoryRuleService.findAll(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		grid.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() != null) {
				UI.getCurrent()
						.navigate(String.format(CATEGORY_RULE_EDIT_ROUTE_TEMPLATE, event.getValue().getCategoryRuleId()));
			} else {
				clearForm();
				UI.getCurrent().navigate(RulesView.class);
			}
		});

		// Configure Form
		binder = new BeanValidationBinder<>(CategoryRule.class);
		binder.bindInstanceFields(this);

		cancel.addClickListener(e -> {
			clearForm();
			refreshGrid();
		});

		save.addClickListener(e -> {
			try {
				if (this.categoryRule == null) {
					this.categoryRule = new CategoryRule();
				}
				binder.writeBean(this.categoryRule);
				categoryRuleService.save(this.categoryRule);
				clearForm();
				refreshGrid();
				Notification.show("Data updated");
				UI.getCurrent().navigate(RulesView.class);
			} catch (ObjectOptimisticLockingFailureException exception) {
				Notification n = Notification.show(
						"Error updating the data. Somebody else has updated the record while you were making changes.");
				n.setPosition(Position.MIDDLE);
				n.addThemeVariants(NotificationVariant.LUMO_ERROR);
			} catch (ValidationException validationException) {
				Notification.show("Failed to update the data. Check again that all values are valid");
			}
		});
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Optional<Long> categoryRuleId = event.getRouteParameters().get(CATEGORY_RULE_ID).map(Long::parseLong);
		if (categoryRuleId.isPresent()) {
			Optional<CategoryRule> categoryRuleFromBackend = categoryRuleService.findById(categoryRuleId.get());
			if (categoryRuleFromBackend.isPresent()) {
				populateForm(categoryRuleFromBackend.get());
			} else {
				Notification.show(
						String.format("The requested category rule was not found, ID = %s", categoryRuleId.get()), 3000,
						Notification.Position.BOTTOM_START);
				refreshGrid();
				event.forwardTo(RulesView.class);
			}
		}
	}


	private void createEditorLayout(SplitLayout splitLayout) {
		Div editorLayoutDiv = new Div();
		editorLayoutDiv.setClassName("editor-layout");

		Div editorDiv = new Div();
		editorDiv.setClassName("editor");
		editorLayoutDiv.add(editorDiv);

		FormLayout formLayout = new FormLayout();
		categoryId = new TextField("Category ID");
		ruleName = new TextField("Rule Name"); 
		description = new TextField("Description"); 
		priority = new TextField("Priority");
		isActive = new TextField("Active"); 
		accountId = new TextField("Account ID"); 
		minAmount = new TextField("Min Amount"); 
		maxAmount = new TextField("Max Amount"); 
		direction = new TextField("Direction"); 
		currency = new TextField("Currency"); 
		transactionType = new TextField("Transaction Type");
		otherPartyName = new TextField("Other Party Name");
		otherPartyAccountNumber = new TextField("Other Party Account Number"); 
		notePattern = new TextField("Note Pattern"); 
		bookingDateFrom = new DatePicker("Booking Date From"); 
		bookingDateTo = new DatePicker("Booking Date To"); 
		transactionDateFrom = new DatePicker("Transaction Date From"); 
		transactionDateTo = new DatePicker("Transaction Date To"); 

		formLayout.add(categoryId, ruleName, description, priority, isActive, accountId, minAmount, maxAmount,
				direction, currency, transactionType, otherPartyName, otherPartyAccountNumber, notePattern,
				bookingDateFrom, bookingDateTo, transactionDateFrom, transactionDateTo);

		editorDiv.add(formLayout);
		createButtonLayout(editorLayoutDiv);

		splitLayout.addToSecondary(editorLayoutDiv);
	}


	private void createButtonLayout(Div editorLayoutDiv) {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setClassName("button-layout");
		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		buttonLayout.add(save, cancel);
		editorLayoutDiv.add(buttonLayout);
	}

	private void createGridLayout(SplitLayout splitLayout) {
		Div wrapper = new Div();
		wrapper.setClassName("grid-wrapper");
		splitLayout.addToPrimary(wrapper);
		wrapper.add(grid);
	}

	private void refreshGrid() {
		grid.select(null);
		grid.getDataProvider().refreshAll();
	}

	private void clearForm() {
		populateForm(null);
	}

	private void populateForm(CategoryRule value) {
		this.categoryRule = value;
		binder.readBean(this.categoryRule);
	}
}