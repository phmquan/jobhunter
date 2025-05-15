package vn.uit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.uit.jobhunter.domain.Company;
import java.util.Optional;


@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {

    Boolean existsByName(String name);
    Optional<Company> findById(Long id);
}
