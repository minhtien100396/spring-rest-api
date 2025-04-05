package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.err.IdInvalidException;

@RequestMapping("/api/v1")
@RestController
public class UserController {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User rqUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(rqUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + rqUser.getEmail() + " da ton tai, vui long su dung email khac");
        }
        String hashPassword = this.passwordEncoder.encode(rqUser.getPassword());
        rqUser.setPassword(hashPassword);
        userService.handleCreateUser(rqUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(rqUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUserById(@PathVariable(name = "id") long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User voi id = " + id + " khong ton tai");
        }

        this.userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{user-id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDTO> fetchUserById(@PathVariable(name = "user-id") long userId)
            throws IdInvalidException {
        User fetchUser = userService.fetchUserById(userId);
        if (fetchUser == null) {
            throw new IdInvalidException("User voi id = " + userId + "khong ton tai");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(fetchUser));
    }

    // @GetMapping("/users")
    // public ResponseEntity<ResultPaginationDTO> fetchAllUser(
    // @RequestParam("current") Optional<String> currentOptional,
    // @RequestParam("pageSize") Optional<String> pageSizeOptional) {
    // String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
    // String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() :
    // "";

    // int current = Integer.parseInt(sCurrent);
    // int pageSize = Integer.parseInt(sPageSize);
    // Pageable pageable = PageRequest.of(current - 1, pageSize);
    // ResultPaginationDTO rDto = userService.fetchAllUser(pageable);
    // return ResponseEntity.ok(rDto);
    // }

    @GetMapping("/users")
    @ApiMessage("fetch all user")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
            @Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update user by Id")
    public ResponseEntity<ResUpdateUserDTO> updateUserById(@RequestBody User rqUser) throws IdInvalidException {
        User userUpdate = userService.handleUpdateUser(rqUser);
        if (rqUser == null) {
            throw new IdInvalidException("User voi id = " + rqUser.getId() + " khong ton tai");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(userUpdate));
    }

}
