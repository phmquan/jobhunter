package vn.uit.jobhunter.service.validation;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import vn.uit.jobhunter.domain.Company;
import vn.uit.jobhunter.domain.Skill;
import vn.uit.jobhunter.repository.CompanyRepository;
import vn.uit.jobhunter.repository.SkillRepository;

@Component
@AllArgsConstructor
public class JobValidator {
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public List<Skill> fetchSkillsIfExist(List<Long> skillIds) {
        return skillRepository.findByIdIn(skillIds);
    }

    public Company fetchCompanyIfExist(Long companyId) {
        return companyRepository.findById(companyId).orElse(null);
    }
}
