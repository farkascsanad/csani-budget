package hu.csani.budget.views.category;

import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import hu.csani.budget.data.Category;
import hu.csani.budget.repositories.CategoryRepository;

@PageTitle("Categories")
@Route("categories/:categoryId?/:action?(edit)")
@Menu(order = 3, icon = LineAwesomeIconUrl.FOLDER_SOLID)
@Uses(Icon.class)
public class CategoriesView extends Div implements BeforeEnterObserver {

	private final String CATEGORY_ID = "categoryId";
	private final String CATEGORY_EDIT_ROUTE_TEMPLATE = "categories/%s/edit";

//	private final Grid<Category> grid = new Grid<>(Category.class, false);

	private TextField categoryName;
	private TextField description;
	private TextField isActive;
	private TextField parentId;

	private final Button cancel = new Button("Cancel");
	private final Button save = new Button("Save");

	private final BeanValidationBinder<Category> binder;

	private Category category;

	private final CategoryRepository categoryRepository;

	private final Grid<Category> grid = new Grid<>(Category.class, false);
	private ListDataProvider<Category> dataProvider;

	public CategoriesView(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
		addClassNames("category-view");

		// Create UI
		SplitLayout splitLayout = new SplitLayout();
		createGridLayout(splitLayout);
		createEditorLayout(splitLayout);
		splitLayout.setSplitterPosition(70); // 70% for grid, 30% for editor
		add(splitLayout);

		// 1) Fetch all categories into a ListDataProvider
		dataProvider = new ListDataProvider<>(categoryRepository.findAll());
		grid.setDataProvider(dataProvider);

		// Configure Grid
		// 2) Define your columns (capture references for filtering)
		Grid.Column<Category> idColumn = grid.addColumn(Category::getCategoryId).setHeader("Category ID")
				.setAutoWidth(true);
		Grid.Column<Category> nameColumn = grid.addColumn(Category::getCategoryName).setHeader("Category Name")
				.setAutoWidth(true);
		Grid.Column<Category> descColumn = grid.addColumn(Category::getDescription).setHeader("Description")
				.setAutoWidth(true);
		Grid.Column<Category> activeColumn = grid.addColumn(Category::getIsActive).setHeader("Active")
				.setAutoWidth(true);
		Grid.Column<Category> parentColumn = grid
				.addColumn(c -> c.getParent() != null ? c.getParent().getCategoryId() : null).setHeader("Parent ID")
				.setAutoWidth(true);

		// 3) Add a header row for the filters
		HeaderRow filterRow = grid.appendHeaderRow();

		// 4) Create and wire up each TextField filter

//		// --- Category ID filter (numeric) ---
//		TextField idFilter = new TextField();
//		idFilter.setPlaceholder("Filter");
//		idFilter.setClearButtonVisible(true);
//		idFilter.addValueChangeListener(e -> {
//			dataProvider.addFilter(category -> String.valueOf(category.getCategoryId()).contains(e.getValue().trim()));
//		});
//		filterRow.getCell(idColumn).setComponent(idFilter);

		// --- Category Name filter ---
		TextField nameFilter = new TextField();
		nameFilter.setPlaceholder("Filter");
		nameFilter.setClearButtonVisible(true);
		nameFilter.addValueChangeListener(event -> {
			String value = event.getValue();
			if (value == null || value.trim().isEmpty()) {
				// Show all
				grid.setItems(query -> categoryRepository.findAll(VaadinSpringDataHelpers.toSpringPageRequest(query))
						.stream());
			} else {
				// Apply filter
				grid.setItems(query -> categoryRepository.findByCategoryNameContainingIgnoreCase(value,
						VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
			}
		});
		filterRow.getCell(nameColumn).setComponent(nameFilter);

//		// --- Description filter ---
//		TextField descFilter = new TextField();
//		descFilter.setPlaceholder("Filter");
//		descFilter.setClearButtonVisible(true);
//		descFilter.addValueChangeListener(e -> {
//			String val = e.getValue().trim().toLowerCase();
//			dataProvider.addFilter(category -> {
//				String d = category.getDescription();
//				return d != null && d.toLowerCase().contains(val);
//			});
//		});
//		filterRow.getCell(descColumn).setComponent(descFilter);
//
//		// --- Active filter (simple text match on "true"/"false") ---
//		TextField activeFilter = new TextField();
//		activeFilter.setPlaceholder("Filter");
//		activeFilter.setClearButtonVisible(true);
//		activeFilter.addValueChangeListener(e -> {
//			String val = e.getValue().trim().toLowerCase();
//			dataProvider.addFilter(category -> String.valueOf(category.getIsActive()).contains(val));
//		});
//		filterRow.getCell(activeColumn).setComponent(activeFilter);
//
//		// --- Parent ID filter ---
//		TextField parentFilter = new TextField();
//		parentFilter.setPlaceholder("Filter");
//		parentFilter.setClearButtonVisible(true);
//		parentFilter.addValueChangeListener(e -> {
//			String val = e.getValue().trim();
//			dataProvider.addFilter(category -> {
//				Long p = category.getParent() != null ? category.getParent().getCategoryId() : null;
//				return p != null && String.valueOf(p).contains(val);
//			});
//		});
//		filterRow.getCell(parentColumn).setComponent(parentFilter);

//		idFilter.setValueChangeMode(ValueChangeMode.TIMEOUT);
//		idFilter.setValueChangeTimeout(300); // 300 ms after last keystroke

		nameFilter.setValueChangeMode(ValueChangeMode.TIMEOUT);
		nameFilter.setValueChangeTimeout(300);

//		descFilter.setValueChangeMode(ValueChangeMode.TIMEOUT);
//		descFilter.setValueChangeTimeout(300);
//
//		activeFilter.setValueChangeMode(ValueChangeMode.TIMEOUT);
//		activeFilter.setValueChangeTimeout(300);
//
//		parentFilter.setValueChangeMode(ValueChangeMode.TIMEOUT);
//		parentFilter.setValueChangeTimeout(300);

		// 5) Final touches
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		grid.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() != null) {
				UI.getCurrent().navigate(String.format(CATEGORY_EDIT_ROUTE_TEMPLATE, event.getValue().getCategoryId()));
			} else {
				clearForm();
				UI.getCurrent().navigate(CategoriesView.class);
			}
		});

