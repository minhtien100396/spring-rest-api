package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
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
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rDto.setMeta(mt);

        List<ResUserDTO> listUser = pageUser.getContent().stream()
                .map(item -> new ResUserDTO(
                        item.getId(),
                        item.getName(),
                        item.getEmail(),
                        item.getAge(),
                        item.getGender(),
                        item.getAddress(),
                        item.getCreatedAt(),
                        item.getUpdatedAt()))
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
        resCreateUserDTO.setId(rqUser.getId());
        resCreateUserDTO.setAge(rqUser.getAge());
        resCreateUserDTO.setAddress(rqUser.getAddress());
        resCreateUserDTO.setCreatedAt(rqUser.getCreatedAt());
        resCreateUserDTO.setEmail(rqUser.getEmail());
        resCreateUserDTO.setName(rqUser.getName());
        resCreateUserDTO.setGender(rqUser.getGender());
        return resCreateUserDTO;
    }

    public ResUserDTO convertToResUserDTO(User fetchUser) {
        ResUserDTO resUserDTO = new ResUserDTO();
        resUserDTO.setId(fetchUser.getId());
        resUserDTO.setAge(fetchUser.getAge());
        resUserDTO.setAddress(fetchUser.getAddress());
        resUserDTO.setCreatedAt(fetchUser.getCreatedAt());
        resUserDTO.setEmail(fetchUser.getEmail());
        resUserDTO.setName(fetchUser.getName());
        resUserDTO.setGender(fetchUser.getGender());
        resUserDTO.setUpdatedAt(fetchUser.getUpdatedAt());

        return resUserDTO;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User fetchUser) {
        ResUpdateUserDTO rDto = new ResUpdateUserDTO();
        rDto.setId(fetchUser.getId());
        rDto.setAge(fetchUser.getAge());
        rDto.setAddress(fetchUser.getAddress());
        rDto.setName(fetchUser.getName());
        rDto.setGender(fetchUser.getGender());
        rDto.setUpdatedAt(fetchUser.getUpdatedAt());
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
