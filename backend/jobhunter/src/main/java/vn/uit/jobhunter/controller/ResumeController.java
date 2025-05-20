package vn.uit.jobhunter.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.uit.jobhunter.domain.Resume;
import vn.uit.jobhunter.domain.request.ReqCreateResumeDTO;
import vn.uit.jobhunter.domain.request.ReqUpdateResumeDTO;
import vn.uit.jobhunter.domain.response.ResResumeDTO;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.service.ResumeService;
import vn.uit.jobhunter.util.anotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping
    @ApiMessage("Get all resumes")
    public ResponseEntity<ResultPaginationDTO> getAllResumes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort,
            @Filter Specification<Resume> spec) {
        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(resumeService.fetchAllResumes(spec, pageable));
    }

    @PostMapping
    @ApiMessage("Create resume")
    public ResponseEntity<ResResumeDTO> createResume(@Valid @RequestBody ReqCreateResumeDTO dto) {
        return resumeService.handleCreateResume(dto);
    }

    @PutMapping("/{id}")
    @ApiMessage("Update resume status")
    public ResponseEntity<ResResumeDTO> updateResume(
            @PathVariable long id,
            @Valid @RequestBody ReqUpdateResumeDTO dto) {
        return resumeService.handleUpdateResume(id, dto);
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete resume")
    public ResponseEntity<?> deleteResume(@PathVariable long id) {
        return resumeService.handleDeleteResume(id);
    }

    @GetMapping("/{id}")
    @ApiMessage("Get resume by id")
    public ResponseEntity<ResResumeDTO> getResumeById(@PathVariable long id) {
        return ResponseEntity.ok(resumeService.fetchResumeById(id));
    }
} 