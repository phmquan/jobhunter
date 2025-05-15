package vn.uit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.uit.jobhunter.domain.Job;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    Boolean existsByName(String name);
    Optional<Job> findById(Long id);
} 