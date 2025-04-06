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
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.err.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    public final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create new role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) throws IdInvalidException {
        // check name exists
        boolean isRoleNameExist = this.roleService.isRoleNameExist(role);
        if (isRoleNameExist) {
            throw new IdInvalidException("Role voi name = " + role.getName() + " da ton tai");
        }

        // create new role
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update role")
    public ResponseEntity<Role> update(@RequestBody Role role) throws IdInvalidException {
        // check id exists
        Optional<Role> roleOptional = this.roleService.fetchById(role.getId());
        if (!roleOptional.isPresent()) {
            throw new IdInvalidException("Role voi Id = " + role.getId() + " khong ton tai");
        }

        // check name exists
        boolean isRoleNameExist = this.roleService.isRoleNameExist(role);
        if (isRoleNameExist) {
            throw new IdInvalidException("Role voi name = " + role.getName() + " da ton tai");
        }

        // update role
        return ResponseEntity.ok().body(this.roleService.update(role));
    }

    @GetMapping("/roles")
    @ApiMessage("fetch all role")
    public ResponseEntity<ResultPaginationDTO> fetchAllRole(
            @Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.fetchAllRole(spec, pageable));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRoleById(@PathVariable(name = "id") long id) throws IdInvalidException {
        Optional<Role> currentRole = this.roleService.fetchById(id);
        if (!currentRole.isPresent()) {
            throw new IdInvalidException("Role voi id = " + id + " khong ton tai");
        }

        this.roleService.deleteRoleById(id);
        return ResponseEntity.ok().build();
    }
}
