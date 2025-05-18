package vn.uit.jobhunter.controller;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.uit.jobhunter.domain.Job;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.service.JobService;
import vn.uit.jobhunter.util.anotation.ApiMessage;
import vn.uit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @GetMapping("")
    @ApiMessage("Get all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort) {
        return ResponseEntity.ok(jobService.getAllJobs(page, size, sort));
    }

    @GetMapping("{id}")
    @ApiMessage("Get Job By id")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        return jobService.handleFindJobById(id);
    }

    @PostMapping("")
    @ApiMessage("Create Job")
    public ResponseEntity<?> createJob(@Valid @RequestBody Job postJob) throws IdInvalidException{
        return ResponseEntity.ok(jobService.handleCreateJob(postJob));
        
    }

    @PutMapping("")
    @ApiMessage("Update Job")
    public ResponseEntity<?> updateJob(@Valid @RequestBody Job postJob) throws IdInvalidException {
        return jobService.handleUpdateJob(postJob);
    }

    @DeleteMapping("{id}")
    @ApiMessage("Delete Job")
    public ResponseEntity<?> deleteJob(@PathVariable("id") Long id) {
        return jobService.handleDeleteById(id);
    }
} 