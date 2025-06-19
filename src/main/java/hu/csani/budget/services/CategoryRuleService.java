package hu.csani.budget.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import hu.csani.budget.data.CategoryRule;
import hu.csani.budget.repositories.CategoryRuleRepository;

@Service
public class CategoryRuleService {

	private final CategoryRuleRepository categoryRuleRepository;

	public CategoryRuleService(CategoryRuleRepository categoryRuleRepository) {
		this.categoryRuleRepository = categoryRuleRepository;
	}

	public List<CategoryRule> findAll() {
		return categoryRuleRepository.findAll();
	}

	public Page<CategoryRule> findAll(Pageable pageable) {
		return categoryRuleRepository.findAll(pageable);
	}

	public Optional<CategoryRule> findById(Long id) {
		return categoryRuleRepository.findById(id);
	}

	public CategoryRule save(CategoryRule categoryRule) {
		return categoryRuleRepository.save(categoryRule);
	}

	public void deleteById(Long id) {
		categoryRuleRepository.deleteById(id);
	}
}