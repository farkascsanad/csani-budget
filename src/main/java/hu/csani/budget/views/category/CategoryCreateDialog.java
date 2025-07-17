package hu.csani.budget.views.category;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;

import hu.csani.budget.data.Category;
import hu.csani.budget.services.CategoryService;

public class CategoryCreateDialog extends Dialog {

	private final Binder<Category> binder = new Binder<>(Category.class);
	private final Category category = new Category();

	private TextField categoryNameField;
	private TextArea descriptionField;
	private Checkbox isActiveCheckbox;
	private ComboBox<Category> parentComboBox;

	private Consumer<Category> saveHandler;
	private CategoryService categoryService;
	private ComboBox<Category> connectedCombox;

//	public CategoryCreateDialog(List<Category> availableParents) {
	
	
	public CategoryCreateDialog(ComboBox<Category> cb, CategoryService categoryService) {
		
		this.categoryService = categoryService;
		this.connectedCombox  =cb;
		
		setModal(true);
		setDraggable(true);
		setResizable(true);
		setWidth("500px");
		setHeight("400px");

		createDialogContent(categoryService.findAllAndBuildTree());
		setupValidation();

	}

	private void createDialogContent(List<Category> availableParents) {
		// Header
		H2 title = new H2("Create New Category");
		title.getStyle().set("margin", "0 0 20px 0");

		// Form layout
		FormLayout formLayout = new FormLayout();
		formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

		// Category Name field (required)
		categoryNameField = new TextField("Category Name");
		categoryNameField.setPlaceholder("Enter category name");
		categoryNameField.setRequired(true);
		categoryNameField.setRequiredIndicatorVisible(true);
		categoryNameField.setWidthFull();

		// Description field
		descriptionField = new TextArea("Description");
		descriptionField.setPlaceholder("Enter category description (optional)");
		descriptionField.setWidthFull();
		descriptionField.setMaxLength(500);
		descriptionField.setValueChangeMode(com.vaadin.flow.data.value.ValueChangeMode.EAGER);

		// Is Active checkbox
		isActiveCheckbox = new Checkbox("Active");
		isActiveCheckbox.setValue(true); // Default to active
		isActiveCheckbox.setTooltipText("Inactive categories won't be available for selection");

		// Parent category combo box
		parentComboBox = new ComboBox<>("Parent Category");
		parentComboBox.setItems(availableParents);
		parentComboBox.setItemLabelGenerator(category -> {
			if (category == null) {
				return "";
			}
			return category.getCategoryName() != null ? category.getCategoryName() : "Unnamed Category";
		});
		parentComboBox.setPlaceholder("Select parent category (optional)");
		parentComboBox.setClearButtonVisible(true);
		parentComboBox.setWidthFull();

		// Add fields to form
		formLayout.add(categoryNameField, descriptionField, isActiveCheckbox, parentComboBox);

		// Button layout
		HorizontalLayout buttonLayout = createButtonLayout();

		// Main layout
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.add(title, formLayout, buttonLayout);
		mainLayout.setPadding(true);
		mainLayout.setSpacing(true);
		mainLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

		add(mainLayout);
	}

	private HorizontalLayout createButtonLayout() {
		Button saveButton = new Button("Save");
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(event -> handleSave());

		Button cancelButton = new Button("Cancel");
		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		cancelButton.addClickListener(event -> close());

		HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
		buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		buttonLayout.setWidthFull();
		buttonLayout.getStyle().set("margin-top", "20px");

		return buttonLayout;
	}

	private void setupValidation() {
		// Bind fields to the category object
		binder.forField(categoryNameField).withValidator(new StringLengthValidator("Category name is required", 1, 255))
				.bind(Category::getCategoryName, Category::setCategoryName);

		binder.forField(descriptionField).bind(Category::getDescription, Category::setDescription);

		binder.forField(isActiveCheckbox).bind(Category::getIsActive, Category::setIsActive);

		binder.forField(parentComboBox).bind(Category::getParent, Category::setParent);

		// Set the bean
		binder.setBean(category);
	}

	private void handleSave() {
		try {
			binder.writeBean(category);

			// Call the save handler if it's set
			if (saveHandler != null) {
				saveHandler.accept(category);
			}

			categoryService.save(category);
			List<Category> allAndBuildTree = categoryService.findAllAndBuildTree();
			connectedCombox.setItems(allAndBuildTree);
			connectedCombox.setValue(category);
			
			close();
		} catch (ValidationException e) {
			// Validation failed, stay open and show errors
			// The binder will automatically show validation errors on the fields
		}
	}

	/**
	 * Sets the save handler that will be called when the user clicks Save
	 * 
	 * @param saveHandler Consumer that handles the save operation
	 */
	public void setSaveHandler(Consumer<Category> saveHandler) {
		this.saveHandler = saveHandler;
	}

	/**
	 * Gets the current category being edited
	 * 
	 * @return the category with current form values
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * Resets the form to initial state
	 */
	public void resetForm() {
		category.setCategoryName(null);
		category.setDescription(null);
		category.setIsActive(true);
		category.setParent(null);
		binder.readBean(category);
	}

	/**
	 * Updates the available parent categories
	 * 
	 * @param availableParents new list of available parent categories
	 */
	public void updateAvailableParents(List<Category> availableParents) {
		parentComboBox.setItems(availableParents);
	}

	/**
	 * Pre-fills the form with an existing category (for editing)
	 * 
	 * @param existingCategory the category to edit
	 */
	public void setCategory(Category existingCategory) {
		if (existingCategory != null) {
			binder.readBean(existingCategory);
		}
	}
}