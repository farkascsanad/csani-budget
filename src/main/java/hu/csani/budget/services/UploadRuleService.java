package hu.csani.budget.services;

import hu.csani.budget.data.UploadRule;
import hu.csani.budget.repositories.UploadRuleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UploadRuleService {

    private final UploadRuleRepository uploadRuleRepository;

    public UploadRuleService(UploadRuleRepository uploadRuleRepository) {
        this.uploadRuleRepository = uploadRuleRepository;
    }

    public List<UploadRule> findAll() {
        return uploadRuleRepository.findAll();
    }

    public Page<UploadRule> findAll(Pageable pageable) {
        return uploadRuleRepository.findAll(pageable);
    }

    public Optional<UploadRule> findById(Integer id) {
        return uploadRuleRepository.findById(id);
    }

    public UploadRule save(UploadRule uploadRule) {
        return uploadRuleRepository.save(uploadRule);
    }

    public void deleteById(Integer id) {
        uploadRuleRepository.deleteById(id);
    }
}
