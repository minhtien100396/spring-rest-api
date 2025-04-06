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
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.err.IdInvalidException;

@RequestMapping("/api/v1")
@RestController
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create new job")
    public ResponseEntity<ResCreateJobDTO> createNewJob(@Valid @RequestBody Job rqJob) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.create(rqJob));
    }

    @PutMapping("/jobs")
    @ApiMessage("update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job rqJob) throws IdInvalidException {
        Job currentJob = this.jobService.fetchJobById(rqJob.getId());
        if (currentJob == null) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.ok().body(this.jobService.update(rqJob, currentJob));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> deleteJobById(@PathVariable(name = "id") long id) throws IdInvalidException {
        Job currentJob = this.jobService.fetchJobById(id);
        if (currentJob == null) {
            throw new IdInvalidException("Job voi id = " + id + " khong ton tai");
        }

        this.jobService.deleteJobById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get Job By Id")
    public ResponseEntity<Job> fetchJobById(@PathVariable(name = "id") long id) throws IdInvalidException {
        Job currentJob = this.jobService.fetchJobById(id);
        if (currentJob == null) {
            throw new IdInvalidException("Job voi id = " + id + " khong ton tai");
        }
        return ResponseEntity.ok().body(currentJob);
    }

    @GetMapping("/jobs")
    @ApiMessage("fetch all job")
    public ResponseEntity<ResultPaginationDTO> fetchAllJob(
            @Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.fetchAllJob(spec, pageable));
    }
}
