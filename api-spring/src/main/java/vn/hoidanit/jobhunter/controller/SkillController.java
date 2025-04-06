package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.err.IdInvalidException;

@RequestMapping("/api/v1")
@RestController
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create new skill")
    public ResponseEntity<Skill> create(@Valid @RequestBody Skill rqSkill) throws IdInvalidException {
        // check name
        if (rqSkill.getName() != null && this.skillService.isNameExist(rqSkill.getName())) {
            throw new IdInvalidException("Skill name = " + rqSkill.getName() + " da ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(rqSkill));
    }

    @PutMapping("/skills")
    @ApiMessage("Update skill by Id")
    public ResponseEntity<Skill> updateUserById(@RequestBody Skill rqSkill) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(rqSkill.getId());
        // check id
        if (currentSkill == null) {
            throw new IdInvalidException("Skill id = " + rqSkill.getId() + " khong ton tai");
        }

        // check name
        if (rqSkill.getName() != null && this.skillService.isNameExist(rqSkill.getName())) {
            throw new IdInvalidException("Skill name = " + rqSkill.getName() + " da ton tai");
        }

        currentSkill.setName(rqSkill.getName());

        return ResponseEntity.ok(this.skillService.updateSkill(currentSkill));
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skill")
    public ResponseEntity<ResultPaginationDTO> fetchAllSkill(
            @Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.fetchAllSkill(spec, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> deleteSkillById(@PathVariable(name = "id") long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Skill voi id = " + id + " khong ton tai");
        }

        this.skillService.deleteSkillById(id);
        return ResponseEntity.ok().build();
    }

}
