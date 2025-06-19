package hu.csani.budget.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.csani.budget.data.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
}
