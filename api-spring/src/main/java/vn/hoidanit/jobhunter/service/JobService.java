package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public ResCreateJobDTO create(Job rqJob) {

        // check skill
        if (rqJob.getSkills() != null) {
            List<Long> rqSkill = rqJob.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(rqSkill);
            rqJob.setSkills(dbSkills);
        }

        if (rqJob.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(rqJob.getCompany().getId());
            if (cOptional.isPresent()) {
                rqJob.setCompany(cOptional.get());
            }
        }

        // create job
        Job currentJob = this.jobRepository.save(rqJob);

        // convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();

        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setDescription(currentJob.getDescription());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills().stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;

    }

    public Job fetchJobById(long id) {
        Optional<Job> currentJobOptional = this.jobRepository.findById(id);
        if (currentJobOptional.isPresent()) {
            return currentJobOptional.get();
        }
        return null;
    }

    public ResUpdateJobDTO update(Job rqJob, Job jobInDB) {

        // check skill
        if (rqJob.getSkills() != null) {
            List<Long> rqSkill = rqJob.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(rqSkill);
            jobInDB.setSkills(dbSkills);
        }

        // If the job already exists, don't overwrite createdBy and createdAt
        if (rqJob.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(rqJob.getCompany().getId());
            if (cOptional.isPresent()) {
                jobInDB.setCompany(cOptional.get());
            }
        }

        jobInDB.setName(rqJob.getName());
        jobInDB.setSalary(rqJob.getSalary());
        jobInDB.setQuantity(rqJob.getQuantity());
        jobInDB.setLocation(rqJob.getLocation());
        jobInDB.setLevel(rqJob.getLevel());
        jobInDB.setDescription(rqJob.getDescription());
        jobInDB.setStartDate(rqJob.getStartDate());
        jobInDB.setEndDate(rqJob.getEndDate());
        jobInDB.setActive(rqJob.isActive());

        // create job
        Job currentJob = this.jobRepository.save(jobInDB);
        // convert response
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    public void deleteJobById(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rDto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());

        rDto.setMeta(mt);

        rDto.setResult(pageJob.getContent());

        return rDto;
    }

}
