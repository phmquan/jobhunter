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
import vn.uit.jobhunter.domain.Skill;
import vn.uit.jobhunter.domain.response.ResultPaginationDTO;
import vn.uit.jobhunter.service.SkillService;
import vn.uit.jobhunter.util.anotation.ApiMessage;
import vn.uit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @GetMapping("")
    @ApiMessage("Get all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt,desc") String sort) {
        return ResponseEntity.ok(skillService.getAllSkills(page, size, sort));
    }

    @GetMapping("{id}")
    @ApiMessage("Get Skill By id")
    public ResponseEntity<?> getSkillById(@PathVariable Long id) {
        return skillService.handleFindSkillById(id);
    }

    @PostMapping("")
    @ApiMessage("Create Skill")
    public ResponseEntity<?> createSkill(@Valid @RequestBody Skill postSkill) {
        if (skillService.findSkillByName(postSkill)) {
            return ResponseEntity.badRequest().body("Kỹ năng đã tồn tại");
        } else {
            return ResponseEntity.ok(skillService.handleCreateSkill(postSkill));
        }
    }

    @PutMapping("")
    @ApiMessage("Update Skill")
    public ResponseEntity<?> updateSkill(@Valid @RequestBody Skill postSkill) throws IdInvalidException {
        return skillService.handleUpdateSkill(postSkill);
    }

    @DeleteMapping("{id}")
    @ApiMessage("Delete Skill")
    public ResponseEntity<?> deleteSkill(@PathVariable("id") Long id) {
        return skillService.handleDeleteById(id);
    }
} 