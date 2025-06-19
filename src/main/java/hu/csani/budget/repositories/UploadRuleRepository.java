package hu.csani.budget.repositories;

import hu.csani.budget.data.UploadRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadRuleRepository extends JpaRepository<UploadRule, Integer> {
}
