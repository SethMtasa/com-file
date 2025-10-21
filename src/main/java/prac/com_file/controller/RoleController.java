package prac.com_file.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.RoleRequest;
import prac.com_file.dto.RoleResponseDto;
import prac.com_file.service.RoleService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Creates a new role.
     * @param roleRequest The DTO containing the role name.
     * @return ApiResponse containing the created RoleResponseDto.
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<RoleResponseDto>> createRole(@RequestBody RoleRequest roleRequest) {
        ApiResponse<RoleResponseDto> apiResponse = roleService.createRole(roleRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.success() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    /**
     * Retrieves all roles in the system.
     * @return ApiResponse containing a list of RoleResponseDto.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponseDto>>> getAllRoles() {
        ApiResponse<List<RoleResponseDto>> apiResponse = roleService.getAllRoles();
        return new ResponseEntity<>(apiResponse, apiResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    /**
     * Updates an existing role.
     * @param id The ID of the role to update.
     * @param roleRequest The DTO containing the new role name.
     * @return ApiResponse containing the updated RoleResponseDto.
     */
    @PutMapping("update/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDto>> updateRole(
            @PathVariable Long id,
            @RequestBody RoleRequest roleRequest) {
        ApiResponse<RoleResponseDto> apiResponse = roleService.updateRole(id, roleRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}