package vn.uit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.uit.jobhunter.domain.Skill;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Boolean existsByName(String name);
    Optional<Skill> findById(Long id);
    List<Skill> findByIdIn(List<Long> postSkills);
} 