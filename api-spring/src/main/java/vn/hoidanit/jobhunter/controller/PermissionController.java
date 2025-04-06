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
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.err.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    public final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create new permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission permission) throws IdInvalidException {
        // check permission exists
        boolean isPermissionExist = this.permissionService.isPermissionExist(permission);
        if (isPermissionExist) {
            throw new IdInvalidException("Permission da ton tai");
        }

        // create new permissions
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> update(@RequestBody Permission permission) throws IdInvalidException {
        // check id exists
        Optional<Permission> permissionOptional = this.permissionService.fetchById(permission.getId());
        if (!permissionOptional.isPresent()) {
            throw new IdInvalidException("Permissionme voi Id = " + permission.getId() + " khong ton tai");
        }

        // check permission exists
        boolean isPermissionExist = this.permissionService.isPermissionExist(permission);
        if (isPermissionExist) {
            throw new IdInvalidException("Permission da ton tai. Vui long update bang thong tin khac");
        }
        // create new permission
        return ResponseEntity.ok().body(this.permissionService.update(permission));
    }

    @GetMapping("/permissions")
    @ApiMessage("fetch all permission")
    public ResponseEntity<ResultPaginationDTO> fetchAllPermission(
            @Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.permissionService.fetchAllPermission(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deletePermissionById(@PathVariable(name = "id") long id) throws IdInvalidException {
        Optional<Permission> currentPermission = this.permissionService.fetchById(id);
        if (!currentPermission.isPresent()) {
            throw new IdInvalidException("Permission voi id = " + id + " khong ton tai");
        }

        this.permissionService.deletePermissionById(id);
        return ResponseEntity.ok().build();
    }
}
