package vn.uit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.uit.jobhunter.domain.Resume;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>,
        JpaSpecificationExecutor<Resume> {
    Optional<Resume> findTopByEmailOrderByCreatedAtDesc(String email);
}
