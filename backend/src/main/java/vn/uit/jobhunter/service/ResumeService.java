package vn.uit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import lombok.AllArgsConstructor;
import vn.uit.jobhunter.domain.Job;
import vn.uit.jobhunter.domain.Resume;
import vn.uit.jobhunter.domain.User;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.uit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.uit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.uit.jobhunter.repository.JobRepository;
import vn.uit.jobhunter.repository.ResumeRepository;
import vn.uit.jobhunter.repository.UserRepository;
import vn.uit.jobhunter.service.mapper.ResumeMapperDTO;
import vn.uit.jobhunter.service.pagination.PaginationHelper;
import vn.uit.jobhunter.util.SecurityUtil;

@Service
@AllArgsConstructor
public class ResumeService {
    @Autowired
    FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ResumeMapperDTO resumeMapperDTO;
    private final PaginationHelper paginationHelper;

    public Optional<Resume> fetchById(long id) {
        return this.resumeRepository.findById(id);
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        // check user by id
        if (resume.getUser() == null)
            return false;
        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        if (userOptional.isEmpty())
            return false;

        // check job by id
        if (resume.getJob() == null)
            return false;
        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if (jobOptional.isEmpty())
            return false;

        return true;
    }

    public ResCreateResumeDTO create(Resume resume) {
        resume = this.resumeRepository.save(resume);

        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedBy(resume.getCreatedBy());
        res.setCreatedAt(resume.getCreatedAt());

        return res;
    }

    public ResUpdateResumeDTO update(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public void delete(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResFetchResumeDTO getResume(Resume resume) {
        

        return resumeMapperDTO.getResumeDTO(resume);
    }

    public ResultPaginationDTO fetchAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageUser = this.resumeRepository.findAll(spec, pageable);
        

        return paginationHelper.convertResultPagination(pageUser, pageable);
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        return paginationHelper.convertResultPagination(pageResume, pageable);
    }

    public Optional<Resume> fetchLatestResumeByCurrentUser() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if (email.isEmpty()) return Optional.empty();
        return this.resumeRepository.findTopByEmailOrderByCreatedAtDesc(email);
    }
}
