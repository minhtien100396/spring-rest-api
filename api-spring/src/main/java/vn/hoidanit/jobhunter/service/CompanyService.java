package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class CompanyService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public ResultPaginationDTO fetchListCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);

        ResultPaginationDTO rData = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getNumberOfElements());

        rData.setMeta(meta);
        rData.setResult(pageCompany.getContent());

        return rData;
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> companyOptional = this.companyRepository.findById((int) company.getId());
        if (companyOptional.isPresent()) {
            Company currentCompany = companyOptional.get();
            currentCompany.setAddress(company.getAddress());
            currentCompany.setLogo(company.getLogo());
            currentCompany.setName(company.getName());
            currentCompany.setDescription(company.getDescription());
            return this.companyRepository.save(currentCompany);
        }
        return null;

    }

    public void deleteCompany(long idCompany) {
        this.companyRepository.deleteById((int) idCompany);
    }

}
