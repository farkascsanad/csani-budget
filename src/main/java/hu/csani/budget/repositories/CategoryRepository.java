package hu.csani.budget.repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import hu.csani.budget.data.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Page<Category> findByCategoryNameContainingIgnoreCase(String name, Pageable pageable);

//	Collection<Category> findByFilters(String name, String desc, String active, Pageable pageable);

}