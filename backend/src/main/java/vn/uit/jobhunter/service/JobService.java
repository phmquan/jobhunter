package vn.uit.jobhunter.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import lombok.AllArgsConstructor;
import vn.uit.jobhunter.domain.Company;
import vn.uit.jobhunter.domain.Job;
import vn.uit.jobhunter.domain.Skill;
import vn.uit.jobhunter.domain.response.ResEmbeddingJobDes;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.uit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.uit.jobhunter.repository.CompanyRepository;
import vn.uit.jobhunter.repository.JobRepository;
import vn.uit.jobhunter.repository.SkillRepository;
import vn.uit.jobhunter.service.mapper.JobMapperDTO;
import vn.uit.jobhunter.service.pagination.PaginationHelper;
import vn.uit.jobhunter.service.rag_service.PythonBackendService;
import vn.uit.jobhunter.service.validation.JobValidator;

@Service
@AllArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;
    private final PythonBackendService pythonBackendService;
    private final JobMapperDTO jobMapperDTO;
    private final PaginationHelper paginationHelper;
    private final JobValidator jobValidator;


    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResCreateJobDTO create(Job j) {
        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills().stream().map(Skill::getId).collect(Collectors.toList());
            j.setSkills(jobValidator.fetchSkillsIfExist(reqSkills));
        }

        // Handle company
        if (j.getCompany() != null) {
            j.setCompany(jobValidator.fetchCompanyIfExist(j.getCompany().getId()));
        }

        // create job
        Job currentJob = this.jobRepository.save(j);
        ResEmbeddingJobDes resEmbeddingJobDes=new ResEmbeddingJobDes(currentJob.getId(),currentJob.getName(),currentJob.getLevel().toString(),currentJob.getDescription());
         // Send currentJob to Python backend
        pythonBackendService.sendJobToPythonBackend(resEmbeddingJobDes);
        // convert response
        ResCreateJobDTO dto = jobMapperDTO.convertToCreateJobDTO(currentJob);

        return dto;
    }
    
    public ResUpdateJobDTO update(Job j, Job jobInDB) {

        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills().stream().map(Skill::getId).collect(Collectors.toList());
            jobInDB.setSkills(jobValidator.fetchSkillsIfExist(reqSkills));
        }

        // Handle company via validator
        if (j.getCompany() != null) {
            jobInDB.setCompany(jobValidator.fetchCompanyIfExist(j.getCompany().getId()));
        }

        // update correct info
        jobInDB.setName(j.getName());
        jobInDB.setSalary(j.getSalary());
        jobInDB.setQuantity(j.getQuantity());
        jobInDB.setLocation(j.getLocation());
        jobInDB.setLevel(j.getLevel());
        jobInDB.setStartDate(j.getStartDate());
        jobInDB.setEndDate(j.getEndDate());
        jobInDB.setActive(j.isActive());

        // update job
        Job currentJob = this.jobRepository.save(jobInDB);

        // convert response
        ResUpdateJobDTO dto = jobMapperDTO.convertResUpdateJobDTO(currentJob);

        return dto;
    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageUser = this.jobRepository.findAll(spec, pageable);

        
        return paginationHelper.convertResultPagination(pageUser,pageable);
    }
}
