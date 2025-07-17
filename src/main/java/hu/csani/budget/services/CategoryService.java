package hu.csani.budget.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import hu.csani.budget.data.Category;
import hu.csani.budget.repositories.CategoryRepository;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	private List<Category> all;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	public List<Category> findAllAndBuildTree() {

		all = categoryRepository.findAll();

		CategoryTreeBuilder.buildTreeWithFullPath(all, " -> ");

		Collections.sort(all, Comparator.comparing(Category::getOrder));

//		for (Category category : all) {
//			System.out.println(category);
//		}

		return all;
	}

	public List<Category> getAll() {
//		if (all == null || all.size() == 0) {
		return findAllAndBuildTree();
//		}
//		return all;
	}

	public void save(Category category) {
		categoryRepository.save(category);
	}

}
