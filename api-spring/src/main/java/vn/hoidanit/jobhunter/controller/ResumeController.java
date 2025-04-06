package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

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
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.err.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create new resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume) throws IdInvalidException {
        // check id exists
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!isIdExist) {
            throw new IdInvalidException("User id/Job id khong ton tai");
        }

        // create new resume
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume) throws IdInvalidException {
        // check id exists
        Optional<Resume> resumeOptional = this.resumeService.fetchById(resume.getId());
        if (!resumeOptional.isPresent()) {
            throw new IdInvalidException("Resume voi Id = " + resume.getId() + " khong ton tai");
        }

        Resume rqResume = resumeOptional.get();
        rqResume.setStatus(resume.getStatus());
        // create new resume
        return ResponseEntity.ok().body(this.resumeService.update(rqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResumeById(@PathVariable(name = "id") long id) throws IdInvalidException {
        Optional<Resume> currentResume = this.resumeService.fetchById(id);
        if (!currentResume.isPresent()) {
            throw new IdInvalidException("Resume voi id = " + id + " khong ton tai");
        }

        this.resumeService.deleteResumeById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get Resume By Id")
    public ResponseEntity<ResFetchResumeDTO> fetchResumeById(@PathVariable(name = "id") long id)
            throws IdInvalidException {
        Optional<Resume> currentResume = this.resumeService.fetchById(id);
        if (!currentResume.isPresent()) {
            throw new IdInvalidException("Resume voi id = " + id + " khong ton tai");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(currentResume.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("fetch all resume")
    public ResponseEntity<ResultPaginationDTO> fetchAllResume(
            @Filter Specification<Resume> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.fetchAllResume(spec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get List Resume By User")
    public ResponseEntity<ResultPaginationDTO> fetchListResumeByUser(Pageable pageable) {

        return ResponseEntity.ok().body(this.resumeService.fetchListResumeByUser(pageable));
    }

}
