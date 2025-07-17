package hu.csani.budget.services;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import hu.csani.budget.data.Category;

public class CategoryTreeBuilder {
	/**
	 * Builds the category tree, updating the hierarchyDisplay, level, and order
	 * fields for each category.
	 * 
	 * @param categories the flat list of all categories
	 */
	public static void buildTree(List<Category> categories) {
		// Map: categoryId -> Category
		Map<Long, Category> idToCategory = categories.stream()
				.collect(Collectors.toMap(Category::getCategoryId, c -> c));
		// Map: parentId -> List<Category>
		Map<Long, List<Category>> parentIdToChildren = new HashMap<>();
		for (Category cat : categories) {
			Long parentId = cat.getParent() != null ? cat.getParent().getCategoryId() : null;
			parentIdToChildren.computeIfAbsent(parentId, k -> new ArrayList<>()).add(cat);
		}
		// Sort children at each level by categoryName (or by order if you have a custom
		// order)
		for (List<Category> childList : parentIdToChildren.values()) {
			childList.sort(Comparator.comparing(Category::getCategoryName, Comparator.nullsLast(String::compareTo)));
		}
		// Start from root nodes (parentId == null)
		List<Category> roots = parentIdToChildren.getOrDefault(null, Collections.emptyList());
		int[] globalOrder = { 0 }; // To set incremental order
		for (Category root : roots) {
			buildTreeRecursive(root, parentIdToChildren, "", 0, globalOrder, true);
		}
		
	}
	
	/**
	 * Builds the category tree with full parent path display, making it searchable by parent names.
	 * Updates the hierarchyDisplay field to show the complete path from root to current category.
	 * 
	 * @param categories the flat list of all categories
	 * @param separator the separator to use between parent names (e.g., " - ")
	 */
	public static void buildTreeWithFullPath(List<Category> categories, String separator) {
		// Map: categoryId -> Category
		Map<Long, Category> idToCategory = categories.stream()
				.collect(Collectors.toMap(Category::getCategoryId, c -> c));
		
		// Map: parentId -> List<Category>
		Map<Long, List<Category>> parentIdToChildren = new HashMap<>();
		for (Category cat : categories) {
			Long parentId = cat.getParent() != null ? cat.getParent().getCategoryId() : null;
			parentIdToChildren.computeIfAbsent(parentId, k -> new ArrayList<>()).add(cat);
		}
		
		// Sort children at each level by categoryName
		for (List<Category> childList : parentIdToChildren.values()) {
			childList.sort(Comparator.comparing(Category::getCategoryName, Comparator.nullsLast(String::compareTo)));
		}
		
		// Start from root nodes (parentId == null)
		List<Category> roots = parentIdToChildren.getOrDefault(null, Collections.emptyList());
		int[] globalOrder = { 0 }; // To set incremental order
		
		for (Category root : roots) {
			buildTreeWithFullPathRecursive(root, parentIdToChildren, "", 0, globalOrder, separator);
		}
	}
	
	/**
	 * Overloaded method with default separator " - "
	 * 
	 * @param categories the flat list of all categories
	 */
	public static void buildTreeWithFullPath(List<Category> categories) {
		buildTreeWithFullPath(categories, " - ");
	}
	
	// Recursive DFS to set hierarchyDisplay, level, order
	private static void buildTreeRecursive(Category node, Map<Long, List<Category>> parentIdToChildren, String prefix,
			int level, int[] globalOrder, boolean isLast) {
		node.setLevel(level);
		node.setOrder(globalOrder[0]++);
		// Choose the display prefix
		String branch = "";
		if (level > 0) {
			branch = prefix + (isLast ? "└── " : "├── ");
		}
		node.setHierarchyDisplay(branch + node.getCategoryName());
		List<Category> children = parentIdToChildren.getOrDefault(node.getCategoryId(), Collections.emptyList());
		for (int i = 0; i < children.size(); i++) {
			Category child = children.get(i);
			boolean childIsLast = (i == children.size() - 1);
			String childPrefix = prefix;
			if (level > 0) {
				childPrefix += (isLast ? "    " : "│   ");
			}
			buildTreeRecursive(child, parentIdToChildren, childPrefix, level + 1, globalOrder, childIsLast);
		}
	}
	
	// Recursive DFS to set hierarchyDisplay with full path, level, order
	public static void buildTreeWithFullPathRecursive(Category node, Map<Long, List<Category>> parentIdToChildren, 
			String parentPath, int level, int[] globalOrder, String separator) {
		node.setLevel(level);
		node.setOrder(globalOrder[0]++);
		
		// Build the full path from root to current node
		String fullPath = parentPath.isEmpty() ? node.getCategoryName() : parentPath + separator + node.getCategoryName();
		node.setHierarchyDisplay(fullPath);
		
		// Process children
		List<Category> children = parentIdToChildren.getOrDefault(node.getCategoryId(), Collections.emptyList());
		for (Category child : children) {
			buildTreeWithFullPathRecursive(child, parentIdToChildren, fullPath, level + 1, globalOrder, separator);
		}
	}
}