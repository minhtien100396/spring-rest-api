package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;
import vn.hoidanit.jobhunter.util.err.IdInvalidException;

@Service
public class RoleService {
    public final RoleRepository roleRepository;
    public final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean isRoleNameExist(Role role) {
        return this.roleRepository.existsByName(role.getName());
    }

    public Role create(Role role) {

        // check permission
        if (role.getPermissions() != null) {
            List<Long> rqPermission = role.getPermissions().stream().map(permission -> permission.getId())
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(rqPermission);
            role.setPermissions(dbPermissions);
        }

        // create role
        return this.roleRepository.save(role);
    }

    public Optional<Role> fetchById(long id) {
        return this.roleRepository.findById(id);
    }

    public Role update(Role rqRole) throws IdInvalidException {

        // check permission
        if (rqRole.getPermissions() != null) {
            List<Long> rqPermission = rqRole.getPermissions().stream().map(permission -> permission.getId())
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(rqPermission);
            rqRole.setPermissions(dbPermissions);
        }

        Role roleDB = this.roleRepository.findById(rqRole.getId()).isPresent()
                ? this.roleRepository.findById(rqRole.getId()).get()
                : null;

        if (roleDB != null) {
            if (!rqRole.getName().equals(roleDB.getName())) {
                roleDB.setName(rqRole.getName());
            }
            roleDB.setName(rqRole.getName());
            roleDB.setDescription(rqRole.getDescription());
            roleDB.setActive(rqRole.isActive());
            roleDB.setPermissions(rqRole.getPermissions());
            return this.roleRepository.save(roleDB);
        }
        return null;

    }

    public ResultPaginationDTO fetchAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rDto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageRole.getTotalPages());
        mt.setTotal(pageRole.getTotalElements());

        rDto.setMeta(mt);

        rDto.setResult(pageRole.getContent());

        return rDto;
    }

    public void deleteRoleById(long id) {
        this.roleRepository.deleteById(id);
    }
}
