package prac.com_file.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.RoleRequest;
import prac.com_file.dto.RoleResponseDto;
import prac.com_file.model.Role;
import prac.com_file.repository.RoleRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public ApiResponse<RoleResponseDto> createRole(RoleRequest roleRequest) {
        if (roleRepository.existsByName(roleRequest.name())) {
            return new ApiResponse<>(false, "Role with name '" + roleRequest.name() + "' already exists.", null);
        }

        Role newRole = new Role();
        newRole.setName(roleRequest.name());
        newRole.setActiveStatus(true);

        Role savedRole = roleRepository.save(newRole);
        return new ApiResponse<>(true, "Role created successfully.", new RoleResponseDto(savedRole));
    }

    @Override
    public ApiResponse<List<RoleResponseDto>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleResponseDto> dtos = roles.stream()
                .map(RoleResponseDto::new)
                .collect(Collectors.toList());
        return new ApiResponse<>(true, "All roles retrieved successfully.", dtos);
    }

    @Override
    @Transactional
    public ApiResponse<RoleResponseDto> updateRole(Long id, RoleRequest roleRequest) {
        return roleRepository.findById(id)
                .<ApiResponse<RoleResponseDto>>map(existingRole -> {
                    if (roleRepository.existsByName(roleRequest.name()) && !existingRole.getName().equals(roleRequest.name())) {
                        return new ApiResponse<>(false, "Role with name '" + roleRequest.name() + "' already exists.", null);
                    }

                    existingRole.setName(roleRequest.name());
                    Role updatedRole = roleRepository.save(existingRole);
                    return new ApiResponse<>(true, "Role updated successfully.", new RoleResponseDto(updatedRole));
                }).orElseGet(() ->
                        new ApiResponse<>(false, "Role not found with ID: " + id, null)
                );
    }
}