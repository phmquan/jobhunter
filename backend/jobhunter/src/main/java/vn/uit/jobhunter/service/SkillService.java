package vn.uit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.uit.jobhunter.domain.Skill;
import vn.uit.jobhunter.domain.response.RestResponse;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.repository.SkillRepository;
import vn.uit.jobhunter.util.error.IdInvalidException;

@Service
public class SkillService {
    @Autowired
    private SkillRepository skillRepository;

    public ResultPaginationDTO getAllSkills(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        Page<Skill> skillPage = skillRepository.findAll(pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page);
        meta.setPageSize(size);
        meta.setPages(skillPage.getTotalPages());
        meta.setTotal(skillPage.getTotalElements());

        return new ResultPaginationDTO(meta, skillPage.getContent());
    }

    public Skill handleCreateSkill(Skill postSkill) {
        return skillRepository.save(postSkill);
    }

    public boolean findSkillByName(Skill postSkill) {
        return skillRepository.existsByName(postSkill.getName());
    }

    public ResponseEntity<?> handleUpdateSkill(Skill postSkill) throws IdInvalidException {
        if (skillRepository.findById(postSkill.getId()).isPresent()) {
            Skill updateSkill = skillRepository.findById(postSkill.getId()).get();
            updateSkill.setName(postSkill.getName());
            updateSkill.setJobs(postSkill.getJobs());
            updateSkill.setSubscribers(postSkill.getSubscribers());
            return ResponseEntity.ok(skillRepository.save(updateSkill));
        } else {
            throw new IdInvalidException("Id không tồn tại, không thể cập nhật");
        }
    }

    public ResponseEntity<?> handleFindSkillById(Long id) {
        if (skillRepository.findById(id).isPresent()) {
            return ResponseEntity.ok(skillRepository.findById(id));
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy kỹ năng");
        }
    }

    public ResponseEntity<?> handleDeleteById(long id) {
        skillRepository.deleteById(id);
        RestResponse<Void> resp = new RestResponse<>();
        resp.setStatusCode(HttpStatus.OK.value());
        resp.setMessage("Xóa Skill thành công");
        resp.setData(null);
        return ResponseEntity.ok().body(resp);
    }
}