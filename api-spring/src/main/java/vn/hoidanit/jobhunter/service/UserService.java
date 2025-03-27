package vn.hoidanit.jobhunter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
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

    public List<User> fetchAllUser() {
        return userRepository.findAll();
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

}
