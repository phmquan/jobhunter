package vn.uit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vn.uit.jobhunter.domain.Company;
import vn.uit.jobhunter.domain.User;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.repository.CompanyRepository;
import vn.uit.jobhunter.repository.UserRepository;
import vn.uit.jobhunter.service.pagination.PaginationHelper;

@Service
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PaginationHelper paginationHelper;

    public Company handleCreateCompany(Company c) {
        return this.companyRepository.save(c);
    }

    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pCompany = this.companyRepository.findAll(spec, pageable);
        
        return paginationHelper.convertResultPagination(pCompany, pageable);
    }

    public Company updateCompany(Company c) {
        Optional<Company> companyOptional = this.companyRepository.findById(c.getId());
        if (companyOptional.isPresent()) {
            Company currentCompany = companyOptional.get();
            currentCompany.setLogo(c.getLogo());
            currentCompany.setName(c.getName());
            currentCompany.setDescription(c.getDescription());
            currentCompany.setAddress(c.getAddress());
            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

    public void deleteCompany(long id) {
        Optional<Company> comOptional = this.companyRepository.findById(id);
        if (comOptional.isPresent()) {
            Company com = comOptional.get();
            // fetch all user belong to this company
            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }

        this.companyRepository.deleteById(id);
    }

    public Optional<Company> findById(long id) {
        return this.companyRepository.findById(id);
    }
}