		// Configure Form
		binder = new BeanValidationBinder<>(Category.class);
		binder.bindInstanceFields(this);

		cancel.addClickListener(e -> {
			clearForm();
			refreshGrid();
		});

		save.addClickListener(e -> {
			try {
				if (this.category == null) {
					this.category = new Category();
				}
				binder.writeBean(this.category);

				// Set parent if parentId is not empty
				if (parentId.getValue() != null && !parentId.getValue().isEmpty()) {
					Long parentCategoryId = Long.parseLong(parentId.getValue());
					Optional<Category> parentCat = categoryRepository.findById(parentCategoryId);
					this.category.setParent(parentCat.orElse(null));
				} else {
					this.category.setParent(null);
				}

				categoryRepository.save(this.category);
				clearForm();
				refreshGrid();
				Notification.show("Category updated");
				UI.getCurrent().navigate(CategoriesView.class);
			} catch (ValidationException validationException) {
				Notification.show("Failed to update the data. Check again that all values are valid");
			}
		});
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Optional<Long> categoryId = event.getRouteParameters().get(CATEGORY_ID).map(Long::parseLong);
		if (categoryId.isPresent()) {
			Optional<Category> categoryFromBackend = categoryRepository.findById(categoryId.get());
			if (categoryFromBackend.isPresent()) {
				populateForm(categoryFromBackend.get());
			} else {
				Notification.show(String.format("The requested category was not found, ID = %s", categoryId.get()),
						3000, Notification.Position.BOTTOM_START);
				refreshGrid();
				event.forwardTo(CategoriesView.class);
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
		categoryName = new TextField("Category Name");
		description = new TextField("Description");
		isActive = new TextField("Active");
		parentId = new TextField("Parent Category ID");

		formLayout.add(categoryName, description, isActive, parentId);

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

	private void populateForm(Category value) {
		this.category = value;
		binder.readBean(this.category);
		// Set parentId field if editing
		if (this.category != null && this.category.getParent() != null) {
			parentId.setValue(String.valueOf(this.category.getParent().getCategoryId()));
		} else {
			parentId.setValue("");
		}
	}
}