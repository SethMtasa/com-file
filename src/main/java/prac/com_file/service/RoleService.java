package prac.com_file.service;

import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.RoleRequest;
import prac.com_file.dto.RoleResponseDto; // Import the DTO
import java.util.List;

public interface RoleService {

    ApiResponse<RoleResponseDto> createRole(RoleRequest roleRequest);

    ApiResponse<List<RoleResponseDto>> getAllRoles();

    // New method to update a role
    ApiResponse<RoleResponseDto> updateRole(Long id, RoleRequest roleRequest);
}