package hu.csani.budget.services;

import java.util.List;

import org.springframework.stereotype.Service;

import hu.csani.budget.data.Budget;
import hu.csani.budget.repositories.BudgetRepository;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }

    public Budget findById(Integer id) {
        return budgetRepository.findById(id).orElse(null);
    }

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public void deleteById(Integer id) {
        budgetRepository.deleteById(id);
    }

	public void saveList(List<Budget> listOfBudget) {
		budgetRepository.saveAll(listOfBudget);
		
	}

}
