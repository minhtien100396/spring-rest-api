package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
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
        Meta mt = new Meta();

        mt.setPage(pageUser.getNumber() + 1);
        mt.setPageSize(pageUser.getSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rDto.setMeta(mt);
        rDto.setResult(pageUser.getContent());

        return rDto;
    }

    public User handleUpdateUser(User user) {
        User userCurrent = userRepository.findById(user.getId()).orElse(null);
        if (userCurrent != null) {
            userCurrent.setEmail(user.getEmail());
            userCurrent.setName(user.getName());
            userCurrent.setPassword(user.getPassword());
            userRepository.save(userCurrent);
        }
        return userCurrent;
    }

    public User handleGetUserByUserName(String username) {
        return this.userRepository.findByEmail(username);
    }

}
