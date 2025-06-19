package hu.csani.budget.services;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import hu.csani.budget.data.Account;
import hu.csani.budget.repositories.AccountRepository;

@Service
public class AccountService {

	private final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	public Page<Account> findAll(Pageable pageable) {
		return accountRepository.findAll(pageable);
	}

	public Optional<Account> findById(Integer id) {
		return accountRepository.findById(id);
	}

	public Optional<Account> findById(Long id) {
		return accountRepository.findById(Math.toIntExact(id));
	}

	public Account save(Account account) {
		return accountRepository.save(account);
	}

	public void deleteById(Integer id) {
		accountRepository.deleteById(id);
	}
}
