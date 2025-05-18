package vn.uit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.uit.jobhunter.domain.Job;
import vn.uit.jobhunter.domain.Skill;
import vn.uit.jobhunter.domain.response.RestResponse;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.repository.CompanyRepository;
import vn.uit.jobhunter.repository.JobRepository;
import vn.uit.jobhunter.repository.SkillRepository;
import vn.uit.jobhunter.util.error.IdInvalidException;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    private final CompanyRepository companyRepository;

    private final SkillRepository skillRepository;

    public ResultPaginationDTO getAllJobs(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        Page<Job> jobPage = jobRepository.findAll(pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page);
        meta.setPageSize(size);
        meta.setPages(jobPage.getTotalPages());
        meta.setTotal(jobPage.getTotalElements());

        return new ResultPaginationDTO(meta, jobPage.getContent());
    }

    public Job handleCreateJob(Job postJob) {
        if(postJob.getCompany()!=null){
            postJob.setCompany(companyRepository.findById(postJob.getCompany().getId()).get());
        }
        List<Long> postSkills=new ArrayList<>();
        if(postJob.getSkills()!=null){
            for(Skill skill:postJob.getSkills()){
                postSkills.add(skill.getId());
            }
            postJob.setSkills(skillRepository.findByIdIn(postSkills));
        }
        postJob.setSkills(skillRepository.findByIdIn(postSkills));
        return jobRepository.save(postJob);
    }

    public boolean findJobByName(Job postJob) {
        return jobRepository.existsByName(postJob.getName());
    }

    public ResponseEntity<?> handleUpdateJob(Job postJob) throws IdInvalidException {
        if (jobRepository.findById(postJob.getId()).isPresent()) {
            Job updateJob = jobRepository.findById(postJob.getId()).get();
            updateJob.setName(postJob.getName());
            updateJob.setLocation(postJob.getLocation());
            updateJob.setSalary(postJob.getSalary());
            updateJob.setQuantity(postJob.getQuantity());
            updateJob.setLevel(postJob.getLevel());
            updateJob.setDescription(postJob.getDescription());
            updateJob.setStartDate(postJob.getStartDate());
            updateJob.setEndDate(postJob.getEndDate());
            updateJob.setActive(postJob.isActive());
            if(postJob.getCompany()!=null){
                updateJob.setCompany(companyRepository.findById(postJob.getCompany().getId()).get());
            }
            List<Long> postSkills=new ArrayList<>();
            if(postJob.getSkills()!=null){
            for(Skill skill:postJob.getSkills()){
                postSkills.add(skill.getId());
            }
            updateJob.setSkills(skillRepository.findByIdIn(postSkills));
            }
            return ResponseEntity.ok(jobRepository.save(updateJob));
        } else {
            throw new IdInvalidException("Id không tồn tại, không thể cập nhật");
        }
    }

    public ResponseEntity<?> handleFindJobById(Long id) {
        if (jobRepository.findById(id).isPresent()) {
            return ResponseEntity.ok(jobRepository.findById(id));
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy công việc");
        }
    }

    public ResponseEntity<?> handleDeleteById(long id) {
        jobRepository.deleteById(id);
        RestResponse<Void> resp = new RestResponse<>();
        resp.setStatusCode(HttpStatus.OK.value());
        resp.setMessage("Xóa Job thành công");
        resp.setData(null);
        return ResponseEntity.ok().body(resp);
    }
} 