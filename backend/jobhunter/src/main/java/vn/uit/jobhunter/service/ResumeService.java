package vn.uit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import vn.uit.jobhunter.domain.Resume;
import vn.uit.jobhunter.domain.request.ReqCreateResumeDTO;
import vn.uit.jobhunter.domain.request.ReqUpdateResumeDTO;
import vn.uit.jobhunter.domain.response.ResResumeDTO;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;

public interface ResumeService {
    ResultPaginationDTO fetchAllResumes(Specification<Resume> spec, Pageable pageable);
    ResponseEntity<ResResumeDTO> handleCreateResume(ReqCreateResumeDTO dto);
    ResponseEntity<ResResumeDTO> handleUpdateResume(long id, ReqUpdateResumeDTO dto);
    ResponseEntity<?> handleDeleteResume(long id);
    ResResumeDTO fetchResumeById(long id);
    Resume getResumeById(long id);
} 