package prac.com_file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.RegionRequestDto;
import prac.com_file.dto.RegionResponseDto;
import prac.com_file.service.RegionService;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RegionResponseDto>> createRegion(@RequestBody RegionRequestDto regionRequestDto) {
        ApiResponse<RegionResponseDto> response = regionService.createRegion(regionRequestDto);
        return ResponseEntity.status(response.success() ? 201 : 400).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegionResponseDto>> getRegionById(@PathVariable Long id) {
        ApiResponse<RegionResponseDto> response = regionService.getRegionById(id);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RegionResponseDto>>> getAllRegions() {
        ApiResponse<List<RegionResponseDto>> response = regionService.getAllRegions();
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RegionResponseDto>>> getAllActiveRegions() {
        ApiResponse<List<RegionResponseDto>> response = regionService.getAllActiveRegions();
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RegionResponseDto>> updateRegion(
            @PathVariable Long id,
            @RequestBody RegionRequestDto regionRequestDto) {
        ApiResponse<RegionResponseDto> response = regionService.updateRegion(id, regionRequestDto);
        return ResponseEntity.status(response.success() ? 200 : 400).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRegion(@PathVariable Long id) {
        ApiResponse<String> response = regionService.deleteRegion(id);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/code/{regionCode}")
    public ResponseEntity<ApiResponse<RegionResponseDto>> getRegionByCode(@PathVariable String regionCode) {
        ApiResponse<RegionResponseDto> response = regionService.getRegionByCode(regionCode);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }
}