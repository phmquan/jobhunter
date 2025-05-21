package vn.uit.jobhunter.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import vn.uit.jobhunter.domain.Job;
import vn.uit.jobhunter.domain.Resume;
import vn.uit.jobhunter.domain.User;
import vn.uit.jobhunter.domain.request.ReqCreateResumeDTO;
import vn.uit.jobhunter.domain.request.ReqUpdateResumeDTO;
import vn.uit.jobhunter.domain.response.ResResumeDTO;
import vn.uit.jobhunter.domain.response.ResUpdateResumeDTO;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO.Meta;
import vn.uit.jobhunter.domain.response.RestResponse;
import vn.uit.jobhunter.repository.JobRepository;
import vn.uit.jobhunter.repository.ResumeRepository;
import vn.uit.jobhunter.repository.UserRepository;
import vn.uit.jobhunter.service.ResumeService;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Override
    public ResultPaginationDTO fetchAllResumes(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageResume.getContent().stream()
                .map(this::convertToDTO)
                .toList());

        return rs;
    }

    @Override
    @Transactional
    public ResponseEntity<ResResumeDTO> handleCreateResume(ReqCreateResumeDTO dto) {
        User user = userRepository.findById(Long.parseLong(dto.getUser().getId()))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        Job job = jobRepository.findById(Long.parseLong(dto.getJob().getId()))
                .orElseThrow(() -> new EntityNotFoundException("Job not found"));

        Resume resume = new Resume();
        resume.setUrl(dto.getUrl());
        resume.setStatus(dto.getStatus());
        resume.setUser(user);
        resume.setJob(job);

        Resume savedResume = resumeRepository.save(resume);
        return ResponseEntity.ok(convertToDTO(savedResume));
    }

    @Override
    @Transactional
    public ResponseEntity<ResUpdateResumeDTO> handleUpdateResume(ReqUpdateResumeDTO dto) {
        Resume resume = getResumeById(dto.getId());
        resume.setStatus(dto.getStatus());
        Resume updatedResume = resumeRepository.save(resume);
        return ResponseEntity.ok(convertToUpdateDTO(updatedResume));
    }

    @Override
    @Transactional
    public ResponseEntity<?> handleDeleteResume(long id) {
        if (!resumeRepository.existsById(id)) {
            throw new EntityNotFoundException("Resume not found");
        }
        resumeRepository.deleteById(id);
        
        RestResponse<Void> resp = new RestResponse<>();
        resp.setStatusCode(HttpStatus.OK.value());
        resp.setMessage("Xóa thành công");
        resp.setData(null);
        return ResponseEntity.ok().body(resp);
    }

    @Override
    public ResResumeDTO fetchResumeById(long id) {
        return convertToDTO(getResumeById(id));
    }

    @Override
    public Resume getResumeById(long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found"));
    }

    private ResResumeDTO convertToDTO(Resume resume) {
        ResResumeDTO dto = new ResResumeDTO();
        dto.setId(resume.getId());
        dto.setCreatedAt(resume.getCreatedAt());
        dto.setCreatedBy(resume.getCreatedBy());
        return dto;
    }
    private ResUpdateResumeDTO convertToUpdateDTO(Resume resume){
        ResUpdateResumeDTO dto=new ResUpdateResumeDTO(resume.getStatus());
        return dto;
    }
} 