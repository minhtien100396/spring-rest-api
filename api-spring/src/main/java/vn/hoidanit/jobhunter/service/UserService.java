package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
    }

    public User handleCreateUser(User user) {
        // check company
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyRepository.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }

        // check role
        if (user.getRole() != null) {
            Optional<Role> roleOptional = this.roleRepository.findById(user.getRole().getId());
            user.setRole(roleOptional.isPresent() ? roleOptional.get() : null);
        }

        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public User fetchUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // public ResultPaginationDTO fetchAllUser(Pageable pageable) {
    // Page<User> pageUser = this.userRepository.findAll(pageable);
    // ResultPaginationDTO rDto = new ResultPaginationDTO();
    // Meta mt = new Meta();

    // mt.setPage(pageUser.getNumber() + 1);
    // mt.setPageSize(pageUser.getSize());

    // mt.setPages(pageUser.getTotalPages());
    // mt.setTotal(pageUser.getTotalElements());

    // rDto.setMeta(mt);
    // rDto.setResult(pageUser.getContent());
    // return rDto;
    // }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rDto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rDto.setMeta(mt);

        List<ResUserDTO> listUser = pageUser.getContent().stream()
                .map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        rDto.setResult(listUser);

        return rDto;
    }

    public User handleUpdateUser(User user) {
        User userCurrent = userRepository.findById(user.getId()).orElse(null);
        if (userCurrent != null) {
            userCurrent.setAddress(user.getAddress());
            userCurrent.setName(user.getName());
            userCurrent.setGender(user.getGender());
            userCurrent.setAge(user.getAge());

            // check company
            if (user.getCompany() != null) {
                Optional<Company> companyOptional = this.companyRepository.findById(user.getCompany().getId());
                userCurrent.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }

            // check role
            if (user.getRole() != null) {
                Optional<Role> roleOptional = this.roleRepository.findById(user.getRole().getId());
                userCurrent.setRole(roleOptional.isPresent() ? roleOptional.get() : null);
            }

            // update
            userCurrent = this.userRepository.save(userCurrent);
        }
        return userCurrent;
    }

    public User handleGetUserByUserName(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User rqUser) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();
        if (rqUser.getCompany() != null) {
            com.setId(rqUser.getCompany().getId());
            com.setName(rqUser.getCompany().getName());
            resCreateUserDTO.setCompany(com);
        }
        resCreateUserDTO.setId(rqUser.getId());
        resCreateUserDTO.setAge(rqUser.getAge());
        resCreateUserDTO.setAddress(rqUser.getAddress());
        resCreateUserDTO.setCreatedAt(rqUser.getCreatedAt());
        resCreateUserDTO.setEmail(rqUser.getEmail());
        resCreateUserDTO.setName(rqUser.getName());
        resCreateUserDTO.setGender(rqUser.getGender());
        return resCreateUserDTO;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            resUserDTO.setCompany(com);
        }

        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            resUserDTO.setRoleUser(roleUser);
        }

        resUserDTO.setId(user.getId());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setName(user.getName());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());

        return resUserDTO;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO rDto = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            rDto.setCompany(com);
        }
        rDto.setId(user.getId());
        rDto.setAge(user.getAge());
        rDto.setAddress(user.getAddress());
        rDto.setName(user.getName());
        rDto.setGender(user.getGender());
        rDto.setUpdatedAt(user.getUpdatedAt());
        return rDto;
    }

    public void updateUserToken(String email, String token) {
        User currentUser = this.handleGetUserByUserName(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

}
