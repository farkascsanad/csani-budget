package hu.csani.budget.views.accounts;

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

import hu.csani.budget.data.Account;
import hu.csani.budget.services.AccountService;

@PageTitle("Accounts")
@Route("accounts/:accountId?/:action?(edit)")
@Menu(order = 1, icon = LineAwesomeIconUrl.COLUMNS_SOLID)
@Uses(Icon.class)
public class AccountsView extends Div implements BeforeEnterObserver {

	private final String ACCOUNT_ID = "accountId";
	private final String EDIT_ROUTE_TEMPLATE = "accounts/%s/edit";

	private final Grid<Account> grid = new Grid<>(Account.class, false);

	private TextField accountName;
	private TextField accountNumber;
	private TextField ibanAccountNumber;
	private TextField accountDescription;
	private DatePicker accountActiveFrom;
	private DatePicker accountActiveTo;
	private TextField tablePattern;
	private TextField accountType;

	private final Button cancel = new Button("Cancel");
	private final Button save = new Button("Save");

	private final BeanValidationBinder<Account> binder;

	private Account account;

	private final AccountService accountService;

	public AccountsView(AccountService accountService) {
		this.accountService = accountService;
		addClassNames("accounts-view");

		SplitLayout splitLayout = new SplitLayout();
		createGridLayout(splitLayout);
		createEditorLayout(splitLayout);
		add(splitLayout);

		grid.addColumn(Account::getAccountName).setHeader("Name").setAutoWidth(true);
		grid.addColumn(Account::getAccountNumber).setHeader("Number").setAutoWidth(true);
		grid.addColumn(Account::getIbanAccountNumber).setHeader("IBAN").setAutoWidth(true);
		grid.addColumn(Account::getAccountType).setHeader("Type").setAutoWidth(true);
		grid.addColumn(Account::getAccountActiveFrom).setHeader("Active From").setAutoWidth(true);
		grid.addColumn(Account::getAccountActiveTo).setHeader("Active To").setAutoWidth(true);

		grid.setItems(query -> accountService.findAll(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		grid.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() != null) {
				UI.getCurrent().navigate(String.format(EDIT_ROUTE_TEMPLATE, event.getValue().getAccountId()));
			} else {
				clearForm();
				UI.getCurrent().navigate(AccountsView.class);
			}
		});

		binder = new BeanValidationBinder<>(Account.class);
		binder.bindInstanceFields(this);

		cancel.addClickListener(e -> {
			clearForm();
			refreshGrid();
		});

		save.addClickListener(e -> {
			try {
				if (this.account == null) {
					this.account = new Account();
				}
				binder.writeBean(this.account);
				accountService.save(this.account);
				clearForm();
				refreshGrid();
				Notification.show("Account saved successfully", 3000, Position.TOP_END);
				UI.getCurrent().navigate(AccountsView.class);
			} catch (ObjectOptimisticLockingFailureException exception) {
				Notification n = Notification
						.show("Error saving account. Someone else updated it while you were editing.");
				n.setPosition(Position.MIDDLE);
				n.addThemeVariants(NotificationVariant.LUMO_ERROR);
			} catch (ValidationException validationException) {
				Notification.show("Validation failed. Please check your input.");
			}
		});
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Optional<Integer> accountId = event.getRouteParameters().get(ACCOUNT_ID).map(Integer::parseInt);
		if (accountId.isPresent()) {
			Optional<Account> accountFromBackend = accountService.findById(accountId.get());
			if (accountFromBackend.isPresent()) {
				populateForm(accountFromBackend.get());
			} else {
				Notification.show(String.format("Account not found. ID = %s", accountId.get()), 3000,
						Position.BOTTOM_START);
				refreshGrid();
				event.forwardTo(AccountsView.class);
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
		accountName = new TextField("Account Name");
		accountNumber = new TextField("Account Number");
		ibanAccountNumber = new TextField("IBAN");
		accountDescription = new TextField("Description");
		accountActiveFrom = new DatePicker("Active From");
		accountActiveTo = new DatePicker("Active To");
		tablePattern = new TextField("Table Pattern");
		accountType = new TextField("Account Type");

		formLayout.add(accountName, accountNumber, ibanAccountNumber, accountDescription, accountActiveFrom,
				accountActiveTo, tablePattern,  accountType);

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

	private void populateForm(Account value) {
		this.account = value;
		binder.readBean(this.account);
	}
}
